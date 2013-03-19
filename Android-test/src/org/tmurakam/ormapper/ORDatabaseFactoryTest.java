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
    
    public void testInitialize() {
        mFactory.initialize(mContext, "test.db");
        assertSame(mContext.getApplicationContext(), mFactory.mApplicationContext);
        assertEquals("test.db", mFactory.mDatabaseName);
    }

}
