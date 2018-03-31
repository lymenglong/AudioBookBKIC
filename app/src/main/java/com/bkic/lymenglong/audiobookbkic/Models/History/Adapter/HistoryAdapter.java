package com.bkic.lymenglong.audiobookbkic.Models.History.Adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bkic.lymenglong.audiobookbkic.Models.History.Utils.History;
import com.bkic.lymenglong.audiobookbkic.Views.Player.PlayControl;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.Views.Reading.ViewReading;

import java.util.ArrayList;


public class HistoryAdapter extends RecyclerView.Adapter {
    private ArrayList<History> indices;
    private Activity activity;
    private int getIdChapter, getPauseTime;
    private String getTitleChapter,getContentChapter, getFileUrlChapter;

    public HistoryAdapter(Activity activity, ArrayList<History> indices) {
        this.indices = indices;
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

            chapterHolder.name.setText(indices.get(position).getTitle());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return indices.size();
    }

    class ChapterHolder extends RecyclerView.ViewHolder {

        private TextView name;
//        private ImageView imgNext;

        ChapterHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameStory);
//            imgNext = itemView.findViewById(R.id.imgNext);

            itemView.setOnClickListener(onClickListener);
            itemView.setOnLongClickListener(onLongClickListener);
        }
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view == itemView) {
                    getIdChapter = indices.get(getAdapterPosition()).getId();
                    getTitleChapter = indices.get(getAdapterPosition()).getTitle();
                    getContentChapter = indices.get(getAdapterPosition()).getContent();
                    getFileUrlChapter = indices.get(getAdapterPosition()).getFileUrl();
                    getPauseTime = indices.get(getAdapterPosition()).getPauseTime();
                    showAlertDialog();
                }
            }
        };
        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, "Onlongclicklistener", Toast.LENGTH_SHORT).show();
                return true;
            }
        };
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
                intent.putExtra("pauseTime", getPauseTime);
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
