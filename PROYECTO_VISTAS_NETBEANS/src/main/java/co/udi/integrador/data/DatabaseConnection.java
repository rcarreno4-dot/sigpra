package co.udi.integrador.data;

import co.udi.integrador.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class DatabaseConnection {
    private static final DatabaseConfig CONFIG = DatabaseConfig.load();
    private static final String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";
    private static final List<ClassLoader> EXTRA_DRIVER_LOADERS = new ArrayList<>();
    private static boolean oracleDriverReady;

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        ensureOracleDriverLoaded();
        return DriverManager.getConnection(CONFIG.url(), CONFIG.user(), CONFIG.password());
    }

    private static void ensureOracleDriverLoaded() throws SQLException {
        if (oracleDriverReady) {
            return;
        }
        try {
            Class.forName(ORACLE_DRIVER);
            oracleDriverReady = true;
            return;
        } catch (ClassNotFoundException ex) {
            if (tryLoadDriverFromPortableFolders()) {
                oracleDriverReady = true;
                return;
            }
            throw new SQLException(
                    "No se encontro el driver de Oracle JDBC en el classpath. "
                            + "Incluye un driver compatible (por ejemplo ojdbc11.jar u ojdbc14.jar) "
                            + "en la carpeta del portable/app, o verifica el repositorio local Maven (~/.m2/repository).",
                    ex
            );
        }
    }

    private static boolean tryLoadDriverFromPortableFolders() {
        Set<Path> candidateJars = new LinkedHashSet<>();
        for (Path base : discoverBaseFolders()) {
            candidateJars.add(base.resolve("ojdbc11.jar"));
            candidateJars.add(base.resolve("ojdbc8.jar"));
            candidateJars.add(base.resolve("ojdbc14.jar"));
            candidateJars.add(base.resolve("app").resolve("ojdbc11.jar"));
            candidateJars.add(base.resolve("app").resolve("ojdbc8.jar"));
            candidateJars.add(base.resolve("app").resolve("ojdbc14.jar"));
            candidateJars.addAll(findOjdbcByPattern(base));
            candidateJars.addAll(findOjdbcByPattern(base.resolve("app")));
        }
        candidateJars.addAll(findOjdbcInMavenLocalRepository());

        for (Path jar : candidateJars) {
            if (!Files.isRegularFile(jar)) {
                continue;
            }
            try {
                URLClassLoader loader = new URLClassLoader(new URL[]{jar.toUri().toURL()}, DatabaseConnection.class.getClassLoader());
                Class<?> clazz = Class.forName(ORACLE_DRIVER, true, loader);
                Driver driver = (Driver) clazz.getDeclaredConstructor().newInstance();
                DriverManager.registerDriver(new DriverShim(driver));
                EXTRA_DRIVER_LOADERS.add(loader);
                return true;
            } catch (Exception ignored) {
                // keep trying next candidate
            }
        }
        return false;
    }

    private static Set<Path> discoverBaseFolders() {
        Set<Path> bases = new LinkedHashSet<>();
        try {
            bases.add(Paths.get(System.getProperty("user.dir")).toAbsolutePath());
        } catch (Exception ignored) {
        }

        try {
            Path codeSource = Paths.get(DatabaseConnection.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path base = Files.isDirectory(codeSource) ? codeSource : codeSource.getParent();
            if (base != null) {
                bases.add(base.toAbsolutePath());
            }
        } catch (Exception ignored) {
        }
        return bases;
    }

    private static Set<Path> findOjdbcByPattern(Path folder) {
        Set<Path> matches = new LinkedHashSet<>();
        if (folder == null || !Files.isDirectory(folder)) {
            return matches;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "ojdbc*.jar")) {
            for (Path path : stream) {
                matches.add(path.toAbsolutePath());
            }
        } catch (Exception ignored) {
        }
        return matches;
    }

    private static Set<Path> findOjdbcInMavenLocalRepository() {
        Set<Path> matches = new LinkedHashSet<>();
        Path repo = resolveMavenLocalRepository();
        if (repo == null || !Files.isDirectory(repo)) {
            return matches;
        }

        matches.addAll(findOjdbcRecursively(repo.resolve(Paths.get("com", "oracle", "database", "jdbc", "ojdbc11")), 3));
        matches.addAll(findOjdbcRecursively(repo.resolve(Paths.get("com", "oracle", "database", "jdbc", "ojdbc8")), 3));
        matches.addAll(findOjdbcRecursively(repo.resolve(Paths.get("com", "oracle", "ojdbc14")), 3));
        return matches;
    }

    private static Path resolveMavenLocalRepository() {
        String envRepo = System.getenv("M2_REPO");
        if (envRepo != null && !envRepo.isBlank()) {
            try {
                return Paths.get(envRepo.trim()).toAbsolutePath();
            } catch (Exception ignored) {
            }
        }

        String sysRepo = System.getProperty("maven.repo.local");
        if (sysRepo != null && !sysRepo.isBlank()) {
            try {
                return Paths.get(sysRepo.trim()).toAbsolutePath();
            } catch (Exception ignored) {
            }
        }

        String userHome = System.getProperty("user.home");
        if (userHome == null || userHome.isBlank()) {
            return null;
        }
        try {
            return Paths.get(userHome, ".m2", "repository").toAbsolutePath();
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Set<Path> findOjdbcRecursively(Path folder, int maxDepth) {
        Set<Path> matches = new LinkedHashSet<>();
        if (folder == null || !Files.isDirectory(folder)) {
            return matches;
        }
        try (var stream = Files.walk(folder, maxDepth)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
                        return name.startsWith("ojdbc") && name.endsWith(".jar");
                    })
                    .forEach(path -> matches.add(path.toAbsolutePath()));
        } catch (Exception ignored) {
        }
        return matches;
    }

    private static final class DriverShim implements Driver {
        private final Driver delegate;

        private DriverShim(Driver delegate) {
            this.delegate = delegate;
        }

        @Override
        public java.sql.Connection connect(String url, Properties info) throws SQLException {
            return delegate.connect(url, info);
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
            return delegate.acceptsURL(url);
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
            return delegate.getPropertyInfo(url, info);
        }

        @Override
        public int getMajorVersion() {
            return delegate.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return delegate.getMinorVersion();
        }

        @Override
        public boolean jdbcCompliant() {
            return delegate.jdbcCompliant();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        }
    }
}
