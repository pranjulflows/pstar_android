package database;

import android.provider.BaseColumns;

public class ScoresEnContractClass {

    public static final class ScoreEnEntry implements BaseColumns {
        public static final String TABLE_NAME = "SCORES_EN";
        public static final String COLUMN_CATEGORY_KEY = "CategoryKey";
        public static final String COLUMN_RIGHT = "Right";
    }
}
