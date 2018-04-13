package com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.bkic.lymenglong.audiobookbkic.Models.Https.HttpParse;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListBook.ListBook;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListCategory.ListCategory;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListChapter.ListChapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PresenterShowList implements PresenterShowListImp{

    private ListCategory listCategoryActivity;
    private ListBook listBookActivity;
    private ListChapter listChapterActivity;
    private ProgressDialog pDialog;
    private static final String TAG = "PresenterShowList";

    public PresenterShowList(ListChapter listChapterActivity) {
        this.listChapterActivity = listChapterActivity;
    }

    public PresenterShowList(ListCategory listCategoryActivity) {
        this.listCategoryActivity = listCategoryActivity;
    }

    public PresenterShowList(ListBook listBookActivity) {
        this.listBookActivity = listBookActivity;
    }


    @Override
    public void GetSelectedResponse(Activity activity, HashMap<String,String> ResultHash, String HttpHolder) {
        HttpWebCall(activity, ResultHash, HttpHolder);
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
                pDialog = ProgressDialog.show(activity,"Loading Data","Please wait",true,true);
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
                pDialog.dismiss();
                new GetHttpResponseFromHttpWebCall(activity).execute();
            }

        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute();
    }

    //region Parsing Complete JSON Object.
    @SuppressLint("StaticFieldLeak")
    private class GetHttpResponseFromHttpWebCall extends AsyncTask<Void, Void, Void>
    {
        public Activity activity;


        private String jsonAction;
        private String jsonResult;
        private String jsonMessage;
        private String jsonLog;
        private Boolean logSuccess = false;

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
                if(FinalJSonObject != null)
                {
                    JSONObject jsonObject = new JSONObject(FinalJSonObject);
                    jsonAction = jsonObject.getString("Action");
                    jsonResult = jsonObject.getString("Result");
                    jsonMessage = jsonObject.getString("Message");
                    jsonLog = jsonObject.getString("Log");
                    logSuccess = jsonLog.equals("Success");

                    //region Old Parsing Code
                    /*
                    JSONArray jsonArray;

                    JSONArray jsonArrayResult;

                    //region Get data from json server in ListCategoryActivity
                    if (activity == listSubCategoryActivity){
                    try {
                        jsonArray = new JSONArray(FinalJSonObject);
                        JSONObject jsonObject;


                            //compare if data on server is less than phone we del data from phone
                            listSubCategoryActivity.CompareDataPhoneWithServer(jsonArray);

                            for(int i=0; i<jsonArray.length(); i++)
                            {
                                jsonObject = jsonArray.getJSONObject(i);

                                listSubCategoryActivity.SetTableSelectedData(jsonObject);

                            }
                    } catch (Exception ignored){
                    }
                }
                //endregion

                    //region Get data from json server in ListBookActivity
                    if (activity == listBookActivity) {
                        try {
                            //for new api////
                            JSONObject jsonActionObject = new JSONObject(FinalJSonObject);
                            ////////////////
                            tempArray = new ArrayList<>();

                            //compare if data on server is less than phone we del data from phone
//                            listBookActivity.CompareDataPhoneWithServer(jsonArray);

                            String jsonResult = jsonActionObject.getString("Result");

                            jsonArrayResult = new JSONArray(jsonResult);

                            for (int j = 0; j < jsonArrayResult.length(); j++) {
                                listBookActivity.SetTableSelectedData(jsonArrayResult.getJSONObject(j));
                            }

                            *//*for(int i=0; i<jsonArray.length(); i++)
                            {
                                jsonObject = jsonArray.getJSONObject(i);

                                listBookActivity.SetTableSelectedData(jsonObject);

                            }*//*


                        } catch (JSONException ignored) {
                        }
                    }
                    //endregion

                    //region Get data from json server in ListChapterActivity
                    if(activity == listChapterActivity){
                        try {
                            JSONObject jsonObject = new JSONObject(FinalJSonObject);
                            String resultJsonObject = jsonObject.getString("Result");
                            JSONObject jsonObjectResult = new JSONObject(resultJsonObject);
                            String arrayJsonChapter = jsonObjectResult.getString("ChapterList");
                            JSONArray jsonArrayChapter = new JSONArray(arrayJsonChapter);

                            for (int i = 0; i< jsonArrayChapter.length(); i++){
                                try {
                                    listChapterActivity.SetTableSelectedData(jsonArrayChapter.getJSONObject(i));
                                } catch (JSONException ignored) {
                                }
                            }
                        } catch (JSONException ignored) {
                        }

                    }
                    //endregion
                */
                //endregion
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
            if (logSuccess) {
                switch (jsonAction){
                    //region ListCategory : getListCategory
                    case "getListCategory":
                        try {
                            JSONArray jsonArrayChapter = new JSONArray(jsonResult);
                            if (jsonArrayChapter.length()!=0) {
                                for (int i = 0; i< jsonArrayChapter.length(); i++){
                                    try {
                                        listCategoryActivity.SetTableSelectedData(jsonArrayChapter.getJSONObject(i));
                                    } catch (JSONException ignored) {
                                        Log.d(TAG, "onPostExecute: "+jsonArrayChapter.getJSONObject(i));
                                    }
                                }
                            } else {
                                listCategoryActivity.LoadListDataFailed(jsonMessage);
                            }
                        } catch (JSONException ignored) {
                            Log.d(TAG, "onPostExecute: "+ jsonResult);
                        }
                        listCategoryActivity.ShowListFromSelected();
                        break;
                    //endregion
                    //region ListChapter : getChapterList
                    case "getChapterList":
                        try {
                            JSONArray jsonArrayChapter = new JSONArray(jsonResult);
                            if (jsonArrayChapter.length()!=0) {
                                for (int i = 0; i< jsonArrayChapter.length(); i++){
                                    try {
                                        listChapterActivity.SetTableSelectedData(jsonArrayChapter.getJSONObject(i));
                                    } catch (JSONException ignored) {
                                        Log.d(TAG, "onPostExecute: "+jsonArrayChapter.getJSONObject(i));
                                    }
                                }
                            } else {
                                listChapterActivity.LoadListDataFailed(jsonMessage);
                            }
                        } catch (JSONException ignored) {
                            Log.d(TAG, "onPostExecute: "+ jsonResult);
                        }
                        listChapterActivity.ShowListFromSelected();
                        break;
                    //endregion
                    //region ListChapter : getBookDetail
                    case "getBookDetail":
                        try {
                            JSONObject jsonObjectBookDetail = new JSONObject(jsonResult);
                            if (jsonObjectBookDetail.length()!=0) {
                                try {
                                    listChapterActivity.SetUpdateBookDetail(jsonObjectBookDetail);
                                } catch (JSONException ignored) {
                                    Log.d(TAG, "onPostExecute: "+jsonObjectBookDetail);
                                }
                            } else {
                                listChapterActivity.LoadListDataFailed(jsonMessage);
                            }
                        } catch (JSONException ignored) {
                            Log.d(TAG, "onPostExecute: "+ jsonResult);
                        }
                        break;
                    //endregion
                    //region ListBook : getBooksByCategory
                    case "getBooksByCategory":
                        try {
                            //compare if data on server is less than phone we del data from phone
//                            listBookActivity.CompareDataPhoneWithServer(jsonArray);
                            JSONArray jsonArrayResult = new JSONArray(jsonResult);
                            if(jsonArrayResult.length()!=0) {
                                for (int j = 0; j < jsonArrayResult.length(); j++) {
                                    try {
                                        listBookActivity.SetTableSelectedData(jsonArrayResult.getJSONObject(j));
                                    } catch (JSONException ignored) {
                                        Log.d(TAG, "onPostExecute: "+jsonArrayResult.getJSONObject(j));
                                    }
                                }
                            } else{
                                listBookActivity.LoadListDataFailed(jsonMessage);
                            }
                        } catch (JSONException ignored) {
                            Log.d(TAG, "onPostExecute: "+ jsonResult);
                        }
                        listBookActivity.ShowListFromSelected();
                        break;
                    //endregion

                }
            } else {
                Log.d(TAG, "onPostExecute:" + jsonLog);
            }

        }
    }


    //endregion
    //endregion
}
