package com.bkic.lymenglong.audiobookbkic.Views.Account.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Models.Account.Login.InputValidation;
import com.bkic.lymenglong.audiobookbkic.Models.Account.Login.Session;
import com.bkic.lymenglong.audiobookbkic.Models.CheckInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.Presenters.Account.Login.PresenterLogin;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.Views.Account.Register.ViewRegisterActivity;
import com.bkic.lymenglong.audiobookbkic.Views.Main.MainActivity;


public class ViewLoginActivity extends AppCompatActivity implements ViewLoginImp, View.OnClickListener{
    private PresenterLogin presenter = new PresenterLogin(this);

    private Activity activity = ViewLoginActivity.this;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;
    private InputValidation inputValidation;

    private AppCompatTextView textViewLinkRegister;

    // variable to track event time
    private long mLastClickTime = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initObjects();
        initListener();
    }

    private void initListener() {
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
    }
    private void initObjects() {
        inputValidation = new InputValidation(activity);
        Session session = new Session(this);
        //Intent into MainActivity when session.loggedin = true.
        if(session.loggedin()){
            startActivity(new Intent(ViewLoginActivity.this, MainActivity.class));
            finish();
        }
    }

    /**
     * This method is to initialize views
     */
    private void initViews() {

//        NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);

        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);

        textInputEditTextEmail = findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = findViewById(R.id.textInputEditTextPassword);

        appCompatButtonLogin = findViewById(R.id.appCompatButtonLogin);

        textViewLinkRegister = findViewById(R.id.textViewLinkRegister);

    }

    @Override
    public void onClick(View v) {
        /////tranh viec bấm nút liên tuc trong 1s/////
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        /////////////////////////////////////////////
        switch (v.getId()){
            case R.id.appCompatButtonLogin:

                String textEmail;
                if (!inputValidation.isInputEditTextFilled(
                        textInputEditTextEmail,
                        textInputLayoutEmail,
                        getString(R.string.error_message_email_or_username)))
                {
                    break;
                }   else{
                    textEmail = textInputEditTextEmail.getText().toString().trim();
                }
                String textPassword;
                if (!inputValidation.isInputEditTextFilled(
                        textInputEditTextPassword,
                        textInputLayoutPassword,
                        getString(R.string.error_message_password)))
                {
                    break;
                } else {
                    textPassword = textInputEditTextPassword.getText().toString();
                }
                //check internet connection before request to server
                if (ConnectivityReceiver.isConnected()) {
                    findViewById(R.id.appCompatButtonLogin).setEnabled(false);
                    presenter.Login(textEmail, textPassword);
                } else {
                    Toast.makeText(activity, "Please check internet connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.textViewLinkRegister:
                Intent intentRegister = new Intent(getApplicationContext(), ViewRegisterActivity.class);
                startActivity(intentRegister);
                activity.finish();
                break;
        }
    }

    @Override
    public void LoginSuccess(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        Intent accountsIntent = new Intent(activity, MainActivity.class);
        accountsIntent.putExtra("EMAIL", textInputEditTextEmail.getText().toString().trim());
        startActivity(accountsIntent);
        activity.finish();
    }

    @Override
    public void LoginFailed(String message) {
        findViewById(R.id.appCompatButtonLogin).setEnabled(true);
        Toast.makeText(ViewLoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}


