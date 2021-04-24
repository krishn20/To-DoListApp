package com.example.to_dolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.to_dolist.UtilsService.SharedPreferenceClass;
import com.example.to_dolist.UtilsService.UtilsService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private String email, pass;
    UtilsService utilsService;
    ProgressBar progressBar;
    SharedPreferenceClass sharedPreferenceClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        SharedPreferences todo_pref = getSharedPreferences("user_todo", MODE_PRIVATE);
//
//        String userToken = todo_pref.getString("token", "");
//        setContentView(R.layout.activity_login);
//
//        if (!userToken.isEmpty()) {
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            finish();
//        }


        Button loginBtn = findViewById(R.id.btn_login);
        Button registerBtn = findViewById(R.id.btn_createAccount);

        EditText et_email = findViewById(R.id.et_email_login);
        EditText et_pass = findViewById(R.id.et_password_login);
        progressBar = findViewById(R.id.progress_bar_login);
        utilsService = new UtilsService();
        sharedPreferenceClass = new SharedPreferenceClass(this);


        loginBtn.setOnClickListener(view -> {

            utilsService.hideKeyboard(view, LoginActivity.this);

            email = et_email.getText().toString();
            pass = et_pass.getText().toString();

            if(validate(view)){
                loginUser();
            }

        });


        registerBtn.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    } //onCreate close.

    //----------------------------------------------------------------------------------------------------------------------//
    //----------------------------------------------------------------------------------------------------------------------//
    //----------------------------------------------------------------------------------------------------------------------//

    private void loginUser() {

        progressBar.setVisibility(View.VISIBLE);

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", pass);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String apiKey = "https://to-do-list-app-krishn.herokuapp.com/api/todo/auth/login";

        //Here we have replaced the object response and error listeners with their lambdas.

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiKey, new JSONObject(params), response -> {

            try {

                boolean success = response.getBoolean("success");

                if (success) {

                    progressBar.setVisibility(View.GONE);

                    String msg = response.getString("msg");
                    String token = response.getString("token");

                    sharedPreferenceClass.setValue_String("token", token);

                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                }

                else {
                    progressBar.setVisibility(View.GONE);

                    String errMsg = response.getString("msg");
                    Toast.makeText(LoginActivity.this, errMsg, Toast.LENGTH_LONG).show();
                }

            }

            catch (JSONException e) {
                progressBar.setVisibility(View.GONE);

                e.printStackTrace();
            }

        }, error -> {

            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "Volley Error Occurred", Toast.LENGTH_SHORT).show();

            //Here we write the logic of getting the error msg.(Code for double ensuring that error is reported).
            //We take out the response we get from the network, convert it into a String, and then finally into a JSON Object.
            //Then we pass the msg to a Toast.
            //Although it's a VolleyError, but we can show responses of failed registration etc. here as well.

            //---------------------------------------------------------------------------------------------------------------------------------------//

            NetworkResponse networkResponse = error.networkResponse;

            if(error instanceof ServerError && networkResponse != null){

                try {

                    progressBar.setVisibility(View.GONE);
                    String res = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8"));

                    JSONObject object = new JSONObject(res);
                    Toast.makeText(LoginActivity.this, object.getString("msg"), Toast.LENGTH_LONG).show();

                } catch (JSONException | UnsupportedEncodingException je){
                    progressBar.setVisibility(View.GONE);
                    je.printStackTrace();
                }

            }

            //---------------------------------------------------------------------------------------------------------------------------------------//

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();

                headers.put("Content-Type", "application/json");
                return headers;
            }

        };

        //Volley also provides a RetryPolicy, helping in retrying for connecting to the server OR retrying in processing the request/response.

        int socketTime = 3000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTime, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonObjectRequest.setRetryPolicy(retryPolicy);


        //Adding the request to Volley, which then sends it to the server.

        requestQueue.add(jsonObjectRequest);

    }


    public boolean validate(View view){

        boolean isValid = false;

            if(!TextUtils.isEmpty(email)){
                if(!TextUtils.isEmpty(pass)){
                    isValid = true;
                }
                else {
                    utilsService.showSnackBar(view, "Please enter your password.");
                }
            }
            else {
                utilsService.showSnackBar(view, "Please enter your email.");
            }

        return isValid;
    }

    //----------------------------------------------------------------------------------------------------------------------//
    //------------------------------------------------Override Methods------------------------------------------------------//


    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences todo_pref = getSharedPreferences("user_todo", MODE_PRIVATE);

        if(todo_pref.contains("token")){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

    }

}