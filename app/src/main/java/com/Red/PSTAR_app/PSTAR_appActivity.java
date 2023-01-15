package com.Red.PSTAR_app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.Red.PSTAR_app.utils.AppConstants;
import com.Red.PSTAR_app.utils.IabHelper;
import com.Red.PSTAR_app.utils.IabResult;
import com.Red.PSTAR_app.utils.Inventory;
import com.Red.PSTAR_app.utils.MyContextWrapper;
import com.Red.PSTAR_app.utils.MyExceptionHandler;
import com.Red.PSTAR_app.utils.PSTARApp;
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
import java.util.HashMap;

import database.DBAdapter;
import database.UserIdContractClass;

public class PSTAR_appActivity extends Activity {

    private final String TAG = PSTAR_appActivity.class.getSimpleName();

    private SQLiteDatabase mDb;
    private final Integer mLastPaidAppVersionCode = 8;
    private boolean isPaidUser = false;


    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private String inAppKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqgZzIGmF6MBDw89t1Xq8jm1ZNd5DtT2DBiVp8+jcv4zUvGwuJdE7imE5r2EQqnMzXxxaL+BVSjouvBHfuR11J+sTw3MF9nqUxtUEzqo8DkCr7eZo8nNolafVt4Rfb8cvdI+PidpfxSac5P7qzYn0gRwXHRfmwMdsslwSA1KFJH1+Yq9SbW8kPIUiavQZN47vq+iWmWqwn/tLjwu9piFJr5Fn7J2seVkhcVQRbfRBQYtF0aUC4gxMLovqV70d/e8PQNOAG5KllntoAPup9SF4ljpFSr94lpBlxVojhayAuN/Mdasls9X6EB1h2UxxKivtcnkYV7zERMR4YJhNS7vZRwIDAQAB";
    private IabHelper mHelper;
    private String mSetupMessage = "";
    private boolean isSetup = false;
    private final int RC_REQUEST = 10001;
    private Intent mCurrentIntent = null;
    private String mLang = "";
    private HashMap<String, String> mPremiumDialogEnHashMap = new HashMap<>();
    private HashMap<String, String> mPremiumDialogFRHashMap = new HashMap<>();
    private BillingClient billingClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
        if (getIntent() != null) {
            if (getIntent().hasExtra("crash")) {
                Log.d(TAG, "onCreate: " + getIntent().getBooleanExtra("crash", false));
                clearAppData();
            }
        }



        initializePreferences();
        validatePaidUser();
        setUpPremiumDialogMessageString();
        setContentView(R.layout.main);
        setCallbacks();
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            setupPurchase();
        } else {
            setupPurchaseNew();
        }
    }

    private void validatePaidUser() {
        DBAdapter mDbAdapter = new DBAdapter(this);
        mDb = mDbAdapter.getWritableDatabase();
        Cursor cursor = getPaidUser();
        if (!cursor.moveToFirst()) {
        } else {
            Log.d(TAG, "isPremiumUser");
            isPaidUser = true;
        }
        cursor.close();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Log.d(TAG, "attachBaseContext:called ");
        super.attachBaseContext(MyContextWrapper.wrap(newBase, mLang));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            moveTaskToBack(true);
        }
        return true;
    }

    private void initializePreferences() {
        mSharedPreferences = getSharedPreferences(AppConstants.MY_PREF, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    private boolean isPremiumUser() {
        return mSharedPreferences.getBoolean(AppConstants.PREF_IS_PREMIUM_USER, false);
    }


    private void setCallbacks() {
        View startButtonEn = findViewById(R.id.img_start_en);
        startButtonEn.setOnClickListener(v -> {

            PSTARApp.getInstance().getSharedPreferences(AppConstants.MY_PREF, MODE_PRIVATE).edit().putString(AppConstants.PREF_LANG, AppConstants.EN).apply();

            Intent i = new Intent(getBaseContext(), CategoriesActivity.class);
            startActivity(i);
        });

        View startButtonFr = findViewById(R.id.img_start_fr);
        startButtonFr.setOnClickListener(v -> {

            PSTARApp.getInstance().getSharedPreferences(AppConstants.MY_PREF, MODE_PRIVATE).edit().putString(AppConstants.PREF_LANG, AppConstants.FR).apply();

            Intent i = new Intent(getBaseContext(), CategoriesActivity.class);
            startActivity(i);
        });


        View altpEn = findViewById(R.id.img_alpt_en);
        altpEn.setOnClickListener(v -> {
//            throw new RuntimeException("Test Crash"); // Force a crash

            PSTARApp.getInstance().getSharedPreferences(AppConstants.MY_PREF, MODE_PRIVATE).edit().putString(AppConstants.PREF_LANG, AppConstants.EN).apply();

            if (isPaidUser || isPremiumUser()) {
                Log.d(TAG, "isPremiumUser>" + isPremiumUser() + "isPadUser>" + isPaidUser);
                Intent i = ViewPdfActivity.createIntent(getBaseContext(), "alpt_en.pdf");
                startActivity(i);
            } else {
                mCurrentIntent = ViewPdfActivity.createIntent(getBaseContext(), "alpt_en.pdf");
//                Intent i = ViewPdfActivity.createIntent(getBaseContext(), "alpt_en.pdf");
//                startActivity(i);
                showInAppBillingMessageDialog(AppConstants.EN);
            }
        });

        View altpFr = findViewById(R.id.img_alpt_fr);
        altpFr.setOnClickListener(v -> {

            PSTARApp.getInstance().getSharedPreferences(AppConstants.MY_PREF, MODE_PRIVATE).edit().putString(AppConstants.PREF_LANG, AppConstants.FR).apply();

            if (isPaidUser || isPremiumUser()) {
                Intent i = ViewPdfActivity.createIntent(getBaseContext(), "alpt_fr.pdf");
                startActivity(i);
            } else {
                mCurrentIntent = ViewPdfActivity.createIntent(getBaseContext(), "alpt_fr.pdf");
//                Intent i = ViewPdfActivity.createIntent(getBaseContext(), "alpt_fr.pdf");
//                startActivity(i);
                showInAppBillingMessageDialog(AppConstants.FR);
            }
        });

        View guideEn = findViewById(R.id.img_guide_en);
        guideEn.setOnClickListener(v -> {

            PSTARApp.getInstance().getSharedPreferences(AppConstants.MY_PREF, MODE_PRIVATE).edit().putString(AppConstants.PREF_LANG, AppConstants.EN).apply();

            if (isPaidUser || isPremiumUser()) {
                Intent i = ViewPdfActivity.createIntent(getBaseContext(), "guide_en.pdf");
                startActivity(i);
            } else {
                mCurrentIntent = ViewPdfActivity.createIntent(getBaseContext(), "guide_en.pdf");
//                Intent i = ViewPdfActivity.createIntent(getBaseContext(), "guide_en.pdf");
//                startActivity(i);
                showInAppBillingMessageDialog(AppConstants.EN);
            }
        });


        View guideFr = findViewById(R.id.img_guide_fr);
        guideFr.setOnClickListener(v -> {

            PSTARApp.getInstance().getSharedPreferences(AppConstants.MY_PREF, MODE_PRIVATE).edit().putString(AppConstants.PREF_LANG, AppConstants.FR).apply();

            if (isPaidUser || isPremiumUser()) {
                Intent i = ViewPdfActivity.createIntent(getBaseContext(), "guide_fr.pdf");
                startActivity(i);
            } else {
                mCurrentIntent = ViewPdfActivity.createIntent(getBaseContext(), "guide_fr.pdf");
//                Intent i = ViewPdfActivity.createIntent(getBaseContext(), "guide_fr.pdf");
//                startActivity(i);
                showInAppBillingMessageDialog(AppConstants.FR);
            }
        });

        View linkButton = findViewById(R.id.link_button);
        linkButton.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.PSTARexamAPP.com"));
            startActivity(i);
        });
    }

    private Cursor getPaidUser() {
        return mDb.query(UserIdContractClass.UserIdEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    private void setupPurchase() {


        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(PSTAR_appActivity.this, inAppKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
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

            }
        });
    }

    private void clearAppData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear " + packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {

            } else {

                if (inventory.hasPurchase(AppConstants.SKU_LIFE_TIME_PACKAGE)) {

                    mEditor.putBoolean(AppConstants.PREF_IS_PREMIUM_USER, true).apply();
                    startActivity(mCurrentIntent);
                } else {

                    mEditor.putBoolean(AppConstants.PREF_IS_PREMIUM_USER, false).apply();

                    try {
                        mHelper.launchPurchaseFlow(PSTAR_appActivity.this, AppConstants.SKU_LIFE_TIME_PACKAGE, RC_REQUEST,
                                mPurchaseFinishedListener, "");

                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }
            }
//            }
        }
    };


    IabHelper.QueryInventoryFinishedListener mGotInventoryPremiumListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

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


    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {


            if (result.isFailure()) {

                return;
            }


            if (mHelper == null) {
                return;
            }

            Log.d(TAG, "Item Purchased Successfully" + result.toString());


            mEditor.putBoolean(AppConstants.PREF_IS_PREMIUM_USER, true).apply();
            getApplicationContext().startActivity(mCurrentIntent);
        }
    };

    private void showInAppBillingMessageDialog(String lang) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_premium_purchase_hint);

        TextView mTitleTextView = dialog.findViewById(R.id.textView_dialog_purchase_title);
        TextView mMessageTextView = dialog.findViewById(R.id.textView_dialog_purchase_message);
        Button mContinueButton = dialog.findViewById(R.id.button_confirm);
        TextView mCancelTextView = dialog.findViewById(R.id.button_cancel);
        TextView mPromotionTextView = dialog.findViewById(R.id.textView_dialog_purchase_promotion);

        String mTitle = "";
        String mMessage = "";
        String mPromo = "";
        String mContinue = "";
        String mCancel = "";

        if (lang.equals(AppConstants.EN)) {
            mTitle = mPremiumDialogEnHashMap.get(AppConstants.DIALOG_PURCHASE_TITLE);
            mMessage = mPremiumDialogEnHashMap.get(AppConstants.DIALOG_PURCHASE_MESSAGE);
            mPromo = mPremiumDialogEnHashMap.get(AppConstants.DIALOG_LIFETIME_PURCHASE);
            mContinue = mPremiumDialogEnHashMap.get(AppConstants.DIALOG_BUTTON_CONTINUE);
            mCancel = mPremiumDialogEnHashMap.get(AppConstants.DIALOG_BUTTON_CANCEL);
        } else {
            mTitle = mPremiumDialogFRHashMap.get(AppConstants.DIALOG_PURCHASE_TITLE);
            mMessage = mPremiumDialogFRHashMap.get(AppConstants.DIALOG_PURCHASE_MESSAGE);
            mPromo = mPremiumDialogFRHashMap.get(AppConstants.DIALOG_LIFETIME_PURCHASE);
            mContinue = mPremiumDialogFRHashMap.get(AppConstants.DIALOG_BUTTON_CONTINUE);
            mCancel = mPremiumDialogFRHashMap.get(AppConstants.DIALOG_BUTTON_CANCEL);
        }

        mTitleTextView.setText(mTitle);
        mMessageTextView.setText(mMessage);
        mPromotionTextView.setText(mPromo);
        mContinueButton.setText(mContinue);
        mCancelTextView.setText(mCancel);

        if (lang.equals(AppConstants.FR)) {
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

            Log.d(TAG, "Start In App Billing");
            try {
                if (isSetup) {
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                        mHelper.queryInventoryAsync(mGotInventoryListener);

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

                } else {
                    Toast.makeText(getApplicationContext(), mSetupMessage, Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

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

    ProductDetails productDetails;

    private void setupPurchaseNew() {

        billingClient = BillingClient.newBuilder(this).setListener(purchasesUpdatedListener).enablePendingPurchases().build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                Log.e(TAG, "onBillingSetupFinished: "+billingResult.getResponseCode());
                Log.e(TAG, "onBillingSetupFinished: "+billingResult.getDebugMessage());

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    isSetup = true;
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
                mEditor.putBoolean(AppConstants.PREF_IS_PREMIUM_USER, true).apply();

            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            mEditor.putBoolean(AppConstants.PREF_IS_PREMIUM_USER, true).apply();

        }
        Log.e(TAG, "purchasesUpdatedListener : " + billingResult.getResponseCode());

    };


    private void setUpPremiumDialogMessageString() {

        mPremiumDialogEnHashMap.put(AppConstants.DIALOG_PURCHASE_TITLE, "Get Premium Access Today");
        mPremiumDialogEnHashMap.put(AppConstants.DIALOG_PURCHASE_MESSAGE, "Get access to all PSTAR sections,\n" +
                "unlimited Sample Exams, the Aviation\n" +
                "Language Proficiency Test Guide, the\n" +
                "Radio Study Guide for the Restricted\n" +
                "Operator Certificate (ROC-A) with\n" +
                "sample questions, and future updates!");
        mPremiumDialogEnHashMap.put(AppConstants.DIALOG_BUTTON_CONTINUE, "Continue");
        mPremiumDialogEnHashMap.put(AppConstants.DIALOG_BUTTON_CANCEL, "Cancel");
        mPremiumDialogEnHashMap.put(AppConstants.DIALOG_LIFETIME_PURCHASE, "If you have previously paid for the full version, you do not need to pay again. Just send us an email with prove of purchase to info@pstarexamapp.com for a free code.)");


        mPremiumDialogFRHashMap.put(AppConstants.DIALOG_PURCHASE_TITLE, "Accédez au service Premium");
        mPremiumDialogFRHashMap.put(AppConstants.DIALOG_PURCHASE_MESSAGE, "Accès à toutes les sections du PSTAR,\n" +
                "échantillons d’examens, guide pour le\n" +
                "test d’évaluation des compétences\n" +
                "linguistiques en aviation, guide pour\n" +
                "l’examen d’opérateur radio avec\n" +
                "échantillons de questions.");
        mPremiumDialogFRHashMap.put(AppConstants.DIALOG_BUTTON_CONTINUE, "Continuez");
        mPremiumDialogFRHashMap.put(AppConstants.DIALOG_BUTTON_CANCEL, "Annulez");
        mPremiumDialogFRHashMap.put(AppConstants.DIALOG_LIFETIME_PURCHASE, "(Si vous avez déjà payé la version complète, vous n’avez pas besoin de payer à nouveau. Il suffit de nous envoyer un email avec la preuve d\\'achat à info@pstarexamapp.com pour un code gratuit.)");

    }
}