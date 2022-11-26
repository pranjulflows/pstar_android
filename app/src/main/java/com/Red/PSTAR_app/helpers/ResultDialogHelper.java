package com.Red.PSTAR_app.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.Red.PSTAR_app.R;
import com.Red.PSTAR_app.utils.AppConstants;
import com.Red.PSTAR_app.utils.AppSharedMethod;

import java.text.DecimalFormat;

import database.DBAdapter;
import database.DatabaseHelper;
import database.ScoresEnContractClass;

/**
 * Created by Alexey Matrosov on 01.10.2018.
 */
public class ResultDialogHelper {
    public interface ICloseDialog {
        void onClose();
    }

    public static void showResultDialog(Context context, String right, String wrong, String categoryKey, ICloseDialog closeListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cat_result, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(dialog1 -> {
            if (closeListener != null)
                closeListener.onClose();
        });

        initView(view, right, wrong);
        updateDb(context, categoryKey, right);

        view.findViewById(R.id.ok_button).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private static void initView(View view, String right, String wrong) {
        TextView correctAns = view.findViewById(R.id.txt_corretAns);
        TextView wrongAns = view.findViewById(R.id.txt_wrongAns);
        TextView txtpercentage = view.findViewById(R.id.txt_percentage);

        int total = Integer.parseInt(right) + Integer.parseInt(wrong);
        String count = String.valueOf(total);

        float rightAnswers = Float.parseFloat(right);
        float totalQuestions = Float.parseFloat(count);

        float average = ((rightAnswers/totalQuestions) * 100);
        DecimalFormat df = new DecimalFormat("#");
        String percentage = String.valueOf(df.format(average));

        correctAns.setText(right);
        wrongAns.setText(wrong);
        txtpercentage.setText(percentage+"%");
    }

    private static void updateDb(Context context, String categoryKey, String value) {
        DBAdapter openHelper = new DBAdapter(context);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        String mScoreTableName = "";
        if (AppSharedMethod.isEnglishLayout(context)) {
            mScoreTableName = ScoresEnContractClass.ScoreEnEntry.TABLE_NAME;
        } else {
            mScoreTableName = AppConstants.TABLE_SCORE_FR_NAME;
        }

        DatabaseHelper.updateScores(db, categoryKey, value,mScoreTableName);

        db.close();
    }
}