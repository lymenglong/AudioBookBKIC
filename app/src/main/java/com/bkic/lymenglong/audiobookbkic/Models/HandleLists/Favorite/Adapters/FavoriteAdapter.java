package com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Favorite.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Models.Database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Book;
import com.bkic.lymenglong.audiobookbkic.Models.Utils.Const;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListChapter.ListChapter;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter {
    private ArrayList<Book> books;
    private Activity activity;
    private Book bookModel;
    private int adapterPosition;
    //    private int getIdChapter;
//    private String getTitleChapter,getContentChapter, getFileUrlChapter;

    public FavoriteAdapter(Activity activity, ArrayList<Book> books) {
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

    class ChapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView name;
//        private ImageView imgNext;

        ChapterHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameStory);
//            imgNext = itemView.findViewById(R.id.imgNext);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            bookModel = new Book(
                    books.get(getAdapterPosition()).getId(),
                    books.get(getAdapterPosition()).getTitle(),
                    books.get(getAdapterPosition()).getUrlImage(),
                    books.get(getAdapterPosition()).getLength(),
                    books.get(getAdapterPosition()).getAuthor()
            );
            adapterPosition = getAdapterPosition();
            showAlertDialog();
            return true;
        }

        @Override
        public void onClick(View view) {
            if(view == itemView) {
                /*
                getIdChapter = books.get(getAdapterPosition()).getId();
                getTitleChapter = books.get(getAdapterPosition()).getTitle();
                getContentChapter = books.get(getAdapterPosition()).getContent();
                getFileUrlChapter = books.get(getAdapterPosition()).getFileUrl();
                showAlertDialog();
                */
                Intent intent = new Intent(activity, ListChapter.class);
                intent.putExtra("BookId", books.get(getAdapterPosition()).getId());
                intent.putExtra("BookTitle", books.get(getAdapterPosition()).getTitle());
                intent.putExtra("BookImage", books.get(getAdapterPosition()).getUrlImage());
                intent.putExtra("BookLength", books.get(getAdapterPosition()).getLength());
                intent.putExtra("BookAuthor", books.get(getAdapterPosition()).getAuthor());
                activity.startActivity(intent);
            }
        }
    }

    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle("Chọn Dạng Sách");
        builder.setMessage("Bạn muốn xóa khỏi danh sách không?");
        builder.setCancelable(false);
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                books.remove(adapterPosition);
                notifyDataSetChanged();
                Toast.makeText(activity, bookModel.getTitle()+" Đã Xóa", Toast.LENGTH_SHORT).show();
                RemoveFavoriteData(bookModel.getId());
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(activity, "Đã Kích Không", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void RemoveFavoriteData(int bookId){
        DBHelper dbHelper = new DBHelper(activity, Const.DB_NAME, null, Const.DB_VERSION);
        dbHelper.QueryData("UPDATE favorite SET BookRemoved = '"+Const.BOOK_REQUEST_REMOVE_WITH_SERVER+"' WHERE BookId = '"+bookId+"'");

        try {
            dbHelper.QueryData(
                    "INSERT INTO bookFavoriteSyncs " +
                            "VALUES " +
                            "(" +
                                    "'"+bookId+"', " +
                                    "'"+Const.BOOK_SYNCED_WITH_SERVER+"', " +
                                    "'"+Const.BOOK_REQUEST_REMOVE_WITH_SERVER+"'" +
                            ")" +
                        ";"
            );
        } catch (Exception ignored) {
            dbHelper.QueryData(
                    "UPDATE bookFavoriteSyncs " +
                            "SET " +
                            "BookSync = '"+Const.BOOK_SYNCED_WITH_SERVER+"', " +
                            "BookRemoved = '"+Const.BOOK_REQUEST_REMOVE_WITH_SERVER+"' " +
                            "WHERE BookId = '"+bookId+"'" +
                            ";"
            );
        }

        dbHelper.QueryData("DELETE FROM favorite WHERE BookId = '"+bookId+"'");

        dbHelper.close();
    }
}
