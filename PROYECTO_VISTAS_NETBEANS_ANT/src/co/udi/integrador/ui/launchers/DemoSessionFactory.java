package co.udi.integrador.ui.launchers;

import co.udi.integrador.model.AuthenticatedUser;
import co.udi.integrador.model.Role;
import co.udi.integrador.session.AppSession;
import co.udi.integrador.session.RunMode;

public final class DemoSessionFactory {
    private DemoSessionFactory() {}

    public static void asStudent() {
        AppSession.setRunMode(RunMode.DEMO);
        AppSession.setCurrentUser(new AuthenticatedUser(1001L, "Estudiante Demo", "estudiante.demo@udi.edu.co", Role.ESTUDIANTE));
    }

    public static void asTeacher() {
        AppSession.setRunMode(RunMode.DEMO);
        AppSession.setCurrentUser(new AuthenticatedUser(2001L, "Docente Demo", "docente.demo@udi.edu.co", Role.DOCENTE));
    }

    public static void asDirector() {
        AppSession.setRunMode(RunMode.DEMO);
        AppSession.setCurrentUser(new AuthenticatedUser(3001L, "Directora Demo", "directora.demo@udi.edu.co", Role.DIRECTOR));
    }

    public static void clear() {
        AppSession.clear();
    }
}
