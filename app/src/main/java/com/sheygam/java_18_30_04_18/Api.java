package com.sheygam.java_18_30_04_18;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {
    @POST("_ah/api/contactsApi/v1/registration")
    Call<AuthToken> registration(@Body Auth body);
    @POST("_ah/api/contactsApi/v1/login")
    Call<AuthToken> login(@Body Auth auth);
}
