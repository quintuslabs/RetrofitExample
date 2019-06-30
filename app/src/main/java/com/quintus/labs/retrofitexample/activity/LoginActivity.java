package com.quintus.labs.retrofitexample.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.quintus.labs.retrofitexample.R;
import com.quintus.labs.retrofitexample.api.clients.RestClient;
import com.quintus.labs.retrofitexample.model.User;
import com.quintus.labs.retrofitexample.model.UserResult;
import com.quintus.labs.retrofitexample.util.LocalStorage;
import com.quintus.labs.retrofitexample.util.NetworkCheck;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * RetrofitExample
 * https://github.com/quintuslabs/RetrofitExample
 * Created on 18-Feb-2019.
 * Created by : Santosh Kumar Dash:- http://santoshdash.epizy.com
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity==>";
    EditText email, password;
    String _email, _password;
    LocalStorage localStorage;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.al_email);
        password = findViewById(R.id.al_password);
        localStorage = new LocalStorage(getApplicationContext());
        progressDialog = new ProgressDialog(LoginActivity.this);
    }

    public void onLoginClicked(View view) {
        _email = email.getText().toString().trim();
        _password = password.getText().toString().trim();
        if (_email.length() == 0 || _email.length() < 10) {
            email.setError("Please Enter Your Email Or Mobile number");
            email.requestFocus();
        } else if (_password.length() == 0 || _password.length() < 6) {
            password.setError("Please Enter Your Password");
            password.requestFocus();
        } else {
            if (NetworkCheck.isNetworkAvailable(this)) {
                user = new User(_email, _password);
                userLogin();
            }

        }
    }

    private void userLogin() {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Gson gson = new Gson();
        Log.d(TAG, "Doing Login Request: ");
        String loginRequestStr = gson.toJson(user);
        Log.d(TAG, "Register Request: " + loginRequestStr);

        Call<UserResult> call = RestClient.getRestService(getApplicationContext()).login(user);
        call.enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                Log.d(TAG, "Response code : " + response.code());
                // Log.d(TAG, "Response message : " + response.body().getMessage());
                UserResult result = response.body();

                if (response.code() != 200) {
                    Toast.makeText(LoginActivity.this, "Login Faild ! Please Check Credential", Toast.LENGTH_SHORT).show();
                } else if (result != null && !result.getError()) {

                    User user = response.body().getUser();
                    if (null != user) {
                        Gson gson = new Gson();
                        String userStr = gson.toJson(user);
                        Log.d(TAG, "User Data :" + userStr);
                        localStorage.setUserLogin(userStr);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        Toast.makeText(LoginActivity.this, "Login Successfull !!", Toast.LENGTH_SHORT).show();


                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login Faild ! Please Check Credential", Toast.LENGTH_SHORT).show();

                }
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                if (t.getMessage().contains("30000ms")) {
                    Toast.makeText(LoginActivity.this, "Unable to connect Server. Please Try Again !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Please Try Again !", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });

    }

    public void onReisterTextClicked(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        finish();

    }
}
