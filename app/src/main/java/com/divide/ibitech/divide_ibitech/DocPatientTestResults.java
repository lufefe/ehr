package com.divide.ibitech.divide_ibitech;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class DocPatientTestResults extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home)
            this.finish();

        return super.onOptionsItemSelected(item);
    }

    android.support.v7.widget.Toolbar toolbar;
    String patientName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_patient_test_results);

        //shared prefs for patient data
        SharedPreferences prefs = getSharedPreferences("PATIENT",MODE_PRIVATE);
        patientName = prefs.getString("pName","");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(patientName + "\'s Test Results");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
