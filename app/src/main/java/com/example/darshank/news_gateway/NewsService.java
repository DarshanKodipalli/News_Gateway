package com.example.darshank.news_gateway;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
public class NewsService extends android.app.Service {
    private boolean isApplicationRunning = true;
    private ServiceReceiver serviceReceiver;
    private ArrayList<IndividualArticle> entireArticlesList = new ArrayList <IndividualArticle>();

    public NewsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceReceiver = new ServiceReceiver();
        IntentFilter filter1 = new IntentFilter(MainActivity.ACTION_MSG_TO_SERVICE);
        registerReceiver(serviceReceiver, filter1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isApplicationRunning) {
                    while(entireArticlesList.isEmpty()){
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent();
                    intent.setAction(MainActivity.ACTION_NEWS_STORY);
                    intent.putExtra(MainActivity.ARTICLE_LIST, entireArticlesList);
                    sendBroadcast(intent);
                    entireArticlesList.clear();
                }
            }
        }).start();
        return android.app.Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        isApplicationRunning = false;
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
    }

    public void setArticles(ArrayList<IndividualArticle> list){
        entireArticlesList.clear();
        entireArticlesList.addAll(list);

    }

    class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MainActivity.ACTION_MSG_TO_SERVICE:
                    String sourceId ="";
                    String temp="";
                    if (intent.hasExtra(MainActivity.SOURCE_ID)) {
                        sourceId = intent.getStringExtra(MainActivity.SOURCE_ID);
                        temp=sourceId.replaceAll(" ","-");
                    }
                    new NewsArticleAsync(NewsService.this, temp).execute();
                    break;
            }
        }
    }
}