package com.client.ui.friends;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.client.FriendsListAdapter;
import com.client.FriendsRequestsAdapter;
import com.client.MainActivity;
import com.client.MyAdapter;
import com.client.NetworkService;
import com.client.R;
import com.client.models.Friends;
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

public class FriendsRequestsFragment extends Fragment {

    RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    final String ACCESS_TOKEN = "accessToken";
    final String REFRESH_TOKEN = "refreshToken";
    SharedPreferences sharedPreferences;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends_requests, container, false);
        sharedPreferences = this.getActivity().getSharedPreferences("preferences", MODE_PRIVATE);


        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView3);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

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
                Response<List<Friends>> response = NetworkService.getInstance().getJSONApi().getRequests(
                        sharedPreferences.getString(ACCESS_TOKEN, "")).execute();
                if (response.code() == 200) {
                    List<Friends> friends = response.body();
                    if (response.code() == 200)
                        for (int i = 0; i < friends.size(); i++)
                            logins.add(friends.get(i).getLogin1());

                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return logins;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
                mAdapter = new FriendsRequestsAdapter(strings, getActivity());
                recyclerView.setAdapter(mAdapter);
        }
    }
}