package com.example.akelkar3.myapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
                result = parseFeed(connection.getInputStream());


            }
            Log.d(TAG, "result "+result.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(TAG, "doInBackground: "+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "doInBackground: "+e.getMessage());
        } catch (XmlPullParserException e) {
            e.printStackTrace();
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

    public ArrayList<NewsItem> parseFeed(InputStream inputStream) throws XmlPullParserException,
            IOException {
        String title = null;
       String link = null;
        String publishedAt=null;
        String description = null;
        boolean isItem = false;
        ArrayList<NewsItem> items = new ArrayList<>();
        NewsItem item=null;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
          //  xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, "UTF-8");

int event = parser.getEventType();
while(event!= XmlPullParser.END_DOCUMENT){
    switch (event){
        case XmlPullParser.START_TAG:

            if (parser.getName().equals("item")){
                item=new NewsItem();
                isItem=true;
            }else  if (isItem && parser.getName().equals("title")){
                item.title= parser.nextText().trim();
            }else  if (isItem && parser.getName().equals("media:content")){
                if (link==null){
                  link= parser.getAttributeValue(1);
               item.urlToImage=link;
                }
            }else  if (isItem && parser.getName().equals("description")){
                item.description= parser.nextText().trim().split("<")[0];
            }else  if (isItem && parser.getName().equals("pubDate")){
                item.publishedAt= parser.nextText().trim();
            }
          //  Log.d(TAG, "parseFeed: ===>" + parser.getName());
            break;

        case XmlPullParser.END_TAG:
            if (parser.getName().equals("item")){
            items.add(item);
                isItem=false;
                link=null;
        }
            break;
        default:
            break;

    }
    event=parser.next();

}

            return items;
        } finally {
            inputStream.close();
        }
    }
}
