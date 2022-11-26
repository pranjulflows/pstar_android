package database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.RawRes;
import android.util.Log;

import com.Red.PSTAR_app.Question;
import com.Red.PSTAR_app.R;
import com.Red.PSTAR_app.Score;
import com.Red.PSTAR_app.localization.CategoryKey;
import com.Red.PSTAR_app.utils.AppConstants;
import com.Red.PSTAR_app.utils.PSTARApp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static database.DBConstants.COLUMN_A;
import static database.DBConstants.COLUMN_B;
import static database.DBConstants.COLUMN_C;
import static database.DBConstants.COLUMN_CATEGORY_KEY;
import static database.DBConstants.COLUMN_CORRECT;
import static database.DBConstants.COLUMN_D;
import static database.DBConstants.COLUMN_QNO;
import static database.DBConstants.COLUMN_QUESTION;
import static database.DBConstants.COLUMN_RIGHT;
import static database.DBConstants.TABLE_LOCALIZED_QUESTIONS;

/**
 * Created by Alexey Matrosov on 30.09.2018.
 */
public class DatabaseHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    public static List<Score> getScores(SQLiteDatabase db, String SCORE_TABLE_NAME) {
        List<Score> Scores = new ArrayList<>();

        String query = String.format("SELECT * FROM %s ;", SCORE_TABLE_NAME);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Score score = new Score();
                score.category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_KEY));
                score.right = cursor.getString(cursor.getColumnIndex(COLUMN_RIGHT));
                score.total = String.valueOf(getCategoryCount(db, score.category));

                if (Integer.parseInt(score.right) > Integer.parseInt(score.total))
                    score.right = score.total;

                Scores.add(score);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return Scores;
    }

    public static int getCategoryCount(SQLiteDatabase db, String categoryKey) {
        if (categoryKey.equals(CategoryKey.Exam))
            return Question.ExamCount;

        int count;

        String query = String.format("SELECT * FROM %s WHERE %s ='%s';", TABLE_LOCALIZED_QUESTIONS, COLUMN_CATEGORY_KEY, categoryKey);
        Cursor cursor = db.rawQuery(query, null);

        count = cursor.getCount() / 2;
        cursor.close();
        return count;
    }

    public static void resetScores(SQLiteDatabase db, String categoryKey, String SCORE_TABLE_NAME) {
        updateScores(db, categoryKey, "0", SCORE_TABLE_NAME);
    }

    public static void resetAllScores(SQLiteDatabase db, String SCORE_TABLE_NAME) {
        for (String category : CategoryKey.CATEGORIES) {
            resetScores(db, category, SCORE_TABLE_NAME);
        }
    }

    public static void updateScores(SQLiteDatabase db, String categoryKey, String value, String SCORE_TABLE_NAME) {
        Cursor cursor = db.rawQuery(String.format("SELECT * FROM " + SCORE_TABLE_NAME + "  WHERE " + COLUMN_CATEGORY_KEY + " ='%s'", categoryKey), null);

        boolean hasElement = cursor.moveToFirst();
        cursor.close();

        ContentValues scoresValues = new ContentValues();
        if (hasElement) {
            scoresValues.put(COLUMN_RIGHT, value);
            db.update(SCORE_TABLE_NAME, scoresValues, COLUMN_CATEGORY_KEY + " = ?", new String[]{categoryKey});
        } else {
            scoresValues.put(COLUMN_CATEGORY_KEY, categoryKey);
            scoresValues.put(COLUMN_RIGHT, value);
            db.insert(SCORE_TABLE_NAME, null, scoresValues);
        }
    }


    public static List<Question> getQuestionsByCategory(SQLiteDatabase db, String categoryKey) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOCALIZED_QUESTIONS +
                "  WHERE CategoryKey = '" + categoryKey + "' AND Locale='" + PSTARApp.getInstance().getSharedPreferences(AppConstants.MY_PREF, Context.MODE_PRIVATE).getString(AppConstants.PREF_LANG, AppConstants.EN) + "' ORDER BY RANDOM()", null);

        List<Question> questions = generateQuestionsFromCursor(cursor);
        cursor.close();
        return questions;
    }

    public static List<Question> getQuestionsForExam(SQLiteDatabase db) {
        String lang = PSTARApp.getInstance().getSharedPreferences(AppConstants.MY_PREF, Context.MODE_PRIVATE)
                .getString(AppConstants.PREF_LANG, AppConstants.EN);

        Cursor cursor = db.rawQuery("SELECT * FROM  LOCALIZED_QUESTIONS " +
                        " WHERE Locale='" + lang + "' ORDER BY RANDOM() LIMIT 50",
                null);

        List<Question> questions = generateQuestionsFromCursor(cursor);
        cursor.close();
        return questions;
    }

    private static List<Question> generateQuestionsFromCursor(Cursor cursor) {
        List<Question> questions = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Question pro = new Question();
                pro.Qno = cursor.getString(cursor.getColumnIndex(COLUMN_QNO));
                pro.Question = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION));
                pro.A = cursor.getString(cursor.getColumnIndex(COLUMN_A));
                pro.B = cursor.getString(cursor.getColumnIndex(COLUMN_B));
                pro.C = cursor.getString(cursor.getColumnIndex(COLUMN_C));
                pro.D = cursor.getString(cursor.getColumnIndex(COLUMN_D));
                pro.Correct = cursor.getString(cursor.getColumnIndex(COLUMN_CORRECT));
                pro.Category = CategoryKey.getKeyByNumber(pro.Qno);

                questions.add(pro);
            } while (cursor.moveToNext());
        }

        return questions;
    }

    // Get localized Abbreviations list
    public static List<String> getAbbreviations(Resources resources) {
        List<String> abbreviations = new ArrayList<>();
        int resId = getAbbreviationsFile();

        try {
            InputStream is = resources.openRawResource(resId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty())
                    abbreviations.add(line);
            }

            reader.close();
            is.close();
        } catch (Exception ex) {
            Log.e("PSTAR", ex.getMessage(), ex);
        }

        return abbreviations;
    }

    @RawRes
    private static int getAbbreviationsFile() {
        switch (PSTARApp.getInstance().getSharedPreferences(AppConstants.MY_PREF, Context.MODE_PRIVATE).getString(AppConstants.PREF_LANG, AppConstants.EN)) {
            case AppConstants.EN:
                return R.raw.abbreviations_en;
            case AppConstants.FR:
                return R.raw.abbreviations_fr;
            default:
                throw new RuntimeException("Unknown locale");
        }
    }

    private static String SentenceCase(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}