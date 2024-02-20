package com.Red.PSTAR_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.Red.PSTAR_app.helpers.ResultDialogHelper;
import com.Red.PSTAR_app.localization.CategoryKey;
import com.Red.PSTAR_app.utils.AppConstants;
import com.Red.PSTAR_app.utils.AppSharedMethod;
import com.Red.PSTAR_app.utils.MyContextWrapper;
import com.Red.PSTAR_app.utils.PSTARApp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import database.DBAdapter;
import database.DatabaseHelper;
import database.ScoresEnContractClass;

public class Cat_QuestionsActivity extends Activity implements OnClickListener {
    private static final String ExtraKey = "extra_key";

    TextView category = null;
    TextView question = null;
    TextView choice1 = null;
    TextView choice2 = null;
    TextView choice3 = null;
    TextView choice4 = null;
    TextView question_count = null;

    private View choise1Container;
    private View choise2Container;
    private View choise3Container;
    private View choise4Container;

    ImageView RW_1;
    ImageView RW_2;
    ImageView RW_3;
    ImageView RW_4;
    ImageView next;
    ImageView pic;

    String value = null; // Name of Category receive from PREVIOUS ACTIVITY
    String wrong_s = "0";
    String right_s = "0";
    String set;
    String question_count_s = null;

    int[] result; // ARRAY FOR CHECKING THE CORRECT/WRONG ANSWERS
    int count = 0; // TOTAL NUMBER OF QUESTIONS IN A CATEGORY
    int no = 0; // ID/REFERENCE OF QUESTION NUMBER
    int corrects = 0; // total correct number in result array
    int wrongs = 0; // total wrong number
    int clicks = 0; // Check the first click Answer
    int controlBack = 0; //control the back
    int last = 0;
    boolean lastcheck = false;
    int lastcheck2 = 0;

    public List<Question> All_Question; // All Questions in a category will
    // be stored in this arrayList


    String[] random;

    public static Intent createIntent(Context context, String categoryKey) {
        Intent intent = new Intent(context, Cat_QuestionsActivity.class);
        intent.putExtra(ExtraKey, categoryKey);
        return intent;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cat_questions);

        category = (TextView) findViewById(R.id.txt_cat_category);
        question = (TextView) findViewById(R.id.txt_cat_quesion);
        choice1 = (TextView) findViewById(R.id.btn_cat_choice1);
        choice2 = (TextView) findViewById(R.id.btn_cat_choice2);
        choice3 = (TextView) findViewById(R.id.btn_cat_choice3);
        choice4 = (TextView) findViewById(R.id.btn_cat_choice4);
        question_count = (TextView) findViewById(R.id.txt_question_count);

        choise1Container = findViewById(R.id.linearLayout1);
        choise2Container = findViewById(R.id.linearLayout2);
        choise3Container = findViewById(R.id.linearLayout3);
        choise4Container = findViewById(R.id.linearLayout4);

        choise1Container.setClickable(true);
        choise2Container.setClickable(true);
        choise3Container.setClickable(true);
        choise4Container.setClickable(true);

        choice1.setClickable(false);
        choice2.setClickable(false);
        choice3.setClickable(false);
        choice4.setClickable(false);

        RW_1 = (ImageView) findViewById(R.id.img_RW_1);
        RW_2 = (ImageView) findViewById(R.id.img_RW_2);
        RW_3 = (ImageView) findViewById(R.id.img_RW_3);
        RW_4 = (ImageView) findViewById(R.id.img_RW_4);
        next = (ImageView) findViewById(R.id.img_next);
        pic = (ImageView) findViewById(R.id.img_pic);

        choise1Container.setOnClickListener(this);
        choise2Container.setOnClickListener(this);
        choise3Container.setOnClickListener(this);
        choise4Container.setOnClickListener(this);

        // Get Category Name from Previous Activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString(ExtraKey);
        }

        // Loads a Specific Category or Random 50 questions

        DBAdapter openHelper = new DBAdapter(this);
        SQLiteDatabase db = openHelper.getWritableDatabase();

        All_Question = (value.equals(CategoryKey.Exam))
                ? DatabaseHelper.getQuestionsForExam(db)
                : DatabaseHelper.getQuestionsByCategory(db, value);

        count = All_Question.size();

        db.close();

        set = 1 + "/" + count;
        result = new int[count];

        next.setClickable(false); // first time next button is disabled

        // by Default first question set on the views
        question_count.setText("(" + set + ")");

        if (value.equalsIgnoreCase(CategoryKey.Exam)) {
            if (AppSharedMethod.isEnglishLayout(Cat_QuestionsActivity.this)) {
                category.setText(PSTARApp.getCategoryEnHashMap().get(CategoryKey.Exam));
            } else {
                category.setText(PSTARApp.getCategoryFRHashMap().get(CategoryKey.Exam));
            }

        } else {
            if (AppSharedMethod.isEnglishLayout(Cat_QuestionsActivity.this)) {
                category.setText(PSTARApp.getCategoryEnHashMap().get(All_Question.get(no).Category));
            } else {
                category.setText(PSTARApp.getCategoryFRHashMap().get(All_Question.get(no).Category));
            }
        }
        randomAnswers();
        pic.setImageResource(R.drawable.nn);

        String ss = All_Question.get(no).Qno;

        question.setText(All_Question.get(no).Question);
        choice1.setText(random[0]);
        choice2.setText(random[1]);
        choice3.setText(random[2]);
        choice4.setText(random[3]);
    }


    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.linearLayout1:

                clicks++;
                CheckCorrect(random[0], clicks);

                break;
            case R.id.linearLayout2:

                clicks++;
                CheckCorrect(random[1], clicks);

                break;
            case R.id.linearLayout3:

                clicks++;
                CheckCorrect(random[2], clicks);

                break;
            case R.id.linearLayout4:

                clicks++;
                CheckCorrect(random[3], clicks);

                break;

        }

    }

    public void enableRW() {

        RW_1.setImageResource(R.drawable.nn);
        RW_2.setImageResource(R.drawable.nn);
        RW_3.setImageResource(R.drawable.nn);
        RW_4.setImageResource(R.drawable.nn);
        choise1Container.setClickable(true);
        choise2Container.setClickable(true);
        choise3Container.setClickable(true);
        choise4Container.setClickable(true);

        next.setClickable(false);
    }

    public void enableRW2() {
        if (All_Question.get(no).Correct.equalsIgnoreCase(random[0])) {
            RW_1.setImageResource(R.drawable.right);
            RW_2.setImageResource(R.drawable.wrong);
            RW_3.setImageResource(R.drawable.wrong);
            RW_4.setImageResource(R.drawable.wrong);
            choise1Container.setClickable(false);
            choise2Container.setClickable(false);
            choise3Container.setClickable(false);
            choise4Container.setClickable(false);
        } else if (All_Question.get(no).Correct.equalsIgnoreCase(random[1])) {
            RW_1.setImageResource(R.drawable.wrong);
            RW_2.setImageResource(R.drawable.right);
            RW_3.setImageResource(R.drawable.wrong);
            RW_4.setImageResource(R.drawable.wrong);
            choise1Container.setClickable(false);
            choise2Container.setClickable(false);
            choise3Container.setClickable(false);
            choise4Container.setClickable(false);
        } else if (All_Question.get(no).Correct.equalsIgnoreCase(random[2])) {
            RW_1.setImageResource(R.drawable.wrong);
            RW_2.setImageResource(R.drawable.wrong);
            RW_3.setImageResource(R.drawable.right);
            RW_4.setImageResource(R.drawable.wrong);
            choise1Container.setClickable(false);
            choise2Container.setClickable(false);
            choise3Container.setClickable(false);
            choise4Container.setClickable(false);
        } else {
            RW_1.setImageResource(R.drawable.wrong);
            RW_2.setImageResource(R.drawable.wrong);
            RW_3.setImageResource(R.drawable.wrong);
            RW_4.setImageResource(R.drawable.right);
            choise1Container.setClickable(false);
            choise2Container.setClickable(false);
            choise3Container.setClickable(false);
            choise4Container.setClickable(false);
        }
        next.setClickable(true);

    }


    public void CheckCorrect(String choice, int nos) {

        String s = All_Question.get(no).Correct;
        String ss = choice;

        if (choice.equalsIgnoreCase(s) && nos == 1) {
            clicks = 0;
            next.setClickable(true);
            if (count - 1 == no) {
                next.setImageResource(R.drawable.result);
                lastcheck = true;

            }

            result[no] = 1;
            lastcheck = true;

            if (choice.equalsIgnoreCase(random[0])) {
                RW_1.setImageResource(R.drawable.right);
                RW_2.setImageResource(R.drawable.wrong);
                RW_3.setImageResource(R.drawable.wrong);
                RW_4.setImageResource(R.drawable.wrong);
                choise1Container.setClickable(false);
                choise2Container.setClickable(false);
                choise3Container.setClickable(false);
                choise4Container.setClickable(false);

            } else if (choice.equalsIgnoreCase(random[1])) {
                RW_1.setImageResource(R.drawable.wrong);
                RW_2.setImageResource(R.drawable.right);
                RW_3.setImageResource(R.drawable.wrong);
                RW_4.setImageResource(R.drawable.wrong);
                choise1Container.setClickable(false);
                choise2Container.setClickable(false);
                choise3Container.setClickable(false);
                choise4Container.setClickable(false);
            } else if (choice.equalsIgnoreCase(random[2])) {
                RW_1.setImageResource(R.drawable.wrong);
                RW_2.setImageResource(R.drawable.wrong);
                RW_3.setImageResource(R.drawable.right);
                RW_4.setImageResource(R.drawable.wrong);
                choise1Container.setClickable(false);
                choise2Container.setClickable(false);
                choise3Container.setClickable(false);
                choise4Container.setClickable(false);
            } else if (choice.equalsIgnoreCase(random[3])) {
                RW_1.setImageResource(R.drawable.wrong);
                RW_2.setImageResource(R.drawable.wrong);
                RW_3.setImageResource(R.drawable.wrong);
                RW_4.setImageResource(R.drawable.right);
                choise1Container.setClickable(false);
                choise2Container.setClickable(false);
                choise3Container.setClickable(false);
                choise4Container.setClickable(false);
            }

        } else if (choice.equalsIgnoreCase(s)) {
            result[no] = 0;
            lastcheck = true;
            clicks = 0;
            next.setClickable(true);
            if (count - 1 == no) {
                next.setImageResource(R.drawable.result);

            }

            if (choice.equalsIgnoreCase(random[0])) {
                RW_1.setImageResource(R.drawable.right);
                RW_2.setImageResource(R.drawable.wrong);
                RW_3.setImageResource(R.drawable.wrong);
                RW_4.setImageResource(R.drawable.wrong);
                choise1Container.setClickable(false);
                choise2Container.setClickable(false);
                choise3Container.setClickable(false);
                choise4Container.setClickable(false);

            } else if (choice.equalsIgnoreCase(random[1])) {
                RW_1.setImageResource(R.drawable.wrong);
                RW_2.setImageResource(R.drawable.right);
                RW_3.setImageResource(R.drawable.wrong);
                RW_4.setImageResource(R.drawable.wrong);
                choise1Container.setClickable(false);
                choise2Container.setClickable(false);
                choise3Container.setClickable(false);
                choise4Container.setClickable(false);
            } else if (choice.equalsIgnoreCase(random[2])) {
                RW_1.setImageResource(R.drawable.wrong);
                RW_2.setImageResource(R.drawable.wrong);
                RW_3.setImageResource(R.drawable.right);
                RW_4.setImageResource(R.drawable.wrong);
                choise1Container.setClickable(false);
                choise2Container.setClickable(false);
                choise3Container.setClickable(false);
                choise4Container.setClickable(false);
            } else if (choice.equalsIgnoreCase(random[3])) {
                RW_1.setImageResource(R.drawable.wrong);
                RW_2.setImageResource(R.drawable.wrong);
                RW_3.setImageResource(R.drawable.wrong);
                RW_4.setImageResource(R.drawable.right);
                choise1Container.setClickable(false);
                choise2Container.setClickable(false);
                choise3Container.setClickable(false);
                choise4Container.setClickable(false);
            }
        } else {
            result[no] = 0;
            if (choice.equalsIgnoreCase(random[0])) {

                RW_1.setImageResource(R.drawable.wrong);

            } else if (choice.equalsIgnoreCase(random[1])) {
                RW_2.setImageResource(R.drawable.wrong);
            } else if (choice.equalsIgnoreCase(random[2])) {
                RW_3.setImageResource(R.drawable.wrong);
            } else if (choice.equalsIgnoreCase(random[3])) {
                RW_4.setImageResource(R.drawable.wrong);
            }
        }
    }

    // When Home Button Pressed
    public void onHome(View v) {
        firstUpdateScore();
    }

    // When Next Button Pressed
    public void onNext(View v) {
        no++;
        pic.setImageResource(R.drawable.nn);
        int n2 = no + 1;

        set = n2 + "/" + count;

        // if qustionID = last question then show the result button else show
        // the next question

        if (count - 1 == no) {
            next.setImageResource(R.drawable.result);
        }

        if (count == no) {
            for (int i = 0; i < result.length; i++) {
                corrects += result[i];
            }

            last = 5;
            wrong_s = String.valueOf(count - corrects);
            right_s = String.valueOf(corrects);

            ResultDialogHelper.showResultDialog(Cat_QuestionsActivity.this, right_s, wrong_s, value, () -> {
                Intent i = CategoriesActivity.createIntent(this, value.equalsIgnoreCase(CategoryKey.Exam));
                startActivity(i);
            });
        } else {

            randomAnswers();

            question_count.setText("(" + set + ")");
//            if (value.equalsIgnoreCase(CategoryKey.Exam))
//                category.setText(Localizer.getString(CategoryKey.Exam));
//            else
//                category.setText(Localizer.getString(All_Question.get(no).Category));

            if (value.equalsIgnoreCase(CategoryKey.Exam)) {
                if (AppSharedMethod.isEnglishLayout(Cat_QuestionsActivity.this)) {
                    category.setText(PSTARApp.getCategoryEnHashMap().get(CategoryKey.Exam));
                } else {
                    category.setText(PSTARApp.getCategoryFRHashMap().get(CategoryKey.Exam));
                }

            } else {
                if (AppSharedMethod.isEnglishLayout(Cat_QuestionsActivity.this)) {
                    category.setText(PSTARApp.getCategoryEnHashMap().get(All_Question.get(no).Category));
                } else {
                    category.setText(PSTARApp.getCategoryFRHashMap().get(All_Question.get(no).Category));
                }
            }


            if (controlBack == 0) {
                String ss = All_Question.get(no).Qno;
                question.setText(All_Question.get(no).Question);
                choice1.setText(random[0]);
                choice2.setText(random[1]);
                choice3.setText(random[2]);
                choice4.setText(random[3]);
                enableRW();
                lastcheck = false;


            } else if (controlBack == 1) {
                controlBack--;
                //	lastcheck2=controlBack;
                question.setText(All_Question.get(no).Question);
                choice1.setText(random[0]);
                choice2.setText(random[1]);
                choice3.setText(random[2]);
                choice4.setText(random[3]);
                if (lastcheck) {
                    lastcheck = false;
                    enableRW2();
                    if (count - 1 == no) {
                        next.setImageResource(R.drawable.result);
                        lastcheck = true;
                    }
                } else {
                    lastcheck = false;
                    enableRW();
                }
            } else {
                controlBack--;
                question.setText(All_Question.get(no).Question);
                choice1.setText(All_Question.get(no).A);
                choice2.setText(All_Question.get(no).B);
                choice3.setText(All_Question.get(no).C);
                choice4.setText(All_Question.get(no).D);
                enableRW2();


            }


        }


    }

    // Sentence Case Method
    public String SentenseCase(String word) {
        String firstLetter = word.substring(0, 1); // Get first letter
        String remainder = word.substring(1); // Get remainder of word.
        String capitalized = firstLetter.toUpperCase()
                + remainder;

        return capitalized;
    }

    // When Device Back Button Pressed Load Previous Question
    @Override
    public void onBackPressed() {

        pic.setImageResource(R.drawable.nn);
        if (no == 0) {

        } else {


            next.setImageResource(R.drawable.next_btn);
            controlBack++;
            no--;
            int n2 = no + 1;

            set = n2 + "/" + count;
            question_count.setText("(" + set + ")");

//            if (value.equalsIgnoreCase(CategoryKey.Exam))
//                category.setText(Localizer.getString(CategoryKey.Exam));
//            else
//                category.setText(Localizer.getString(All_Question.get(no).Category));

            if (value.equalsIgnoreCase(CategoryKey.Exam)) {
                if (AppSharedMethod.isEnglishLayout(Cat_QuestionsActivity.this)) {
                    category.setText(PSTARApp.getCategoryEnHashMap().get(CategoryKey.Exam));
                } else {
                    category.setText(PSTARApp.getCategoryFRHashMap().get(CategoryKey.Exam));
                }

            } else {
                if (AppSharedMethod.isEnglishLayout(Cat_QuestionsActivity.this)) {
                    category.setText(PSTARApp.getCategoryEnHashMap().get(All_Question.get(no).Category));
                } else {
                    category.setText(PSTARApp.getCategoryFRHashMap().get(All_Question.get(no).Category));
                }
            }


            question.setText(All_Question.get(no).Question);
            choice1.setText(All_Question.get(no).A);
            choice2.setText(All_Question.get(no).B);
            choice3.setText(All_Question.get(no).C);
            choice4.setText(All_Question.get(no).D);
            enableRW2();
        }

        return;
    }

    // update score before leaving the quiz
    public void firstUpdateScore() {
        DBAdapter openHelper = new DBAdapter(this);
        SQLiteDatabase db = openHelper.getWritableDatabase();

        for (int i = 0; i < result.length; i++) {
            corrects += result[i];
        }

        wrong_s = String.valueOf(count - corrects);
        right_s = String.valueOf(corrects);

        String mScoreTableName = "";
        if (AppSharedMethod.isEnglishLayout(Cat_QuestionsActivity.this)) {
            mScoreTableName = ScoresEnContractClass.ScoreEnEntry.TABLE_NAME;
        } else {
            mScoreTableName = AppConstants.TABLE_SCORE_FR_NAME;
        }
        DatabaseHelper.updateScores(db, value, right_s, mScoreTableName);

        db.close();

        Intent i = new Intent(getBaseContext(), CategoriesActivity.class);
        startActivity(i);
    }

    //THis Method Use to make the choices of Question Random
    public void randomAnswers() {
        random = new String[4];
        random[0] = All_Question.get(no).A;
        random[1] = All_Question.get(no).B;
        random[2] = All_Question.get(no).C;
        random[3] = All_Question.get(no).D;
        Collections.shuffle(Arrays.asList(random));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase, ""));
    }
}
