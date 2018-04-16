package com.usto.rafik.administration;



/**
 * Created by SilentControle on 05/02/2016.
 */
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IncomingSms extends BroadcastReceiver {


    final SmsManager sms = SmsManager.getDefault();
    public  String code1;
    public String tel1;

    JSONParser jsonParser = new JSONParser();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_TAXI = "taxi";
    private static final String TAG_TID = "tid";
    private static final String TAG_UPDATING= "updating";
    private static final String TAG_NAME = "name";
    private static final String TAG_TEL = "tel";
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_STATUT = "statut";

    private static final String url_taxi_valider = "http://0.0.0.0.0.0.com/valider.php"; //to edit




    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    //verification si code taxi... envoi un appel a asynctask
                    String test = "taxi";
                    if (message.toLowerCase().contains(test.toLowerCase()))
                    {
                        String abc = message.replaceAll("[^0-9?!\\.]", "");//prendre juste les numero dun string du sms
                        String num = senderNum.replaceAll("[^0-9?!\\.]", "");//prendre juste les numero dun string enlève le + du tel
                        String finalnum = num;
                        if (finalnum.length() == 14){
                            finalnum = finalnum.substring(5);
                            finalnum = "0"+finalnum;
                        }
                        if (finalnum.length() == 12){
                            finalnum = finalnum.substring(3);
                            finalnum = "0"+finalnum;
                        }
                        code1 = abc;
                        tel1 = finalnum;
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, "Ce chauffeur viens d'etre activer Tel: " + tel1 + ", Code: " + code1, duration);
                        toast.show();
                        new Validation().execute(code1,tel1);
                    }
                } // end for loop
            } // bundle is null
        } catch (Exception e) {
            Log.e("Sms", "Exception" + e);
        }
    }

    class Validation extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
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
            if (json != null) {
                try {
                    success = json.getInt(TAG_SUCCESS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (success == 1) {
                //validation effectué
            }else{
            }
        }

    }



}

