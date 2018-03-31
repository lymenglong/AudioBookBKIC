package com.bkic.lymenglong.audiobookbkic.Presenters.Account.Login;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bkic.lymenglong.audiobookbkic.Models.Account.Login.Session;
import com.bkic.lymenglong.audiobookbkic.Models.Account.Utils.User;
import com.bkic.lymenglong.audiobookbkic.Views.Account.Login.ViewLoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.HttpURL_GetUser;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.HttpURL_Login;


public class PresenterLoginLogic implements PresenterLoginImp {
    private ViewLoginActivity loginActivity;
    private Session session;

    public PresenterLoginLogic(ViewLoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }


    @Override
    public void Login(String email, String password) {
        RequestLogin(email,password);
    }

    private void RequestLogin(final String email, final String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(loginActivity);
        session = new Session(loginActivity);
        StringRequest request = new StringRequest(Request.Method.POST, HttpURL_Login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {
//                        Toast.makeText(loginActivity,"Thành công, "+jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                        getJSON(HttpURL_GetUser, email);
                        loginActivity.LoginSuccess();
                    } else {
                        // Snack Bar to show success message that record is wrong
//                                Snackbar.make(nestedScrollView, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
                        Toast.makeText(loginActivity, "Lỗi, " + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loginActivity.LoginFailed();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Email", email);
                hashMap.put("Password", password);

                return hashMap;
            }
        };

        requestQueue.add(request);
    }

    private void getJSON(final String urlWebService, final String email) {

        @SuppressLint("StaticFieldLeak")
        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    getUser(s, email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    java.net.URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json).append("\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void getUser(String json, String textEmail) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
//        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            if (obj.getString("Email").trim().equals(textEmail)
                    || obj.getString("Username").trim().equals(textEmail) ) {
//                userModel = new User(obj.getInt("Id"), obj.getString("Fullname"),obj.getString("Email"));
                User userModel = new User();
                userModel.setId(Integer.parseInt(obj.getString("Id")));
                userModel.setName(obj.getString("Fullname"));
                userModel.setEmail(obj.getString("Email"));
                userModel.setPassword(obj.getString("Password"));
                userModel.setAddress(obj.getString("Address"));
                userModel.setIdentitynumber(obj.getString("IdentityNumber"));
                userModel.setBirthday(obj.getString("Birthday"));
                userModel.setPhonenumber(obj.getString("PhoneNumber"));
                userModel.setUsername(obj.getString("Username"));
                // add to list
//                users.add(userModel);
                session.setUserInfo(userModel);
                session.setUserIdLoggedIn(String.valueOf(userModel.getId()));
                session.setNameLoggedIn(userModel.getName());
                session.setLoggedin(true);
                session.getUserInfo();
                break;
            }
        }
    }


}
