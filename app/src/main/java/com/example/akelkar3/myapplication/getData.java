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

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            Log.d(TAG, "parseFeed: parsed stream");
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MyXmlParser", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }
                Log.d(TAG, "parseFeed: name");
                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("urlToImage")) {
                    link = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                }else if (name.equalsIgnoreCase("publishedAt")) {
                    publishedAt = result;
                }

                if (title != null && link != null && description != null) {
                    if(isItem) {
                        NewsItem item = new NewsItem();
                        item.title=title;
                        item.description=description;
                        item.urlToImage=link;
                        items.add(item);
                    }


                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
            }

            return items;
        } finally {
            inputStream.close();
        }
    }
}
