package com.bkic.lymenglong.audiobookbkic.Models.Customizes;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.bkic.lymenglong.audiobookbkic.R;


public class CustomActionBar implements CustomActionBarImp, View.OnClickListener{
    private View imBack, imRefresh;
    private TextView tvToolbar;
    private Activity activity;
    /**
     * Custom actionbar cho các activity với title và right btn
     * @param activity activity cần dùng bar
     * @param text title header
     * @param hasSearch true nếu muốn có thêm btn search
     */
    @Override
    public void eventToolbar(Activity activity, String text, boolean hasSearch) {
        this.activity = activity;
        imBack = activity.findViewById(R.id.imBack);
        imRefresh = activity.findViewById(R.id.imRefresh);
        tvToolbar = activity.findViewById(R.id.tvToolbar);

        tvToolbar.setText(text);
        if(hasSearch) {
            imRefresh.setVisibility(View.VISIBLE);
        }else {
            imRefresh.setVisibility(View.GONE);
        }
        imBack.setOnClickListener(this);
        imRefresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == imBack) {
            activity.onBackPressed();
        }
        if(view == imRefresh){
            //todo: refresh list
//            presenter.RefreshContent(activity);

        }
    }
}
