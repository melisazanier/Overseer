package com.example.monitorapp_v1;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import DatabaseManager.DatabaseAccess;
import Utilities.UtilityLibrary;

public class ShopListOfItemsActivity extends AppCompatActivity  {

    private static ArrayList<String> imageList = new ArrayList<>();
    private static ArrayList<String> titleList = new ArrayList<>();
    private static ArrayList<String> priceList = new ArrayList<>();
    private static ArrayList<String> URLList = new ArrayList<>();

    private String ShopNameAttribute,LinkOfProductsItem,OriginalLinkOfProductsItem;
    private String pathToListOfItemsImage,pathToItemURL,pathToListOfItemsTitle,pathToListOfItemsPrice,pathToListOfItemsImageAtrr,pathToListOfItemsTitleAtrr,pathToLastPageNumber;
    private String startURLToNextPages, stopURLToNextPages;
    private String backUpLink;
    private static String searchQuery;

    public static final ArrayList <String> SearchLinkStart = new ArrayList<String>(){
        {
            add("https://www.emag.ro/search/");
            add("https://mediagalaxy.ro/cauta/filtru/p/1/?q=");
            add("https://www.pcgarage.ro/cauta/");
        }
    };

    private int ID;
    private static int pageIndex=1;
    private static int numberOfPages=1;

    private ProgressBar progressBarItems;
    private ListView list;
    private TextView noProductsSearch;
    private ArrayAdapter adapter;
    private Thread thread;
    private Vibrator vibrator;
    private static boolean searchOptionActivated = false;
    private boolean pageLoaded = true;
    private static String currentURLMG="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list_of_items);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        final SwipeRefreshLayout mSwipeRefreshLayout= findViewById(R.id.swiperefresh);

        //Action bar logo display settings
        //Action bar logo display settings
        if (getSupportActionBar() != null) {
            //Action bar logo display settings
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().isHideOnContentScrollEnabled();
        }

        final Intent intent = getIntent();

        //Get attribute from previously activity
        ShopNameAttribute = intent.getStringExtra("ShopNameAttribute");
        LinkOfProductsItem = intent.getStringExtra("LinkOfProductsItem");
        OriginalLinkOfProductsItem = LinkOfProductsItem;

        //Clear the arrays before we use them from previous use
        deleteListsContent();

        pageIndex=1;
        backUpLink=LinkOfProductsItem;
        list=findViewById(R.id.list);
        progressBarItems=findViewById(R.id.progressBar);
        noProductsSearch=findViewById(R.id.textNoProductsSearch);
        noProductsSearch.setVisibility(View.GONE);
        searchOptionActivated = false;

        readDataFromDB();
        displayItemsList();

        //The actions that happen when you press a element from the listView
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(!Objects.requireNonNull(UtilityLibrary.getTypeOfRingerMode(getApplicationContext())).equals("SILENT"))
                    vibrator.vibrate(50);//the phone will vibrate when the button is touched
                view.setSelected(true);
                if( UtilityLibrary.verifyConnectivity(getApplicationContext())) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(getApplicationContext(), ShopIndividualProductDisplay.class);

                    //Messages that will be send to the next activity that will open
                    intent.putExtra("ShopNameAttribute", ShopNameAttribute);
                    intent.putExtra("PositionOfSubcategory", Integer.toString(position));
                    intent.putExtra("LinkOfIndividualProduct", URLList.get(position));
                    intent.putExtra("TitleOfIndividualProduct", titleList.get(position));
                    intent.putExtra("ImageOfIndividualProduct", imageList.get(position));
                    intent.putExtra("PriceOfIndividualProduct", priceList.get(position));

                    startActivity(intent);//Opens a new activity
                }
            }
        });


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh list view data.
                finish();
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });


        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override public void onScrollStateChanged(AbsListView absListView, int i) {}

            @Override
            public void onScroll(AbsListView absListView,int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if((ShopNameAttribute.equals("Media Galaxy") && searchOptionActivated))
                    return;
                //Display/get information for the next pages when the list reach the end
                if((totalItemCount - firstVisibleItem <= visibleItemCount) && (totalItemCount != 0) && pageLoaded && (pageIndex < numberOfPages) ){
                    pageLoaded=false;
                    pageIndex++;

                    //Create the new link with afferent the page number
                    if(ShopNameAttribute.matches("Emag")){
                        if(!searchOptionActivated){
                            String splitLink = backUpLink.substring(0,backUpLink.lastIndexOf("/"));
                            LinkOfProductsItem = splitLink+startURLToNextPages+pageIndex+stopURLToNextPages;
                        }
                        else LinkOfProductsItem = backUpLink+startURLToNextPages+pageIndex+stopURLToNextPages;
                    }
                    else if(!searchOptionActivated) LinkOfProductsItem=backUpLink+startURLToNextPages+pageIndex+stopURLToNextPages;
                        else {
                            if(pageIndex==2) backUpLink=currentURLMG;
                            LinkOfProductsItem = backUpLink + startURLToNextPages + pageIndex + stopURLToNextPages;
                        }
                    new UpdateAsyncTask().execute();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchOptionActivated = true;
                pageIndex=1;
                if(ShopNameAttribute.equals("Media Galaxy")){
                    deleteListsContent();
                    readDataFromDB();
                    searchQuery = query;
                    new SearchEngine().execute();
                }
                else {
                    query = query.replace(' ', '+');
                    if (ShopNameAttribute.equals("Emag")) {
                        LinkOfProductsItem = SearchLinkStart.get(0) + query;

                    } else if (ShopNameAttribute.equals("PC Garage")) {
                        LinkOfProductsItem = SearchLinkStart.get(2) + query;
                    }

                    backUpLink=LinkOfProductsItem;
                    deleteListsContent();
                    readDataFromDB();
                    searchQuery = query;
                    new UpdateAsyncTask().execute();
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty() && !LinkOfProductsItem.equals(OriginalLinkOfProductsItem)){
                    searchOptionActivated = false;
                    pageIndex=1;
                    backUpLink=OriginalLinkOfProductsItem;
                    deleteListsContent();
                    LinkOfProductsItem = OriginalLinkOfProductsItem;
                    readDataFromDB();
                    new UpdateAsyncTask().execute();
                }

                return true;
            }
        });
        return true;
    }


    //Add the product information for the next pages
    class SearchEngine extends AsyncTask<Void, Void, String>   {
        @Override protected void onPreExecute(){
            super.onPreExecute();
            deleteListsContent();
            adapter.notifyDataSetChanged();
            progressBarItems.setVisibility(View.VISIBLE);
        }
        @Override protected String doInBackground(Void... params) {
            deleteListsContent();
            int currentPage=1;
            while(currentPage<=numberOfPages) {
                try {
                    scrapeDataFromWebSearchEngine(LinkOfProductsItem, pathToListOfItemsImage, pathToListOfItemsTitle, pathToListOfItemsPrice, pathToListOfItemsImageAtrr, pathToListOfItemsTitleAtrr, pathToItemURL, ID);
                } catch (IOException e) {e.printStackTrace();}
                currentPage++;
                LinkOfProductsItem = backUpLink + startURLToNextPages + currentPage + stopURLToNextPages;
            }

            return null;
        }
        @Override protected void onPostExecute(String result) {
            progressBarItems.setVisibility(View.GONE);
            if(adapter.isEmpty())
                noProductsSearch.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged(); //update the adapter in order to display all pages
        }
    }

    //Add the product information for the next pages
    class UpdateAsyncTask extends AsyncTask<Void, Void, String>   {
        @Override protected void onPreExecute(){
            super.onPreExecute();
            if(searchOptionActivated){
                adapter.notifyDataSetChanged();
            }
            progressBarItems.setVisibility(View.VISIBLE);
        }
        @Override protected String doInBackground(Void... params) {
                try {
                    getNumberOfPages();
                    scrapeDataFromWeb(LinkOfProductsItem, pathToListOfItemsImage, pathToListOfItemsTitle, pathToListOfItemsPrice, pathToListOfItemsImageAtrr, pathToListOfItemsTitleAtrr, pathToItemURL, ID);
                } catch (IOException e) {e.printStackTrace();}


            return null;
        }
        @Override protected void onPostExecute(String result) {
            progressBarItems.setVisibility(View.GONE);
            pageLoaded=true;
            progressBarItems.setVisibility(View.GONE);
            if(adapter.isEmpty() && searchOptionActivated)
                noProductsSearch.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged(); //update the adapter in order to display all pages
        }
    }

    private void readDataFromDB(){
        //Create the query strings for getting the information from database latter
        String sqlRuleID = UtilityLibrary.createString(DatabaseAccess.IDField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);
        String sqlRulePathToListOfItemsImage = UtilityLibrary.createString(DatabaseAccess.PathToListOfItemsImageField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);
        String sqlRulePathToListOfItemsTitle = UtilityLibrary.createString(DatabaseAccess.PathToListOfItemsTitleField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);
        String sqlRulePathToListOfItemsPrice = UtilityLibrary.createString(DatabaseAccess.PathToListOfItemsPriceField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);
        String sqlRulePathToListOfItemsImageAtrr = UtilityLibrary.createString(DatabaseAccess.PathToListOfItemsImageAtrrField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);
        String sqlRulePathToListOfItemsTitleAtrr = UtilityLibrary.createString(DatabaseAccess.PathToListOfItemsTitleAtrrField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);
        String sqlRulePathToItemURL = UtilityLibrary.createString(DatabaseAccess.PathToIndividualItemURLField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);
        String sqlRulePathToLastPageNumber = UtilityLibrary.createString(DatabaseAccess.PathToLastPageNumberField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);
        String sqlRuleStartURLToNextPages = UtilityLibrary.createString(DatabaseAccess.StartURLToNextPagesField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);
        String sqlRuleStopURLToNextPages = UtilityLibrary.createString(DatabaseAccess.StopURLToNextPagesField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();//Opens the database

        //Get information from database
        ID = Integer.parseInt(databaseAccess.getData(sqlRuleID).get(0));
        pathToListOfItemsImage = databaseAccess.getData(sqlRulePathToListOfItemsImage).get(0);
        pathToListOfItemsTitle = databaseAccess.getData(sqlRulePathToListOfItemsTitle).get(0);
        pathToListOfItemsPrice = databaseAccess.getData(sqlRulePathToListOfItemsPrice).get(0);
        pathToListOfItemsImageAtrr = databaseAccess.getData(sqlRulePathToListOfItemsImageAtrr).get(0);
        pathToListOfItemsTitleAtrr = databaseAccess.getData(sqlRulePathToListOfItemsTitleAtrr).get(0);
        pathToItemURL = databaseAccess.getData(sqlRulePathToItemURL).get(0);
        pathToLastPageNumber = databaseAccess.getData(sqlRulePathToLastPageNumber).get(0);
        startURLToNextPages = databaseAccess.getData(sqlRuleStartURLToNextPages).get(0);
        stopURLToNextPages = databaseAccess.getData(sqlRuleStopURLToNextPages).get(0);

        databaseAccess.close();//Closes the database
    }

    private void displayItemsList() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Try to scrape information by calling the scrapeDataFromWeb() method
                try {
                    getNumberOfPages();
                    scrapeDataFromWeb(LinkOfProductsItem, pathToListOfItemsImage, pathToListOfItemsTitle, pathToListOfItemsPrice, pathToListOfItemsImageAtrr, pathToListOfItemsTitleAtrr, pathToItemURL, ID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        list.setAdapter(adapter);
                        progressBarItems.setVisibility(View.GONE);
                        UtilityLibrary.verifyConnectivity(getApplicationContext());
                    }
                });
            }
        });
        thread.start();
        adapter = new CustomListAdapters.CustomList(ShopListOfItemsActivity.this,imageList, titleList,priceList);
    }



    //This is where the scrape magic happens using JSoup
    private void scrapeDataFromWeb(String LinkOfProductsItem, final String pathToListOfItemsImage, String pathToListOfItemsTitle, String pathToListOfItemsPrice, String pathToListOfItemsImageAtrr, String pathToListOfItemsTitleAtrr, String pathToItemURL, int ID) throws IOException {
        Document doc = Jsoup.connect(LinkOfProductsItem).maxBodySize(0).get();//Connect to the website
        Elements linksImage = doc.select(pathToListOfItemsImage);//Select the information from the website
        Elements linksTitle = doc.select(pathToListOfItemsTitle);
        Elements linksPrice = doc.select(pathToListOfItemsPrice);
        Elements linksURL = doc.select(pathToItemURL);

            for (Element link : linksTitle) {
                titleList.add(link.attr(pathToListOfItemsTitleAtrr));
            }
            for (Element link : linksPrice) {
                String price=link.text();

                //For Emag because it displays the price without comma (wrong price displayed)
                if(ID==1){ price=UtilityLibrary.addCommaToPrice(link.text());}
                if(!price.equals(""))
                    priceList.add("Preț: " + price);
            }
            for (Element link : linksImage) {
                imageList.add(link.attr(pathToListOfItemsImageAtrr));
            }
            for (Element link : linksURL) {
                URLList.add(link.attr("abs:href"));
            }
            if(ShopNameAttribute.equals("PC Garage") && searchOptionActivated) {
                Element currentLink = doc.select("link").last();
                currentURLMG=currentLink.attr("href");
                if(currentURLMG.charAt(currentURLMG.length()-1)!='/')
                    currentURLMG=currentURLMG+"/";
            }

    }

    private void scrapeDataFromWebSearchEngine(final String LinkOfProductsItem, final String pathToListOfItemsImage, String pathToListOfItemsTitle, String pathToListOfItemsPrice, String pathToListOfItemsImageAtrr, String pathToListOfItemsTitleAtrr, String pathToItemURL, int ID) throws IOException{

        Document doc = Jsoup.connect(LinkOfProductsItem).maxBodySize(0).get();//Connect to the website
        Elements linksImage = doc.select(pathToListOfItemsImage);//Select the information from the website
        Elements linksTitle = doc.select(pathToListOfItemsTitle);
        Elements linksPrice = doc.select(pathToListOfItemsPrice);
        Elements linksURL = doc.select(pathToItemURL);
        ArrayList<Integer>searchPositions=new ArrayList<>();

            int index=0;
            for (Element link : linksTitle) {
                if(UtilityLibrary.searchVerification(link.attr(pathToListOfItemsTitleAtrr).toLowerCase(), searchQuery.toLowerCase())){
                    titleList.add(link.attr(pathToListOfItemsTitleAtrr));
                    searchPositions.add(index);
                }
                index++;
            }
            index=0;
            for (Element link : linksPrice) {
                if(searchPositions.contains(index))
                    priceList.add("Preț: " + link.text());
                index++;
            }
            index=0;
            for (Element link : linksImage) {
                if(searchPositions.contains(index))
                    imageList.add(link.attr(pathToListOfItemsImageAtrr));
                index++;
            }
            index=0;
            for (Element link : linksURL) {
                if(searchPositions.contains(index))
                    URLList.add(link.attr("abs:href"));
                index++;
            }
    }

    //Get from website the total number of pages for one specific subcategory
    private void getNumberOfPages() throws IOException {
        Document doc = Jsoup.connect(LinkOfProductsItem).maxBodySize(0).get();
        numberOfPages=1;

        if(ID!=2){
            Elements link = doc.select(pathToLastPageNumber);
            for(Element l : link) {
                try { numberOfPages=Integer.parseInt(l.text()); }
                catch(Exception ignored) {}
            }
        }
        else{
            Element link = doc.select(pathToLastPageNumber).last();
            numberOfPages=Integer.parseInt(link.text().split("/ ")[1]);
        }
    }

    private void deleteListsContent(){
        imageList.clear();
        titleList.clear();
        priceList.clear();
        URLList.clear();
    }

    //Stop the Thread when the back button is pressed, otherwise will still run in the background while entering in other subcategory
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        thread.interrupt();

    }

}

