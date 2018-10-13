package com.divide.ibitech.divide_ibitech;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tooltip.Tooltip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Diagnosis extends AppCompatActivity {

    AutoCompleteTextView act_Diagnosis;
    String[] conditionNames, medicationNames;
    AutoCompleteTextView act_Medication;
    TextView tvPatientName, tvDate;
    EditText  etPatientSymptoms;
    Button btnCancel, btnSave;
    ImageView img_Info;
    String patientID = "", patientName = "", symptomID = "", doctorID = "", medRegNo =  "", date = "";

    String URL_GETSYMPTMS = "http://sict-iis.nmmu.ac.za/ibitech/app/getpatientsymptomsfordiagnosis.php";
//    String URL_GETMED = "http://sict-iis.nmmu.ac.za/ibitech/app/getmedicineid.php";
//    String URL_GETCOND = "http://sict-iis.nmmu.ac.za/ibitech/app/getconditionid.php";
    String URL_ADDVISIT = "http://sict-iis.nmmu.ac.za/ibitech/app/insertvisit.php";
//    String URL_ADD = "http://sict-iis.nmmu.ac.za/ibitech/app/addsymptom.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis);


        SharedPreferences prefs = getSharedPreferences("DIAGNOSIS",MODE_PRIVATE);
        SharedPreferences preferences = getSharedPreferences("DOCPREFS", MODE_PRIVATE);

        tvPatientName = findViewById(R.id.txtPatientName);
        etPatientSymptoms = findViewById(R.id.etSymptoms);
        tvDate = findViewById(R.id.txtDate);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Date date = new Date();
        tvDate.setText(dateFormat.format(date));

        img_Info = findViewById(R.id.imgInfo);

        img_Info.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Tooltip tooltip = new Tooltip.Builder(img_Info)
                        .setText("Add more symptoms by separating them using a comma (,).")
                        .setTextColor(Color.WHITE)
                        .setGravity(Gravity.TOP)
                        .setCornerRadius(8f)
                        .setDismissOnClick(true)
                        .show();
            }
        });

        act_Diagnosis = findViewById(R.id.actx_Diagnosis);
        act_Medication = findViewById(R.id.actx_Medication);

        conditionNames = getResources().getStringArray(R.array.conditions);
        ArrayAdapter<String> cAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,conditionNames);
        act_Diagnosis.setAdapter(cAdapter);

        medicationNames = getResources().getStringArray(R.array.medication);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,medicationNames);
        act_Medication.setAdapter(mAdapter);

        patientID = prefs.getString("pID","");
        patientName = prefs.getString("pName","");

        doctorID = preferences.getString("pID","");
        medRegNo = preferences.getString("pRegNo","");
        //Log.i("tagconvertstr", "["+medRegNo+"]");

        GetPatientSymptoms(patientID);

        tvPatientName.setText(patientName);

        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Diagnosis.this, ViewPatientVisits.class));
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String diagnosis = act_Diagnosis.getText().toString();
                final String medication = act_Medication.getText().toString();
                //final String symptoms = etPatientSymptoms.getText().toString();
                final String date = tvDate.getText().toString();
                if(diagnosis.isEmpty() || medication.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please ensure all fields are entered.",Toast.LENGTH_SHORT).show();
                }
                else {
//                    addSymptom(symptoms,date,patientID);
                    addVisit(date, doctorID, medRegNo, symptomID, patientID, diagnosis, medication);
                }
            }
        });


    }

/*    private void addSymptom(final String symptoms, final String date, final String patientID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if (success.equals("1")) {
                        GetPatientSymptoms(patientID);
                    }
                    else {
                        Toast.makeText(Diagnosis.this, "Sorry, diagnosis cannot be added at the moment.", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Diagnosis.this, "Error : There was an internal error in adding the diagnosis, try again later", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Diagnosis.this,"Error : There was an internal error with the response from our server, try again later.",Toast.LENGTH_LONG).show();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();

                params.put("symptom",symptoms);
                params.put("date", date);
                params.put("id",patientID);

                return params;
            }
        };

        Singleton.getInstance(Diagnosis.this).addToRequestQue(stringRequest);
    }*/


    public void GetPatientSymptoms(final String userID){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GETSYMPTMS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");

                    JSONObject object = jsonArray.getJSONObject(0);
                    if(object.getString("symptom_id").isEmpty() || object.getString("symptom_name").isEmpty() ){
//                     etPatientSymptoms.setEnabled(true);
                        btnSave.setEnabled(false);
                    }
                    else {
                        etPatientSymptoms.setText(object.getString("symptom_name"));
                        symptomID = object.getString("symptom_id");
                        etPatientSymptoms.setEnabled(false);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Diagnosis.this, "Note: This patient has no symptoms inserted.", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Diagnosis.this,"2Register Error"+error.toString(),Toast.LENGTH_LONG).show();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();

                params.put("id",userID);

                return params;
            }
        };

        Singleton.getInstance(Diagnosis.this).addToRequestQue(stringRequest);
    }

    public void addVisit(final String date, final String docID, final String medRegNo, final String symptomID, final String patientID,  final String condition, final String medicine){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADDVISIT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    //Log.i("tagconvertstr", "["+response+"]");
                    if (success.equals("1")) {
                        Toast.makeText(Diagnosis.this, "Diagnosis added successfully.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Diagnosis.this, ViewPatientVisits.class));
                    }
                    else {
                        Toast.makeText(Diagnosis.this, "Sorry, diagnosis cannot be added at the moment.", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Diagnosis.this, "Error : There was an internal error in adding the diagnosis, try again later", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Diagnosis.this,"Error : There was an internal error with the response from our server, try again later.",Toast.LENGTH_LONG).show();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();


                params.put("date",date);
                params.put("docID",docID);
                params.put("medRegNo",medRegNo);
                params.put("symptomID",symptomID);
                params.put("patID",patientID);

                params.put("condition", condition);
                params.put("medicine", medicine);
                return params;
            }
        };

        Singleton.getInstance(Diagnosis.this).addToRequestQue(stringRequest);
    }
}
