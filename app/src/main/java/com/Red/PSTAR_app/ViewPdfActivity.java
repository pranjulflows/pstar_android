package com.Red.PSTAR_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;

/**
 * Created by Alexey Matrosov on 27.09.2018.
 */
public class ViewPdfActivity extends Activity {
    private static final String ExtraFileName = "extra_file_name";

    public static Intent createIntent(Context context, String fileName) {
        Intent intent = new Intent(context, ViewPdfActivity.class);
        intent.putExtra(ExtraFileName, fileName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        String fileName = getFileName();
        if (fileName == null)
            finish();

        PDFView pdfView = findViewById(R.id.pdf_view);
        pdfView.fromAsset(fileName)
                .onError(errorListener)
                .spacing(5)
                .load();
    }

    private String getFileName() {
        Intent intent = getIntent();
        if (intent == null)
            return null;

        return intent.getStringExtra(ExtraFileName);
    }

    private final OnErrorListener errorListener = error -> {
        Toast.makeText(this, "Wrong PDF file name", Toast.LENGTH_LONG)
                .show();

        finish();
    };
}