package co.udi.integrador.session;

import co.udi.integrador.model.AuthenticatedUser;

public final class AppSession {
    private static AuthenticatedUser currentUser;
    private static RunMode runMode = RunMode.ORACLE;

    private AppSession() {
    }

    public static void setCurrentUser(AuthenticatedUser user) {
        currentUser = user;
    }

    public static AuthenticatedUser getCurrentUser() {
        return currentUser;
    }

    public static RunMode getRunMode() {
        return runMode;
    }

    public static void setRunMode(RunMode mode) {
        runMode = mode == null ? RunMode.ORACLE : mode;
    }

    public static boolean isDemoMode() {
        return runMode == RunMode.DEMO;
    }

    public static void clear() {
        currentUser = null;
        runMode = RunMode.ORACLE;
    }
}
