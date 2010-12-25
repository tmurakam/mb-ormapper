// -*-  Mode:java; c-basic-offset:4; tab-width:8; indent-tabs-mode:nil -*-

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

public class ORDatabase extends SQLiteOpenHelper {
    private static final String TAG = "ORMapper";

    private static final int VERSION = 1;

    /** アプリケーションコンテキスト */
    private static Context mApplicationContext;
    
    /** データベース名 */
    private static String mDatabaseName;
    
    /** データベースインスタンス */
    private static ORDatabase sInstance;

    private static SimpleDateFormat sDateFormat;
    
    private static SimpleDateFormat sOlderDateFormat;

    private static Date sWorkDate;

    private SQLiteDatabase mDb;
    
    static {
        sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        sDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        sOlderDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        sOlderDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * 初期化
     * @param context
     */
    public static void initialize(Context context, String databaseName) {
        mApplicationContext = context.getApplicationContext();
        mDatabaseName = databaseName;
    }
    
    /**
     * データベースをオープンして SQLiteDatabase ハンドルを返す
     */
    public static SQLiteDatabase getDB() {
        assert(mApplicationContext != null);
        if (sInstance == null) {
            sInstance = new ORDatabase(mApplicationContext, mDatabaseName);
        }
        return sInstance._getDB();
    }

    /**
     * 開いているデータベースを閉じる
     */
    public static void closeDB() {
        if (sInstance != null) {
            sInstance.close();
            sInstance = null;
        }
    }

    public static void sync() {
        closeDB();
    }

    // --- Internal methods

    private ORDatabase(Context context, String databaseName) {
        super(context.getApplicationContext(), databaseName, null, VERSION);
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
    public static String date2str(long d) {
        if (sWorkDate == null) {
            sWorkDate = new Date();
        }
        sWorkDate.setTime(d);
        return sDateFormat.format(sWorkDate);
    }

    public static long str2date(String d) {
        try {
            return sDateFormat.parse(d).getTime();
        } catch (ParseException ex) {
            // 秒を含まないフォーマット(古い iPhone 版 DB フォーマット)で再度試みる
            try {
                return sOlderDateFormat.parse(d).getTime();
            } catch (ParseException ex2) {
                return 0; // 1970/1/1 0:00:00 GMT
            }
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
