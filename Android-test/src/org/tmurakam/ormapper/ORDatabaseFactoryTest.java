package org.tmurakam.ormapper;

import android.content.Context;
import android.test.AndroidTestCase;

public class ORDatabaseFactoryTest extends AndroidTestCase {
    private ORDatabaseFactory mFactory;
    private Context mContext;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        mContext = getContext();
        mFactory = new ORDatabaseFactory();
    }
    
    /**
     * 正常初期化テスト。
     */
    public void testInitialize() {
        mFactory.initialize(mContext, "test.db");
        assertSame(mContext.getApplicationContext(), mFactory.mApplicationContext);
        assertEquals("test.db", mFactory.mDatabaseName);
    }
    
    /**
     * 初期化 : databaseName が null のとき初期化されないこと。
     */
    public void testInitializeNullDatabaseName() {
        mFactory.initialize(mContext, null);
        assertNull(mFactory.mDatabaseName);
    }

    /**
     * setDatabaseName : 上書きすること。
     */
    public void testSetDatabaseName() {
        mFactory.initialize(mContext, "test.db");
        mFactory.setDatabaseName("test2.db");
        assertEquals("test2.db", mFactory.mDatabaseName);
    }
}
