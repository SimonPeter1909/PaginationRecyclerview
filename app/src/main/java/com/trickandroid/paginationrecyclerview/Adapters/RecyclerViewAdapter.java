package com.trickandroid.paginationrecyclerview.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.trickandroid.paginationrecyclerview.Models.RecyclerViewModel;
import com.trickandroid.paginationrecyclerview.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewVH> {

    private List<RecyclerViewModel> recyclerViewModelList;
    private Context context;

    public RecyclerViewAdapter(List<RecyclerViewModel> recyclerViewModelList, Context context) {
        this.recyclerViewModelList = recyclerViewModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item,viewGroup,false);

        return new RecyclerViewVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewVH holder, int i) {
        RecyclerViewModel recyclerViewModel = recyclerViewModelList.get(i);

        holder.rowNumberTV.setText(recyclerViewModel.getId());
        holder.nameTV.setText(recyclerViewModel.getName());
        holder.numberTV.setText(recyclerViewModel.getNumber());
    }

    @Override
    public int getItemCount() {
        return recyclerViewModelList.size();
    }

    class RecyclerViewVH extends RecyclerView.ViewHolder{

        TextView rowNumberTV;
        TextView nameTV, numberTV;

        public RecyclerViewVH(@NonNull View itemView) {
            super(itemView);
            rowNumberTV = itemView.findViewById(R.id.numberTV);
            nameTV = itemView.findViewById(R.id.textView1);
            numberTV = itemView.findViewById(R.id.textView2);
        }
    }
}
