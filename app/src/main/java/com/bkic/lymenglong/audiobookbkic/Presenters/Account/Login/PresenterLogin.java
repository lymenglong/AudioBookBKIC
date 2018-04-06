package com.bkic.lymenglong.audiobookbkic.Presenters.Account.Login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;

import com.bkic.lymenglong.audiobookbkic.Models.Account.Login.Session;
import com.bkic.lymenglong.audiobookbkic.Models.Account.Utils.User;
import com.bkic.lymenglong.audiobookbkic.Models.Https.HttpParse;
import com.bkic.lymenglong.audiobookbkic.Views.Account.Login.ViewLoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.HttpURL_API;


public class PresenterLogin implements PresenterLoginImp {
    private ViewLoginActivity loginActivity;
    private Session session;

    public PresenterLogin(ViewLoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }


    @Override
    public void Login(String email, String password) {
        RequestLogin(email,password);
    }

    @Override
    public void UserDetail(String email){
        HashMap<String, String> ResultHash = new HashMap<>();
        // GetUserDetail
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\": \"getUserDetail\", " +
                        "\"UserName\": \"" + email + "\" " +
                "}";
        ResultHash.put(keyPost,valuePost);
        HttpWebCall(loginActivity,ResultHash,HttpURL_API);
    }

    private void RequestLogin(final String email, final String password) {
        HashMap<String,String> ResultHash = new HashMap<>();
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\": \"login\", " +
                        "\"UserName\": \""+email+"\", " +
                        "\"UserPassword\": \""+password+"\"" +
                "}";
        ResultHash.put(keyPost,valuePost);
        HttpWebCall(loginActivity,ResultHash,HttpURL_API);
    }

    //region Method to show current record Current Selected Record
    private String FinalJSonObject;
    private String ParseResult;
    private HttpParse httpParse = new HttpParse();
    private void HttpWebCall(final Activity activity, final HashMap<String,String> ResultHash, final String httpHolder){

        @SuppressLint("StaticFieldLeak")
        class HttpWebCallFunction extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                pDialog = ProgressDialog.show(activity,"Loading Data","Please wait",true,true);
            }

            @Override
            protected String doInBackground(Void... voids) {

                ParseResult = httpParse.postRequest(ResultHash, httpHolder);

                return ParseResult;
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;
                //Parsing the Stored JSOn String to GetHttpResponse Method.
//                pDialog.dismiss();
                new GetHttpResponseFromHttpWebCall(activity).execute();
            }

        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute();
    }

    //region Parsing Complete JSON Object.
    @SuppressLint("StaticFieldLeak")
    private class GetHttpResponseFromHttpWebCall extends AsyncTask<Void, Void, String>
    {
        public Activity activity;

        Boolean LogInSuccess = false;

        ArrayList<User> tempArray;

        String ResultJsonObject;

        String Result = null;

        GetHttpResponseFromHttpWebCall(Activity activity)
        {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0)
        {
            try
            {
                if(FinalJSonObject != null && !FinalJSonObject.equals("No Results Found.")) //When no data, it will return "No Results Found." Value to String JSONObject.
                {
                    JSONObject jsonObject;
                    JSONArray jsonArrayResult;

                    try {
                        jsonObject = new JSONObject(FinalJSonObject);

                        tempArray = new ArrayList<>();

                        ResultJsonObject = jsonObject.getString("Result");

                        try {
                            jsonArrayResult = new JSONArray(ResultJsonObject);

                            if (jsonArrayResult.length()==0) {

                                LogInSuccess = jsonObject.getString("Log").equals("Success");

                                Result = null;

                            }
                        } catch (JSONException ignored) {
                            Result = ResultJsonObject;
                        }

                    }
                    catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return Result;
        }

        @Override
        protected void onPostExecute(String Result)
        {
            if(Result == null) {
                session = new Session(loginActivity);
                if (LogInSuccess) {
                    session.setLoggedin(true);
                    loginActivity.LoginSuccess();
                } else {
                    loginActivity.LoginFailed();
                }
            } else {
                try {
                    GetUserDetail(Result);
                } catch (JSONException ignored) {
                }
            }
        }
    }

    private void GetUserDetail(String ResultJsonObject) throws JSONException {
        JSONObject jsonObjectResult = new JSONObject(ResultJsonObject);
        if (jsonObjectResult.length()!=0){
            User userModel = new User();
            userModel.setId(Integer.parseInt(jsonObjectResult.getString("UserId")));
//            userModel.setName(jsonObjectResult.getString("UserFullName"));
            userModel.setEmail(jsonObjectResult.getString("UserMail"));
//            userModel.setPassword(jsonObjectResult.getString("Password"));
            userModel.setAddress(jsonObjectResult.getString("UserAddress"));
//            userModel.setIdentitynumber(jsonObjectResult.getString("IdentityNumber"));
//            userModel.setBirthday(jsonObjectResult.getString("Birthday"));
            userModel.setPhonenumber(jsonObjectResult.getString("UserPhone"));
            userModel.setUsername(jsonObjectResult.getString("UserName"));
            // add to list
    //                           users.add(userModel);
//            session = new Session(loginActivity);
            session.setUserInfo(userModel);
            session.setUserIdLoggedIn(String.valueOf(userModel.getId()));
            session.setNameLoggedIn(userModel.getName());
            session.getUserInfo();
        }
    }
    //endregion
    //endregion

}
