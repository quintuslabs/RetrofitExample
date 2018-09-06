package com.quintus.labs.retrofitexample.api;


import com.quintus.labs.retrofitexample.model.User;
import com.quintus.labs.retrofitexample.model.UserResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Bapunu on 16-02-2018.
 */

public interface RestService {

    @POST("api/users/register")
    Call<UserResult> register(@Body User user);

    @POST("api/users/login")
    Call<UserResult> login(@Body User user);


}
