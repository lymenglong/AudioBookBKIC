package com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListCategory.ListCategory;

import java.util.ArrayList;


public class MenuAdapter extends RecyclerView.Adapter {
    private ArrayList<Chapter> chapters;
    private Activity activity;
    private View view;

    public MenuAdapter(Activity activity, ArrayList<Chapter> chapters) {
        this.chapters = chapters;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ChapterHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChapterHolder) {
            ChapterHolder chapterHolder = (ChapterHolder) holder;

            chapterHolder.name.setText(chapters.get(position).getTitle());
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

    class ChapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private ImageView imgNext;

        public ChapterHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.nameStory);
            imgNext = (ImageView) itemView.findViewById(R.id.imgNext);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == itemView) {
                Intent intent = new Intent(activity, ListCategory.class);
                intent.putExtra("idChapter", chapters.get(getAdapterPosition()).getId());
                intent.putExtra("titleChapter", chapters.get(getAdapterPosition()).getTitle());
                intent.putExtra("content", chapters.get(getAdapterPosition()).getContent());
                activity.startActivity(intent);
            }
        }
    }
}
