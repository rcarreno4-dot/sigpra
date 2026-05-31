package co.udi.integrador.model;

public enum Role {
    ESTUDIANTE("Estudiante"),
    DOCENTE("Docente Asesor"),
    DIRECTOR("Director de Programa");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
