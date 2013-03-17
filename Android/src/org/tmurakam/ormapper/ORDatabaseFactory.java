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

/**
 * ORDatabase ファクトリ。
 * ORDatabase のサブクラスを使用する場合は、本ファクトリを継承して {@link #create}
 * をオーバライドし、ORDatabase にファクトリをセットする。
 */
public class ORDatabaseFactory {
    //private static final String TAG = ORDatabaseFactory.class.getSimpleName();
    
    /** アプリケーションコンテキスト */
    protected Context mApplicationContext;

    /** データベース名 */
    protected String mDatabaseName;

    /** データベースヘルパインスタンス */
    protected ORDatabase mInstance;

    /**
     * 初期化。getDB() 前に呼び出されている必要がある。
     * @param context           コンテキスト
     * @param databaseName      データベース名 (null時は無指定)
     */
    public void initialize(Context context, String databaseName) {
        mApplicationContext = context.getApplicationContext();
        setDatabaseName(databaseName);
    }

    /**
     * データベース名を指定する。
     * @param databaseName  データベース名
     */
    public void setDatabaseName(String databaseName) {
        if (databaseName != null) {
            mDatabaseName = databaseName;
        }
    }

    /**
     * インスタンス取得。
     */
    public ORDatabase getInstance() {
        assert(mApplicationContext != null);
        assert(mDatabaseName != null);
        
        if (mInstance == null) {
            mInstance = create();
        }
        return mInstance;
    }
    
    /**
     * インスタンス生成
     */
    protected ORDatabase create() {
        return new ORDatabase(mApplicationContext, mDatabaseName, 1);
    }
    
    /**
     * インスタンスをクローズする。
     */
    public void close() {
        if (mInstance != null) {
            mInstance.close();
            mInstance = null;
        }
    }
}
