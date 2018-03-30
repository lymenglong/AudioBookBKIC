package com.bkic.lymenglong.audiobookbkic.Models.Main.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListBookType.ListBookType;
import com.bkic.lymenglong.audiobookbkic.Views.History.ListHistory;

import java.util.ArrayList;


public class MainAdapter extends RecyclerView.Adapter {
    private ArrayList<Chapter> chapters;
    private Activity activity;

    public MainAdapter(Activity activity, ArrayList<Chapter> chapters) {
        this.chapters = chapters;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new HomeHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HomeHolder) {
            HomeHolder homeHolder = (HomeHolder) holder;

            homeHolder.name.setText(chapters.get(position).getTitle());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    class HomeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
//        private ImageView imgNext;

        HomeHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameStory);
//            imgNext = itemView.findViewById(R.id.imgNext);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == itemView){
                if (chapters.get(getAdapterPosition()).getTitle().equals(activity.getString(R.string.prompt_book_type))) {
                    Intent intent = new Intent(activity, ListBookType.class);
                    intent.putExtra("idHome", chapters.get(getAdapterPosition()).getId());
                    intent.putExtra("titleHome", chapters.get(getAdapterPosition()).getTitle());
                    activity.startActivity(intent);
                }
                if (chapters.get(getAdapterPosition()).getTitle().equals(activity.getString(R.string.prompt_history))){
                    Intent intent = new Intent(activity, ListHistory.class);
                    intent.putExtra("idHome", chapters.get(getAdapterPosition()).getId());
                    intent.putExtra("titleHome", chapters.get(getAdapterPosition()).getTitle());
                    activity.startActivity(intent);
                }
            }

        }
    }
}
