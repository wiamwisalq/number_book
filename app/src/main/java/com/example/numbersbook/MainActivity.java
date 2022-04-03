package com.example.numbersbook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button b;
    TextView textView;
    RadioButton r1,r2;
    EditText numNom;
    RequestQueue queue;
    String nameR, numR;
    CountryCodePicker ccp;
    ImageView v;
    String insertUrl = "http://192.168.0.163:8889/contact";  //192.168.0.163 10.0.2.2
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ccp=findViewById(R.id.ccp);
        v=findViewById(R.id.imageView);
        //v.setImageDrawable(ccp.getImageViewFlag().getDrawable());
        textView=findViewById(R.id.r);
        b=findViewById(R.id.buttonS);
        r1=findViewById(R.id.rNom);
        r2=findViewById(R.id.rNum);
        numNom=findViewById(R.id.editText2);
        recupContacts();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(r1.isChecked()){
                    queue = Volley.newRequestQueue(getApplicationContext());
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                            insertUrl+"/name/"+numNom.getText(), null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //Log.d(TAG,response.toString());
                                    response.toString();
                                    try {
                                        v.setImageDrawable(ccp.getImageViewFlag().getDrawable());
                                        View popup = LayoutInflater.from(MainActivity.this).inflate(R.layout.details_num, null,
                                                false);
                                        final TextView name=popup.findViewById(R.id.name);
                                        final TextView number=popup.findViewById(R.id.num);
                                        name.setText(response.getString("name"));
                                        number.setText(response.getString("number"));
                                        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                                .setView(popup)
                                                .create();
                                        dialog.show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, "Untrouvable!", Toast.LENGTH_LONG).show();
                        }
                    });
                    queue.add(jsonObjReq);
                }else{
                    queue = Volley.newRequestQueue(getApplicationContext());

                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                            insertUrl+"/num/"+numNom.getText(), null,
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    //Log.d(TAG, response.toString());
                                    response.toString();
                                    String n="";
                                    try {
                                        v.setImageDrawable(ccp.getImageViewFlag().getDrawable());
                                        View popup = LayoutInflater.from(MainActivity.this).inflate(R.layout.details_num, null,
                                                false);
                                        final TextView name=popup.findViewById(R.id.name);
                                        final TextView number=popup.findViewById(R.id.num);
                                        name.setText(response.getString("name"));
                                        number.setText(response.getString("number"));
                                        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                                .setView(popup)
                                                .create();
                                        dialog.show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, "Untrouvable", Toast.LENGTH_LONG).show();
                        }
                    });
                    queue.add(jsonObjReq);

                }

            }
        });
       // deletAll();
    }



    private void deletAll() {
        queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.DELETE,insertUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(MainActivity.this, response+"", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(sr);
    }
    public void recupContacts(){
        ContentResolver contentResolver=this.getContentResolver();
        Cursor cursor=contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_ALTERNATIVE,
                        ContactsContract.CommonDataKinds.Phone.NUMBER},null,null,null);
        if(cursor == null) {
            Log.d("recup", "***********ERROR***********");
        }else{
            while (cursor.moveToNext()){
                int n=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_ALTERNATIVE);
                String name=cursor.getString(n);
                int m=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number=cursor.getString(m);
                String resN="";
                for (int i=0 ; i< number.length();i++){
                    if(Character.isDigit(number.charAt(i)) || number.charAt(i)=='+' ){
                        resN+=number.charAt(i);
                    }
                }
                //textView.setText(textView.getText()+" - "+"("+name +" "+ number+")");
                ////


                queue = Volley.newRequestQueue(getApplicationContext());
                Map<String, String> postParam = new HashMap<String, String>();
                postParam.put("name", name);
                postParam.put("number", number);
                postParam.put("country_id","1");
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        insertUrl, new JSONObject(postParam),
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                //Log.d(TAG, response.toString());
                                //textView.setText(response.toString());

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };
                queue.add(jsonObjReq);


                ////
            }
        }
    }
}