package com.example.darshank.news_gateway;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final String ACTION_MSG_TO_SERVICE = "@strings/action_msg_to_service";
    static final String ACTION_NEWS_STORY = "@strings/action_news_story";
    static final String ARTICLE_LIST = "@strings/article_list";
    static final String SOURCE_ID = "@strings/source_id";
    
    private DrawerLayout drawerArrangement;
    private ListView drawerCatalog;
    private ActionBarDrawerToggle toggle;
    private boolean serviceRunning = false;
    private ArrayList<String> newsSourceList = new ArrayList <>();
    private ArrayList<String> catList = new ArrayList <>();
    private ArrayList<NewsSources> newsSourceArrayList = new ArrayList <>();
    private ArrayList<IndividualArticle> newsArticleArrayList = new ArrayList <>();
    private HashMap<String, NewsSources> newsSourceDataMap = new HashMap<>();
    private Menu options_menu;
    private NewsReceiver newsReceiver;
    private String electronicSource;
    private String currentNewsSource;
    private UtilityForColours colorUtility;
    private PageAdapter pageUtility;
    private List <Fragment> fragments;
    private ViewPager pager;
    private boolean stateFlag;
    private int currentSourcePointer;
    ArrayList<UtilityForContent> contentDrawers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!serviceRunning &&  savedInstanceState == null) {
            Intent intent = new Intent(MainActivity.this, NewsService.class);
            startService(intent);
            serviceRunning = true;
        }
        newsReceiver = new NewsReceiver();
        IntentFilter filter = new IntentFilter(MainActivity.ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter);
        drawerArrangement = findViewById(R.id.drawer_layout);
        drawerCatalog = findViewById(R.id.left_drawer);
        drawerCatalog.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pager.setBackgroundResource(0);
                        currentSourcePointer = position;
                        selectItem(position);
                    }
                }
        );
        toggle = new ActionBarDrawerToggle(
                this,
                drawerArrangement,
                R.string.drawer_open,
                R.string.drawer_close
        );
        colorUtility = new UtilityForColours(this,contentDrawers);
        drawerCatalog.setAdapter(colorUtility);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        fragments = new ArrayList<>();
        pageUtility = new PageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewPager);
        pager.setAdapter(pageUtility);
        if (newsSourceDataMap.isEmpty() && savedInstanceState == null )
            new NewsSourceAsync(this, "").execute();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    private void colorMenuOptions(MenuItem item) {
        switch (item.getTitle().toString()) {
            case "business":
                color_menu_items(item,Color.CYAN);
                break;
            case "entertainment":
                color_menu_items(item,Color.GREEN);
                break;
            case "sports":
                color_menu_items(item,Color.RED);
                break;
            case "science":
                color_menu_items(item,Color.LTGRAY);
                break;
            case "technology":
                color_menu_items(item,Color.MAGENTA);
                break;
            case "general":
                color_menu_items(item,Color.rgb(255,223,0));
                break;
            case "health":
                color_menu_items(item,Color.BLUE);

        }
    }

    private void color_menu_items(MenuItem item, int color) {
        SpannableString spannableString = new SpannableString(item.getTitle());
        spannableString.setSpan(new ForegroundColorSpan(color), 0, spannableString.length(), 0);
        item.setTitle(spannableString);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    private void selectItem(int position) {
        currentNewsSource = newsSourceList.get(position);
        Intent intent = new Intent(MainActivity.ACTION_MSG_TO_SERVICE);
        intent.putExtra(SOURCE_ID, currentNewsSource);
        sendBroadcast(intent);
        drawerArrangement.closeDrawer(drawerCatalog);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        FrameLayout layoutRestore = new FrameLayout();
        layoutRestore.setCategories(catList);
        layoutRestore.setSourceList(newsSourceArrayList);
        layoutRestore.setCurrentArticle(pager.getCurrentItem());
        layoutRestore.setCurrentSource(currentSourcePointer);
        layoutRestore.setArticleList(newsArticleArrayList);
        outState.putSerializable("state", layoutRestore);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        FrameLayout layoutRestore1 = (FrameLayout) savedInstanceState.getSerializable("state");
        stateFlag = true;
        new NewsSourceAsync(this, "").execute();
        catList = layoutRestore1.getCategories();
        newsSourceArrayList = layoutRestore1.getSourceList();
        for(int i=0;i<newsSourceArrayList.size();i++){
            newsSourceList.add(newsSourceArrayList.get(i).getsName());
            newsSourceDataMap.put(newsSourceArrayList.get(i).getsName(), (NewsSources) newsSourceArrayList.get(i));
        }
        drawerCatalog.clearChoices();
        colorUtility.notifyDataSetChanged();
        electronicSource = newsSourceList.get(layoutRestore1.getCurrentSource());
        Intent intent = new Intent(MainActivity.ACTION_MSG_TO_SERVICE);
        intent.putExtra(SOURCE_ID, electronicSource);
        sendBroadcast(intent);
        drawerArrangement.closeDrawer(drawerCatalog);
        drawerCatalog.setOnItemClickListener(

                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pager.setBackgroundResource(0);
                        currentSourcePointer = position;
                        selectItem(position);
                    }
                }
        );
        setTitle("News Gateway");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_item, menu);
        options_menu=menu;
        if(stateFlag){
            options_menu.add("All");
            for (int i=0;i<catList.size();i++){
                SpannableString s = new SpannableString(catList.get(i));
                if(s.toString().equals("business")){
                    s.setSpan(new ForegroundColorSpan(Color.CYAN),0,s.length(),0);
                    options_menu.add(s);
                }
                if(s.toString().equals("entertainment")){
                    s.setSpan(new ForegroundColorSpan(Color.GREEN),0,s.length(),0);
                    options_menu.add(s);
                }
                if(s.toString().equals("sports")){
                    s.setSpan(new ForegroundColorSpan(Color.RED),0,s.length(),0);
                    options_menu.add(s);
                }
                if(s.toString().equals("science")){
                    s.setSpan(new ForegroundColorSpan(Color.LTGRAY),0,s.length(),0);
                    options_menu.add(s);
                }
                if(s.toString().equals("technology")){
                    s.setSpan(new ForegroundColorSpan(Color.MAGENTA),0,s.length(),0);
                    options_menu.add(s);
                }
                if(s.toString().equals("health")){
                    s.setSpan(new ForegroundColorSpan(Color.BLUE),0,s.length(),0);
                    options_menu.add(s);
                }
                if(s.toString().equals("general")){
                    s.setSpan(new ForegroundColorSpan(Color.rgb(255,223,0)),0,s.length(),0);
                    options_menu.add(s);
                }
            }        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        new NewsSourceAsync(this, item.getTitle().toString()).execute();
        colorMenuOptions(item);
        drawerArrangement.openDrawer(drawerCatalog);
        return super.onOptionsItemSelected(item);
    }

    public void initialiseSource(ArrayList<NewsSources> sourceList, ArrayList<String> categoryList)
    {
        newsSourceDataMap.clear();
        contentDrawers.clear();
        newsSourceList.clear();
        newsSourceArrayList.clear();
        newsSourceArrayList.addAll(sourceList);
        for(int i=0;i<sourceList.size();i++){
            newsSourceList.add(sourceList.get(i).getsName());
            newsSourceDataMap.put(sourceList.get(i).getsName(), (NewsSources) sourceList.get(i));
        }
        if(!options_menu.hasVisibleItems()) {
            catList.clear();
            catList =categoryList;
            options_menu.add("All");
            Collections.sort(categoryList);
            for (int i=0;i<categoryList.size();i++){
                SpannableString s = new SpannableString(categoryList.get(i));
                if(s.toString().equals("business")){
                    s.setSpan(new ForegroundColorSpan(Color.CYAN),0,s.length(),0);
                    options_menu.add(s);
                }
                if(s.toString().equals("entertainment")){
                    s.setSpan(new ForegroundColorSpan(Color.GREEN),0,s.length(),0);
                    options_menu.add(s);
                }
                if(s.toString().equals("sports")){
                    s.setSpan(new ForegroundColorSpan(Color.RED),0,s.length(),0);
                    options_menu.add(s);
                }
                if(s.toString().equals("science")){
                    s.setSpan(new ForegroundColorSpan(Color.LTGRAY),0,s.length(),0);
                    options_menu.add(s);
                }
                if(s.toString().equals("technology")){
                    s.setSpan(new ForegroundColorSpan(Color.MAGENTA),0,s.length(),0);
                    options_menu.add(s);
                }
                if(s.toString().equals("health")){
                    s.setSpan(new ForegroundColorSpan(Color.BLUE),0,s.length(),0);
                    options_menu.add(s);
                }
                if(s.toString().equals("general")){
                    s.setSpan(new ForegroundColorSpan(Color.rgb(255,223,0)),0,s.length(),0);
                    options_menu.add(s);
                }
            }
        }
        for( NewsSources s : sourceList){
            UtilityForContent drawerContent = new UtilityForContent();
            switch (s.getsCategory()){
                case "business":
                    drawerContent.setColor(Color.CYAN);
                    drawerContent.setName(s.getsName());
                    contentDrawers.add(drawerContent);
                    break;
                case "entertainment":
                    drawerContent.setColor(Color.GREEN);
                    drawerContent.setName(s.getsName());
                    contentDrawers.add(drawerContent);
                    break;
                case "sports":
                    drawerContent.setColor(Color.RED);
                    drawerContent.setName(s.getsName());
                    contentDrawers.add(drawerContent);
                    break;
                case "science":
                    drawerContent.setColor(Color.LTGRAY);
                    drawerContent.setName(s.getsName());
                    contentDrawers.add(drawerContent);
                    break;
                case "technology":
                    drawerContent.setColor(Color.MAGENTA);
                    drawerContent.setName(s.getsName());
                    contentDrawers.add(drawerContent);
                    break;
                case "general":
                    drawerContent.setColor(Color.rgb(255,223,0));
                    drawerContent.setName(s.getsName());
                    contentDrawers.add(drawerContent);
                    break;
                case "health":
                    drawerContent.setColor(Color.BLUE);
                    drawerContent.setName(s.getsName());
                    contentDrawers.add(drawerContent);
            }
        }
        colorUtility.notifyDataSetChanged();
    }

    private void doFragments(ArrayList<IndividualArticle> articles) {
        setTitle(currentNewsSource);
        for (int i = 0; i < pageUtility.getCount(); i++)
            pageUtility.notifyChangeInPosition(i);

        fragments.clear();
        for (int i = 0; i < articles.size(); i++) {
            IndividualArticle a = articles.get(i);
            fragments.add(Fragments.newFragment(articles.get(i), i, articles.size()));
        }
        pageUtility.notifyDataSetChanged();
        pager.setCurrentItem(0);
        newsArticleArrayList = articles;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(MainActivity.this, NewsReceiver.class);
        stopService(intent);
        super.onDestroy();
    }

    private class PageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;
        public PageAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
        @Override
        public int getCount() {
            return fragments.size();
        }
        @Override
        public long getItemId(int position) {
            return baseId + position;
        }
        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }
    }

    class NewsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_NEWS_STORY:
                    ArrayList<IndividualArticle> artList;
                    if (intent.hasExtra(ARTICLE_LIST)) {
                        artList = (ArrayList <IndividualArticle>) intent.getSerializableExtra(ARTICLE_LIST);
                        doFragments(artList);
                    }
                    break;
            }
        }
    }
}