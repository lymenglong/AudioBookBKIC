package com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Category;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListBook.ListBook;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListCategory.ListCategory;

import java.util.ArrayList;


public class CategoryAdapter extends RecyclerView.Adapter {
    private ArrayList<Category> categories;
    private Activity activity;

    public CategoryAdapter(Activity activity, ArrayList<Category> categories) {
        this.categories = categories;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ChapterHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChapterHolder) {
            ChapterHolder chapterHolder = (ChapterHolder) holder;

            chapterHolder.name.setText(categories.get(position).getTitle());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class ChapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
//        private ImageView imgNext;

        ChapterHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameStory);
//            imgNext = itemView.findViewById(R.id.imgNext);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == itemView) {
                int numOfChild = categories.get(getAdapterPosition()).getNumOfChild();
                if (numOfChild != 0) {
                    Intent intent = new Intent(activity, ListCategory.class);
                    intent.putExtra("CategoryId", categories.get(getAdapterPosition()).getId());
                    intent.putExtra("CategoryTitle", categories.get(getAdapterPosition()).getTitle());
                    intent.putExtra("CategoryDescription", categories.get(getAdapterPosition()).getDescription());
                    intent.putExtra("CategoryParent", categories.get(getAdapterPosition()).getParentId());
                    intent.putExtra("NumOfChild", categories.get(getAdapterPosition()).getNumOfChild());
                    activity.startActivity(intent);
                } else {
                    Intent intent = new Intent(activity, ListBook.class);
                    intent.putExtra("CategoryId", categories.get(getAdapterPosition()).getId());
                    intent.putExtra("CategoryTitle", categories.get(getAdapterPosition()).getTitle());
                    intent.putExtra("CategoryDescription", categories.get(getAdapterPosition()).getDescription());
                    intent.putExtra("CategoryParent", categories.get(getAdapterPosition()).getParentId());
                    intent.putExtra("NumOfChild", categories.get(getAdapterPosition()).getNumOfChild());
                    activity.startActivity(intent);
                }

            }
        }
    }
}
