package com.example.akelkar3.myapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by akelkar3 on 2/21/2018.
 */

public class getData  extends AsyncTask<String, Void, ArrayList<NewsItem>> {
    String TAG="test";
    String line="";
    private ProgressDialog dialog;
    IData iData;

    public getData(IData idata, MainActivity activity) {
        this.iData = idata;
        this.dialog= new ProgressDialog(activity);
    }
    @Override
    protected  ArrayList<NewsItem> doInBackground(String... params) {
        // StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        ArrayList<NewsItem>result=new ArrayList<NewsItem>();

        // String result = null;
        Log.d(TAG, "back");
        try {
            URL url = new URL(params[0]);
            Log.d(TAG, "doInBackground: URL "+ url);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                JSONObject root = new JSONObject(json);
                JSONArray newsitems = root.getJSONArray("articles");
                for (int i=0;i<newsitems.length();i++) {
                    JSONObject newsItemJson = newsitems.getJSONObject(i);

                    NewsItem newsItem = new NewsItem();


                    newsItem.title = newsItemJson.getString("title");
                    newsItem.publishedAt = newsItemJson.getString("publishedAt");
                    newsItem.description = newsItemJson.getString("description");
                    newsItem.urlToImage = newsItemJson.getString("urlToImage");


                    result.add(newsItem);

                    //   result = stringBuilder.toString();

                }

            }
            Log.d(TAG, "result "+result.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(TAG, "doInBackground: "+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "doInBackground: "+e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "doInBackground: "+e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "doInBackground: "+e.getMessage());
                }
            }
        }


        return result;
    }


    @Override
    protected void onPreExecute() {
        dialog.setMessage("Loading");
        dialog.show();
    }
    @Override
    protected void onPostExecute(ArrayList<NewsItem> result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if(result!=null)
        {
            Log.d(TAG, "inpost");
            if (iData!=null) {
                iData.handleData(result);
            }
        } else {
            Log.d(TAG, "null result");
        }
    }
    public static  interface IData{
        public void handleData(ArrayList<NewsItem> data);
    }
}
