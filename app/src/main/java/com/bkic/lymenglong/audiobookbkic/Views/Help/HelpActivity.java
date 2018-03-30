package com.bkic.lymenglong.audiobookbkic.Views.Help;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Presenters.Help.PresenterHelp;
import com.bkic.lymenglong.audiobookbkic.R;



public class HelpActivity extends AppCompatActivity implements HelpImp{
    PresenterHelp presenterHelp = new PresenterHelp(this);
    private TextView tvReadFile;
    private String titleHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        getDataFromIntent();
        initView();
        initObject();
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
}
