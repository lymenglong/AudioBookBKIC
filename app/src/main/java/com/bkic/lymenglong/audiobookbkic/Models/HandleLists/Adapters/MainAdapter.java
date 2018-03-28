package com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Chapter;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListMenu.ListMenu;

import java.util.ArrayList;


public class MainAdapter extends RecyclerView.Adapter {
    private ArrayList<Chapter> chapters;
    private Activity activity;
    private View view;

    public MainAdapter(Activity activity, ArrayList<Chapter> chapters) {
        this.chapters = chapters;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
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
        private ImageView imgNext;

        public HomeHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.nameStory);
            imgNext = (ImageView) itemView.findViewById(R.id.imgNext);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == itemView){
                Intent intent = new Intent(activity, ListMenu.class);
                intent.putExtra("idHome", chapters.get(getAdapterPosition()).getId());
                intent.putExtra("titleHome", chapters.get(getAdapterPosition()).getTitle());
                activity.startActivity(intent);
            }
        }
    }
}
