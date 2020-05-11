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
import Controller.ShopSubCategoryController;
import Utilities.UtilityLibrary;

public class ShopSubCategoryActivity extends AppCompatActivity {
    private ListView listView;
    private ProgressBar progressBar;
    private Vibrator vibrator;
    private ImageView logo;
    private TextView subcategoryShopsNameLabel;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String ShopNameAttribute,PositionOfCategory;
    private ShopSubCategoryController shopSubCategoryController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_sub_category);

        //Action bar logo display settings
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        Intent intent = getIntent();
        //Get attribute from previously activity
        ShopNameAttribute = intent.getStringExtra("ShopNameAttribute");
        PositionOfCategory = intent.getStringExtra("PositionOfCategory");

        initializeXMLVariables();
        subcategoryShopsNameLabel.setText("Subcategorii ");
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
                if(!Objects.requireNonNull(UtilityLibrary.getTypeOfRingerMode(getApplicationContext())).equals("SILENT"))
                    vibrator.vibrate(50);

                if( UtilityLibrary.verifyConnectivity(getApplicationContext())) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(getApplicationContext(), ShopListOfItemsActivity.class);
                    //Messages that will be send to the next activity that will open
                    intent.putExtra("ShopNameAttribute", ShopNameAttribute);
                    intent.putExtra("PositionOfSubcategory", Integer.toString(position));
                    intent.putExtra("LinkOfProductsItem", shopSubCategoryController.getLinkToPass().get(position));
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
    }

    private void initializeXMLVariables(){
        logo = findViewById(R.id.imageLogo);
        listView = findViewById(R.id.list_view);
        progressBar = findViewById(R.id.progressBar);
        mSwipeRefreshLayout= findViewById(R.id.swiperefresh4);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        subcategoryShopsNameLabel = findViewById(R.id.subcategoryShopsNameLabel);
    }

    private void setListContent(){
        shopSubCategoryController = new ShopSubCategoryController(getApplicationContext(),
                ShopNameAttribute,PositionOfCategory);
        ArrayAdapter aAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                shopSubCategoryController.getSubcategoriesList());

        listView.setAdapter(aAdapter);
        shopSubCategoryController.manageSubCategoriesList();
        shopSubCategoryController.joinThread();
        aAdapter.notifyDataSetChanged();
    }

    private void setImageLogo(){
        if(ShopNameAttribute.equals("Emag"))
            logo.setImageResource(R.drawable.logo_emag);
        else if(ShopNameAttribute.equals("Media Galaxy"))
            logo.setImageResource(R.drawable.logo_media_galaxy_2);
        else if(ShopNameAttribute.equals("PC Garage"))
            logo.setImageResource(R.drawable.logo_pc_garage);
    }

    //Stop the Thread when the back button is pressed
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        shopSubCategoryController.stopThread();
    }

}
