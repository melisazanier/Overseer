package com.example.monitorapp_v1;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import DatabaseManager.DatabaseAccess;
import SlideNewestProductsManager.*;
import Utilities.UtilityLibrary;

public class MainActivity extends FragmentActivity {
    private Vibrator vibrator;

    private static SlideNewestProductsManager.ProductElement[] slideShopArray;
    private static int slides = 0;

    private static int NUM_PAGES = 1;
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;
    private static boolean refresh=false,restartServiceState=false;
    private static int settingsInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        final Button goToShopsBtn = findViewById(R.id.goToShopsButton);
        final Button favoriteProductsBtn = findViewById(R.id.favBtn);
        final Button settingsButton= findViewById(R.id.settingsButton);
        TextView noProductsSlide = findViewById(R.id.noProductsSlide);

        noProductsSlide.setVisibility(View.GONE);
        final ImageView logoText = findViewById(R.id.logoTextImage);
        logoText.setImageResource(R.drawable.logo_text_3);

        getProductsForSlide();

        if(!refresh )
            startServiceMethod();

        if(slides == 0)
            noProductsSlide.setVisibility(View.VISIBLE);
        NUM_PAGES = slides;

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setAdapter(pagerAdapter);

        logoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Refresh list view data.
                if(!Objects.requireNonNull(UtilityLibrary.getTypeOfRingerMode(getApplicationContext())).equals("SILENT"))
                    vibrator.vibrate(50);
                setRefreshState(true);
                finish();
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });

        favoriteProductsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!Objects.requireNonNull(UtilityLibrary.getTypeOfRingerMode(getApplicationContext())).equals("SILENT"))
                    vibrator.vibrate(50);
                setRefreshState(true);
                favoriteProductsBtn.setBackgroundColor(getResources().getColor(R.color.newSlideColor));
                Intent intent = new Intent(getApplicationContext(), FavoriteProductsList.class);
                startActivity(intent);
            }
        });

        goToShopsBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(!Objects.requireNonNull(UtilityLibrary.getTypeOfRingerMode(getApplicationContext())).equals("SILENT"))
                        vibrator.vibrate(50);
                    setRefreshState(true);
                    goToShopsBtn.setBackgroundColor(getResources().getColor(R.color.newSlideColor));
                    Intent intent = new Intent(getApplicationContext(), ShopsListActivity.class);
                    startActivity(intent);
                }
            });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!Objects.requireNonNull(UtilityLibrary.getTypeOfRingerMode(getApplicationContext())).equals("SILENT"))
                    vibrator.vibrate(50);
                setRefreshState(true);
                settingsButton.setBackgroundColor(getResources().getColor(R.color.newSlideColor));
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getProductsForSlide() {
        String sqlRuleFavShopName = UtilityLibrary.createString(DatabaseAccess.ShopNameFavField,DatabaseAccess.StockPricesTable);
        String sqlRuleFavURL = UtilityLibrary.createString(DatabaseAccess.LinkToTheProductField,DatabaseAccess.StockPricesTable);
        String sqlRuleFavTitleOfItem = UtilityLibrary.createString(DatabaseAccess.TitleOfTheProductField,DatabaseAccess.StockPricesTable);
        String sqlRuleFavPriceOfItem = UtilityLibrary.createString(DatabaseAccess.PricesField,DatabaseAccess.StockPricesTable);
        String sqlRuleFavImageOfItem = UtilityLibrary.createString(DatabaseAccess.ImageOfTheProductField,DatabaseAccess.StockPricesTable);
        String sqlRuleFavDateOfItem = UtilityLibrary.createString(DatabaseAccess.DatesField,DatabaseAccess.StockPricesTable);
        String sqlRuleFavHourOfItem = UtilityLibrary.createString(DatabaseAccess.HourField,DatabaseAccess.StockPricesTable);

        String sqlRuleTimeInterval = UtilityLibrary.createString(DatabaseAccess.ServiceTimeIntervalOptionField,DatabaseAccess.AppSettingsTable);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();//Opens the database


        try {
            settingsInterval=Integer.parseInt(databaseAccess.getData(sqlRuleTimeInterval).get(0));

        }catch (IndexOutOfBoundsException e){settingsInterval=60;}

        //databaseAccess.doIt();

        ArrayList<String> slideShopNameArray = new ArrayList<>();
        ArrayList<String> slideImagesArray = new ArrayList<>();
        ArrayList<String> slideTitleArray = new ArrayList<>();
        ArrayList<String> slidePriceArray = new ArrayList<>();
        ArrayList<String> slideURLArray = new ArrayList<>();
        ArrayList<String> slideDateArray = new ArrayList<>();
        ArrayList<String> slideHourArray = new ArrayList<>();

        slideShopNameArray.addAll(databaseAccess.getData(sqlRuleFavShopName)) ;
        slideURLArray.addAll(databaseAccess.getData(sqlRuleFavURL));
        slideTitleArray.addAll(databaseAccess.getData(sqlRuleFavTitleOfItem)) ;
        slidePriceArray.addAll(databaseAccess.getData(sqlRuleFavPriceOfItem)) ;
        slideImagesArray.addAll(databaseAccess.getData(sqlRuleFavImageOfItem)) ;
        slideDateArray.addAll(databaseAccess.getData(sqlRuleFavDateOfItem)) ;
        slideHourArray.addAll(databaseAccess.getData(sqlRuleFavHourOfItem)) ;

        databaseAccess.close();//close database

        //Sort the list and take the newest 7 or less elements to display them in slide
        ProductSlide productSlide = new ProductSlide(7);
        for(int i=0;i<slideDateArray.size();i++) {
            ProductElement productElement = new ProductElement(
                    slideShopNameArray.get(i),
                    slideImagesArray.get(i),
                    slideTitleArray.get(i),
                    slideURLArray.get(i),
                    slideDateArray.get(i),
                    slideHourArray.get(i));
            productSlide.addProduct(productElement);
        }
        slides = productSlide.getCounter();
        if(slides!=0) {
            slideShopArray = productSlide.getProducts();
            if(slides==7)
                slideShopArray = productSlide.sortProducts();
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ScreenSlidePageFragment pageFragment = new ScreenSlidePageFragment();
            Bundle bundle = new Bundle();

            bundle.putString("title",slideShopArray[position].getTitle());
            bundle.putString("image",slideShopArray[position].getImage());
            bundle.putString("url",slideShopArray[position].getUrl());
            bundle.putString("shopName",slideShopArray[position].getShopName());

            pageFragment.setArguments(bundle);
            return pageFragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public void startServiceMethod (){
        if(settingsInterval<30)
            settingsInterval=settingsInterval*60;
        ComponentName componentName = new ComponentName(this, MonitorService.MonitorJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(123,componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                //.setRequiresCharging(true)
                .setPersisted(true)
                .setPeriodic(settingsInterval*60*1000)
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(jobInfo);

        if(resultCode==JobScheduler.RESULT_SUCCESS)Log.d("ExampleJobService","Job scheduled");
        else Log.d("ExampleJobService","Job scheduled failed");
    }

    public static void setRefreshState(boolean val){
        refresh=val;
    }
    public static void restartService(boolean val){
        restartServiceState=val;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Refresh list view data.
        if(refresh) {
            finish();
            startActivity(getIntent());
            overridePendingTransition(0, 0);
            if(restartServiceState){
                startServiceMethod();
                restartServiceState=false;
            }
        }

    }

    @Override
    public void onBackPressed() {
        setRefreshState(false);
        finish();
        return;
    }
}
