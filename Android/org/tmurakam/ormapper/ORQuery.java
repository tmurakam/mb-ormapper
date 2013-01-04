package org.tmurakam.ormapper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/** 
 * O/R mapper query object
 */
public class ORQuery<T extends ORRecord> {
    private final static String TAG = "ORQuery";
    
    private Constructor<T> mConstructor;
    private String mTableName;
    private String mWhere;
    private String[] mWhereParams;
    private String mOrder;
    private int mLimit = 0;

    /**
     * Constructor
     * @param clazz  Class object
     * @param tableName Table name
     */
    public ORQuery(Class<T> clazz, String tableName) {
        try {
            mConstructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "no constructor");
        }
        mTableName = tableName;
    }
    
    /**
     * Set 'WHERE' conditions
     * @param cond conditions
     * @param params parameters for each placeholders
     * @return
     */
    public ORQuery<T> where(String cond, String... params) {
        mWhere = cond;
        mWhereParams = params;
        return this;
    }
    
    /**
     * set 'ORDER BY' parameter
     * @param order ORDER BY parameter string
     * @return
     */
    public ORQuery<T> order(String order) {
        mOrder = order;
        return this;
    }
    
    /**
     * set 'LIMIT' parameter 
     * @param limit parameter
     * @return
     */
    public ORQuery<T> limit(int limit) {
        mLimit = limit;
        return this;
    }
    
    /**
     * Execute query and returns all elements
     * @return elements
     */
    public List<T> all() {
        Cursor cursor = execQuery();
        cursor.moveToFirst();
        
        ArrayList<T> array = new ArrayList<T>();
        
        while (!cursor.isAfterLast()) {
            T entity;
            try {
                entity = mConstructor.newInstance();
                entity._loadRow(cursor);
                array.add(entity);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            cursor.moveToNext();
        }
        cursor.close();
        return array;
    }
    
    /**
     * Execute query and get first element
     * @return first element
     */
    public T first() {
        mLimit = 1;
        
        Cursor cursor = execQuery();
        cursor.moveToFirst();

        T entity = null;
        if (!cursor.isAfterLast()) {
            try {
                entity = mConstructor.newInstance();
                entity._loadRow(cursor);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "constructor failed.");
            }
        }
        cursor.close();
        return entity;
    }
    
    /**
     * Execute query
     * @return Cursor
     */
    private Cursor execQuery() {
        String sql = getSql();

        SQLiteDatabase db = ORDatabase.getDB();
        Cursor cursor = db.rawQuery(sql.toString(), mWhereParams);

        return cursor;
    }

    /**
     * get SQL statement
     * @return SQL statement
     * @note placeholder('?') are not be expanded
     */
    public String getSql() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ");
        sql.append(mTableName);
        
        // where conditions
        if (mWhere != null) {
            sql.append(" WHERE ");
            sql.append(mWhere);
        }
        if (mOrder != null) {
            sql.append(" ORDER BY ");
            sql.append(mOrder);
        }
        if (mLimit > 0) {
            sql.append(" LIMIT ");
            sql.append(mLimit);
        }
        return sql.toString();
    }
}
