package com.bkic.lymenglong.audiobookbkic.Views.Account.Register;

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

import com.bkic.lymenglong.audiobookbkic.Models.Account.Login.InputValidation;
import com.bkic.lymenglong.audiobookbkic.Models.Account.User;
import com.bkic.lymenglong.audiobookbkic.Presenters.Account.Register.PresenterRegisterLogic;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.Views.Account.Login.ViewLoginActivity;


public class ViewRegisterActivity extends AppCompatActivity implements ViewRegisterImp, View.OnClickListener {
    PresenterRegisterLogic presenterRegisterLogic;
    private Activity registerActivity = ViewRegisterActivity.this;
    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutUserName;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;
    private TextInputLayout textInputLayoutAddress;
    private TextInputLayout textInputLayoutPhoneNumber;

    private TextInputEditText textInputEditTextName;
    private TextInputEditText textInputEditTextUserName;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextConfirmPassword;
    private TextInputEditText textInputEditTextAddress;
    private TextInputEditText textInputEditTextPhoneNumber;

    private AppCompatButton appCompatButtonRegister;
    private AppCompatTextView appCompatTextViewLoginLink;

    private InputValidation inputValidation;

    private User userModel;
    private AppCompatTextView textViewLinkLogin;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initObject();
        initListener();
    }

    private void initObject() {
        presenterRegisterLogic = new PresenterRegisterLogic(this);
        inputValidation = new InputValidation(registerActivity);
    }

    private void initView() {
        textViewLinkLogin = findViewById(R.id.appCompatTextViewLoginLink);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        textInputLayoutName = findViewById(R.id.textInputLayoutName);
        textInputLayoutUserName = findViewById(R.id.textInputLayoutUserName);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = findViewById(R.id.textInputLayoutConfirmPassword);
        textInputLayoutAddress = findViewById(R.id.textInputLayoutAddress);
        textInputLayoutPhoneNumber = findViewById(R.id.textInputLayoutPhoneNumber);

        textInputEditTextName = findViewById(R.id.textInputEditTextName);
        textInputEditTextUserName = findViewById(R.id.textInputEditTextUserName);
        textInputEditTextEmail = findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = findViewById(R.id.textInputEditTextPassword);
        textInputEditTextConfirmPassword = findViewById(R.id.textInputEditTextConfirmPassword);
        textInputEditTextAddress = findViewById(R.id.textInputEditTextAddress);
        textInputEditTextPhoneNumber = findViewById(R.id.textInputEditTextPhoneNumber);

        appCompatButtonRegister = findViewById(R.id.appCompatButtonRegister);

        appCompatTextViewLoginLink = findViewById(R.id.appCompatTextViewLoginLink);
    }

    private void initListener() {
        textViewLinkLogin.setOnClickListener(this);
        appCompatButtonRegister.setOnClickListener(this);
    }

    @Override
    public void RegisterSuccess(String message) {
        Toast.makeText(registerActivity,"Thành công: "+message,Toast.LENGTH_SHORT).show();
        startActivity(new Intent(registerActivity,ViewLoginActivity.class));
        registerActivity.finish();
    }

    @Override
    public void RegisterFailed(String message) {
        Toast.makeText(registerActivity, "Lỗi: " +message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.appCompatTextViewLoginLink:
                    IntentViewLoginActivity();
                break;
            case R.id.appCompatButtonRegister:
                userModel = new User();
                if (!inputValidation.isInputEditTextFilled(textInputEditTextName, textInputLayoutName, getString(R.string.error_message_name))) {
                    return;
                } else {
                    userModel.setName(textInputEditTextName.getText().toString());
                }

                if (!inputValidation.isInputEditTextFilled(textInputEditTextUserName, textInputLayoutUserName, getString(R.string.error_message_username))) {
                    return;
                } else {
                    userModel.setUsername(textInputEditTextUserName.getText().toString());
                }

                if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
                    return;
                } else{
                    userModel.setEmail(textInputEditTextEmail.getText().toString());
                }
                if (!inputValidation.isInputEditTextFilled(textInputEditTextPhoneNumber, textInputLayoutPhoneNumber, getString(R.string.error_message_phone_number))) {
                    return;
                } else{
                    userModel.setPhonenumber(textInputEditTextPhoneNumber.getText().toString());
                }
                if (!inputValidation.isInputEditTextFilled(textInputEditTextAddress, textInputLayoutAddress, getString(R.string.error_message_address))) {
                    return;
                } else {
                    userModel.setAddress(textInputEditTextAddress.getText().toString());
                }
                if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
                    return;
                } else{
                  userModel.setPassword(textInputEditTextPassword.getText().toString());
                }
                if (!inputValidation.isInputEditTextMatches(textInputEditTextPassword, textInputEditTextConfirmPassword,
                        textInputLayoutConfirmPassword, getString(R.string.error_password_match))) {
                    return;
                } else userModel.setConfirmPassword(textInputEditTextConfirmPassword.getText().toString());
                presenterRegisterLogic.Register(userModel);
                break;
        }
    }

    private void IntentViewLoginActivity() {
        Intent intentLogin = new Intent(registerActivity, ViewLoginActivity.class);
        startActivity(intentLogin);
        registerActivity.finish();
    }
}
