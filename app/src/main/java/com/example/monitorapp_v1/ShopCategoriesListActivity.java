package com.example.monitorapp_v1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Objects;
import Controller.ShopCategoriesListController;
import Utilities.UtilityLibrary;


public class ShopCategoriesListActivity extends AppCompatActivity {
    private ImageView logo;
    private Vibrator vibrator;
    private ListView listView;
    private ProgressBar progressBar;
    private TextView categoryShopNameLabel;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String ShopNameAttribute;
    private ShopCategoriesListController shopCategoriesListController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_category_list);
        if (getSupportActionBar() != null)  getSupportActionBar().hide();

        Intent intent = getIntent();
        ShopNameAttribute = intent.getStringExtra("ShopNameAttribute");//Get attribute from previously activity
        initializeXMLVariables();

        categoryShopNameLabel.setText("Categorii ");
        setImageLogo();
        setListContent();

        progressBar.setVisibility(View.GONE);
        UtilityLibrary.verifyConnectivity(getApplicationContext());

        //The actions that happen when you press a element from the listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
        int position, long id) {
        // TODO Auto-generated method stub
            if(!Objects.requireNonNull(UtilityLibrary.getTypeOfRingerMode(getApplicationContext())).equals("SILENT"))
                vibrator.vibrate(50);

            if( UtilityLibrary.verifyConnectivity(getApplicationContext())) {
                Intent intent = new Intent(getApplicationContext(), ShopSubCategoryActivity.class);
                intent.putExtra("ShopNameAttribute", ShopNameAttribute);
                intent.putExtra("PositionOfCategory",Integer.toString( position));
                startActivity(intent);
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
    }

    private void initializeXMLVariables(){
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh2);
        categoryShopNameLabel = findViewById(R.id.categoryShopNameLabel);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        progressBar = findViewById(R.id.progressBar);
        listView = findViewById(R.id.list_view);
        logo = findViewById(R.id.imageLogo);
    }

    private void setListContent() {
        shopCategoriesListController = new ShopCategoriesListController(getApplicationContext(),ShopNameAttribute);
        shopCategoriesListController.displayCategoriesList();
        ArrayAdapter aAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                shopCategoriesListController.getCategoriesList());
        listView.setAdapter(aAdapter);
    }

    private void setImageLogo(){
        if(ShopNameAttribute.equals("Emag"))
            logo.setImageResource(R.drawable.logo_emag);
        else if(ShopNameAttribute.equals("Media Galaxy"))
            logo.setImageResource(R.drawable.logo_media_galaxy_2);
        else if(ShopNameAttribute.equals("PC Garage"))
            logo.setImageResource(R.drawable.logo_pc_garage);
    }

    //Stop the Thread when the back button is pressed, otherwise will still run in the background while entering in other category
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        shopCategoriesListController.stopThread();
    }
}
