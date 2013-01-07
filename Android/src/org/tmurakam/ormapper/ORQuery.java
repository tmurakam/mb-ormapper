// -*-  Mode:java; c-basic-offset:4; tab-width:8; indent-tabs-mode:nil -*-
/*
  O/R Mapper library for Android

  Copyright (c) 2010-2013, Takuya Murakami. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1. Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer. 

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
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
    private int mOffset = 0;

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
     * @return this
     * @note You can set only 1 where condition.
     */
    public ORQuery<T> where(String cond, String... params) {
        mWhere = cond;
        mWhereParams = params;
        return this;
    }
    
    /**
     * Set 'WHERE' conditions
     * @param column column name
     * @param param parameter (string)
     * @return this
     * @note You can set only 1 where condition.
     */
    public ORQuery<T> where_eq(String column, String param) {
        mWhere = column + " = ?";
        mWhereParams = new String[] { param };
        return this;
    }

    /**
     * Set 'WHERE' conditions
     * @param column column name
     * @param param parameter (integer)
     * @return this
     * @note You can set only 1 where condition.
     */
    public ORQuery<T> where_eq(String column, int param) {
        return where_eq(column, Integer.toString(param));
    }
    
    /**
     * set 'ORDER BY' parameter
     * @param order ORDER BY parameter string
     * @return this
     */
    public ORQuery<T> order(String order) {
        mOrder = order;
        return this;
    }
    
    /**
     * set 'LIMIT' parameter 
     * @param limit parameter
     * @return this
     */
    public ORQuery<T> limit(int limit) {
        mLimit = limit;
        return this;
    }
    
    /**
     * set 'OFFSET' parameter
     * @param offset offset parameter
     * @return this
     */
    public ORQuery<T> offset(int offset) {
        mOffset = offset;
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
        if (mOffset > 0) {
            sql.append(" OFFSET ");
            sql.append(mOffset);
        }
        return sql.toString();
    }
}
