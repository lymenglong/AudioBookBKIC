package com.bkic.lymenglong.audiobookbkic.Views.Help;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.bkic.lymenglong.audiobookbkic.Views.Main.MainActivity;
import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Presenters.Help.PresenterHelp;
import com.bkic.lymenglong.audiobookbkic.R;



public class HelpActivity extends AppCompatActivity implements HelpImp, View.OnClickListener{
    PresenterHelp presenterHelp = new PresenterHelp(this);
    private TextView tvReadFile;
    private String titleHome;
    private View imgBack;
    private Activity activity = HelpActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        getDataFromIntent();
        initView();
        initObject();
        initListener();
    }

    private void initListener() {
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Lấy dữ liệu thông qua intent
     */
    private void getDataFromIntent() {
        titleHome = getIntent().getStringExtra("titleHome");
//        int idHome = getIntent().getIntExtra("idHome", -1);
    }


    private void initObject() {
        presenterHelp.ShowHelp(tvReadFile);
    }


    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void initView() {
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, titleHome, false );
        tvReadFile = findViewById(R.id.tv_read_file);
        imgBack = findViewById(R.id.imBack);
    }

    @Override
    public void ShowHelpDone() {
        String TAG = "HelpActivity";
        Log.d(TAG, "ShowHelpDone");
    }

    @Override
    public void ShowHelpFailed() {
        Toast.makeText(this, "Reading file failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imBack:
                activity.onBackPressed();
                break;
        }
    }
}
