package com.sheygam.java_18_30_04_18;

import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputEmail, inputPassword;
    private Button regBtn, loginBtn;
    private ProgressBar myProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        loginBtn = findViewById(R.id.loginBtn);
        regBtn = findViewById(R.id.regBtn);
        myProgress = findViewById(R.id.myProgress);

        loginBtn.setOnClickListener(this);
        regBtn.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.regBtn){
            new RegTask().execute();
        }else if(v.getId() == R.id.loginBtn){
            login();
        }
    }

    private void login() {
        showProgress(true);
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        HttpProvider.getInstance().login(email, password, new Callback<AuthToken>() {
            @Override
            public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                showProgress(false);
                if(response.isSuccessful()){
                    String token = response.body().getToken();
                    Log.d("MY_TAG", "onResponse: token: " + token );
                    Toast.makeText(MainActivity.this, "Login OK!", Toast.LENGTH_SHORT).show();
                }else if(response.code() == 401){
                    showError("Wrong email or password");
                }else{
                    showError("Server error");
                    try {
                        Log.d("MY_TAG", "onResponse: server error: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthToken> call, Throwable t) {
                showProgress(false);
                showError("Connection error!");
            }
        });
    }

    private void showProgress(boolean isShow){
        myProgress.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        inputPassword.setEnabled(!isShow);
        inputEmail.setEnabled(!isShow);
        regBtn.setEnabled(!isShow);
        loginBtn.setEnabled(!isShow);
    }

    private void showError(String s) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(s)
                .setPositiveButton("Ok",null)
                .setCancelable(false)
                .create()
                .show();
    }

    class RegTask extends AsyncTask<Void,Void,String>{

        private String email, password;
        private boolean isSuccess = true;

        @Override
        protected void onPreExecute() {
            email = inputEmail.getText().toString();
            password = inputPassword.getText().toString();
            showProgress(true);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "Registration ok";
            try {
                String token = HttpProvider.getInstance().registration(email,password);
                Log.d("MY_TAG", "doInBackground: token: " + token);
            }catch (IOException e){
                isSuccess = false;
                result = "Connection error!";
                e.printStackTrace();
            }catch (Exception e) {
                isSuccess = false;
                result = e.getMessage();
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            showProgress(false);
            if(isSuccess){
                //Todo show next activity
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }else{
                showError(s);
            }
        }
    }


}
