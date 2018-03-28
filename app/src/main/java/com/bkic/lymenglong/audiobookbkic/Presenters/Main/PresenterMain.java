package com.bkic.lymenglong.audiobookbkic.Presenters.Main;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Chapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Home;
import com.bkic.lymenglong.audiobookbkic.Models.Https.HttpServicesClass;
import com.bkic.lymenglong.audiobookbkic.Views.Main.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PresenterMain implements PresenterMainImp {
    MainActivity mainActivity;
    String HttpUrl = "http://20121969.tk/SachNoiBKIC/AllMenuData.php";
    public PresenterMain(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void GetHttpResponse() {
    }

    private void GetCursorData() {/*
        menuList.clear();
        Cursor cursor = dbHelper.GetData("SELECT * FROM menu");
        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            int id = cursor.getInt(0);
            menuList.add(new Chapter(id, name));
        }
        cursor.close();
        mainAdapter.notifyDataSetChanged();
        dbHelper.close();
    */}

    //region JSON parse class started from here.
    /*private class GetHttpResponse extends AsyncTask<Void, Void, Void> {

        public Context context;
        String JSonResult;
        ArrayList<Home> home;

        public GetHttpResponse(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = ProgressDialog.show(activity, "Load Data", "Please wait...", true, true);
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

                            Home homeModel;
//                            Student student;

//                            studentList = new ArrayList<Student>();
                            home = new ArrayList<Home>();

                            for (int i = 0; i < jsonArray.length(); i++) {
//                                student = new Student();
                                homeModel = new Home();

                                jsonObject = jsonArray.getJSONObject(i);

                                // Adding Student Id TO IdList Array.
//                                IdList.add(jsonObject.getString("Id").toString());
                                homeModel.setId(Integer.parseInt(jsonObject.getString("Id")));

                                //Adding Student Name.
//                                student.StudentName = jsonObject.getString("Name").toString();
                                homeModel.setTitle(jsonObject.getString("Name"));
                                home.add(homeModel);
                                int Id = homeModel.getId();
                                String Name = homeModel.getTitle();
                                if (menuList.size() >= home.size()) {
                                    if (!homeModel.getTitle().equals(menuList.get(i).getTitle())) {
                                        String UPDATE_DATA = "UPDATE menu SET Name = '" + Name + "' WHERE Id = '" + Id + "'";
                                        dbHelper.QueryData(UPDATE_DATA);
                                    }
                                } else {
                                    String INSERT_DATA = "INSERT INTO menu VALUES('" + Id + "','" + Name + "')";
                                    dbHelper.QueryData(INSERT_DATA);
                                }
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
            pDialog.dismiss();
            progressBar.setVisibility(View.GONE);
            GetCursorData();
            Log.d("MyTagView", "onPostExecute: " + getTitle());
        }
    }*/
    //endregion
}
