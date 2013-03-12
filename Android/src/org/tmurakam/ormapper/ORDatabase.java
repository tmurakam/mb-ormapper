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
    private static final String TAG = "ORMapper";

    /** アプリケーションコンテキスト */
    private static Context mApplicationContext;

    /** データベース名 */
    private static String mDatabaseName;

    /** データベースヘルパインスタンス */
    private static ORDatabase sInstance;

    /** データベーススキーマバージョン */
    private static int sSchemaVersion = 1;

    private static SimpleDateFormat sDateFormat;

    /** SQLiteDatabase インスタンス */
    private SQLiteDatabase mDb;

    static {
        sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        sDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * 初期化。getDB() 前に呼び出されている必要がある。
     * @param context コンテキスト
     * @param databaseName データベース名
     * @param schemaVersion スキーマバージョン
     */
    public static void initialize(Context context, String databaseName, int schemaVersion) {
        mApplicationContext = context.getApplicationContext();
        sSchemaVersion = schemaVersion;
        setDatabaseName(databaseName);
    }

    /**
     * 初期化。getDB() 前に呼び出されている必要がある。
     * <p>
     * スキーマバージョンは 1 が指定される。
     * @param context コンテキスト
     * @param databaseName データベース名 (null時は無指定)
     */
    public static void initialize(Context context, String databaseName) {
        initialize(context, databaseName, 1);
    }

    /**
     * 初期化。getDB() 前に呼び出されている必要がある。
     * @param context コンテキスト
     */
    public static void initialize(Context context) {
        Context c = context.getApplicationContext();
        if (c != mApplicationContext) {
            // re-init
            mApplicationContext = c;
            sInstance = null;
        }
    }

    /**
     * データベース名を指定する
     * @param databaseName  データベース名
     */
    public static void setDatabaseName(String databaseName) {
        if (databaseName != null) {
            mDatabaseName = databaseName;
        }
    }

    /**
     * データベースをオープンして SQLiteDatabase ハンドルを返す。
     * 
     * すでにインスタンスがある場合はこれを返す。
     */
    public static synchronized SQLiteDatabase getDB() {
        assert(mApplicationContext != null);
        assert(mDatabaseName != null);
        if (sInstance == null) {
            sInstance = new ORDatabase(mApplicationContext, mDatabaseName, sSchemaVersion);
        }
        return sInstance._getDB();
    }

    /**
     * 開いているデータベースを閉じる。
     * 
     * シングルトンインスタンスは解放される。
     */
    public static synchronized void closeDB() {
        if (sInstance != null) {
            sInstance.close();
            sInstance = null;
        }
    }

    public static void sync() {
        closeDB();
    }

    // --- Internal methods

    private ORDatabase(Context context, String databaseName, int schemaVersion) {
        super(context.getApplicationContext(), databaseName, null, schemaVersion);
    }

    private SQLiteDatabase _getDB() {
        if (mDb == null) {
            mDb = getWritableDatabase();
        }
        return mDb;
    }

    @Override
    public void close() {
        if (mDb != null) {
            mDb.close();
        }
        super.close();
    }

    public void onCreate(SQLiteDatabase db) {
    }

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
        InputStream in = mApplicationContext.getResources().openRawResource(sqlResourceId);
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
}
