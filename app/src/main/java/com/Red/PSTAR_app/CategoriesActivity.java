package com.Red.PSTAR_app;

import static database.DBConstants.TABLE_SCORE_EN;
import static database.DBConstants.TABLE_SCORE_FR;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.Red.PSTAR_app.helpers.RateUsHelper;
import com.Red.PSTAR_app.localization.CategoryKey;
import com.Red.PSTAR_app.utils.AppConstants;
import com.Red.PSTAR_app.utils.AppSharedMethod;
import com.Red.PSTAR_app.utils.IabHelper;
import com.Red.PSTAR_app.utils.IabResult;
import com.Red.PSTAR_app.utils.Inventory;
import com.Red.PSTAR_app.utils.MyContextWrapper;
import com.Red.PSTAR_app.utils.Purchase;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;

import database.DBAdapter;
import database.DBConstants;
import database.DatabaseHelper;

public class CategoriesActivity extends Activity implements OnClickListener {

    private final String TAG = CategoriesActivity.class.getSimpleName();
    private static final String ExtraRatePopup = "extra_rate_popup";
    private List<Score> score;
    private SQLiteDatabase mDb;
    private boolean isPaidUser = false;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private int mCurrentScoreIndex = 0;
    private String mCurrentCategoryKey = "";


    private String inAppKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqgZzIGmF6MBDw89t1Xq8jm1ZNd5DtT2DBiVp8+jcv4zUvGwuJdE7imE5r2EQqnMzXxxaL+BVSjouvBHfuR11J+sTw3MF9nqUxtUEzqo8DkCr7eZo8nNolafVt4Rfb8cvdI+PidpfxSac5P7qzYn0gRwXHRfmwMdsslwSA1KFJH1+Yq9SbW8kPIUiavQZN47vq+iWmWqwn/tLjwu9piFJr5Fn7J2seVkhcVQRbfRBQYtF0aUC4gxMLovqV70d/e8PQNOAG5KllntoAPup9SF4ljpFSr94lpBlxVojhayAuN/Mdasls9X6EB1h2UxxKivtcnkYV7zERMR4YJhNS7vZRwIDAQAB";
    private IabHelper mHelper;
    private String mSetupMessage = "";
    private final int RC_REQUEST = 10001;
    private boolean isSetup = false;

    private ImageView mCatCollisionImageView;
    private ImageView mCatSignalImageView;
    private ImageView mCatCommunicationImageView;
    private ImageView mCatAerodromesImageView;
    private ImageView mCatEquipmentsImageView;
    private ImageView mCatPilotResponsibilityImageView;
    private ImageView mCatWakeTurboImageView;
    private ImageView mCatAeromedicalImageView;
    private ImageView mCatFlightPlansImageView;
    private ImageView mCatClearenceInstructionsImageView;
    private ImageView mCatAircraftTopsImageView;
    private ImageView mCatGeneralAirspImageView;
    private ImageView mCatControlledAirspImageView;
    private ImageView mCatAviationOccurrencesImageView;
    private ImageView mCatExamsImageView;
    private ImageView mAbbreviationsImageView;
    private BillingClient billingClient;

    public static Intent createIntent(Context context, boolean showRate) {
        Intent intent = new Intent(context, CategoriesActivity.class);
        intent.putExtra(ExtraRatePopup, showRate);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories);
        initView();
        initList();
        initializePreferences();
        getAllScore();
        setScores();

        if (isRatePopupRequested()) {
            Handler handler = new Handler();
            handler.postDelayed(() -> RateUsHelper.showIfNeed(CategoriesActivity.this), 1000);
        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            setupPurchase();
        } else {
            setupPurchaseNew();
        }
    }

    private void setupPurchaseNew() {
        billingClient = BillingClient.newBuilder(this).setListener(purchasesUpdatedListener).enablePendingPurchases().build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.e(TAG, "onBillingSetupFinished: ");
                    queryPurchases();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });


    }

    ProductDetails productDetails;

    void queryPurchases() {
        QueryProductDetailsParams queryProductDetailsParams = QueryProductDetailsParams.newBuilder().setProductList(Collections.singletonList(QueryProductDetailsParams.Product.newBuilder().setProductId(AppConstants.SKU_LIFE_TIME_PACKAGE).setProductType(BillingClient.ProductType.INAPP).build())).build();

        billingClient.queryProductDetailsAsync(queryProductDetailsParams, (billingResult, productDetailsList) -> {
            // check billingResult
            // process returned productDetailsList
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                productDetails = productDetailsList.get(0);

                Log.e(TAG, productDetailsList.size() + " setupPurchaseNew: " + productDetailsList.get(0).toString());
                //This list should contain the products added above

            }
        });
    }

    private final PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (com.android.billingclient.api.Purchase purchase : purchases) {
//                        handleNonConcumablePurchase(purchase)
                Log.e(TAG, "Purchase purchase : " + purchase.getProducts());
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    };

    private void initView() {
        mAbbreviationsImageView = findViewById(R.id.btn_abrivation);
        mCatCollisionImageView = findViewById(R.id.img_cat_1);
        mCatSignalImageView = findViewById(R.id.img_cat2);
        mCatCommunicationImageView = findViewById(R.id.img_cat3);
        mCatAerodromesImageView = findViewById(R.id.img_cat4);
        mCatEquipmentsImageView = findViewById(R.id.img_cat5);
        mCatPilotResponsibilityImageView = findViewById(R.id.img_cat6);
        mCatWakeTurboImageView = findViewById(R.id.img_cat7);
        mCatAeromedicalImageView = findViewById(R.id.img_cat8);
        mCatFlightPlansImageView = findViewById(R.id.img_cat9);
        mCatClearenceInstructionsImageView = findViewById(R.id.img_cat10);
        mCatAircraftTopsImageView = findViewById(R.id.img_cat11);
        mCatGeneralAirspImageView = findViewById(R.id.img_cat12);
        mCatControlledAirspImageView = findViewById(R.id.img_cat13);
        mCatAviationOccurrencesImageView = findViewById(R.id.img_cat14);
        mCatExamsImageView = findViewById(R.id.img_cat15);
    }

    private void initList() {
        mAbbreviationsImageView.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), AbbreviationsActivity.class);
            startActivity(i);
        });
        mCatCollisionImageView.setOnClickListener(this);
        mCatSignalImageView.setOnClickListener(this);
        mCatCommunicationImageView.setOnClickListener(this);
        mCatAerodromesImageView.setOnClickListener(this);
        mCatEquipmentsImageView.setOnClickListener(this);
        mCatPilotResponsibilityImageView.setOnClickListener(this);
        mCatWakeTurboImageView.setOnClickListener(this);
        mCatAeromedicalImageView.setOnClickListener(this);
        mCatFlightPlansImageView.setOnClickListener(this);
        mCatClearenceInstructionsImageView.setOnClickListener(this);
        mCatAircraftTopsImageView.setOnClickListener(this);
        mCatGeneralAirspImageView.setOnClickListener(this);
        mCatControlledAirspImageView.setOnClickListener(this);
        mCatAviationOccurrencesImageView.setOnClickListener(this);
        mCatExamsImageView.setOnClickListener(this);
    }

    public void onCollision(View v) {
        Intent i = new Intent(getBaseContext(), Cat_QuestionsActivity.class);
        startActivity(i);
    }

    private void initializePreferences() {
        mSharedPreferences = getSharedPreferences(AppConstants.MY_PREF, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    private boolean isPremiumUser() {
//        return true;
        return mSharedPreferences.getBoolean(AppConstants.PREF_IS_PREMIUM_USER, false);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "isPremiumUser>" + isPremiumUser() + "isPaidUser>" + isPaidUser);
        switch (v.getId()) {
            case R.id.img_cat_1:
                categoryClickImplementations(0, CategoryKey.COLLISION_AVOIDANCE);
                break;
            case R.id.img_cat2:
                categoryClickImplementations(1, CategoryKey.VISUAL_SIGNALS);
                break;
            case R.id.img_cat3:
                categoryClickImplementations(2, CategoryKey.COMMUNICATIONS);
                break;
            case R.id.img_cat4:
                categoryClickImplementations(3, CategoryKey.AERODROMES);
                break;
            case R.id.img_cat5:
                mCurrentScoreIndex = 4;
                mCurrentCategoryKey = CategoryKey.EQUIPMENT;
                if (!(isPremiumUser() || isPaidUser)) {
                    showInAppBillingMessageDialog();
                } else categoryClickImplementations(4, CategoryKey.EQUIPMENT);
                break;
            case R.id.img_cat6:
                mCurrentScoreIndex = 5;
                mCurrentCategoryKey = CategoryKey.PILOT_RESPONSIBILITIES;
                if (!(isPremiumUser() || isPaidUser)) {
                    showInAppBillingMessageDialog();
                } else categoryClickImplementations(5, CategoryKey.PILOT_RESPONSIBILITIES);
                break;
            case R.id.img_cat7:
                mCurrentScoreIndex = 6;
                mCurrentCategoryKey = CategoryKey.WAKE_TURBULENCE;
                if (!(isPremiumUser() || isPaidUser)) {
                    showInAppBillingMessageDialog();
                } else categoryClickImplementations(6, CategoryKey.WAKE_TURBULENCE);
                break;
            case R.id.img_cat8:
                mCurrentScoreIndex = 7;
                mCurrentCategoryKey = CategoryKey.AEROMEDICAL;
                if (!(isPremiumUser() || isPaidUser)) {
                    showInAppBillingMessageDialog();
                } else categoryClickImplementations(7, CategoryKey.AEROMEDICAL);
                break;
            case R.id.img_cat9:
                mCurrentScoreIndex = 8;
                mCurrentCategoryKey = CategoryKey.FLIGHT_PLANS;
                if (!(isPremiumUser() || isPaidUser)) {
                    showInAppBillingMessageDialog();
                } else categoryClickImplementations(8, CategoryKey.FLIGHT_PLANS);
                break;
            case R.id.img_cat10:
                mCurrentScoreIndex = 9;
                mCurrentCategoryKey = CategoryKey.CLEARANCES_AND_INSTRUCTIONS;
                if (!(isPremiumUser() || isPaidUser)) {
                    showInAppBillingMessageDialog();
                } else categoryClickImplementations(9, CategoryKey.CLEARANCES_AND_INSTRUCTIONS);
                break;
            case R.id.img_cat11:
                mCurrentScoreIndex = 10;
                mCurrentCategoryKey = CategoryKey.AIRCRAFT_OPERATIONS;
                if (!(isPremiumUser() || isPaidUser)) {
                    showInAppBillingMessageDialog();
                } else categoryClickImplementations(10, CategoryKey.AIRCRAFT_OPERATIONS);
                break;
            case R.id.img_cat12:
                mCurrentScoreIndex = 11;
                mCurrentCategoryKey = CategoryKey.GENERAL_AIRSPACE;
                if (!(isPremiumUser() || isPaidUser)) {
                    showInAppBillingMessageDialog();
                } else categoryClickImplementations(11, CategoryKey.GENERAL_AIRSPACE);
                break;
            case R.id.img_cat13:
                mCurrentScoreIndex = 12;
                mCurrentCategoryKey = CategoryKey.CONTROLLED_AIRSPACE;
                if (!(isPremiumUser() || isPaidUser)) {
                    showInAppBillingMessageDialog();
                } else categoryClickImplementations(12, CategoryKey.CONTROLLED_AIRSPACE);
                break;
            case R.id.img_cat14:
                mCurrentScoreIndex = 13;
                mCurrentCategoryKey = CategoryKey.AVIATION_OCCURRENCES;
                if (!(isPremiumUser() || isPaidUser)) {
                    showInAppBillingMessageDialog();
                } else categoryClickImplementations(13, CategoryKey.AVIATION_OCCURRENCES);
                break;
            case R.id.img_cat15: {
                mCurrentScoreIndex = 14;
                mCurrentCategoryKey = CategoryKey.Exam;
                if (!(isPremiumUser() || isPaidUser)) {
                    showInAppBillingMessageDialog();
                } else {
                    goToExamScreen();
                    break;
                }
            }
        }
    }

    private void categoryClickImplementations(int scoreIndex, String categoryKey) {
        Log.e(TAG, categoryKey + " categoryClickImplementations: " + scoreIndex);
        if ((score.get(scoreIndex).right.equalsIgnoreCase(score.get(scoreIndex).total))) {
            resetScoresDialog(categoryKey);
        } else {
            Intent intent = Cat_QuestionsActivity.createIntent(this, categoryKey);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent i = new Intent(getBaseContext(), PSTAR_appActivity.class);
            startActivity(i);
        }
        return super.onKeyDown(keyCode, event);

    }

    private void getAllScore() {
        DBAdapter openHelper = DBAdapter.getInstance(this);
        mDb = openHelper.getWritableDatabase();

//        TODO 18-02-2019
        String mTableName = "";
        if (AppSharedMethod.isEnglishLayout(CategoriesActivity.this)) {
            mTableName = TABLE_SCORE_EN;
        } else {
            mTableName = TABLE_SCORE_FR;
        }
        score = DatabaseHelper.getScores(mDb, mTableName);

        validatePaidUser();

        mDb.close();
    }

    @SuppressLint("SetTextI18n")
    public void setScores() {
        setCategoryCount(R.id.score1, 0);
        setCategoryCount(R.id.score2, 1);
        setCategoryCount(R.id.score3, 2);
        setCategoryCount(R.id.score4, 3);
        setCategoryCount(R.id.score5, 4);
        setCategoryCount(R.id.score6, 5);
        setCategoryCount(R.id.score7, 6);
        setCategoryCount(R.id.score8, 7);
        setCategoryCount(R.id.score9, 8);
        setCategoryCount(R.id.score10, 9);
        setCategoryCount(R.id.score11, 10);
        setCategoryCount(R.id.score12, 11);
        setCategoryCount(R.id.score13, 12);
        setCategoryCount(R.id.score14, 13);

        //counting Percentage score for category_ALL
        TextView score15 = findViewById(R.id.score15);
        float rightAnswers = Float.parseFloat(score.get(14).right);
        float totalQuestions = Float.parseFloat(score.get(14).total);

        float average = ((rightAnswers / totalQuestions) * 100);
        String percentage = String.valueOf((int) average);
        score15.setText(percentage + "%");

        if (AppSharedMethod.isEnglishLayout(CategoriesActivity.this)) {
            mCatCollisionImageView.setImageResource(setCategoryGoldImage(0) ? R.drawable.collision_avoidance_en_gold : R.drawable.collision_avoidance_en);
            mCatSignalImageView.setImageResource(setCategoryGoldImage(1) ? R.drawable.visual_signals_en_gold : R.drawable.visual_signals_en);
            mCatCommunicationImageView.setImageResource(setCategoryGoldImage(2) ? R.drawable.comms_en_gold : R.drawable.comms_en);
            mCatAerodromesImageView.setImageResource(setCategoryGoldImage(3) ? R.drawable.aerodromes_en_gold : R.drawable.aerodromes_en);
            mCatEquipmentsImageView.setImageResource(setCategoryGoldImage(4) ? R.drawable.equipments_en_gold : R.drawable.equipments_en);
            mCatPilotResponsibilityImageView.setImageResource(setCategoryGoldImage(5) ? R.drawable.pilot_responsib_en_gold : R.drawable.pilot_responsib_en);
            mCatWakeTurboImageView.setImageResource(setCategoryGoldImage(6) ? R.drawable.wake_turb_en_gold : R.drawable.wake_turb_en);
            mCatAeromedicalImageView.setImageResource(setCategoryGoldImage(7) ? R.drawable.aeromedical_en_gold : R.drawable.aeromedical_en);
            mCatFlightPlansImageView.setImageResource(setCategoryGoldImage(8) ? R.drawable.flight_plans_en_gold : R.drawable.flight_plans_en);
            mCatClearenceInstructionsImageView.setImageResource(setCategoryGoldImage(9) ? R.drawable.clearence_instructions_en_gold : R.drawable.clearence_instructions_en);
            mCatAircraftTopsImageView.setImageResource(setCategoryGoldImage(10) ? R.drawable.aircraftops_en_gold : R.drawable.aircraftops_en);
            mCatGeneralAirspImageView.setImageResource(setCategoryGoldImage(11) ? R.drawable.general_airsp_en_gold : R.drawable.general_airsp_en);
            mCatControlledAirspImageView.setImageResource(setCategoryGoldImage(12) ? R.drawable.contr_airsp_en_gold : R.drawable.contr_airsp_en);
            mCatAviationOccurrencesImageView.setImageResource(setCategoryGoldImage(13) ? R.drawable.aviation_occurrences_en_gold : R.drawable.aviation_occurrences_en);
            mCatExamsImageView.setImageResource(setCategoryGoldImage(14) ? R.drawable.question_mark_en_gold : R.drawable.question_mark_en);
        } else {
            mCatCollisionImageView.setImageResource(setCategoryGoldImage(0) ? R.drawable.collision_avoidance_fr_gold : R.drawable.collision_avoidance_fr);
            mCatSignalImageView.setImageResource(setCategoryGoldImage(1) ? R.drawable.visual_signals_fr_gold : R.drawable.visual_signals_fr);
            mCatCommunicationImageView.setImageResource(setCategoryGoldImage(2) ? R.drawable.comms_fr_gold : R.drawable.comms_fr);
            mCatAerodromesImageView.setImageResource(setCategoryGoldImage(3) ? R.drawable.aerodromes_fr_gold : R.drawable.aerodromes_fr);
            mCatEquipmentsImageView.setImageResource(setCategoryGoldImage(4) ? R.drawable.equipments_fr_gold : R.drawable.equipments_fr);
            mCatPilotResponsibilityImageView.setImageResource(setCategoryGoldImage(5) ? R.drawable.pilot_responsib_fr_gold : R.drawable.pilot_responsib_fr);
            mCatWakeTurboImageView.setImageResource(setCategoryGoldImage(6) ? R.drawable.wake_turb_fr_gold : R.drawable.wake_turb_fr);
            mCatAeromedicalImageView.setImageResource(setCategoryGoldImage(7) ? R.drawable.aeromedical_fr_gold : R.drawable.aeromedical_fr);
            mCatFlightPlansImageView.setImageResource(setCategoryGoldImage(8) ? R.drawable.flight_plans_fr_gold : R.drawable.flight_plans_fr);
            mCatClearenceInstructionsImageView.setImageResource(setCategoryGoldImage(9) ? R.drawable.clearence_instructions_fr_gold : R.drawable.clearence_instructions_fr);
            mCatAircraftTopsImageView.setImageResource(setCategoryGoldImage(10) ? R.drawable.aircraftops_fr_gold : R.drawable.aircraftops_fr);
            mCatGeneralAirspImageView.setImageResource(setCategoryGoldImage(11) ? R.drawable.general_airsp_fr_gold : R.drawable.general_airsp_fr);
            mCatControlledAirspImageView.setImageResource(setCategoryGoldImage(12) ? R.drawable.contr_airsp_fr_gold : R.drawable.contr_airsp_fr);
            mCatAviationOccurrencesImageView.setImageResource(setCategoryGoldImage(13) ? R.drawable.aviation_occurrences_fr_gold : R.drawable.aviation_occurrences_fr);
            mCatExamsImageView.setImageResource(setCategoryGoldImage(14) ? R.drawable.question_mark_fr_gold : R.drawable.question_mark_fr);
        }
    }

    private void goToExamScreen() {
        Intent i = Cat_QuestionsActivity.createIntent(this, CategoryKey.Exam);
        startActivity(i);
    }

    public void onReset(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset");
        builder.setMessage("Reset All Categories ?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            DBAdapter openHelper = DBAdapter.getInstance(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();


            String mTableName = "";
            if (AppSharedMethod.isEnglishLayout(CategoriesActivity.this)) {
                mTableName = TABLE_SCORE_EN;
            } else {
                mTableName = TABLE_SCORE_FR;
            }

            DatabaseHelper.resetAllScores(db, mTableName);

            db.close();

            Intent i = new Intent(getBaseContext(), CategoriesActivity.class);
            startActivity(i);
        });

        builder.show();
    }

    public void resetScoresDialog(String categoryKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset");
        builder.setMessage("Reset and Start again?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            DBAdapter openHelper = DBAdapter.getInstance(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();

//            TODO 18-02-2019
            String mTableName = "";
            if (AppSharedMethod.isEnglishLayout(CategoriesActivity.this)) {
                mTableName = TABLE_SCORE_EN;
            } else {
                mTableName = TABLE_SCORE_FR;
            }
            DatabaseHelper.resetScores(db, categoryKey, mTableName);

            db.close();

            Intent i = new Intent(getBaseContext(), CategoriesActivity.class);
            startActivity(i);
        });

        builder.show();
    }

    private boolean isRatePopupRequested() {
        Intent intent = getIntent();
        if (intent == null) return false;

        return intent.getBooleanExtra(ExtraRatePopup, false);
    }

    private void setCategoryCount(int textId, int index) {
        TextView categoryText = findViewById(textId);

        categoryText.setText(String.format("%s/%s", score.get(index).right, score.get(index).total));
    }

    private boolean setCategoryGoldImage(int index) {

        return score.get(index).right.equalsIgnoreCase(score.get(index).total);
    }

    private void validatePaidUser() {
        Cursor cursor = getPaidUser();
        if (cursor.moveToFirst()) {
            isPaidUser = true;
            Log.d(TAG, "isPremiumUser");
        }
        cursor.close();
    }

    private Cursor getPaidUser() {
        return mDb.query(DBConstants.TABLE_USER, null, null, null, null, null, null);
    }

    private void setupPurchase() {

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(CategoriesActivity.this, inAppKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(result -> {
            if (!result.isSuccess()) {

                mSetupMessage = result.toString();
                Log.d(TAG, result.getMessage() + " " + result.getResponse());

                return;
            }
            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) {

                return;
            }

            isSetup = true;

            Log.d(TAG, "In app Billing Setup Successfully !");

            try {
                mHelper.queryInventoryAsync(mGotInventoryPremiumListener);
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getClass().getName(), "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (result.isFailure()) {

            } else {

                if (inventory.hasPurchase(AppConstants.SKU_LIFE_TIME_PACKAGE)) {

                    mEditor.putBoolean(AppConstants.PREF_IS_PREMIUM_USER, true).apply();
                    if (mCurrentScoreIndex != 14) {
                        categoryClickImplementations(mCurrentScoreIndex, mCurrentCategoryKey);
                    } else {
                        goToExamScreen();
                    }
                } else {

                    mEditor.putBoolean(AppConstants.PREF_IS_PREMIUM_USER, false).apply();

                    try {
                        mHelper.launchPurchaseFlow(CategoriesActivity.this, AppConstants.SKU_LIFE_TIME_PACKAGE, RC_REQUEST, mPurchaseFinishedListener, "");

                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


    IabHelper.QueryInventoryFinishedListener mGotInventoryPremiumListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (result.isFailure()) {

            } else {

                if (inventory.hasPurchase(AppConstants.SKU_LIFE_TIME_PACKAGE)) {

                    mEditor.putBoolean(AppConstants.PREF_IS_PREMIUM_USER, true).apply();
                } else {
                    mEditor.putBoolean(AppConstants.PREF_IS_PREMIUM_USER, false).apply();
                }
            }
        }
    };


    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {


            if (result.isFailure()) {

                return;
            }


            if (mHelper == null) {
                return;
            }

            mEditor.putBoolean(AppConstants.PREF_IS_PREMIUM_USER, true).apply();

            if (mCurrentScoreIndex != 14) {
                categoryClickImplementations(mCurrentScoreIndex, mCurrentCategoryKey);
            } else {
                goToExamScreen();
            }
        }
    };

    public void showInAppBillingMessageDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_premium_purchase_hint);

        TextView mTitleTextView = dialog.findViewById(R.id.textView_dialog_purchase_title);
        TextView mMessageTextView = dialog.findViewById(R.id.textView_dialog_purchase_message);
        Button mContinueButton = dialog.findViewById(R.id.button_confirm);
        TextView mCancelTextView = dialog.findViewById(R.id.button_cancel);
        TextView mPromotionTextView = dialog.findViewById(R.id.textView_dialog_purchase_promotion);


        if (AppSharedMethod.isFrenchLayout(CategoriesActivity.this)) {
            int mainTextSize = 24;
            int subTextSize = 19;
            Configuration config = getResources().getConfiguration();
            if (config.smallestScreenWidthDp <= 320) {
                mainTextSize = 22;
                subTextSize = 17;
            } else if (config.smallestScreenWidthDp <= 360) {
                mainTextSize = 23;
                subTextSize = 18;
            } else if (config.smallestScreenWidthDp <= 400) {
                mainTextSize = 23;
                subTextSize = 18;
            } else if (config.smallestScreenWidthDp <= 480) {
                mainTextSize = 24;
                subTextSize = 19;
            } else {
                mainTextSize = 25;
                subTextSize = 20;
            }

            mTitleTextView.setTextSize(mainTextSize);
            mMessageTextView.setTextSize(subTextSize);
            mContinueButton.setTextSize(mainTextSize);
            mCancelTextView.setTextSize(subTextSize);
        }
        mContinueButton.setOnClickListener(view -> {

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                try {
                    if (isSetup) {
                        mHelper.queryInventoryAsync(mGotInventoryListener);
                    } else {
                        Toast.makeText(getApplicationContext(), mSetupMessage, Toast.LENGTH_LONG).show();
                    }

                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            } else {
                ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = com.google.common.collect.ImmutableList.of(BillingFlowParams.ProductDetailsParams.newBuilder()
                        // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                        .setProductDetails(productDetails)
                        // to get an offer token, call ProductDetails.getSubscriptionOfferDetails()
                        // for a list of offers that are available to the user
                        .build());
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList).build();

// Launch the billing flow
                BillingResult billingResult = billingClient.launchBillingFlow(this, billingFlowParams);

            }
//            Log.d(TAG, "Start In App Billing");


            dialog.dismiss();

        });
        mCancelTextView.setOnClickListener(view -> dialog.dismiss());


        dialog.show();

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        int x = (int) (point.x * 1);
        int y = (int) (point.y * 1);

        dialog.setCanceledOnTouchOutside(false);

        dialog.getWindow().setLayout(x, y);

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase, ""));
    }
}