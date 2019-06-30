package com.quintus.labs.retrofitexample.api;


import com.quintus.labs.retrofitexample.model.UploadResponse;
import com.quintus.labs.retrofitexample.model.User;
import com.quintus.labs.retrofitexample.model.UserResult;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * RetrofitExample
 * https://github.com/quintuslabs/RetrofitExample
 * Created on 18-Feb-2019.
 * Created by : Santosh Kumar Dash:- http://santoshdash.epizy.com
 */

public interface RestService {

    @POST("api/users/register")
    Call<UserResult> register(@Body User user);

    @POST("api/users/login")
    Call<UserResult> login(@Body User user);

    @Multipart
    @POST("api/users/uploadimage")
    Call<UploadResponse> uploadUserImage(@Header("Authorization") String api_key, @Part MultipartBody.Part image);

    @PUT("api/users/{id}")
    Call<UserResult> updateUser(@Header("Authorization") String api_key, @Path("id") String id,
                                @Body User user);


}
