package database;

import android.provider.BaseColumns;

public final class UserIdContractClass {

    public static final class UserIdEntry implements BaseColumns{
        public static final String TABLE_NAME = "premium_user";
        public static final String COLUMN_USER_ID = "userId";
        public static final String COLUMN_APP_VERSION_CODE = "appVersionCode";
    }
}
