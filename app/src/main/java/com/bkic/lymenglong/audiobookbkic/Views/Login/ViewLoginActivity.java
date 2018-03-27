package com.bkic.lymenglong.audiobookbkic.Views.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.MainActivity;
import com.bkic.lymenglong.audiobookbkic.Models.Login.InputValidation;
import com.bkic.lymenglong.audiobookbkic.Models.Login.Session;
import com.bkic.lymenglong.audiobookbkic.Presenters.Login.PresenterLoginLogic;
import com.bkic.lymenglong.audiobookbkic.R;


public class ViewLoginActivity extends AppCompatActivity implements ViewLoginImp, View.OnClickListener{
    private Activity activity = ViewLoginActivity.this;

    private String textEmail;
    private String textPassword;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;
    private InputValidation inputValidation;

    private Intent accountsIntent;
    private AppCompatTextView textViewLinkRegister;
    private PresenterLoginLogic presenter = new PresenterLoginLogic(this);
    private Session session;


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
//        session = new Session(this);
/*        if(session.loggedin()){
            startActivity(new Intent(ViewLoginActivity.this, MainActivity.class));
            finish();
        }*/
    }

    /**
     * This method is to initialize views
     */
    private void initViews() {

        nestedScrollView = findViewById(R.id.nestedScrollView);

        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);

        textInputEditTextEmail = findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = findViewById(R.id.textInputEditTextPassword);

        appCompatButtonLogin = findViewById(R.id.appCompatButtonLogin);

        textViewLinkRegister = findViewById(R.id.textViewLinkRegister);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.appCompatButtonLogin:
                if (!inputValidation.isInputEditTextFilled(
                        textInputEditTextEmail,
                        textInputLayoutEmail,
                        getString(R.string.error_message_email_or_username)))
                {
                    return;

                }   else{
                    textEmail = textInputEditTextEmail.getText().toString().trim();
                }
                if (!inputValidation.isInputEditTextFilled(
                        textInputEditTextPassword,
                        textInputLayoutPassword,
                        getString(R.string.error_message_password)))
                {
                    return;
                } else {
                    textPassword = textInputEditTextPassword.getText().toString();
                }

                presenter.Login(textEmail,textPassword);

                break;
            case R.id.textViewLinkRegister:
                //todo: Intent to register activity
                break;
        }
    }

    @Override
    public void LoginSuccess() {
//        Toast.makeText(activity, "Login Success", Toast.LENGTH_SHORT).show();
        accountsIntent = new Intent(activity, MainActivity.class);
        accountsIntent.putExtra("EMAIL", textInputEditTextEmail.getText().toString().trim());
        startActivity(accountsIntent);
        activity.finish();
    }

    @Override
    public void LoginFailed() {
        Toast.makeText(activity, "Login Failed", Toast.LENGTH_SHORT).show();
    }
}


