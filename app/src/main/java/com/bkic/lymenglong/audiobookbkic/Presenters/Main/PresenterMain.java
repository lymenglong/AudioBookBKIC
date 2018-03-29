package com.bkic.lymenglong.audiobookbkic.Presenters.Main;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Models.Https.HttpServicesClass;
import com.bkic.lymenglong.audiobookbkic.Views.Main.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PresenterMain implements PresenterMainImp {
    MainActivity mainActivity;
    String HttpUrl = "http://20121969.tk/SachNoiBKIC/AllMenuData.php";
    public PresenterMain(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void GetHttpResponse() {
        new GetHttpResponse(mainActivity).execute();
    }

    //region JSON parse class started from here.
    private class GetHttpResponse extends AsyncTask<Void, Void, Void> {

        public Context context;
        String JSonResult;

        public GetHttpResponse(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mainActivity.ShowProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Passing HTTP URL to HttpServicesClass Class.
            HttpServicesClass httpServicesClass = new HttpServicesClass(HttpUrl);
            try {
                httpServicesClass.ExecutePostRequest();

                if (httpServicesClass.getResponseCode() == 200) {
                    JSonResult = httpServicesClass.getResponse();

                    if (JSonResult != null) {
                        JSONArray jsonArray = null;

                        try {
                            jsonArray = new JSONArray(JSonResult);

                            JSONObject jsonObject;

                            for (int i = 0; i < jsonArray.length(); i++) {

                                jsonObject = jsonArray.getJSONObject(i);

                                // Adding data TO IdList Array.
                                mainActivity.SetMenuData(jsonObject);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(context, httpServicesClass.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mainActivity.ShowListMenu();

        }
    }
    //endregion
}
