// -*-  Mode:java; c-basic-offset:4; tab-width:8; indent-tabs-mode:nil -*-
/*
  mb-ormapper : O/R Mapper library for iOS/Android
  https://github.com/tmurakam/mb-ormapper

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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.*;

/**
 * ORMデータベースヘルパ。
 * <p>
 * SQLiteOpenHelper を継承している。
 * {@link #initialize} で初期化を行い、{@link #getDB}で
 * SQLiteDatabase のシングルトンインスタンスを取得する。
 */
public class ORDatabase extends SQLiteOpenHelper {
    private static final String TAG = ORDatabase.class.getSimpleName();

    protected static ORDatabaseFactory sFactory = new ORDatabaseFactory();

    /** アプリケーションコンテキスト */
    protected static Context sApplicationContext;

    protected static SimpleDateFormat sDateFormat;

    /** SQLiteDatabase インスタンス */
    protected SQLiteDatabase mDb;

    static {
        sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        sDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * 初期化。getDB() 前に呼び出されている必要がある。
     * @param context           コンテキスト
     * @param databaseName      データベース名 (null時は無指定)
     * @param factory           ORDatabaseファクトリ (null時はデフォルト)
     */
    public static synchronized void initialize(Context context, String databaseName, ORDatabaseFactory factory) {
        if (factory != null) {
            sFactory = factory;
        }
        sApplicationContext = context.getApplicationContext();
        sFactory.initialize(context, databaseName);
    }

    /**
     * 初期化。getDB() 前に呼び出されている必要がある。
     * <p>
     * @param context コンテキスト
     * @param databaseName データベース名 (null時は無指定)
     */
    public static synchronized void initialize(Context context, String databaseName) {
        initialize(context, databaseName, null);
    }

    /**
     * 再初期化。テスト用の API。
     * 未初期化あるいはコンテキストが変更された場合のみ、初期化を実行する。
     * @param context コンテキスト
     */
    public static synchronized void _reinitialize(Context context, String databaseName) {
        Context c = context.getApplicationContext();
        if (sApplicationContext == c) return; // do nothing

        // re-init
        sFactory.close();
        initialize(context, databaseName, null);
    }

    /**
     * データベースをオープンして SQLiteDatabase ハンドルを返す。
     * 
     * すでにインスタンスがある場合はこれを返す。
     */
    public static synchronized SQLiteDatabase getDB() {
        ORDatabase helper = sFactory.getInstance();
        return helper._getDB();
    }

    /**
     * 開いているデータベースを閉じる。
     * 
     * シングルトンインスタンスは解放される。
     */
    public static synchronized void closeDB() {
        sFactory.close();
    }

    public static void sync() {
        closeDB();
    }

    // --- Internal methods

    /**
     * コンストラクタ
     * @param context
     * @param databaseName
     */
    protected ORDatabase(Context context, String databaseName, int schemaVersion) {
        super(context.getApplicationContext(), databaseName, null, schemaVersion);
    }

    /**
     * SQLiteDatabase インスタンスを取得する。
     * @return
     */
    protected SQLiteDatabase _getDB() {
        if (mDb == null) {
            mDb = getWritableDatabase();
        }
        return mDb;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (mDb != null) {
            mDb.close();
        }
        super.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * utilities
     */
    public static String date2str(long milliseconds) {
        synchronized(sDateFormat) {
            return sDateFormat.format(new Date(milliseconds));
        }
    }

    public static long str2date(String d) {
        try {
            synchronized(sDateFormat) {
                return sDateFormat.parse(d).getTime();
            }
        } catch (ParseException ex) {
            return 0; // 1970/1/1 0:00:00 GMT
        }
    }

    /**
     * For unit testing
     * @param sqlResourceId Resource ID of raw SQL data.
     * @return
     */
    public static boolean installSqlFromResource(int sqlResourceId) {
        // open SQL raw resource
        InputStream in = sApplicationContext.getResources().openRawResource(sqlResourceId);
        BufferedReader b = new BufferedReader(new InputStreamReader(in));

        // execute each sql
        SQLiteDatabase db = ORDatabase.getDB();
        String sql;
        try {
            while ((sql = b.readLine()) != null) {
                db.execSQL(sql);
            }
            b.close();
            in.close();
        } catch (IOException e) {
            Log.d(TAG, "instalLSqlFromResource failed : " + e.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * 全テーブルを dump する
     * @return SQL文
     */
    public String dump() {
        StringBuilder sb = new StringBuilder();
        Cursor cursor;
        ArrayList<String> tableNames = new ArrayList<String>();

        // テーブル名一覧取得
        cursor = mDb.rawQuery("SELECT name, sql FROM sqlite_master WHERE type = 'table';", null);
;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String tableName = cursor.getString(0);
            String sql = cursor.getString(1);

            tableNames.add(tableName);
            sb.append("DROP TABLE " + tableName);
            sb.append(sql);
            sb.append("\n");
            cursor.moveToNext();
        }
        cursor.close();

        // 各テーブルの処理
        for (String tableName : tableNames) {
            processTable(tableName, sb);
        }

        return sb.toString();
    }
    
    protected void processTable(String tableName, StringBuilder sb) {
        Cursor cursor;

        // カラム名取得
        cursor = mDb.rawQuery("PRAGMA table_info('" + tableName + "');", null);

        ArrayList<String> columnNames = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            columnNames.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();

        // INSERT 文生成用の SELECT 文作成
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT 'INSERT INTO " + tableName + " VALUES('");
        boolean isFirst = true;
        for (String columnName : columnNames) {
            if (!isFirst) {
                sql.append("|| ','");
            }
            sql.append("||quote(");
            sql.append(columnName);
            sql.append(")");
            isFirst = false;
        }
        sql.append("');'");
        sql.append(" FROM " + tableName + ";\n");

        cursor = mDb.rawQuery(sql.toString(), null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            sb.append(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
    }
}
