package database;

import static database.DBConstants.COLUMN_A;
import static database.DBConstants.COLUMN_APP_VERSION_CODE;
import static database.DBConstants.COLUMN_B;
import static database.DBConstants.COLUMN_C;
import static database.DBConstants.COLUMN_CATEGORY_KEY;
import static database.DBConstants.COLUMN_CORRECT;
import static database.DBConstants.COLUMN_D;
import static database.DBConstants.COLUMN_ID;
import static database.DBConstants.COLUMN_LOCALE;
import static database.DBConstants.COLUMN_QNO;
import static database.DBConstants.COLUMN_QUESTION;
import static database.DBConstants.COLUMN_RIGHT;
import static database.DBConstants.COLUMN_USER_ID;
import static database.DBConstants.TABLE_LOCALIZED_QUESTIONS;
import static database.DBConstants.TABLE_QUESTIONS;
import static database.DBConstants.TABLE_SCORE_EN;
import static database.DBConstants.TABLE_SCORE_FR;
import static database.DBConstants.TABLE_USER;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.Red.PSTAR_app.Question;
import com.Red.PSTAR_app.Score;
import com.Red.PSTAR_app.localization.CategoryKey;
import com.Red.PSTAR_app.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class DBAdapter extends SQLiteOpenHelper {
    private static final String DbName = "app_db";
    private static final int DbVersion = 23;
    private static final String TAG = DBAdapter.class.getSimpleName();

    private static DBAdapter sInstance;

    public DBAdapter(Context context) {
        super(context, DbName, null, DbVersion);
    }

    public static synchronized DBAdapter getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBAdapter(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOCALIZED_QUESTION_TABLE = "CREATE TABLE " + TABLE_LOCALIZED_QUESTIONS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                COLUMN_QNO + " TEXT," +
                COLUMN_QUESTION + " TEXT," +
                COLUMN_A + " TEXT," +
                COLUMN_B + " TEXT," +
                COLUMN_C + " TEXT," +
                COLUMN_D + " TEXT," +
                COLUMN_CORRECT + " TEXT," +
                COLUMN_CATEGORY_KEY + " TEXT," +
                COLUMN_LOCALE + " TEXT" +
                ")";
        try {
            db.execSQL(CREATE_LOCALIZED_QUESTION_TABLE);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: CREATE_LOCALIZED_QUESTION_TABLE ", e);
        }
//        String CREATE_TABLE_SCORES = "CREATE TABLE " + TABLE_SCORES + "(" +
//                COLUMN_ID + " INTEGER PRIMARY KEY," +
//                COLUMN_CATEGORY_KEY + " TEXT," +
//                COLUMN_RIGHT + " TEXT " +
//                ")";
//        try {
//            db.execSQL(CREATE_TABLE_SCORES);
//        } catch (Exception e) {
//            Log.e(TAG, "onCreate:CREATE_TABLE_SCORES ", e);
//        }
        String CREATE_EN_Score_TABLE = "CREATE TABLE " + TABLE_SCORE_EN + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CATEGORY_KEY + " TEXT NOT NULL," +
                COLUMN_RIGHT + " TEXT NOT NULL" +
                ")";
        try {
            db.execSQL(CREATE_EN_Score_TABLE);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: CREATE_EN_Score_TABLE", e);
        }
        String CREATE_FR_SCORE_TABLE = "CREATE TABLE " + TABLE_SCORE_FR + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CATEGORY_KEY + " TEXT NOT NULL," +
                COLUMN_RIGHT + " TEXT NOT NULL" +
                ")";
        try {
            db.execSQL(CREATE_FR_SCORE_TABLE);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: CREATE_EN_Score_TABLE", e);
        }
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USER_ID + " TEXT NOT NULL," +
                COLUMN_APP_VERSION_CODE + " INTEGER NOT NULL" +
                ")";
        try {
            db.execSQL(CREATE_USER_TABLE);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }
        DatabaseHelper.resetAllScores(db, TABLE_SCORE_FR);
        DatabaseHelper.resetAllScores(db, TABLE_SCORE_EN);
        setQuestionsForLocale(db, getQuestions(null, AppConstants.EN), AppConstants.EN);
        setQuestionsForLocale(db, getQuestions(null, AppConstants.FR), AppConstants.FR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        List<Score> oldScores = getOldScores(db, TABLE_SCORE_FR);
        List<Score> oldEnScores = getOldScores(db, TABLE_SCORE_EN);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCALIZED_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE_EN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE_FR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);

        convertOldScores(db, oldEnScores, TABLE_SCORE_EN);                  // Create new tables
        convertOldScores(db, oldScores, TABLE_SCORE_FR);                    // Set old scores to new table
    }

    // Support of old db version
    private List<Score> getOldScores(SQLiteDatabase db, String SCORE_TABLE_NAME) {
        List<Score> Scores = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + SCORE_TABLE_NAME, null);
            try {

                if (cursor.moveToFirst()) {
                    do {
                        Score score = new Score();
                        score.category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_KEY));
                        score.right = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RIGHT));
                        Scores.add(score);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            } catch (Exception e) {
                Log.e(TAG, "getOldScores: ", e);
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }

        return Scores;
    }


    // Support of old db version
    private void convertOldScores(SQLiteDatabase db, List<Score> oldScores, String SCORE_TABLE_NAME) {
        for (Score score : oldScores) {
            String categoryKey = score.category;
            String right = (score.right == null || score.right.isEmpty()) ? "0" : score.right;

            if (categoryKey != null)
                DatabaseHelper.updateScores(db, categoryKey, right, SCORE_TABLE_NAME);
        }
    }


    private void setQuestionsForLocale(SQLiteDatabase db, @NonNull List<Question> questions, String locale) {
        for (Question question : questions) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_QNO, question.Qno);
            values.put(COLUMN_QUESTION, question.Question);
            values.put(COLUMN_A, question.A);
            values.put(COLUMN_B, question.B);
            values.put(COLUMN_C, question.C);
            values.put(COLUMN_D, question.D);
            values.put(COLUMN_CORRECT, question.Correct);
            values.put(COLUMN_CATEGORY_KEY, CategoryKey.getKeyByNumber(question.Qno));
            values.put(COLUMN_LOCALE, locale);
            db.insertOrThrow(TABLE_LOCALIZED_QUESTIONS, null, values);
        }
    }

    private List<Question> getQuestions(@Nullable Resources res, String locale) {
        AssetDatabaseOpenHelper fr = new AssetDatabaseOpenHelper(locale + ".db");
        SQLiteDatabase internalDB = fr.saveDatabase();
        List<Question> list = new ArrayList<>();
        try {
            Cursor cursor = internalDB.rawQuery("SELECT * FROM " + TABLE_QUESTIONS, null);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        Question question = new Question();
                        question.Qno = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QNO));
                        question.Question = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION));
                        question.A = SentenceCase(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_A)));
                        question.B = SentenceCase(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_B)));
                        question.C = SentenceCase(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_C)));
                        question.D = SentenceCase(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_D)));
                        question.Correct = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CORRECT));
                        question.Category = CategoryKey.getKeyByNumber(question.Qno);
                        list.add(question);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                Log.e(TAG, "getQuestions: ", e);
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
                try {
                    if (internalDB.inTransaction()) {
                        internalDB.endTransaction();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "getQuestions: ", e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getQuestions: ", e);
        }
        return list;
    }

    private static String SentenceCase(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}