package com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.Models.Https.HttpParse;
import com.bkic.lymenglong.audiobookbkic.Models.Https.HttpServicesClass;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListBook.ListBook;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListCategory.ListCategory;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListMenu.ListMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PresenterShowList implements PresenterShowListImp{

    ListMenu listMenuActivity;
    ListCategory listCategoryActivity;
    ListBook listBookActivity;

    public PresenterShowList(ListMenu listMenuActivity) {
        this.listMenuActivity = listMenuActivity;
    }

    public PresenterShowList(ListCategory listCategoryActivity) {
        this.listCategoryActivity = listCategoryActivity;
    }

    public PresenterShowList(ListBook listBookActivity) {
        this.listBookActivity = listBookActivity;
    }

    @Override
    public void GetBookTypeResponse(String httpUrl) {
        new GetHttpResponse(listMenuActivity).execute(httpUrl);
    }

    @Override
    public void GetSelectedResponse(String PreviousListViewClickedItem, String HttpHolder) {
        HttpWebCall(PreviousListViewClickedItem, HttpHolder);
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

                                listMenuActivity.SetTableData(jsonObject);

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
            listMenuActivity.ShowListBookType();
        }
    }
    //endregion

    //region Method to show current record Current Selected Record
    private String FinalJSonObject;
    private HashMap<String, String> ResultHash = new HashMap<>();
    private String ParseResult;
    private HttpParse httpParse = new HttpParse();
    public void HttpWebCall(final String PreviousListViewClickedItem, final String httpHolder){

        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                listMenuActivity.ShowProgressDialog();
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                listMenuActivity.DismissDialog();

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;

                //Parsing the Stored JSOn String to GetHttpResponse Method.
                new GetHttpResponseFromHttpWebCall(listMenuActivity).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("UserID",params[0]);

                ParseResult = httpParse.postRequest(ResultHash, httpHolder);

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(PreviousListViewClickedItem);
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

                        listMenuActivity.CompareDataPhoneWithServer(jsonArray);

                        for(int i=0; i<jsonArray.length(); i++)
                        {
                            jsonObject = jsonArray.getJSONObject(i);

                            listMenuActivity.SetTableSelectedData(jsonObject);

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
            listMenuActivity.ShowListFromSelected();
        }
    }
    //endregion

    //endregion
}
