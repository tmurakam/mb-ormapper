// -*-  Mode:java; c-basic-offset:4; tab-width:8; indent-tabs-mode:nil -*-

package org.tmurakam.ormapper;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class ORRecord {
    public int pid; // primary key

    protected boolean isInserted;

    /**
     * Constructor
     */
    public ORRecord() {
        // do nothing
    }

    /**
     * Migrate database table
     * @param tableName Table name
     * @param array Table schema ( column, type, column, type, ... ) 
     * @return true: table newly created, false: table exists
     */
    static protected boolean migrate(String tableName, String[] array) {
        SQLiteDatabase db = Database.getDB();
        boolean ret;
        String tablesql;

        // check if table exists.
        String sql = "SELECT sql FROM sqlite_master WHERE type='table' AND name=?";
        String[] params = {
            tableName
        };
        Cursor cursor = db.rawQuery(sql, params);
        cursor.moveToFirst();

        // create table
        if (cursor.getCount() == 0) {
            sql = "CREATE TABLE " + tableName + " ( key INTEGER PRIMARY KEY );";
            db.execSQL(sql);
            tablesql = sql;
            ret = true;
        } else {
            cursor.moveToFirst();
            tablesql = cursor.getString(0);
            ret = false;
        }
        cursor.close();

        // add columns
        int count = array.length / 2;

        for (int i = 0; i < count; i++) {
            String column = array[i * 2];
            String type = array[i * 2 + 1];

            String regex = String.format(", *%s ", column);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(tablesql);
            if (!matcher.find()) {
                sql = "ALTER TABLE " + tableName + " ADD COLUMN " + column + " " + type + ";";
                db.execSQL(sql);
            }
        }
        return ret;
    }

    /**
     * Save record
     */
    final public void save() {
        if (isInserted) {
            update();
        } else {
            insert();
        }
    }

    /**
     * Insert record
     */
    public void insert() {
        isInserted = true;
        return;
    }

    /**
     * Update record
     */
    public abstract void update();

    /**    
     * Delete record
     */
    public abstract void delete();

    public abstract void delete_cond(String cond);

    final public void delete_all() {
        delete_cond(null);
    }
}
