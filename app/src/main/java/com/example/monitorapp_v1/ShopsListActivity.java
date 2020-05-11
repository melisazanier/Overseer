package com.example.monitorapp_v1;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Objects;

import Controller.ShopsListController;
import Utilities.UtilityLibrary;


public class ShopsListActivity extends AppCompatActivity {

    private Vibrator vibrator;
    private ListView listView;
    private ShopsListController backgroundShopsListController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops_list);

        //Action bar logo display settings
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        listView = findViewById(R.id.listViewShop);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        setListContent();

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
                    Intent intent = new Intent(getApplicationContext(), ShopCategoriesListActivity.class);
                    intent.putExtra("ShopNameAttribute", backgroundShopsListController.getShopsList().get(position)); //Send a attribute to the activity that will be opened
                    startActivity(intent);  //Opens a new activity
                }

            }
        });
    }

    private void setListContent(){
        backgroundShopsListController = new ShopsListController(getApplicationContext());
        backgroundShopsListController.readShopsListThread();
        backgroundShopsListController.setLogoList();

        ArrayAdapter aAdapter = new CustomListAdapters.CustomShopList(ShopsListActivity.this,
                backgroundShopsListController.getLogoList(),
                backgroundShopsListController.getShopsList());
        listView.setAdapter(aAdapter);
    }

}
