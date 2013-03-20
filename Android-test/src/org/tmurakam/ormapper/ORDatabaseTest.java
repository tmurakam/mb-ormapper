package org.tmurakam.ormapper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.io.File;

public class ORDatabaseTest extends AndroidTestCase {
    private static final String TAG = "ORDatabaseTest";
    
    private Context mContext;
    private SQLiteDatabase mDb;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        mContext = getContext();
        ORDatabase.initialize(mContext, "test.db");
        mDb = ORDatabase.getDB();
    }
    
    @Override
    public void tearDown() throws Exception {
        if (ORDatabase.getInstance() != null) {
            String dbPath = ORDatabase.getDB().getPath();
            ORDatabase.shutdown();
            new File(dbPath).delete();
        }
    }
    
    /**
     * 初期化テスト
     */
    public void testInitialize() {
        ORDatabase.shutdown();
        assertNull(ORDatabase.getInstance());

        ORDatabase.initialize(getContext(), "test.db");
        ORDatabase instance = ORDatabase.getInstance();
        assertNotNull(instance);
        mDb = ORDatabase.getDB();
        assertTrue(mDb.getPath().endsWith("test.db"));
        
        // 再初期化されないこと
        ORDatabase.initialize(getContext(), "test2.db");
        assertSame(instance, ORDatabase.getInstance());
    }
    
    /**
     * getDB テスト
     */
    public void testGetDB() {
        SQLiteDatabase db = ORDatabase.getDB();
        assertTrue(db.getPath().endsWith("test.db"));
    }

    /**
     * getDB : sync 後に再度インスタンスが生成されること。
     */
    public void testGetDBAfterSync() {
        ORDatabase.sync();
        
        SQLiteDatabase db = ORDatabase.getDB();
        assertTrue(db.getPath().endsWith("test.db"));
    }

    /**
     * sync テスト
     */
    public void testSync() {
        ORDatabase.sync();
        ORDatabase instance = ORDatabase.getInstance();
        assertNotNull(instance);
        assertNull(instance.mDb);
    }
    
    /**
     * shutdown テスト
     */
    public void testShutdown() {
        ORDatabase instance = ORDatabase.getInstance();
        assertNotNull(instance.mDb);
        
        ORDatabase.shutdown();
        assertNull(ORDatabase.getInstance());
        assertNull(instance.mDb);
        
        ORDatabase.shutdown(); // should never crash
    }
    
    /**
     * dump テスト
     */
    public void testDump() {
        //mDb.execSQL("DROP TABLE test;");
        mDb.execSQL("CREATE TABLE test (id INTEGER PRIMARY KEY, name TEXT);");
        mDb.execSQL("INSERT INTO test VALUES (1, 'test');");
        
        String dump = ORDatabase.dump(mDb);
        Log.d(TAG, "sql = " + dump);
    }
}
