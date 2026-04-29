package co.udi.integrador.model;

public record AuthenticatedUser(long id, String nombreCompleto, String correo, Role role) {
}
