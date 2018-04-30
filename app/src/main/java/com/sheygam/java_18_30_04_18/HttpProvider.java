package com.sheygam.java_18_30_04_18;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpProvider {
    private static final HttpProvider ourInstance = new HttpProvider();

    private Api api;

    public static HttpProvider getInstance() {
        return ourInstance;
    }

    private HttpProvider() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15,TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://telranstudentsproject.appspot.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(Api.class);

    }

    public String registration(String email, String password) throws Exception {
        Auth auth = new Auth(email,password);

        Call<AuthToken> call = api.registration(auth);
        Response<AuthToken> response = call.execute();
        if(response.isSuccessful()){
            AuthToken authToken = response.body();
            return authToken.getToken();
        }else if(response.code() == 409){
            throw new Exception("User already exist!");
        }else{
            Log.d("MY_TAG", "registration: " + response.errorBody().string());
            throw new Exception("Server error!");
        }
    }

    public void login(String email, String password, Callback<AuthToken> callback){
        Auth auth = new Auth(email,password);
        Call<AuthToken> call = api.login(auth);
        call.enqueue(callback);
    }


}
