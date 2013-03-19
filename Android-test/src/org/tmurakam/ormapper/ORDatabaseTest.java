package org.tmurakam.ormapper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

public class ORDatabaseTest extends AndroidTestCase {
    private static final String TAG = "ORDatabaseTest";
    
    private Context mContext;
    private SQLiteDatabase mDb;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        mContext = getContext();
        ORDatabase.closeDB();
        ORDatabase.initialize(mContext, "test.db");
        mDb = ORDatabase.getDB();
    }
    
    /**
     * dump テスト
     */
    public void testDump() {
        mDb.execSQL("DROP TABLE test;");
        mDb.execSQL("CREATE TABLE test (id INTEGER PRIMARY KEY, name TEXT);");
        mDb.execSQL("INSERT INTO test VALUES (1, 'test');");
        
        String dump = ORDatabase.dump(mDb);
        Log.d(TAG, "sql = " + dump);
    }
}
