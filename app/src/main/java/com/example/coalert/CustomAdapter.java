package com.example.coalert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import android.widget.Filter;
import android.widget.Filterable;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> implements Filterable{
    private ArrayList<messageList> arrayList;
    private Context context;
    ArrayList<messageList> preventFilteredList;
    ArrayList<messageList> locationFilteredList;

    public CustomAdapter(ArrayList<messageList> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        this.preventFilteredList = arrayList;
        this.locationFilteredList = arrayList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.message.setText(String.valueOf(arrayList.get(position).getMessage()));
        holder.date.setText(String.valueOf(arrayList.get(position).getDate()));
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView date;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.message = itemView.findViewById(R.id.textMessageView);
            this.date = itemView.findViewById(R.id.textDateView);

        }
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if(charString.isEmpty()) {
                    preventFilteredList = arrayList;
                }
                else{
                    ArrayList<messageList> preventFilteringList = new ArrayList<>();
                    for(messageList message : arrayList){
                        if(message.getMessage().contains(charString)){
                            preventFilteringList.add(message);
                        }
                    }
                    preventFilteredList = preventFilteringList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = preventFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                preventFilteredList = (ArrayList<messageList>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
