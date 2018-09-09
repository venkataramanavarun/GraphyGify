package com.example.avarun.graphygif;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kushal on 26/9/17.
 */

public class APICall extends AsyncTask<Void, Void, Void> {

    private final Context mContext;
    private HashMap<String, String> hashMapJson = null;
    private HashMap<String, File> hashMapFiles = null;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    //private final String credential;

    OkHttpClient client = new OkHttpClient();
    private String json;
    private String url;
    Object object;
    private static String result;

    private boolean isDialogCancelled;
    private ACProgressFlower dialog;


    // basic methos for call back
    public APICall(String json, String url, Object object) {
        this.json = json;
        this.url = url;
        this.object = object;
        apiRespose = (APIRespose) object;
        mContext = (Context) object;
        //credential = Credentials.basic(OnGoConstants.AUTH_USERNAME, OnGoConstants.AUTH_PASSWORD);
    }

    // for multi part upload
    public APICall(String url, HashMap<String, File> hashMapFiles
            , HashMap<String, String> hashMapJson, Object object) {

        this.url = url;
        this.object = object;
        apiRespose = (APIRespose) object;
        mContext = (Context) object;
        //credential = Credentials.basic(OnGoConstants.AUTH_USERNAME, OnGoConstants.AUTH_PASSWORD);
        this.hashMapJson = hashMapJson;
        this.hashMapFiles = hashMapFiles;
    }

    // to be used in frag
    public APICall(String json, String url, Object object, Context mContext) {
        this.json = json;
        this.url = url;
        this.object = object;
        apiRespose = (APIRespose) object;
        this.mContext = mContext;
        //credential = Credentials.basic(OnGoConstants.AUTH_USERNAME, OnGoConstants.AUTH_PASSWORD);
    }

    APIRespose apiRespose;


    // to be used in adapter or getting response in self listener
    public APICall(String json, String url, Context mContext, APIRespose apiRespose) {
        this.json = json;
        this.url = url;
        this.mContext = mContext;
        //credential = Credentials.basic(OnGoConstants.AUTH_USERNAME, OnGoConstants.AUTH_PASSWORD);
        this.apiRespose = apiRespose;
        Log.e("url here is",">>>>>"+url);
    }

    public APICall(String url, HashMap<String, File> hashMapFiles
            , HashMap<String, String> hashMapJson, Object object, APIRespose apiRespose) {

        this.url = url;
        this.object = object;
        this.apiRespose = apiRespose;
        mContext = (Context) object;
        //credential = Credentials.basic(OnGoConstants.AUTH_USERNAME, OnGoConstants.AUTH_PASSWORD);
        this.hashMapJson = hashMapJson;
        this.hashMapFiles = hashMapFiles;
    }


    String post(String json, String url) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                //.header("Authorization", credential)
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    String postMultiPart(String url, HashMap<String, File> hashMapFiles, HashMap<String, String> hashMapJson) throws IOException {
        MultipartBody.Builder multiBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        // add type entity
        for (String key : hashMapJson.keySet()) {
            multiBuilder.addFormDataPart(key, hashMapJson.get(key));
        }
        // add type entity
        if (hashMapFiles != null) {
            MediaType mediaType = MediaType.parse("image/jpg");
            for (String key : hashMapFiles.keySet()) {
                if (hashMapFiles.get(key) != null) {
                    String fileName = hashMapFiles.get(key).getName();
                    multiBuilder.addFormDataPart("srcFile", fileName, RequestBody.create(mediaType, hashMapFiles.get(key)));
                }
            }
        }

        RequestBody requestBody = multiBuilder.build();
        Request request = new Request.Builder()

                //.header("Authorization", credential)
                .url(url)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    String get(String url) throws IOException {
        Request request = new Request.Builder()
                //.header("Authorization", credential)
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //if (!todo.contains("refresh"))
        {

            dialog = new ACProgressFlower.Builder(mContext)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .text("Loading...")
                    .fadeColor(Color.DKGRAY).build();
            dialog.show();
        }
    }


    @Override
    protected Void doInBackground(Void... voids) {
        result = "";

        //.if multipart
        if (hashMapJson != null) {

            try {
                result = postMultiPart(url, hashMapFiles, hashMapJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // if post
            if (json != null) {
                try {
                    result = post(json, url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    result = get(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                dialog.dismiss();
            }
        }

        apiRespose.onResponse(result);
        super.onPostExecute(aVoid);
    }

    public interface APIRespose {
        public void onResponse(String result);
    }
}
