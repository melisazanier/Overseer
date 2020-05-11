package com.example.monitorapp_v1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Objects;

import Controller.FavoriteProductsListController;
import Utilities.UtilityLibrary;

public class FavoriteProductsList extends AppCompatActivity {

    private FavoriteProductsListController backgroundFavoriteProductsListController;
    private ListView list;
    private ArrayAdapter adapter;
    private Vibrator vibrator;
    private AsyncTask asyncTask;
    private TextView textNoProducts;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_products_list);

        //Action bar logo display settings
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        initializeXMLVariables();
        textNoProducts.setVisibility(View.GONE);
        getListContent();

        //The actions that happen when you press a element from the listView
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(!Objects.requireNonNull(UtilityLibrary.getTypeOfRingerMode(getApplicationContext())).equals("SILENT"))
                    vibrator.vibrate(50);
                view.setSelected(true);

                if(UtilityLibrary.verifyConnectivity(getApplicationContext())) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(getApplicationContext(), ShopIndividualProductDisplay.class);
                    intent.putExtra("ShopNameAttribute",
                            backgroundFavoriteProductsListController.getListOfProducts().getFavShopName().get(position));
                    intent.putExtra("LinkOfIndividualProduct",
                            backgroundFavoriteProductsListController.getListOfProducts().getFavURL().get(position));
                    intent.putExtra("ImageOfIndividualProduct",
                            backgroundFavoriteProductsListController.getListOfProducts().getImageList().get(position));
                    startActivity(intent);//Opens a new activity
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
    }

    private void initializeXMLVariables(){
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh1);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        textNoProducts = findViewById(R.id.textNoProducts);
        list = findViewById(R.id.list);
    }

    private void getListContent(){
        backgroundFavoriteProductsListController = new FavoriteProductsListController(getApplicationContext());
        backgroundFavoriteProductsListController.deleteArrays();

        adapter = new CustomListAdapters.CustomList(FavoriteProductsList.this,
                backgroundFavoriteProductsListController.getListOfProducts().getImageList(),
                backgroundFavoriteProductsListController.getListOfProducts().getTitleList(),
                backgroundFavoriteProductsListController.getListOfProducts().getPriceList());
        asyncTask = new FavoriteProductsList.UpdateAsyncTask().execute();

    }

    //Get product information for the favorite products
    public class UpdateAsyncTask extends AsyncTask<Void, Void, String>   {
        @Override protected void onPreExecute(){ super.onPreExecute(); }
        @Override protected String doInBackground(Void... params) {
            backgroundFavoriteProductsListController.getCurrentProductDataFromDB();
            return null;
        }
        @Override protected void onPostExecute(String result) {
            if(adapter.isEmpty())
                textNoProducts.setVisibility(View.VISIBLE);
            else
                textNoProducts.setVisibility(View.GONE);
            list.setAdapter(adapter);
        }
    }

    //Stop the Thread when the back button is pressed, otherwise will still run in the background while entering in other subcategory
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        asyncTask.cancel(true);
    }
}
