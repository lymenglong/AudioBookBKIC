package com.bkic.lymenglong.audiobookbkic.Models.HandleLists.History.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Book;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListChapter.ListChapter;

import java.util.ArrayList;

//import android.widget.ImageView;


public class HistoryAdapter extends RecyclerView.Adapter {
    private ArrayList<Book> books;
    private Activity activity;
    private int bookId, bookLength;
    private String bookTitle, bookImage, bookAuthor;

    public HistoryAdapter(Activity activity, ArrayList<Book> books) {
        this.books = books;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ChapterHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChapterHolder) {
            ChapterHolder chapterHolder = (ChapterHolder) holder;

            chapterHolder.name.setText(books.get(position).getTitle());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return books.size();
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
                    bookId = books.get(getAdapterPosition()).getId();
                    bookTitle = books.get(getAdapterPosition()).getTitle();
                    bookImage = books.get(getAdapterPosition()).getUrlImage();
                    bookLength = books.get(getAdapterPosition()).getLength();
                    bookAuthor = books.get(getAdapterPosition()).getAuthor();
                    IntentActivity(activity,ListChapter.class);
//                    showAlertDialog();
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

    private void IntentActivity(Activity activity, Class classIntent) {
        Intent intent = new Intent(activity, classIntent);
        intent.putExtra("BookId", bookId);
        intent.putExtra("BookTitle", bookTitle);
        intent.putExtra("BookImage", bookImage);
        intent.putExtra("BookLength", bookLength);
        intent.putExtra("BookAuthor", bookAuthor);
        activity.startActivity(intent);
    }

/*    private void showAlertDialog(){
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
                intent.putExtra("idChapter", bookId);
                intent.putExtra("titleChapter", bookTitle);
                intent.putExtra("content", bookImage);
                intent.putExtra("fileUrl", bookAuthor);
                activity.startActivity(intent);

            }
        });
        builder.setNegativeButton("Ghi âm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(activity, "Dạng ghi âm", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
                IntentActivity(activity, ListChapter.class);
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

    }*/
}
