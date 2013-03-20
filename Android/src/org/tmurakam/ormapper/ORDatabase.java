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

    /** シングルトンインスタンス */
    protected static ORDatabase sInstance;

    /** アプリケーションコンテキスト */
    protected Context mContext;
    
    /** データベース名 */
    protected String mDatabaseName;

    protected SimpleDateFormat mDateFormat;

    /** SQLiteDatabase インスタンス */
    protected SQLiteDatabase mDb;

    /**
     * 初期化。getDB() 前に呼び出されている必要がある。
     * @param context           コンテキスト
     * @param databaseName      データベース名 (null時は無指定)
     */
    public static synchronized void initialize(Context context, String databaseName) {
        if (sInstance == null) {
            sInstance = new ORDatabase(context, databaseName, 1);
        }
    }

    /**
     * シングルトンインスタンスを取得する
     */
    public static ORDatabase getInstance() {
        if (sInstance == null) {
            Log.w(TAG, "getInstance : not initialized");
        }
        return sInstance;
    }
    
    /**
     * シングルトンインスタンスを inject する (テスト用)
     */
    public static void _injectInstance(ORDatabase ord) {
        sInstance = ord;
    }
    
    /**
     * データベースをオープンして SQLiteDatabase ハンドルを返す。
     */
    public static synchronized SQLiteDatabase getDB() {
        if (sInstance == null) {
            throw new IllegalStateException("not initialized");
        }
        return sInstance._getDB();
    }

    /**
     * 開いているデータベースをシャットダウンし、初期化前状態に戻す。
     * 
     * SQLiteDatabase, ORDatabase はともに解放される。
     */
    public static synchronized void shutdown() {
        if (sInstance != null) {
            sInstance.close();
            sInstance = null;
        } else {
            Log.w(TAG, "shutdown : not initialized");
        }
    }

    /**
     * 開いているデータベースを閉じる。
     * 次回 getDB() を呼び出すと、再度データベースが開かれる。
     */
    public static void sync() {
        if (sInstance != null) {
            sInstance.close();
        } else {
            Log.w(TAG, "sync : not initialized");
        }
    }

    /**
     * 再初期化。テスト用の API。
     * 未初期化あるいはコンテキストが変更された場合のみ、DBをクローズする。
     * @param context コンテキスト
     */
    public synchronized void _reinitialize(Context context) {
        Context c = context.getApplicationContext();
        if (mContext == c) return; // do nothing

        mContext = c;
        close();
    }

    // --- Internal methods

    /**
     * コンストラクタ
     * @param context
     * @param databaseName
     * @param schemaVersion
     */
    protected ORDatabase(Context context, String databaseName, int schemaVersion) {
        super(context.getApplicationContext(), databaseName, null, schemaVersion);
        mContext = context.getApplicationContext();
        mDatabaseName = databaseName;

        mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * SQLiteDatabase インスタンスを取得する。
     * @return
     */
    protected synchronized SQLiteDatabase _getDB() {
        if (mDb == null) {
            mDb = getWritableDatabase();
        }
        return mDb;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void close() {
        if (mDb != null) {
            mDb.close();
            mDb = null;
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
        return sInstance._date2str(milliseconds);
    }
    
    public String _date2str(long milliseconds) {
        synchronized(mDateFormat) {
            return mDateFormat.format(new Date(milliseconds));
        }
    }

    public static long str2date(String d) {
        return sInstance._str2date(d);
    }
    
    public long _str2date(String d) {
        try {
            synchronized(mDateFormat) {
                return mDateFormat.parse(d).getTime();
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
    public boolean installSqlFromResource(int sqlResourceId) {
        // open SQL raw resource
        InputStream in = mContext.getResources().openRawResource(sqlResourceId);
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
        SQLiteDatabase db = _getDB();
        StringBuilder sb = new StringBuilder();
        Cursor cursor;

        // テーブル名一覧取得
        cursor = db.rawQuery("SELECT name, sql FROM sqlite_master WHERE type = 'table';", null);
;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String tableName = cursor.getString(0);
            String sql = cursor.getString(1);

            if (!tableName.equals("android_metadata")) {
                sb.append("DROP TABLE IF EXISTS ");
                sb.append(tableName);
                sb.append(";\n");
                sb.append(sql);
                sb.append(";\n");
            
                // 各テーブルの処理
                dumpTable(db, tableName, sb);
            }

            cursor.moveToNext();
        }
        cursor.close();

        return sb.toString();
    }
    
    protected void dumpTable(SQLiteDatabase db, String tableName, StringBuilder sb) {
        Cursor cursor;

        // カラム名取得
        cursor = db.rawQuery("PRAGMA table_info('" + tableName + "');", null);

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
        sql.append("|| ')'");
        sql.append(" FROM " + tableName + ";\n");

        cursor = db.rawQuery(sql.toString(), null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            sb.append(cursor.getString(0));
            sb.append(";\n");
            cursor.moveToNext();
        }
        cursor.close();
    }
}
