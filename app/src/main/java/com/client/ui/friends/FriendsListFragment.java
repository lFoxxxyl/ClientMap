package com.client.ui.friends;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.client.FriendsListAdapter;
import com.client.MainActivity;
import com.client.MyAdapter;
import com.client.NavigationActivity;
import com.client.NetworkService;
import com.client.R;
import com.client.models.Tokens;
import com.client.models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.client.models.Tokens.updateTokens;

public class FriendsListFragment extends Fragment {

    RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    final String ACCESS_TOKEN = "accessToken";
    final String REFRESH_TOKEN = "refreshToken";
    SharedPreferences sharedPreferences;
    //DBHelper dbHelper;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends_list, container, false);

        sharedPreferences = this.getActivity().getSharedPreferences("preferences", MODE_PRIVATE);

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        List<String> logins = new ArrayList<>();



        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL));

        MyTask myTask = new MyTask();
        myTask.execute();

        return root;
    }

    private class MyTask extends AsyncTask<String,String,List<String>> {

        @Override
        protected List<String> doInBackground(String... strings) {

            if(Tokens.checkToken(sharedPreferences.getString(ACCESS_TOKEN, ""))<30)
                updateTokens(getActivity());
            List<String> logins = new ArrayList<>();
            try {
                Response<List<String>> response =   NetworkService.getInstance().getJSONApi().getFriends(
                        sharedPreferences.getString(ACCESS_TOKEN, "")).execute();
                logins = response.body();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return logins;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            mAdapter = new FriendsListAdapter(strings, getActivity());
            recyclerView.setAdapter(mAdapter);
        }
    }
}