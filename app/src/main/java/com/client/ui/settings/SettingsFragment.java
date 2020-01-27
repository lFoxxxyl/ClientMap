package com.client.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.auth0.android.jwt.JWT;
import com.client.MainActivity;
import com.client.MyAdapter;
import com.client.NavigationActivity;
import com.client.NetworkService;
import com.client.R;
import com.client.models.Tokens;
import com.client.models.User;
import com.client.ui.home.HomeViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.client.models.Tokens.updateTokens;

public class SettingsFragment extends Fragment {

    SharedPreferences sharedPreferences;
    final String LOGIN = "login";
    final String ACCESS_TOKEN = "accessToken";
    final String REFRESH_TOKEN = "refreshToken";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = this.getActivity().getSharedPreferences("preferences", MODE_PRIVATE);
        String login = sharedPreferences.getString(LOGIN, "");


        Toolbar toolbar = root.findViewById(R.id.toolbar3);

        toolbar.setTitle(login);

        setHasOptionsMenu(true);

        ((NavigationActivity) getActivity()).setSupportActionBar(toolbar);

        return root;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:

                MyTask myTask = new MyTask();
                myTask.execute();

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    private class MyTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void...voids) {

            if(Tokens.checkToken(sharedPreferences.getString(ACCESS_TOKEN, ""))<30)
                updateTokens(getActivity());
            try {
                Response<ResponseBody> response =  NetworkService.getInstance().getJSONApi().logoutUser(
                        sharedPreferences.getString(ACCESS_TOKEN, "")).execute();
                if (response.code()==200) {
                    sharedPreferences.edit().clear().apply();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    getActivity().finish();
                    startActivity(intent);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}