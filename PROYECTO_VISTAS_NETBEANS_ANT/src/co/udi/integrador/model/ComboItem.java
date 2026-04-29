package co.udi.integrador.model;

public record ComboItem(long id, String label) {
    @Override
    public String toString() {
        return label;
    }
}
