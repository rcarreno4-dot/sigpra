package co.udi.integrador.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

public final class DatabaseConfig {
    private static final String DEFAULT_URL = "jdbc:oracle:thin:@localhost:1521/XE";
    private static final String DEFAULT_USER = "PRACTICAS_APP";
    private static final String DEFAULT_PASSWORD = "Practicas2026";

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DatabaseConfig load() {
        Properties p = new Properties();
        loadClasspathProperties(p);
        loadExternalProperties(p);

        String url = pick("DB_URL", "db.url", p, DEFAULT_URL);
        String user = pick("DB_USER", "db.user", p, DEFAULT_USER);
        String password = pick("DB_PASSWORD", "db.password", p, DEFAULT_PASSWORD);

        return new DatabaseConfig(url, user, password);
    }

    public String url() {
        return url;
    }

    public String user() {
        return user;
    }

    public String password() {
        return password;
    }

    private static String pick(String envKey, String propertyKey, Properties p, String fallback) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue.trim();
        }

        String sysPropValue = System.getProperty(propertyKey);
        if (sysPropValue != null && !sysPropValue.isBlank()) {
            return sysPropValue.trim();
        }

        String fileValue = p.getProperty(propertyKey);
        if (fileValue != null && !fileValue.isBlank()) {
            return fileValue.trim();
        }
        return fallback;
    }

    private static void loadClasspathProperties(Properties p) {
        try (InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (IOException ignored) {
            // Uses defaults when classpath configuration is not present.
        }
    }

    private static void loadExternalProperties(Properties p) {
        for (Path candidate : discoverExternalCandidates()) {
            if (!Files.isRegularFile(candidate)) {
                continue;
            }
            try (InputStream in = Files.newInputStream(candidate)) {
                p.load(in);
                return;
            } catch (IOException ignored) {
                // Try next candidate.
            }
        }
    }

    private static Set<Path> discoverExternalCandidates() {
        Set<Path> candidates = new LinkedHashSet<>();

        try {
            Path userDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
            candidates.add(userDir.resolve("db.properties"));
            candidates.add(userDir.resolve("app").resolve("db.properties"));
        } catch (Exception ignored) {
        }

        try {
            Path codeSource = Paths.get(DatabaseConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path base = Files.isDirectory(codeSource) ? codeSource : codeSource.getParent();
            if (base != null) {
                Path abs = base.toAbsolutePath();
                candidates.add(abs.resolve("db.properties"));
                candidates.add(abs.resolve("app").resolve("db.properties"));
            }
        } catch (Exception ignored) {
        }

        return candidates;
    }
}
