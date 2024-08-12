package latte.lib.sql;


import java.lang.reflect.Field;
import java.util.Map;
import latte.lib.api.db.sql.annotation.Column;
import latte.lib.api.db.sql.annotation.Id;
import latte.lib.api.db.sql.annotation.Ignore;
import latte.lib.api.db.sql.annotation.Table;
import latte.lib.api.db.sql.annotation.Type;
import latte.lib.api.db.sql.ddl.CreateTable;
import latte.lib.api.db.sql.dml.Delete;
import latte.lib.api.db.sql.dml.Insert;
import latte.lib.api.db.sql.dml.InsertOrUpdate;
import latte.lib.api.db.sql.dml.Select;
import latte.lib.api.db.sql.dml.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SqlParser<T> implements CreateTable, Insert<T>, Select<T>, Update<T>,
    Delete<T>, InsertOrUpdate<T> {

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
    public String insert(T obj) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Table tableAnnotation = (Table) clazz.getAnnotation(Table.class);
        String tableName = tableAnnotation.name();
        FieldsForEach forEach = new FieldsForEach(obj);
        forEach.addFlag(FieldsForEach.KEY_FLAG)
            .addFlag(FieldsForEach.VALUE_FALG)
            .forEach();
        return String.format(
            "insert into %s (%s) values (%s)",
            tableName,
            forEach.getValueByFlag(FieldsForEach.KEY_FLAG),
            forEach.getValueByFlag(FieldsForEach.VALUE_FALG)
        );
    }

    class FieldsForEach<T> {
        T obj;
        Field[] fields;
        static public final int FLAGCOUNT = 4;
        int[] indexs = new int[FLAGCOUNT];
        StringBuffer[] stringBuffers = new StringBuffer[FLAGCOUNT];
        int flag;
        static final int KEY_FLAG_INDEX = 0;
        static public final int KEY_FLAG = 1 << KEY_FLAG_INDEX;

        static final int VALUE_FALG_INDEX = 1 ;
        static public final int VALUE_FALG = 1 << VALUE_FALG_INDEX;

        static final int KEY_VALUE_FALG_INDEX = 2;
        static public final int KEY_VALUE_FALG = 1 << KEY_VALUE_FALG_INDEX;

        static final int NOTID_KEY_VALUE_FLAG_INDEX = 3;
        static public final int NOTID_KEY_VALUE_FLAG = 1 << NOTID_KEY_VALUE_FLAG_INDEX;

        public FieldsForEach(T obj) {
            this.obj = obj;
            this.fields = clazz.getDeclaredFields();
            for(int i = 0; i < FLAGCOUNT; i++) {
                this.stringBuffers[i] = new StringBuffer();
            }
        }

        public FieldsForEach addFlag(int flag) {
            this.flag |= flag;
            return this;
        }

        String valueToString(Object value) {
            if (value instanceof String) {
                return "'" + value + "'";
            } else if (value != null) {
                return value.toString();
            }
            return null;
        }
        void fieldHandle(String key, String value, boolean isId) {
            if ((this.flag & KEY_FLAG) != 0) {
                if (indexs[KEY_FLAG_INDEX] != 0) {
                    stringBuffers[KEY_FLAG_INDEX].append(",");
                }
                stringBuffers[KEY_FLAG_INDEX].append(key);
                indexs[KEY_FLAG_INDEX]++;
            }
            if ((this.flag & VALUE_FALG) != 0) {
                if (indexs[VALUE_FALG_INDEX] != 0) {
                    stringBuffers[VALUE_FALG_INDEX].append(",");
                }
                stringBuffers[VALUE_FALG_INDEX].append(value);
                indexs[VALUE_FALG_INDEX]++;
            }
            if((this.flag & KEY_VALUE_FALG) != 0) {
                if (indexs[KEY_VALUE_FALG_INDEX] != 0) {
                    stringBuffers[KEY_VALUE_FALG_INDEX].append(" and ");
                }
                stringBuffers[KEY_VALUE_FALG_INDEX].append(key + "=" + value);
                indexs[KEY_VALUE_FALG_INDEX]++;
            }
            if ((this.flag & NOTID_KEY_VALUE_FLAG) != 0 && !isId) {
                if (indexs[NOTID_KEY_VALUE_FLAG_INDEX] != 0) {
                    stringBuffers[NOTID_KEY_VALUE_FLAG_INDEX].append(",");
                }
                stringBuffers[NOTID_KEY_VALUE_FLAG_INDEX].append(key + "=" + value);
                indexs[NOTID_KEY_VALUE_FLAG_INDEX]++;
            }
        }
        public FieldsForEach forEach() throws IllegalAccessException {
            for(int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true); // 允许访问私有字段
                Object value = fields[i].get(obj);
                if (value == null) continue;

                fieldHandle(
                    fields[i].getName(),
                    valueToString(value),
                    fields[i].isAnnotationPresent(Id.class)
                );
            }
            return this;
        }

        String getValueByFlag(int flag) {
            int shiftCount1 = (int) (Math.log(flag) / Math.log(2));;
            return stringBuffers[shiftCount1].toString();
        }
    }

    @Override
    public String insertOrUpdate(T obj) throws Exception {
        Class<?> clazz = obj.getClass();
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        String tableName = tableAnnotation.name();
        FieldsForEach forEach = new FieldsForEach(obj);
        forEach.addFlag(FieldsForEach.KEY_FLAG)
            .addFlag(FieldsForEach.VALUE_FALG)
            .addFlag(FieldsForEach.NOTID_KEY_VALUE_FLAG)
            .forEach();

        return String.format(
            "insert into %s ( %s ) values ( %s ) ON DUPLICATE KEY UPDATE %s",
            tableName,
            forEach.getValueByFlag(FieldsForEach.KEY_FLAG),
            forEach.getValueByFlag(FieldsForEach.VALUE_FALG),
            forEach.getValueByFlag(FieldsForEach.NOTID_KEY_VALUE_FLAG)
        );
    }


    /**
     * SELECT column1, column2, ...
     * FROM table_name
     * ORDER BY column1, column2, ... ASC|DESC;
     * column1, column2, ...：要排序的字段名称，可以为多个字段。
     * ASC：表示按升序排序。
     * DESC：表示按降序排序。
     * @param obj
     * @return
     */
    @Override
    public String select(T obj) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Table tableAnnotation = (Table) clazz.getAnnotation(Table.class);
        String tableName = tableAnnotation.name();
        FieldsForEach forEach = new FieldsForEach(obj);
        forEach.addFlag(FieldsForEach.KEY_VALUE_FALG)
            .forEach();
        return String.format(
            "select * from %s where %s" ,
            tableName ,
            forEach.getValueByFlag(FieldsForEach.KEY_VALUE_FALG)
        );
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
    public String delete(T obj) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Table tableAnnotation = (Table) clazz.getAnnotation(Table.class);
        String tableName = tableAnnotation.name();
        FieldsForEach forEach = new FieldsForEach<>(obj);
        forEach.addFlag(FieldsForEach.KEY_VALUE_FALG)
            .forEach();
        return String.format(
            "delete from %s  where %s" ,
            tableName,
            forEach.getValueByFlag(FieldsForEach.KEY_VALUE_FALG)
       );
    }


}
