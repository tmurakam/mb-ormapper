package org.tmurakam.ormapper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ORQuery<T extends ORRecord> {
    private final static String TAG = "ORQuery";
    
    private Constructor<T> mConstructor;
    private String mTableName;
    private String mWhere;
    private String[] mWhereParams;
    private String mOrder;
    private int mLimit = 0;

    public ORQuery(Class<T> clazz, String tableName) {
        try {
            mConstructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "no constructor");
        }
        mTableName = tableName;
    }
    
    public ORQuery<T> where(String cond, String... params) {
        mWhere = cond;
        mWhereParams = params;
        return this;
    }
    
    public ORQuery<T> order(String order) {
        mOrder = order;
        return this;
    }
    
    public ORQuery<T> limit(int limit) {
        mLimit = limit;
        return this;
    }
    
    public List<T> all() {
        Cursor cursor = query();
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
    
    public T first() {
        mLimit = 1;
        
        Cursor cursor = query();
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
    
    private Cursor query() {
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
        
        SQLiteDatabase db = ORDatabase.getDB();
        Cursor cursor = db.rawQuery(sql.toString(), mWhereParams);
        //cursor.moveToFirst();
        return cursor;
    }
}
