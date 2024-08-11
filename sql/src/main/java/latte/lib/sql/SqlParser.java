package latte.lib.sql;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import latte.lib.api.db.sql.annotation.Column;
import latte.lib.api.db.sql.annotation.Id;
import latte.lib.api.db.sql.annotation.Ignore;
import latte.lib.api.db.sql.annotation.Table;
import latte.lib.api.db.sql.annotation.Type;
import latte.lib.api.db.sql.ddl.CreateTable;
import latte.lib.api.db.sql.dml.DeleteTable;
import latte.lib.api.db.sql.dml.InsertTable;
import latte.lib.api.db.sql.dml.SelectTable;
import latte.lib.api.db.sql.dml.UpdateTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SqlParser<T> implements CreateTable, InsertTable<T>, SelectTable<T>, UpdateTable<T>,
    DeleteTable<T> {

    static Logger logger = LoggerFactory.getLogger(SqlParser.class);

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


    @Override
    public String select(T obj) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Table tableAnnotation = (Table) clazz.getAnnotation(Table.class);
        String tableName = tableAnnotation.name();
        String condition = conditionSql(obj, fields);
        return "select * from " + tableName + " where " + condition;
    }

    @Override
    public String update(T old, T now) {
        Class<?> clazz = old.getClass();
        Class<?> clazz1 = now.getClass();
        if (!clazz1.equals(clazz)) {
            throw new RuntimeException("[latte] update class diff");
        }
        Field[] fields = clazz.getDeclaredFields();
        Table tableAnnotation = (Table) clazz.getAnnotation(Table.class);
        String tableName = tableAnnotation.name();

        StringBuffer conditions = new StringBuffer();
        StringBuffer values = new StringBuffer();

        int conditionIndex = 0;
        int valueIndex = 0;
        for(int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true); // 允许访问私有字段
            try {
                Object oldValue = fields[i].get(old);
                Object nowValue = fields[i].get(now);
                if (oldValue == null && nowValue == null) continue;

                if (oldValue != null) {
                    if (conditionIndex != 0) {
                        conditions.append(" and ");
                    }
                    conditions.append(fields[i].getName() + "=" );
                    if (oldValue instanceof String) {
                        conditions.append("'" + oldValue + "'");
                    } else if (oldValue != null) {
                        conditions.append(oldValue.toString());
                    }
                    conditionIndex++;
                }

                if (nowValue != null && !nowValue.equals(oldValue)) {
                    if (valueIndex != 0) {
                        values.append(" , ");
                    }
                    values.append(fields[i].getName() + "=" );
                    if (nowValue instanceof String) {
                        values.append("'" + nowValue + "'");
                    } else if (nowValue != null) {
                        values.append(nowValue.toString());
                    }
                    valueIndex++;
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return "update " + tableName + " set " + values  + " where " + conditions;
    }

    public T from(Map<String, Object> map) throws InstantiationException, IllegalAccessException {
        T obj = clazz.newInstance();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String propertyName = entry.getKey();
            Object propertyValue = entry.getValue();
            try {
                Field field = clazz.getDeclaredField(propertyName);
                field.setAccessible(true); // 允许访问私有字段
                field.set(obj, propertyValue);
            } catch (NoSuchFieldException e) {
                logger.error("No such field: " + propertyName);
            }
        }
        return obj;
    }

    String conditionSql(T obj, Field[] fields) {
        StringBuffer conditions = new StringBuffer();
        int index = 0;
        for(int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true); // 允许访问私有字段
            try {
                Object value = fields[i].get(obj);
                if (value == null) continue;
                if (index != 0) {
                    conditions.append(" and ");
                }
                conditions.append(fields[i].getName() + "=" );
                if (value instanceof String) {
                    conditions.append("'" + value + "'");
                } else if (value != null) {
                    conditions.append(value.toString());
                }
                index++;
            } catch (Exception e) {
                logger.error("[latte] condition sql:",e);
            }
        }
        return conditions.toString();
    }
    @Override
    public String delete(T obj) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Table tableAnnotation = (Table) clazz.getAnnotation(Table.class);
        String tableName = tableAnnotation.name();
        return "delete from " + tableName + " where " + conditionSql(obj, fields);
    }
}
