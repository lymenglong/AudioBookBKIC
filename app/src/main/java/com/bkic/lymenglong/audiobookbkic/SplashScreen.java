package com.bkic.lymenglong.audiobookbkic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    static private int SPLASH_TIME = 1500;
    private TextView secretTextviewSplash;
    private TextView secretCreatorSplash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //put the splash screen into full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        setTitle(R.string.hint_welcome);
        //Set disable talk back
//        ViewCompat.setImportantForAccessibility(getWindow().getDecorView(),ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
//        getSupportActionBar().hide();
//        bindView();
    }

/*    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //Finish the splash activity so it can't be returned to.
                SplashScreen.this.finish();
                // Create an Intent that will start the main activity.
                Intent mainIntent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(mainIntent);
                //overridePendingTransition(R.anim.bottom_in,R.anim.top_out);
            }
        }, SPLASH_TIME);

    }*/
    private void bindView() {
        secretTextviewSplash = (TextView) findViewById(R.id.secret_textview_splash);
        secretCreatorSplash = (TextView) findViewById(R.id.secret_creator_splash);

        String customHtml = getString(R.string.text_splash);
        String customHtmlCreator = getString(R.string.text_creator);

        secretTextviewSplash.setText(Html.fromHtml(customHtml));
        secretCreatorSplash.setText(Html.fromHtml(customHtmlCreator));

//        secretTextviewSplash.startAnimation(AnimationUtils.loadAnimation(SplashScreen.this, R.anim.fade_in));
        secretCreatorSplash.startAnimation(AnimationUtils.loadAnimation(SplashScreen.this, R.anim.fade_in));
    }
}
