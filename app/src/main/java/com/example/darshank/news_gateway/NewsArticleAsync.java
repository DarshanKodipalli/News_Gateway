package com.example.darshank.news_gateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsArticleAsync extends AsyncTask<String, Integer, String> {

    private String newsSourceIdentifier;
    private NewsService service;
    private String API_KEY ="d06d71d91b2843feb357065d6214cc4f";
    private String partquery1 ="https://newsapi.org/v2/everything?sources=";
    private String partquery2 = "&apiKey="+API_KEY;
    private Uri.Builder completeURL = null;
    private StringBuilder sb1;
    private boolean dataNotFound=false;
    boolean isNoDataFound=true;
    private ArrayList<IndividualArticle> articleArrayList = new ArrayList <>();

    public NewsArticleAsync(NewsService service, String newsSourceIdentifier){
        this.newsSourceIdentifier = newsSourceIdentifier;
        this.service= service;
    }
    @Override
    protected String doInBackground(String... strings) {
        String query ="";
        query = partquery1 +newsSourceIdentifier+ partquery2;
        completeURL = Uri.parse(query).buildUpon();
        connectToAPI();
        if(!isNoDataFound) {
            parseJSON(sb1.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        service.setArticles(articleArrayList);
    }


    public void connectToAPI() {
        String urlToUse = completeURL.build().toString();
        sb1 = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            {
                dataNotFound=true;
            }
            else {
                conn.setRequestMethod("GET");
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
                String line=null;
                while ((line = reader.readLine()) != null) {
                    sb1.append(line).append('\n');
                }
                isNoDataFound=false;
            }
        }
        catch(FileNotFoundException fe){
            fe.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJSON(String s) {
        try{
            if(!dataNotFound){
                JSONObject jObjMain = new JSONObject(s);
                JSONArray articles = jObjMain.getJSONArray("articles");
                for(int i=0;i<articles.length();i++){
                    JSONObject art = (JSONObject) articles.get(i);
                    IndividualArticle artObj = new IndividualArticle();
                    artObj.setArticleUrl(art.getString("url"));
                    artObj.setDescription(art.getString("description"));
                    artObj.setAuthor(art.getString("author"));
                    artObj.setPublishedAt(art.getString("publishedAt"));
                    artObj.setUrlToImage(art.getString("urlToImage"));
                    artObj.setTitle(art.getString("title"));
                    articleArrayList.add(artObj);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}