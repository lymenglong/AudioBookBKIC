package com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.Models.Https.HttpParse;
import com.bkic.lymenglong.audiobookbkic.Models.Https.HttpServicesClass;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListBook.ListBook;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListCategory.ListCategory;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListBookType.ListBookType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PresenterShowList implements PresenterShowListImp{

    private ListBookType listBookTypeActivity;
    private ListCategory listCategoryActivity;
    private ListBook listBookActivity;
    private ProgressDialog pDialog;

    public PresenterShowList(ListBookType listBookTypeActivity) {
        this.listBookTypeActivity = listBookTypeActivity;
    }

    public PresenterShowList(ListCategory listCategoryActivity) {
        this.listCategoryActivity = listCategoryActivity;
    }

    public PresenterShowList(ListBook listBookActivity) {
        this.listBookActivity = listBookActivity;
    }

    @Override
    public void GetDataResponse(String httpUrl) {
        new GetHttpResponse(listBookTypeActivity).execute(httpUrl);
    }

    @Override
    public void GetSelectedResponse(Activity activity, String keyPost, String idPost, String HttpHolder) {
        HttpWebCall(activity, keyPost, idPost, HttpHolder);
    }

    //region JSON parse class started from here.
    @SuppressLint("StaticFieldLeak")
    private class GetHttpResponse extends AsyncTask<String, Void, Void>
    {
        public Context context;

        String JSonResult;

        GetHttpResponse(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... httpUrl)
        {
            // Passing HTTP URL to HttpServicesClass Class.
            HttpServicesClass httpServicesClass = new HttpServicesClass(httpUrl[0]);
            try
            {
                httpServicesClass.ExecutePostRequest();

                if(httpServicesClass.getResponseCode() == 200)
                {
                    JSonResult = httpServicesClass.getResponse();

                    if(JSonResult != null)
                    {
                        JSONArray jsonArray;

                        try {
                            jsonArray = new JSONArray(JSonResult);

                            JSONObject jsonObject;

                            for(int i=0; i<jsonArray.length(); i++)
                            {

                                jsonObject = jsonArray.getJSONObject(i);

                                listBookTypeActivity.SetTableData(jsonObject);

                            }
                        }
                        catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                else
                {
                    Toast.makeText(context, httpServicesClass.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            listBookTypeActivity.ShowListBookType();
        }
    }
    //endregion

    //region Method to show current record Current Selected Record
    private String FinalJSonObject;
    private HashMap<String, String> ResultHash = new HashMap<>();
    private String ParseResult;
    private HttpParse httpParse = new HttpParse();
    private void HttpWebCall(final Activity activity, final String keyPost, final String idPost, final String httpHolder){

        @SuppressLint("StaticFieldLeak")
        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = ProgressDialog.show(activity,"Loading Data","Please wait",true,true);
            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put(keyPost,params[0]);

                ParseResult = httpParse.postRequest(ResultHash, httpHolder);

                return ParseResult;
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;
                //Parsing the Stored JSOn String to GetHttpResponse Method.
                pDialog.dismiss();
                new GetHttpResponseFromHttpWebCall(activity).execute();
            }

        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(idPost);
    }

    //region Parsing Complete JSON Object.
    @SuppressLint("StaticFieldLeak")
    private class GetHttpResponseFromHttpWebCall extends AsyncTask<Void, Void, Void>
    {
        public Activity activity;

        ArrayList<Chapter> tempArray;

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
        protected Void doInBackground(Void... arg0)
        {
            try
            {
                if(FinalJSonObject != null && !FinalJSonObject.equals("No Results Found.")) //When no data, it will return "No Results Found." Value to String JSONObject.
                {
                    JSONArray jsonArray;

                    try {
                        jsonArray = new JSONArray(FinalJSonObject);

                        JSONObject jsonObject;

                        tempArray = new ArrayList<>();

                        if (activity == listBookTypeActivity){
                            //compare if data on server is less than phone we del data from phone
                            listBookTypeActivity.CompareDataPhoneWithServer(jsonArray);

                            for(int i=0; i<jsonArray.length(); i++)
                            {
                                jsonObject = jsonArray.getJSONObject(i);

                                listBookTypeActivity.SetTableSelectedData(jsonObject);

                            }
                        }
                        if (activity == listCategoryActivity){
                            //compare if data on server is less than phone we del data from phone
                            listCategoryActivity.CompareDataPhoneWithServer(jsonArray);

                            for(int i=0; i<jsonArray.length(); i++)
                            {
                                jsonObject = jsonArray.getJSONObject(i);

                                listCategoryActivity.SetTableSelectedData(jsonObject);

                            }
                        }
                        if (activity == listBookActivity){
                            //compare if data on server is less than phone we del data from phone
                            listBookActivity.CompareDataPhoneWithServer(jsonArray);

                            for(int i=0; i<jsonArray.length(); i++)
                            {
                                jsonObject = jsonArray.getJSONObject(i);

                                listBookActivity.SetTableSelectedData(jsonObject);

                            }
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
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            if(activity == listBookTypeActivity){
                listBookTypeActivity.ShowListFromSelected();
            }
            if(activity == listCategoryActivity){
                listCategoryActivity.ShowListFromSelected();
            }
            if(activity == listBookActivity){
                listBookActivity.ShowListFromSelected();
            }
        }
    }
    //endregion

    //endregion
}
