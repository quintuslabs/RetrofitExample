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
public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "RegisterActivity==>";
    EditText name, email, mobile, password;
    String _name, _email, _mobile, _password;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    User user;
    String registerJsonStr;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressDialog = new ProgressDialog(RegisterActivity.this);
        name = findViewById(R.id.ar_name);
        mobile = findViewById(R.id.ar_mobile);
        email = findViewById(R.id.ar_email);
        password = findViewById(R.id.ar_password);
    }

    public void onLoginClicked(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();

    }

    public void onRegisterClicked(View view) {
        _name = name.getText().toString().trim();
        _mobile = mobile.getText().toString().trim();
        _email = email.getText().toString().trim();
        _password = password.getText().toString().trim();

        if (_name.length() == 0 || name.length() < 3) {
            name.setError("Please Enter Your Name");
            name.requestFocus();
        } else if (_email.length() == 0) {
            email.setError("Please Enter Your Email");
            email.requestFocus();
        } else if (!_email.matches(emailPattern)) {
            email.setError("Please Enter Correct Email");
            email.requestFocus();
        } else if (_mobile.length() == 0) {
            mobile.setError("Please Enter Your Mobile Number");
            mobile.requestFocus();
        } else if (_mobile.length() < 10) {
            mobile.setError("Please Enter Correct Mobile Number");
            mobile.requestFocus();
        } else if (_password.length() == 0) {
            password.setError("Please Enter Password");
            password.requestFocus();
        } else if (_password.length() < 6) {
            password.setError("Please Enter 6digit Password");
            password.requestFocus();
        } else {
            if (NetworkCheck.isNetworkAvailable(getApplicationContext())) {
                user = new User(_name, _email, _mobile, _password);
                userRegister();
            }
        }
    }

    private void userRegister() {
        progressDialog.setMessage("Registering Data........");
        progressDialog.show();
        Log.d(TAG, "User Registration Request!");
        Gson gson = new Gson();
        String registerRequestStr = gson.toJson(user);
        Log.d(TAG, "Register Request: " + registerRequestStr);
        Call<UserResult> call = RestClient.getRestService(getApplicationContext()).register(user);
        call.enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                Log.d("Response Code : ", response.code() + "");

                if (response.code() == 201) {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                    progressDialog.dismiss();

                }
            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Please try again !!", Toast.LENGTH_LONG).show();


            }
        });
    }
}
