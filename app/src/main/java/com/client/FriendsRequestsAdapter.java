package com.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.client.models.Tokens;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.client.models.Tokens.updateTokens;

public class FriendsRequestsAdapter extends RecyclerView.Adapter<FriendsRequestsAdapter.ViewHolder>{
    private List<String> data;
    Context context;

    public FriendsRequestsAdapter(List<String> data,Context context){
        this.data= data;
        this.context= context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view =inflater.inflate(R.layout.friends_requests_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,final int position) {
        holder.textView.setText(data.get(position));
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.imageButton.setEnabled(false);
                holder.imageButton2.setEnabled(false);
                MyTask myTask = new MyTask();
                myTask.execute(data.get(position),"true", Integer.toString(position));

                Snackbar.make(v, "Принято", Snackbar.LENGTH_LONG)
                        .show();
            }
        });
        holder.imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageButton.setEnabled(false);
                holder.imageButton2.setEnabled(false);
                MyTask myTask = new MyTask();
                myTask.execute(data.get(position),"false", Integer.toString(position));
                Snackbar.make(v, "Отклонено", Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageButton imageButton, imageButton2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView4);
            imageButton = itemView.findViewById(R.id.imageButton2);
            imageButton2 = itemView.findViewById(R.id.imageButton3);

        }

    }

    private class MyTask extends AsyncTask<String,Void,Integer[]> {

        @Override
        protected Integer[] doInBackground(String...strings) {

            final String ACCESS_TOKEN = "accessToken";

            SharedPreferences sharedPreferences = context.getSharedPreferences("preferences", MODE_PRIVATE);

            if(Tokens.checkToken(sharedPreferences.getString(ACCESS_TOKEN, ""))<30)
                updateTokens(context);
            try {
                Response<ResponseBody> response= NetworkService.getInstance().getJSONApi().requestAccept(
                        sharedPreferences.getString(ACCESS_TOKEN, ""),strings[0],Boolean.parseBoolean(strings[1])).execute();
                Integer[] arg ={response.code(),Integer.parseInt(strings[2])} ;
                return arg;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer[] values) {

            if (values[0]==200) {
                int position = values[1];
                data.remove(position);

                notifyItemRemoved(position);
                notifyItemRangeChanged(position, data.size());
            }
        }
    }


}
