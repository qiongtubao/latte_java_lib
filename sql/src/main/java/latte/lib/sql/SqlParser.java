package latte.lib.sql;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;
import latte.lib.api.db.sql.SqlDao;
import latte.lib.api.db.sql.annotation.Column;
import latte.lib.api.db.sql.annotation.Id;
import latte.lib.api.db.sql.annotation.Table;


public class SqlParser {
    Class clazz;
    public SqlParser(Class clazz) {
        this.clazz = clazz;
    }
    String createTable() {
        Table tableAnnotation = (Table) clazz.getAnnotation(Table.class);
        if (tableAnnotation == null) {
            throw new IllegalArgumentException("Class must be annotated with @Table");
        }

        String tableName = tableAnnotation.name();

        String columns = Arrays.stream(clazz.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(Column.class))
            .map(SqlParser::generateColumnDefinition)
            .collect(Collectors.joining(", "));

        return "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ");";
    }

    private static String generateColumnDefinition(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        String columnName = columnAnnotation.name();
        String columnType = columnAnnotation.type();
        int columnLength = columnAnnotation.length();
        boolean isNullable = columnAnnotation.nullable();

        StringBuilder definition = new StringBuilder(columnName).append(" ").append(columnType);
        if (columnType.equalsIgnoreCase("VARCHAR")) {
            definition.append("(").append(columnLength).append(")");
        }
        if (!isNullable) {
            definition.append(" NOT NULL");
        }
        if (field.isAnnotationPresent(Id.class)) {
            definition.append(" PRIMARY KEY AUTO_INCREMENT");
        }

        return definition.toString();
    }
}
