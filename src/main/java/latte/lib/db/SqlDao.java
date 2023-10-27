package latte.lib.db;

import latte.lib.json.JacksonJson;
import latte.lib.json.Json;

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
        String sql = String.format("INSERT INTO %s (%s) values (%s)", getTableName(), gen, gen);
        Object[] params = new Object[2* map.size()];
        int i = 0;
        for(Map.Entry<String,Object> entry: map.entrySet()) {
            params[i] = entry.getKey();
            params[i + map.size()] = entry.getValue();
            i++;
        }
        System.out.println("sql:" + sql + ",params:" +  params[0] + "," +params[1] + "," + params[2] + "," + params[3]);
        impl.execSQL(sql, params);
    }

    @Override
    public void update(T t) throws Exception {
        Map<String, Object> map = parseToMap(t);
        List<String> key = getKey();
        String sql = String.format("UPDATE %s SET %s WHERE %s",
                getTableName(),
                generateQuestionMarks(map.size() - key.size(), "? = ?", ","),
                generateQuestionMarks(key.size(), "? = ?", "and"));
        Object[] params = new Object[2* map.size()];
        params[0] = getTableName();
        int param_index = 0;
        int key_index = (map.size() - key.size()) * 2;
        for(Map.Entry<String,Object> entry: map.entrySet()) {
            if (key.contains(entry.getKey())) {
                params[key_index++] = entry.getKey();
                params[key_index++] = entry.getValue();
            } else {
                params[param_index++] = entry.getKey();
                params[param_index++] = entry.getValue();
            }
        }
        impl.execSQL(sql, params);
    }
    
    public T find(T t) throws Exception {
        Map<String, Object> map = parseToMap(t);
        String sql = String.format("select * from %s  WHERE %s",
                getTableName(), 
                generateQuestionMarks(map.size(), "? = ?", "and"));
        Object[] params = new Object[2* map.size()];
        int param_index = 0;
        for(Map.Entry<String,Object> entry: map.entrySet()) {
            params[param_index++] = entry.getKey();
            params[param_index++] = entry.getValue();
        }
        return impl.execSQLToObject(sql, params, (Class<T>)t.getClass());
    }

    @Override
    public void del(T t) throws Exception {
        Map<String, Object> map = parseToMap(t);
        String sql = String.format("delete from %s WHERE %s",
                getTableName(),
                generateQuestionMarks(map.size(), "? = ?", "and"));
        Object[] params = new Object[2* map.size()];
        int param_index = 0;
        for(Map.Entry<String,Object> entry: map.entrySet()) {
            params[param_index++] = entry.getKey();
            params[param_index++] = entry.getValue();
        }
        impl.execSQL(sql, params);
    }
}
