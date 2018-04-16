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
public class Valider extends AppCompatActivity implements View.OnClickListener  {

    EditText editText;
    EditText editText2;
    Button button;
    public int su = 0;
    public  String code;
    public String tel;
    ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    // single taxi url

    private static final String url_taxi_valider = "http://rafik.hostei.com/valider.php";
    // JSON node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_TAXI = "taxi";
    private static final String TAG_TID = "tid";
    private static final String TAG_UPDATING= "updating";
    private static final String TAG_NAME = "name";
    private static final String TAG_TEL = "tel";
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_STATUT = "statut";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.valider);
        editText2 = (EditText)findViewById(R.id.editText2);
        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(this);
    }


    class Validation extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Valider.this);
            pDialog.setMessage("Validation Du Chauffeur..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("code", args[0]);
                params.put("tel", args[1]);

                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        url_taxi_valider, "POST", params);

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
                Toast.makeText(getApplicationContext(), "Validation éffectuer",
                        Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "un problem est survenue",
                        Toast.LENGTH_LONG).show();
            }
        }

    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            su = 0;
             code = editText2.getText().toString();
            code = code.replaceAll("[^0-9?!\\.]", "");
             tel = editText.getText().toString();
            String fp = tel;
            if (fp.length() == 14){
                fp = fp.substring(5);
                tel = "0"+fp;
            }
            if (!code.isEmpty() && !tel.isEmpty()) {
                new Validation().execute(code,tel);
            } else {
                Toast.makeText(getApplicationContext(), "information vide",
                        Toast.LENGTH_LONG).show();
            }
            }

    }
}
