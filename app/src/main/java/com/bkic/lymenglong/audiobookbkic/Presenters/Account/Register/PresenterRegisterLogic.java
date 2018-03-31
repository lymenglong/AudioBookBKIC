package com.bkic.lymenglong.audiobookbkic.Presenters.Account.Register;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bkic.lymenglong.audiobookbkic.Models.Account.Utils.User;
import com.bkic.lymenglong.audiobookbkic.Views.Account.Register.ViewRegisterActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class PresenterRegisterLogic implements PresenterRegisterImp {
    private ViewRegisterActivity registerActivity;
    //    private static final String HttpUrl_Register = "http://192.168.1.27:80/audiobook/mobile_registration/register.php";
    private static final String HttpUrl_Register = "http://20121969.tk/audiobook/mobile_registration/register.php";

    public PresenterRegisterLogic(ViewRegisterActivity registerActivity) {
        this.registerActivity = registerActivity;
    }
    @Override
    public void Register(User userModel) {
        String TAG = "PresenterRegisterLogic";
        Log.d(TAG, "Register: " + userModel.toString());
        RequestRegister(userModel);
    }

    private void RequestRegister(final User userModel) {
        RequestQueue requestQueue = Volley.newRequestQueue(registerActivity);
        StringRequest request = new StringRequest(Request.Method.POST, HttpUrl_Register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {
                        registerActivity.RegisterSuccess(jsonObject.getString("success"));
                    } else {
                        registerActivity.RegisterFailed(jsonObject.getString("error"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(registerActivity, "Lỗi, Kết nối thất bại", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Fullname", userModel.getName());
                hashMap.put("Username", userModel.getUsername().trim().toLowerCase());
                hashMap.put("Email", userModel.getEmail().trim());
                hashMap.put("Password", userModel.getPassword().trim());
                hashMap.put("confirm_password", userModel.getConfirmPassword().trim());
                hashMap.put("Address", userModel.getAddress().trim());
                hashMap.put("PhoneNumber", userModel.getPhonenumber().trim());
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

}
