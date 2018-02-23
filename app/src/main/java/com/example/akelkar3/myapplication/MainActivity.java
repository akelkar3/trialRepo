package com.example.akelkar3.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity implements getData.IData  {
    String TAG="test";
    AlertDialog dialog;
    ImageButton next;
    ImageButton prev;
    ImageView gal=null;
    TextView keyword;
    TextView title;
    TextView description;
    TextView date;
    String apiKey="f3e97abda19c49eabbfe788bba799421";
    ArrayList<NewsItem> newsItems =null;
    final    String[] cat= {"Top Stories","World", "U.S.", "Business", "Politics", "Technology","Health", "Politics", "Entertainment","Travel", "Living","Most Recent"};
final String[] urls = {"http://rss.cnn.com/rss/cnn_topstories.rss","http://rss.cnn.com/rss/cnn_world.rss","http://rss.cnn.com/rss/cnn_us.rss","http://rss.cnn.com/rss/money_latest.rss","http://rss.cnn.com/rss/cnn_allpolitics.rss","http://rss.cnn.com/rss/cnn_tech.rss","http://rss.cnn.com/rss/cnn_health.rss","http://rss.cnn.com/rss/cnn_showbiz.rss","http://rss.cnn.com/rss/cnn_travel.rss","http://rss.cnn.com/rss/cnn_living.rss","http://rss.cnn.com/rss/cnn_latest.rss"};

    int current;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "starting");
     /*   urls.put(cat[0],"http://rss.cnn.com/rss/cnn_topstories.rss");
        urls.put(cat[1],"http://rss.cnn.com/rss/cnn_world.rss");
        urls.put(cat[2],"http://rss.cnn.com/rss/cnn_us.rss");
        urls.put(cat[3],"http://rss.cnn.com/rss/money_latest.rss");
        urls.put(cat[4],"http://rss.cnn.com/rss/cnn_allpolitics.rss");
        urls.put(cat[5],"http://rss.cnn.com/rss/cnn_tech.rss");
        urls.put(cat[6],"http://rss.cnn.com/rss/cnn_health.rss");
        urls.put(cat[7],"http://rss.cnn.com/rss/cnn_showbiz.rss");
        urls.put(cat[8],"http://rss.cnn.com/rss/cnn_travel.rss");
        urls.put(cat[8],"http://rss.cnn.com/rss/cnn_living.rss");
        urls.put(cat[9],"http://rss.cnn.com/rss/cnn_latest.rss");*/

        next = (ImageButton)findViewById(R.id.btnnext);
        prev = (ImageButton)findViewById(R.id.btnprev);
        keyword =(TextView)findViewById(R.id.keyword);
        next.setEnabled(false);
        prev.setEnabled(false);
        next.setAlpha(0.4f);
        prev.setAlpha(0.4f);


        findViewById(R.id.btnGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
                // new GetSimpleAsync().execute("http://dev.theappsdr.com/apis/photos/keywords.php");
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setItems(cat, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                        dialog.cancel();
                        keyword.setText(cat[i]);
                        //   Log.d(TAG, generatedKeywords[i]);
                        String url=urls[i];


                        new getData(MainActivity.this,MainActivity.this).execute( url);
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    current= (current+1) % newsItems.size();
                    showData(newsItems.get(current));
                    //  new getAsyncImage(MainActivity.this, MainActivity.this).execute(imageUrls.get(current));
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    current= (current-1 +newsItems.size()) % newsItems.size();
                    showData(newsItems.get(current));
                    //  new getAsyncImage(MainActivity.this, MainActivity.this).execute(imageUrls.get(current));
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    public void showData(NewsItem input){
        title=(TextView)findViewById(R.id.title);
        description =(TextView)findViewById(R.id.description);
        date=(TextView)findViewById(R.id.date);
        gal =(ImageView) findViewById(R.id.gallary);
        TextView count= (TextView)findViewById(R.id.count);
        count.setText(String.valueOf(current+1)+" of "+newsItems.size());
        title.setText(input.title);
        description.setText(input.description);
        date.setText(input.publishedAt);
        if(input.urlToImage !=null && !input.urlToImage.isEmpty()) {
            Picasso.with(MainActivity.this).load(input.urlToImage.toString()).placeholder(R.drawable.notfound).error(R.drawable.notfound).into(gal);
        }else{
            Picasso.with(MainActivity.this).load(R.drawable.notfound);
        }

    }
    public void clearData(){
        title=(TextView)findViewById(R.id.title);
        description =(TextView)findViewById(R.id.description);
        date=(TextView)findViewById(R.id.date);
        gal =(ImageView) findViewById(R.id.gallary);
        TextView count= (TextView)findViewById(R.id.count);
        count.setText("");
        title.setText("");
        description.setText("");
        date.setText("");
        Picasso.with(MainActivity.this).load(R.drawable.notfound);

    }
    @Override
    public void handleData(ArrayList<NewsItem> data) {
        current= 0;
        newsItems=data;
        if (data == null || data.size()==0) {
            Toast.makeText(this, "No News Found", Toast.LENGTH_SHORT).show();
            next.setEnabled(false);
            prev.setEnabled(false);
            next.setAlpha(0.4f);
            prev.setAlpha(0.4f);
            clearData();
        }else{
            showData(data.get(current));
        }
        if(data.size()>1){
            next.setEnabled(true);
            prev.setEnabled(true);
            next.setAlpha(1.0f);
            prev.setAlpha(1.0f);
        }


    }
}
