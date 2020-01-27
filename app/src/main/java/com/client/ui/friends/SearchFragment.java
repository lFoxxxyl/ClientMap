package com.client.ui.friends;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class SearchFragment extends Fragment {
    TextView textView4;
    //ListView listView;
    RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    final String ACCESS_TOKEN = "accessToken";
    final String REFRESH_TOKEN = "refreshToken";
    SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        sharedPreferences = this.getActivity().getSharedPreferences("preferences", MODE_PRIVATE);


        Toolbar toolbar = root.findViewById(R.id.toolbar5);

        //listView = root.findViewById(R.id.listView);

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        setHasOptionsMenu(true);

        ((NavigationActivity) getActivity()).setSupportActionBar(toolbar);

        if (((NavigationActivity) getActivity()).getSupportActionBar() != null){
            ((NavigationActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((NavigationActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        return root;
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setQueryHint("Поиск друзей");
            searchView.setMaxWidth(Integer.MAX_VALUE);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    MyTask myTask = new MyTask();
                    myTask.execute(query);

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    MyTask myTask = new MyTask();
                    myTask.execute(newText);

                    return true;
                }
            });

        }
        super.onCreateOptionsMenu(menu,inflater);
    }


    private class MyTask extends AsyncTask<String,String,List<String>>{

        @Override
        protected List<String> doInBackground(String... strings) {

            if(Tokens.checkToken(sharedPreferences.getString(ACCESS_TOKEN, ""))<30)
                updateTokens(getActivity());
            List<String> logins = new ArrayList<>();
            try {
                Response<List<User>> response =  NetworkService.getInstance().getJSONApi().searchUser(sharedPreferences.getString(ACCESS_TOKEN, ""), strings[0]).execute();
                List<User> users = response.body();
                if(response.code()==200)
                    for (int i = 0; i<users.size();i++)
                        logins.add(users.get(i).getLogin());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return logins;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            mAdapter = new MyAdapter(strings ,getActivity());
            recyclerView.setAdapter(mAdapter);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Fragment newFragment = new FriendsFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        return super.onOptionsItemSelected(item);
    }
}