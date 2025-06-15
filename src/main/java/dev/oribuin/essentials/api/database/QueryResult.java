package dev.oribuin.essentials.api.database;

import dev.oribuin.essentials.EssentialsPlugin;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryResult {

    private final List<Row> results;

    /**
     * Hold all the relevant query data into the object
     *
     * @param set The set
     */
    public QueryResult(@Nullable ResultSet set) {
        this.results = new ArrayList<>();

        if (set == null) return;

        try {
            ResultSetMetaData data = set.getMetaData();
            int columnCount = data.getColumnCount();

            while (set.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(data.getColumnName(i), set.getObject(i));
                }

                results.add(new Row(row));
            }
        } catch (SQLException ex) {
            EssentialsPlugin.get().getLogger().severe("An error occurred establishing query result: " + ex.getMessage());
        }
    }

    public record Row(Map<String, Object> content) {

        /**
         * Parse the value from the object to dissect what type it is
         *
         * @param dataType The row data type
         * @param name     The name of the column
         * @param <T>      The mysql data type this is
         */
        @SuppressWarnings("unchecked")
        public <T> T get(Class<T> dataType, String name) {
            Object object = content.get(name);
            if (object == null) return null;

            return object.getClass().equals(dataType) ? (T) object : null;
        }

        /**
         * Parse the value from the object and assume it will be a string
         *
         * @param name The name of the column
         *
         * @return The row value
         */
        public String getString(String name) {
            return content.get(name) instanceof String result ? result : null;
        }

        /**
         * Parse the value from the object and assume it will be an integer
         *
         * @param name The name of the column
         *
         * @return The row value
         */
        public int getInt(String name) {
            Object object = content.get(name);
            return object instanceof Integer result ? result : 0;
        }

        /**
         * Parse the value from the object and assume it will be a double
         *
         * @param name The name of the column
         *
         * @return The row value
         */
        public double getDouble(String name) {
            Object object = content.get(name);
            return object instanceof Double result ? result : 0;
        }

        /**
         * Parse the value from the object and assume it will be a BigDecimal
         *
         * @param name The name of the column
         *
         * @return The row value
         */
        public BigDecimal getBigDecimal(String name) {
            Object object = content.get(name);
            if (object instanceof Number number) {
                return BigDecimal.valueOf(number.doubleValue());
            }
            return BigDecimal.ZERO;
        }

        /**
         * Parse the value from the object and assume it will be a boolean
         *
         * @param name The name of the column
         *
         * @return The row value
         */
        public boolean getBoolean(String name) {
            Object object = content.get(name);
            return object instanceof Boolean result && result;
        }

        /**
         * Parse the value from the object and assume it will be a long
         *
         * @param name The name of the column
         *
         * @return The row value
         */
        public long getLong(String name) {
            Object object = content.get(name);
            return object instanceof Long result ? result : 0;
        }

        @Override
        public String toString() {
            return "Row{" +
                   "content=" + content +
                   '}';
        }
    }

    public List<Row> results() {
        return results;
    }
    
    
    public @Nullable Row first() {
        if (this.results.isEmpty()) return null;
        
        return this.results.get(0);
    }

    @Override
    public String toString() {
        return "QueryResult{" +
               "results=" + results +
               '}';
    }
}