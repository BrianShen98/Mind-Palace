package com.example.jingyue.mindpalace.data;

import android.provider.BaseColumns;

public final class MindContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private MindContract() {}

    /* Inner class that defines the table contents */
    public static class MindEntry implements BaseColumns { //BaseColumns auto create id String
        public static final String TABLE_NAME = "db";
        public static final String COLUMN_URI = "uri";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_FEATURE1 = "feature1";
        public static final String COLUMN_FEATURE2 = "feature2";
        public static final String COLUMN_FEATURE3 = "feature3";
        public static final String COLUMN_FEATURE4 = "feature4";
        public static final String COLUMN_FEATURE5 = "feature5";
        public static final String COLUMN_FEATURE6 = "feature6";
        public static final String COLUMN_FEATURE7 = "feature7";
        public static final String COLUMN_FEATURE8 = "feature8";
        public static final String COLUMN_FEATURE9 = "feature9";
        public static final String COLUMN_FEATURE10 = "feature10";
    }

    
}

