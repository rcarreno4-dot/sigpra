package co.udi.integrador.session;

public enum RunMode {
    ORACLE("Oracle (BD real)"),
    DEMO("Demo (sin BD)");

    private final String label;

    RunMode(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
