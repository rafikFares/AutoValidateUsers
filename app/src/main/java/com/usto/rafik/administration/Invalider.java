package com.usto.rafik.administration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SilentControle on 19/01/2016.
 */
public class Invalider extends AppCompatActivity implements View.OnClickListener {
    EditText editText3;
    Button button2;
    public int zeta =0;
    ProgressDialog pDialog;
    public String tel;
    JSONParser jsonParser = new JSONParser();
    // single taxi url

    private static final String url_taxi_invalider = "http://rafik.hostei.com/invalider.php";
    // JSON node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_TAXI = "taxi";
    private static final String TAG_TID = "tid";
    private static final String TAG_UPDATING = "updating";
    private static final String TAG_NAME = "name";
    private static final String TAG_TEL = "tel";
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_STATUT = "statut";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invalider);
        editText3 = (EditText) findViewById(R.id.editText3);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);

    }


    class Invalidation extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Invalider.this);
            pDialog.setMessage("Invalidation Du Chauffeur..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("tel", args[0]);


                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        url_taxi_invalider, "POST", params);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    protected void onPostExecute(JSONObject json) {

        int success = 0;

        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }

        if (json != null) {


            try {
                success = json.getInt(TAG_SUCCESS);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (success == 1) {
            //validation effectué
            Toast.makeText(getApplicationContext(), "Invalidation éffectuer",
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "un problem est survenue",
                    Toast.LENGTH_LONG).show();
        }
    }

    }





    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button2) {
            zeta = 0;
             tel = editText3.getText().toString();

            if (!tel.isEmpty()) {
                new Invalidation().execute(tel);
            } else {
                Toast.makeText(getApplicationContext(), "information vide",
                        Toast.LENGTH_LONG).show();
            }
            }

        }

}
