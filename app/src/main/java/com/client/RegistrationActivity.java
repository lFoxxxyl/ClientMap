package com.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Button button3 = findViewById(R.id.button3);

        final EditText editText3 = findViewById(R.id.editText3);
        final EditText editText4 = findViewById(R.id.editText4);
        final EditText editText5 = findViewById(R.id.editText5);

        final LinearLayout linearLayout = findViewById(R.id.linearLayout2);
        linearLayout.setVisibility(View.VISIBLE);

        final ProgressBar progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);


        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                NetworkService.getInstance().getJSONApi().createUser(
                        editText3.getText().toString(),
                        editText4.getText().toString(),
                        editText5.getText().toString()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.code()==201) {
                            RegistrationActivity.this.finish();
                        }
                        else  {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                Snackbar.make(editText3, jsonObject.getString("error"), Snackbar.LENGTH_LONG)
                                        .show();
                            }
                            catch (Exception e){

                            }

                        }
                        linearLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        Snackbar.make(editText3, "Проверьте подключени к сети", Snackbar.LENGTH_LONG)
                                .show();
                        linearLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

