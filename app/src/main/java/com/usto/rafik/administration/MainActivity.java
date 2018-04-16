package com.usto.rafik.administration;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button b1;
    Button b2;
    Button b3;
    Button b4;
    TextView textView7,textView5; // 7 active ou non ; 5 version
    ProgressDialog pDialog;
    ProgressDialog poDialog;
    ProgressDialog paDialog;
    JSONParser jsonParser = new JSONParser();
    JSONArray taxio = null;
    String tl;
    int b,bb;
    int cont;
    String fone;
    int j;
    String os;
    int k,kk;
    ArrayList<String> sms = new ArrayList<String>();
    String test = "taxi";
    String [] teltableau=new String[1000];
    String [] smstel=new String[1000];
    String [] smscode=new String[1000];
    // single taxi url
    private static final String url_taxi_valider = "http://0.0.0.0.hostei.com/valider.php";//TO edit
    private static final String url_taxi_supp = "http://0.0.0.0.hostei.com/tx_delete.php";//To edit
    private static final String url_sms_verification = "http://0.0.0.0.hostei.com/sms_verification.php";//les non valider // TO edit
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
        setContentView(R.layout.activity_main);
        b1 = (Button) findViewById(R.id.b1);
        b2 = (Button) findViewById(R.id.b2);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b4 = (Button) findViewById(R.id.b4);
        b4.setOnClickListener(this);
        b3 = (Button) findViewById(R.id.b3);
        b3.setOnClickListener(this);
        textView7 = (TextView) findViewById(R.id.textView7);// 7 active ou non ; 5 version
        textView5 = (TextView) findViewById(R.id.textView5);
        comencement();
    }

public void comencement (){
    poDialog = new ProgressDialog(MainActivity.this);
    poDialog.setMessage("Verification de version d'android...");
    poDialog.setIndeterminate(false);
    poDialog.setCancelable(false);
    poDialog.show();
    os = Build.VERSION.RELEASE;
    textView5.setText(os);
    //os="5.1";
    if (os.length() == 5){
        os=os.substring(0,2);
        Log.i("aaaaaa",os);
    }
    double aos = Double.parseDouble(os);
    if (aos >= 4.4){
        textView7.setText("Desactiver");
        ComponentName receiver=new ComponentName(getApplicationContext() ,IncomingSms.class);
        PackageManager p = getApplicationContext().getPackageManager();
        p.setComponentEnabledSetting(receiver,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);// desactive le broadcast receiver
        poDialog.dismiss();
        //pour les permission dans android M
        PackageManager pm = getApplicationContext().getPackageManager();
        int Perm = pm.checkPermission(
                android.Manifest.permission.READ_SMS,
                getApplicationContext().getPackageName());
        if (Perm != PackageManager.PERMISSION_GRANTED) {//si permission nes pa activer
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
            dialogBuilder.setMessage("vous devez dabord accepter tous les permissions de l'application dans paramètre/applications/Administration/permissions");
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("OK", null);
            dialogBuilder.show();
        }

    }else {
        textView7.setText("Activer");
        poDialog.dismiss();
    }

}

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.b1) {
            finish();
            startActivity(new Intent(this, Valider.class));
        }

        if (v.getId() == R.id.b2) {
            finish();
            startActivity(new Intent(this, Invalider.class));
        }

        if (v.getId() == R.id.b4) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
            dialogBuilder.setMessage("Ceci vas automatiquement supprimer tous les taxieurs non valider, voulez-vous confirmer votre choix ?");
            dialogBuilder.setCancelable(false);
            dialogBuilder.setNegativeButton("Cancel", null);
            dialogBuilder.setPositiveButton("Oui",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    new Suppression().execute();}
            });
            dialogBuilder.show();

        }

        if (v.getId() == R.id.b3) {
            cont = 0;
            //pour les permission dans android 6.0
            PackageManager pm = getApplicationContext().getPackageManager();
            int Perm = pm.checkPermission(
                    android.Manifest.permission.READ_SMS,
                    getApplicationContext().getPackageName());
            if (Perm != PackageManager.PERMISSION_GRANTED) {//si permission nes pa activer
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setMessage("vous devez dabord accepter tous les permissions de l'application dans paramètre/applications/Administration/permissions");
                dialogBuilder.setCancelable(false);
                dialogBuilder.setPositiveButton("OK", null);
                dialogBuilder.show();
            }else {
                new Verifier().execute();
            }
        }

    }


    public void readAllMessage() {


        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cur = getContentResolver().query(uri, new String[]{"_id", "address", "date", "body"}, null, null, null);
        cur.moveToFirst();
        k=0;
        while (cur.moveToNext()) {

            String address = cur.getString(1);//le numero
            Log.i("adress", address);
            String body = cur.getString(3);//le contenu du sms
            Log.i("body", body);
            String num = address.replaceAll("[^0-9?!\\.]", "");//prendre juste les numero dun string enlève le + du tel
            String finalnum = num;
            if (finalnum.length() == 12){
                finalnum = finalnum.substring(3);
                finalnum = "0"+finalnum;
            }
            if (body.toLowerCase().contains(test.toLowerCase())) {//si le msg contien taxi
                j = 0;
                Log.i("namrouuuuu", finalnum);
                while (j < bb) {
                    fone = teltableau[j];
                    if (fone.equals(finalnum) ) {
                        body = body.replaceAll("[^0-9?!\\.]", "");
                        k++;
                        smscode[k]=body;
                        smstel[k]=finalnum;

                    }
                    j++;
                }
            }
        }

        if (k!=0){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        dialogBuilder.setMessage("Il y'a"+" "+k+" "+"chauffeurs pas encore valider, voulez-les validez mentenant?");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                kk=1;
                valider();
            }
        });
        dialogBuilder.setNegativeButton("Anuler", null);
        dialogBuilder.show();}
        else {
            Toast.makeText(getApplicationContext(), "Tous les Chauffeurs dans votre boîte de réception sont valider", Toast.LENGTH_LONG).show();
        }

    }

    public void valider(){
        if (kk <= k){
            String code = smscode[kk];
            String tel = smstel[kk];
            kk++;
            new Validation().execute(code, tel);
        }else {
            paDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Validation éffectuer",
                    Toast.LENGTH_LONG).show();
        }
    }

    class Verifier extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Verification en cours...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();

                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        url_sms_verification, "GET", params);

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

                // taxieur trouvé
                // recevoir tous les taxieur dans une list
                try {
                    if (success == 1) {
                    taxio = json.getJSONArray(TAG_TAXI);

                b = taxio.length();//la longeur du resultat(combien ya de taxi online)
                bb=b;
                // bouclé dans tous les taxieurs
                int i = 0;
                while( i < b){
                    JSONObject t = taxio.getJSONObject(i);
                    tl = t.getString(TAG_TEL);
                    teltableau[i]=tl;
                    i++;
                         }
                        pDialog.dismiss();

                        Toast.makeText(getApplicationContext(), "trouver "+b+" taxieurs pas encore valider dans la base de donnée ", Toast.LENGTH_LONG).show();
                        readAllMessage();
                    }else {
                        //rien trouver
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Rien trouver",
                                Toast.LENGTH_LONG).show();
            }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

    }

    class Validation extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            paDialog = new ProgressDialog(MainActivity.this);
            paDialog.setMessage("Validation Des Chauffeurs..");
            paDialog.setIndeterminate(false);
            paDialog.setCancelable(false);
            paDialog.show();
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
                valider();
            }else{
                paDialog.dismiss();
                Toast.makeText(getApplicationContext(), "un problem est survenue",
                        Toast.LENGTH_LONG).show();
            }
        }

    }


    class Suppression extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            paDialog = new ProgressDialog(MainActivity.this);
            paDialog.setMessage("Suppression Des Chauffeurs..");
            paDialog.setIndeterminate(false);
            paDialog.setCancelable(false);
            paDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();

                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        url_taxi_supp, "POST", params);

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
                paDialog.dismiss();
                Toast.makeText(getApplicationContext(), "les taxis non valider on était supprimer",
                        Toast.LENGTH_LONG).show();
            }else{
                paDialog.dismiss();
                Toast.makeText(getApplicationContext(), "un problem est survenue",
                        Toast.LENGTH_LONG).show();
            }
        }

    }
}
