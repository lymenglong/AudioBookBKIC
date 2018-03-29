package com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.Views.Player.PlayControl;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.Views.Reading.ViewReading;

import java.util.ArrayList;


public class FavoriteAdapter extends RecyclerView.Adapter {
    private ArrayList<Chapter> chapters;
    private Activity activity;
    private View view;
    private int getIdChapter;
    private String getTitleChapter,getContentChapter, getFileUrlChapter;

    public FavoriteAdapter(Activity activity, ArrayList<Chapter> chapters) {
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

        ChapterHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameStory);
            imgNext = itemView.findViewById(R.id.imgNext);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == itemView) {
                getIdChapter = chapters.get(getAdapterPosition()).getId();
                getTitleChapter = chapters.get(getAdapterPosition()).getTitle();
                getContentChapter = chapters.get(getAdapterPosition()).getContent();
                getFileUrlChapter = chapters.get(getAdapterPosition()).getFileUrl();
                showAlertDialog();
            }
        }
    }

    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle("Chọn Dạng Sách");
        builder.setMessage("Bạn muốn chọn dạng nào?");
        builder.setCancelable(false);
        builder.setPositiveButton("Văn bản", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(activity, "Dạng văn bản", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
                Intent intent = new Intent(activity, ViewReading.class);
                intent.putExtra("idChapter", getIdChapter);
                intent.putExtra("titleChapter", getTitleChapter);
                intent.putExtra("content", getContentChapter);
                intent.putExtra("fileUrl", getFileUrlChapter);
                activity.startActivity(intent);

            }
        });
        builder.setNegativeButton("Ghi âm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(activity, "Dạng ghi âm", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
                Intent intent = new Intent(activity, PlayControl.class);
                intent.putExtra("idChapter", getIdChapter);
                intent.putExtra("titleChapter", getTitleChapter);
                intent.putExtra("content", getContentChapter);
                intent.putExtra("fileUrl", getFileUrlChapter);
                activity.startActivity(intent);
            }
        });
        builder.setNeutralButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
