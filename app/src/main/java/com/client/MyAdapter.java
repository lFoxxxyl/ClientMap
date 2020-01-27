package com.client;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.client.models.Tokens;
import java.io.IOException;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.client.models.Tokens.updateTokens;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private List<String> data;
    Context context;

    public MyAdapter(List<String> data,Context context){
        this.data= data;
        this.context= context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view =inflater.inflate(R.layout.list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.textView.setText(data.get(position));
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.imageButton.setEnabled(false);
                MyTask myTask = new MyTask();
                if (position<=data.size())
                    myTask.execute(data.get(position), Integer.toString(position));

            }
        });

    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageButton imageButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView6);
            imageButton = itemView.findViewById(R.id.imageButton);
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
                Response<ResponseBody> response= NetworkService.getInstance().getJSONApi().requestFriends(
                        sharedPreferences.getString(ACCESS_TOKEN, ""),strings[0]).execute();
                Integer[] arg ={response.code(),Integer.parseInt(strings[1])} ;
                return arg;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer[] values) {

            if (values[0]==201) {
                int position = values[1];
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, data.size());
                data.remove(position);
            }
        }
    }
}
