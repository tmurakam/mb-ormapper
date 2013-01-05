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

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * O/R mapper record base class
 */
public abstract class ORRecord {
    /** primary key */
    public int pid;

    /** inserted flag */
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
     * @param pkeyName primary key name
     * @param array Table schema ( column, type, column, type, ... ) 
     * @return true: table newly created, false: table exists
     */
    static protected boolean migrate(String tableName, String pkeyName, String[] array) {
        SQLiteDatabase db = ORDatabase.getDB();
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
            sql = "CREATE TABLE " + tableName + " ( " + pkeyName + " INTEGER PRIMARY KEY );";
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
     * Load row columns from cursor
     * @param cursor
     */
    public abstract void _loadRow(Cursor cursor);
    
    /**
     * Quote SQL string
     */
    protected String quoteSqlString(String s) {
        return "'" + s.replaceAll("'", "''") + "'";
    }
}
