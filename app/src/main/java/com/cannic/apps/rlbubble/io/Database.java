package com.cannic.apps.rlbubble.io;

import android.provider.BaseColumns;

class Database {

    // Management instructions
    private static final String CRT_TBL = " CREATE TABLE IF NOT EXISTS ";
    private static final String DROP_TBL = " DROP TABLE IF EXISTS ";
    private static final String DELETE = " DELETE ";
    private static final String INT_PK = " INTEGER PRIMARY KEY AUTOINCREMENT ";
    private static final String TEXT_PK = " TEXT PRIMARY KEY ";
    private static final String REF = " REFERENCES ";
    private static final String CST = " CONSTRAINT ";
    private static final String CST_PK = " CONSTRAINT PRIMARY KEY ";
    private static final String TEXT = " TEXT ";
    private static final String INT = " INTEGER ";
    private static final String DOUBLE = " DOUBLE ";

    // Search instructions
    private static final String SELECT = " SELECT ";
    private static final String DISTINCT = " DISTINCT ";
    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";
    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private static final String LIKE = " LIKE ";
    private static final String IN = " IN ";
    private static final String UPPER = " UPPER ";
    private static final String ORDER_BY = " ORDER BY ";

    static class Exception implements BaseColumns {
        // Table name
        static final String TABLE_NAME = "Exception";

        // ActionConnection columns
        static final String ID = "idException";
        static final String PACKAGE_NAME = "packageName";

        static final String CREATE_TABLE = CRT_TBL + TABLE_NAME
                + " ("
                + ID + INT_PK + ", "
                + PACKAGE_NAME + TEXT + "); ";

        static final String DROP_TABLE = DROP_TBL + TABLE_NAME + "; ";

        static final String GET_ALL_EXCEPTIONS = SELECT + PACKAGE_NAME
                + FROM + TABLE_NAME;

        static final String REMOVE_EXCEPTION_FROM_PACKAGE_NAME = DELETE
                + FROM + TABLE_NAME
                + WHERE + PACKAGE_NAME + "='%s' ;";
    }
}