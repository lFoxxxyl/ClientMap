package com.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.client.models.Tokens;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    final String LOGIN = "login";
    final String ACCESS_TOKEN = "accessToken";
    final String REFRESH_TOKEN = "refreshToken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = this.getSharedPreferences("preferences", MODE_PRIVATE);
        if (!sharedPreferences.getString(LOGIN, "").isEmpty() && !sharedPreferences.getString(REFRESH_TOKEN, "").isEmpty()){
            String refreshToken = sharedPreferences.getString(REFRESH_TOKEN, "");
            JWT jwt = new JWT(refreshToken);
            Date expiresAt = jwt.getExpiresAt();
            Date now = new Date();
            if (expiresAt.getTime()/1000-now.getTime()/1000>=10) {
                /*Toast toast = Toast.makeText(getApplicationContext(),
                        now.toString(), Toast.LENGTH_SHORT);
                toast.show();*/
                Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        }
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final LinearLayout linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setVisibility(View.VISIBLE);

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        final Button button = findViewById(R.id.button);
        final Button button2 = findViewById(R.id.button2);

        final EditText editText = findViewById(R.id.editText);
        final EditText editText2 = findViewById(R.id.editText2);

        final TextView textView = findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                NetworkService.getInstance().getJSONApi().loginUser(
                        editText.getText().toString(),
                        editText2.getText().toString()).enqueue(new Callback<Tokens>() {
                    @Override
                    public void onResponse(@NonNull Call<Tokens> call, @NonNull Response<Tokens> response) {
                       if (response.code()==200) {
                           Tokens tokens = response.body();
                               sharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE);
                               SharedPreferences.Editor editor = sharedPreferences.edit();
                               editor.putString(LOGIN, editText.getText().toString());
                               editor.putString(ACCESS_TOKEN, tokens.getAccessToken());
                               editor.putString(REFRESH_TOKEN, tokens.getRefreshToken());
                               editor.apply();

                               Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                               intent.putExtra("login", editText.getText().toString());
                               intent.putExtra("accessToken", tokens.getAccessToken());
                               intent.putExtra("refreshToken", tokens.getRefreshToken());
                               MainActivity.this.finish();
                               startActivity(intent);
                       }
                       if (response.code()==403) {
                           try {
                               JSONObject jsonObject = new JSONObject(response.errorBody().string());
                               Snackbar.make(button, jsonObject.getString("error"), Snackbar.LENGTH_LONG)
                                       .show();
                           }
                           catch (Exception e){

                           }
                           linearLayout.setVisibility(View.VISIBLE);
                           progressBar.setVisibility(View.INVISIBLE);
                       }
                    }
                    @Override
                    public void onFailure(@NonNull Call<Tokens> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        Snackbar.make(button, "Проверьте подключени к сети", Snackbar.LENGTH_LONG)
                                .show();
                        linearLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
}
