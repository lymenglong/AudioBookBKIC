package com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists;

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

    ListBookType listBookTypeActivity;
    ListCategory listCategoryActivity;
    ListBook listBookActivity;

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
    public void GetSelectedResponse(String keyPost, String idPost, String HttpHolder) {
        HttpWebCall(keyPost, idPost, HttpHolder);
    }

    //region JSON parse class started from here.
    private class GetHttpResponse extends AsyncTask<String, Void, Void>
    {
        public Context context;

        String JSonResult;

        public GetHttpResponse(Context context)
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
                        JSONArray jsonArray = null;

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
    public void HttpWebCall(final String keyPost, final String idPost, final String httpHolder){

        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                listBookTypeActivity.ShowProgressDialog();
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                listBookTypeActivity.DismissDialog();

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;

                //Parsing the Stored JSOn String to GetHttpResponse Method.
                new GetHttpResponseFromHttpWebCall(listBookTypeActivity).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put(keyPost,params[0]);

                ParseResult = httpParse.postRequest(ResultHash, httpHolder);

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(idPost);
    }

    //region Parsing Complete JSON Object.
    private class GetHttpResponseFromHttpWebCall extends AsyncTask<Void, Void, Void>
    {
        public Context context;

        ArrayList<Chapter> tempArray;

        public GetHttpResponseFromHttpWebCall(Context context)
        {
            this.context = context;
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
                    JSONArray jsonArray = null;

                    try {
                        jsonArray = new JSONArray(FinalJSonObject);

                        JSONObject jsonObject;

                        Chapter tempModel;
                        tempArray = new ArrayList<>();

                        //compare if data on server is less than phone we del data from phone

                        listBookTypeActivity.CompareDataPhoneWithServer(jsonArray);

                        for(int i=0; i<jsonArray.length(); i++)
                        {
                            jsonObject = jsonArray.getJSONObject(i);

                            listBookTypeActivity.SetTableSelectedData(jsonObject);

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
            listBookTypeActivity.ShowListFromSelected();
        }
    }
    //endregion

    //endregion
}
