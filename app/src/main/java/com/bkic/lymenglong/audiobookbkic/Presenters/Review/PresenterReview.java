package com.bkic.lymenglong.audiobookbkic.Presenters.Review;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bkic.lymenglong.audiobookbkic.Models.Https.HttpParse;
import com.bkic.lymenglong.audiobookbkic.Models.Utils.Const;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.Views.Player.PlayControl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PresenterReview
        implements
            PresenterReviewImp ,
            DialogInterface.OnShowListener,
            DialogInterface.OnDismissListener,
            View.OnClickListener,
            RadioGroup.OnCheckedChangeListener{
    private PlayControl playControlActivity;
    private static final String TAG = "PresenterReview";
    private RadioButton radioButton;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private RadioButton radioButton5;
    private int rateNumber;
    private Boolean SubmitBntIsClicked = false;
    public PresenterReview(PlayControl playControlActivity) {
        this.playControlActivity = playControlActivity;
    }
    private Dialog dialog;

    @Override
    public void ReviewBookDialog() {
        dialog = new Dialog(playControlActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_review);
        Button buttonDismiss = dialog.findViewById(R.id.button_dismiss);
        Button buttonSubmit = dialog.findViewById(R.id.button_submit);
        radioButton = dialog.findViewById(R.id.rb1);
        radioButton2 = dialog.findViewById(R.id.rb2);
        radioButton3 = dialog.findViewById(R.id.rb3);
        radioButton4 = dialog.findViewById(R.id.rb4);
        radioButton5 = dialog.findViewById(R.id.rb5);
        buttonSubmit.setOnClickListener(this);
        buttonDismiss.setOnClickListener(this);
        dialog.setOnShowListener(this);
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    @Override
    public void RequestReviewBook(Activity activity, int userId, int bookId, int rateNumber, String review){
        String keyPost = "json";
        String value =
                "{" +
                        "\"Action\":\"addReview\", " +
                        "\"UserId\":\""+userId+"\", " +
                        "\"BookId\":\""+bookId+"\", " +
                        "\"Rate\":\""+rateNumber+"\"," +
                        "\"Review\":\""+review+"\"" +
                "}";
        HashMap<String,String> ResultHash = new HashMap<>();
        ResultHash.put(keyPost,value);
        HttpWebCall(activity,ResultHash, Const.HttpURL_API);
    }


    @Override
    public void onShow(DialogInterface dialog) {
        Log.d(TAG, "onShow: " +dialog.toString());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d(TAG, "onDismiss: " +dialog.toString());
        if(SubmitBntIsClicked){
            playControlActivity.ReviewBook();
            SubmitBntIsClicked = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_submit:
                SubmitBntIsClicked = true;
                if(radioButton.isChecked()){
                    rateNumber = 1;
                } else if(radioButton2.isChecked()){
                    rateNumber = 2;
                } else if(radioButton3.isChecked()){
                    rateNumber = 3;
                } else if(radioButton4.isChecked()){
                    rateNumber = 4;
                } else if(radioButton5.isChecked()){
                    rateNumber = 5;
                }
                playControlActivity.setRateNumber(rateNumber);
                playControlActivity.setReview("");//todo add Comment Review Of User
                dialog.dismiss();
                break;
            case R.id.button_dismiss:
                dialog.dismiss();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    //region Method to Update Record
    private String FinalJSonObject;
    private String finalResult ;
    private HttpParse httpParse = new HttpParse();
    private void HttpWebCall(final Activity activity, final HashMap<String, String> ResultHash, final String HttpUrl){

        @SuppressLint("StaticFieldLeak")
        class UpdateRecordDataClass extends AsyncTask<Void,Void,String> {
            @Override
            protected String  doInBackground(Void... voids) {

                finalResult = httpParse.postRequest(ResultHash, HttpUrl);

                return finalResult;
            }
            @Override
            protected void onPostExecute(String httpResponseMsg) {
                super.onPostExecute(httpResponseMsg);
                FinalJSonObject = httpResponseMsg;
                new GetHttpResponseFromHttpWebCall(activity).execute();
            }
        }
        UpdateRecordDataClass updateRecordDataClass = new UpdateRecordDataClass();
        updateRecordDataClass.execute();
    }

    //region Parsing Complete JSON Object.
    @SuppressLint("StaticFieldLeak")
    private class GetHttpResponseFromHttpWebCall extends AsyncTask<Void, Void, Void>
    {
        public Activity activity;

        String jsonAction = null;

        Boolean LogSuccess = false;

        String ResultJsonObject;

        String message;

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
                if(FinalJSonObject != null )
                {
                    JSONObject jsonObject;

                    try {
                        jsonObject = new JSONObject(FinalJSonObject);

                        jsonAction = jsonObject.getString("Action");

                        ResultJsonObject = jsonObject.getString("Result");

                        LogSuccess = jsonObject.getString("Log").equals("Success");

                        message = jsonObject.getString("Message");

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
        protected void onPostExecute(Void aVoid)
        {
            if (LogSuccess) {
                playControlActivity.UpdateReviewSuccess(message);
            } else {
                playControlActivity.UpdateReviewFailed(message);
            }

        }
    }
    //endregion

}
