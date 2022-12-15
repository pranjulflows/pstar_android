package com.Red.PSTAR_app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.Red.PSTAR_app.adapters.AbbreviationsAdapter;
import com.Red.PSTAR_app.utils.MyContextWrapper;

import database.DatabaseHelper;

public class AbbreviationsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abbreviations);

        TextView titleText = findViewById(R.id.abbreviations_title);
        titleText.setText(getString(R.string.abbreviations_title));

        RecyclerView container = findViewById(R.id.abbreviations_container);
        container.setLayoutManager(new LinearLayoutManager(this));
        container.setAdapter(new AbbreviationsAdapter(DatabaseHelper.getAbbreviations(getResources()), AbbreviationsActivity.this));
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase, ""));
    }
}