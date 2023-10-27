package latte.lib.db;

import latte.lib.json.JacksonJson;
import latte.lib.json.Json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SqlDao<T> implements Dao<T> {
    SqlImpl impl;
    
    public SqlDao() {
        
    }
    public SqlDao(SqlImpl impl) {
        this.impl = impl;
    }

    public void setImpl(SqlImpl impl) {
        this.impl = impl;
    }

    Json json = new JacksonJson();

    protected Map<String, Object> parseToMap(T t) throws Exception {
        String str = json.encode(t);
        Map<String,Object> result = json.decode(str, Map.class);
        return result.entrySet().stream().filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    public static String generateQuestionMarks(int count, String str, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
            if (i < count - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    protected abstract String getTableName();
    protected abstract List<String> getKey();
    @Override
    public void insert(T t) throws Exception {

        Map<String, Object> map = parseToMap(t);
        String gen = generateQuestionMarks(map.size(), "?", ",");

        Object[] params = new Object[map.size()];
        List<String> keys = new ArrayList<>();

        int i = 0;
        for(Map.Entry<String,Object> entry: map.entrySet()) {
            keys.add(entry.getKey());
            params[i] = entry.getValue();
            i++;
        }

        String sql = String.format("INSERT INTO %s (%s) values (%s)", getTableName(), String.join(",", keys), gen);
        impl.execSQL(sql, params);
    }

    @Override
    public void update(T t) throws Exception {
        Map<String, Object> map = parseToMap(t);
        List<String> key = getKey();

        Object[] params = new Object[map.size()];
        params[0] = getTableName();
        int param_index = 0;
        int key_index = (map.size() - key.size());
        List<String> setKeys = new ArrayList<>();
        List<String> whereKeys = new ArrayList<>();
        for(Map.Entry<String,Object> entry: map.entrySet()) {
            if (key.contains(entry.getKey())) {
                whereKeys.add(entry.getKey() + " = ?");
                params[key_index++] = entry.getValue();
            } else {
                setKeys.add(entry.getKey() + " = ?");
                params[param_index++] = entry.getValue();
            }
        }
        String sql = String.format("UPDATE %s SET %s WHERE %s",
                getTableName(),
                String.join(",", setKeys),
                String.join("and", whereKeys));
        System.out.println("sql:" + sql);
        impl.execSQL(sql, params);
    }
    
    public T find(T t) throws Exception {
        return find(parseToMap(t), (Class<T>)t.getClass());
    }

    public T find(Map<String, Object> map, Class<T> glass) throws  Exception {
        Object[] params = new Object[map.size()];
        List<String> keys = new ArrayList<>();
        int param_index = 0;
        for(Map.Entry<String,Object> entry: map.entrySet()) {
            keys.add(entry.getKey() + "= ?");
            params[param_index++] = entry.getValue();
        }
        String sql = String.format("select * from %s  WHERE %s",
                getTableName(),
                String.join(" and ", keys));
        System.out.println("sql:" + map);
        List<T> ts =  impl.execSQLToObject(sql, params, glass);
        if (ts.size() == 0) return null;
        return ts.get(0);
    }

    @Override
    public void del(T t) throws Exception {
        Map<String, Object> map = parseToMap(t);

        Object[] params = new Object[map.size()];
        List<String> keys = new ArrayList<>();
        int param_index = 0;
        for(Map.Entry<String,Object> entry: map.entrySet()) {
            keys.add(entry.getKey() + " = ?");
            params[param_index++] = entry.getValue();
        }
        String sql = String.format("delete from %s WHERE %s",
                getTableName(),
                String.join(" and ", keys));
        impl.execSQL(sql, params);
    }
}
