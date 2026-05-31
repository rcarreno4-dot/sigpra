package co.udi.integrador.data;

import java.sql.Connection;

final class DbSchemaHelper {
    private DbSchemaHelper() {
    }

    static String resolveDirectorTable(Connection cn) {
        return "director";
    }
}
