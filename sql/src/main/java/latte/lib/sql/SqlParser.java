package latte.lib.sql;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import latte.lib.api.db.sql.annotation.Column;
import latte.lib.api.db.sql.annotation.Id;
import latte.lib.api.db.sql.annotation.Ignore;
import latte.lib.api.db.sql.annotation.Table;
import latte.lib.api.db.sql.annotation.Type;
import latte.lib.api.db.sql.ddl.CreateTable;
import latte.lib.api.db.sql.dml.InsertTable;


public class SqlParser<T> implements CreateTable, InsertTable<T> {
    Class<T> clazz;
    public SqlParser(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String createTable() {
        Table tableAnnotation = (Table) clazz.getAnnotation(Table.class);
        if (tableAnnotation == null) {
            throw new IllegalArgumentException("Class must be annotated with @Table");
        }

        String tableName = tableAnnotation.name();
        StringBuffer columnBuffer = new StringBuffer();

        Field[] fields = clazz.getDeclaredFields();
        String id = null;
        for(int i = 0; i < fields.length; i++) {
            if (fields[i].isAnnotationPresent(Ignore.class)) {
                continue;
            }
            if (i != 0) {
                columnBuffer.append(", ");
            }
            columnBuffer.append(generateColumnDefinition(fields[i]));
            if (fields[i].isAnnotationPresent(Id.class)) {
                Id idFiled = fields[i].getAnnotation(Id.class);
                id = String.format("PRIMARY KEY (%s)", idFiled.name().equals("") ?
                    idFiled.name():
                    fields[i].getName());
            }
        }
        if (id != null) {
            columnBuffer.append(",");
            columnBuffer.append(id);
        }


//        columns += Arrays.stream(clazz.getDeclaredFields())
//            .filter(field -> !field.isAnnotationPresent(Ignore.class))
//            .map(SqlParser::generateColumnDefinition)
//            .collect(Collectors.joining(", "));



        return "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columnBuffer.toString() + ");";
    }


    private static String generateColumnDefinition(Field field) {
        StringBuilder definition = new StringBuilder("");
        String columnName;
        Type columnType;
        int columnLength;
        boolean isNullable;
        if (field.isAnnotationPresent(Column.class)) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            columnName = columnAnnotation.name();
            if ("".equals(columnName) || columnName == null) {
                columnName = field.getName();
            }
            columnType = columnAnnotation.type();
            columnLength = columnAnnotation.length();
            isNullable = columnAnnotation.nullable();
        } else {
            columnName = field.getName();
            columnType = Type.get(field.getType());
            if (columnType == null) {
                throw new RuntimeException(String.format("%s columnType is null", columnName));
            }
            columnLength = 256;
            isNullable = false;
        }

        definition.append(columnName).append(" ").append(columnType);
        if (columnType.equals(Type.VARCHAR)) {
            definition.append("(").append(columnLength).append(")");
        }
        if (!isNullable) {
            definition.append(" NOT NULL");
        }
//        if (field.isAnnotationPresent(Id.class)) {
//            definition.append(" AUTO_INCREMENT");
//        }

        return definition.toString();
    }


    @Override
    public String insert(T obj) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Table tableAnnotation = (Table) clazz.getAnnotation(Table.class);
        String tableName = tableAnnotation.name();

        StringBuffer columns = new StringBuffer();
        StringBuffer values = new StringBuffer();
        int index = 0;
        for(int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true); // 允许访问私有字段
            try {
                Object value = fields[i].get(obj);
                if (value == null) continue;
                if (index != 0) {
                    columns.append(",");
                    values.append(",");
                }
                columns.append(fields[i].getName());
                if (value instanceof String) {
                    values.append("'" + value + "'");
                } else if (value != null) {
                    values.append(value.toString());
                }
                index++;
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }


        return "INSERT INTO " + tableName + " (" + columns.toString() + ") VALUES (" + values.toString() + ");";
    }
}
