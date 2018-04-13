package com.bkic.lymenglong.audiobookbkic.Views.Account.ShowUserInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Views.Main.MainActivity;
import com.bkic.lymenglong.audiobookbkic.Models.Account.Login.Session;
import com.bkic.lymenglong.audiobookbkic.Models.Account.ShowUserInfo.Adapter.UserInfoRecyclerAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.Account.Utils.User;
import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Presenters.Account.ShowUserInfo.PresenterUserInfo;
import com.bkic.lymenglong.audiobookbkic.R;
import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity implements UserInfoImp, View.OnClickListener{
    PresenterUserInfo presenterUserInfo = new PresenterUserInfo(this);
    private AppCompatActivity activity = UserInfoActivity.this;
    private AppCompatTextView textViewName;
    private AppCompatButton btnLogout;
    private RecyclerView recyclerViewUsers;
    private List<User> listUsers;
    private UserInfoRecyclerAdapter userInfoRecyclerAdapter;
    private Session session;
    private String message;
    private String menuTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initDataFromIntent();
        initToolbar();
        initViews();
        initObjects();
        initListener();

        new getDataFromPrefs(this).execute();
    }

    private void initDataFromIntent() {
        menuTitle = getIntent().getStringExtra("MenuTitle");
    }

    /**
     * This method is to initialize views
     */
    private void initViews() {
        textViewName = findViewById(R.id.textViewName);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        btnLogout = findViewById(R.id.btn_logout);
        //make talk don't move to toolbar
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar),
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    /**
     * This method is to initialize objects to be used
     */
    @SuppressLint("SetTextI18n")
    private void initObjects() {
        session = new Session(this);
        listUsers = new ArrayList<>();
        userInfoRecyclerAdapter = new UserInfoRecyclerAdapter(listUsers);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewUsers.setLayoutManager(mLayoutManager);
        recyclerViewUsers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setAdapter(userInfoRecyclerAdapter);

        String Name = session.getFullName();
        textViewName.setText(getString(R.string.text_hello)+" "+ Name.toUpperCase());

    }

    private void initListener() {
        btnLogout.setOnClickListener(this);
    }

    /**
    *This will clear the data and remove your app from memory.
    *It is equivalent to clear data option under Settings --> Application Manager --> Your App --> Clear data
    *todo: clear app data
    **/
/*    private void deleteAppData() {
        try {
            // clearing app data
            String packageName = getApplicationContext().getPackageName();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear "+packageName);

        } catch (Exception e) {
            e.printStackTrace();
        } }*/

    private void initToolbar() {
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, menuTitle, false);
    }

    /**
     * This method is to fetch all user records from Server
     */
    @SuppressLint("StaticFieldLeak")
    private class getDataFromPrefs extends AsyncTask<Void, Void, Void>
    {
        // AsyncTask is used that SQLite operation not blocks the UI Thread.
        Context context;

        getDataFromPrefs(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            listUsers.clear();
            listUsers.addAll(session.getListUserInfo());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            userInfoRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_logout :
                presenterUserInfo.ShowAlertLogoutDialog();
                break;
        }
    }

    @Override
    public void LogoutFailed() {
        message = getString(R.string.message_logout_failed);
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void LogoutSuccess() {
        session.getClearSession();
        session = new Session(this);
        session.setLoggedin(false);
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// This flag ensures all activities on top of the MainActivity are cleared.
        intent.putExtra("EXIT", true);
        message = getString(R.string.message_logout_success);
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        String TAG = "UserInfoActivity";
        Log.d(TAG, "LogoutSuccess");
        activity.startActivity(intent);
        activity.finish();
        //todo: Drop Database on phone when logout
    }
}
