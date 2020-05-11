package com.example.monitorapp_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import ChartManager.ChartManager;
import DatabaseManager.DatabaseAccess;
import Utilities.UtilityLibrary;

public class ShopIndividualProductDisplay extends AppCompatActivity {

    private ArrayList<String> specsList = new ArrayList<>();
    private ArrayList<String> reviewList = new ArrayList<>();

    private static String ShopNameAttribute,LinkOfIndividualProduct,PriceOfIndividualProduct,TitleOfIndividualProduct,ImageOfIndividualProduct;
    private String pathToTechSpecificationsIndvProduct,pathToReviewOfTheProduct,pathToRatingOfTheProduct,pathToListOfItemsImage,pathToListOfItemsTitle,pathToListOfItemsPrice;
    private String pricesFromDB, datesFromDB;
    private String Rating;

    private Thread thread;
    private Vibrator vibrator;
    private int ID;
    private static boolean productExistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_individual_product_display);

        final Intent intent = getIntent();
        //Get attribute from previously activity
        ShopNameAttribute = intent.getStringExtra("ShopNameAttribute");
        LinkOfIndividualProduct = intent.getStringExtra("LinkOfIndividualProduct");
        ImageOfIndividualProduct = intent.getStringExtra("ImageOfIndividualProduct");

        //Action bar logo display settings
        if (getSupportActionBar() != null) {
            //Action bar logo display settings
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().hide();
        }

        //Go to the original site when the button is pushed
        Button button = findViewById(R.id.toWebsiteBut);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!Objects.requireNonNull(UtilityLibrary.getTypeOfRingerMode(getApplicationContext())).equals("SILENT"))
                    vibrator.vibrate(50);
                if( UtilityLibrary.verifyConnectivity(getApplicationContext())) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(LinkOfIndividualProduct));
                    startActivity(intent);
                }
            }
        });



        displayItemDetails();

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        if (productExistence) floatingActionButton.setImageResource(R.drawable.delete_button);
        else floatingActionButton.setImageResource(R.drawable.add_button);

        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        //Create chart diagram for individual product
        LineChart lineChart = findViewById(R.id.lineChart);
        ChartManager chartManager = new ChartManager();
        Context context = getApplicationContext();
        chartManager.displayChart(context,lineChart,pricesFromDB,datesFromDB,ShopNameAttribute);

        //Add the wanting product in the db
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!Objects.requireNonNull(UtilityLibrary.getTypeOfRingerMode(getApplicationContext())).equals("SILENT"))
                    vibrator.vibrate(100);//the phone will vibrate when the button is touched
                DatabaseAccess databaseAccess =  DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();//opens the database

                JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                if (scheduler != null) scheduler.cancel(123);

                if (!productExistence) {
                    Toast.makeText(getApplicationContext(), "Produs adaugat.", Toast.LENGTH_SHORT).show();
                    databaseAccess.addData(LinkOfIndividualProduct, PriceOfIndividualProduct, ShopNameAttribute, TitleOfIndividualProduct, ImageOfIndividualProduct);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Produs sters.", Toast.LENGTH_SHORT).show();
                    databaseAccess.deleteData(LinkOfIndividualProduct);
                }
                finish();
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                databaseAccess.close();//closes the database
            }
        });
    }

    private void displayItemDetails() {

         thread = new Thread(new Runnable() {

            @Override
            public void run() {
                DatabaseAccess databaseAccess =  DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();//opens the database

                //Create the query strings for getting the information from database latter
                String sqlRuleID = UtilityLibrary.createString(DatabaseAccess.IDField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);
                String sqlRulePathToTechSpecificationsIndvProduct = UtilityLibrary.createString(DatabaseAccess.PathToTechSpecificationsIndvProductField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);
                String sqlRulePathToReviewIndvProduct = UtilityLibrary.createString(DatabaseAccess.PathToProductReviewsField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);
                String sqlRulePathToRatingIndvProduct = UtilityLibrary.createString(DatabaseAccess.PathToProductRatingsField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);
                String sqlRulePathToListOfItemsImage = UtilityLibrary.createString(DatabaseAccess.PathToItemImageField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);
                String sqlRulePathToListOfItemsTitle = UtilityLibrary.createString(DatabaseAccess.PathToItemTitleField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);
                String sqlRulePathToListOfItemsPrice = UtilityLibrary.createString(DatabaseAccess.PathToItemPriceField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,ShopNameAttribute);

                //Get information from database
                ID = Integer.parseInt(databaseAccess.getData(sqlRuleID).get(0));
                pathToTechSpecificationsIndvProduct = databaseAccess.getData(sqlRulePathToTechSpecificationsIndvProduct).get(0);
                pathToReviewOfTheProduct= databaseAccess.getData(sqlRulePathToReviewIndvProduct).get(0);
                pathToRatingOfTheProduct = databaseAccess.getData(sqlRulePathToRatingIndvProduct).get(0);
                pathToListOfItemsImage = databaseAccess.getData(sqlRulePathToListOfItemsImage).get(0);
                pathToListOfItemsTitle = databaseAccess.getData(sqlRulePathToListOfItemsTitle).get(0);
                pathToListOfItemsPrice = databaseAccess.getData(sqlRulePathToListOfItemsPrice).get(0);

                String sqlRulePrices = UtilityLibrary.createString(DatabaseAccess.PricesField,DatabaseAccess.StockPricesTable,DatabaseAccess.LinkToTheProductField,LinkOfIndividualProduct);
                String sqlRuleDates = UtilityLibrary.createString(DatabaseAccess.DatesField,DatabaseAccess.StockPricesTable,DatabaseAccess.LinkToTheProductField,LinkOfIndividualProduct);

                //For the products that are not added that are not found in db, place a default value
                try {
                    pricesFromDB = databaseAccess.getData(sqlRulePrices).get(0);
                    datesFromDB = databaseAccess.getData(sqlRuleDates).get(0);
                }catch (Exception e){
                    pricesFromDB="0";
                    datesFromDB = UtilityLibrary.getCurrentDate();
                }

                //Erase the array content every time before we use it
                specsList.clear();
                reviewList.clear();

                //Try to scrape information by calling the scrapeDataFromWeb() method
                try {
                    scrapeDataFromWeb(LinkOfIndividualProduct,pathToTechSpecificationsIndvProduct,pathToReviewOfTheProduct,pathToRatingOfTheProduct, pathToListOfItemsImage,  pathToListOfItemsTitle, pathToListOfItemsPrice,ID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                productExistence = databaseAccess.verifyDataExistence(LinkOfIndividualProduct);

                databaseAccess.close();//closes the database
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Context context=getApplicationContext();

                        TextView txtTitle = findViewById(R.id.textTitle);
                        TextView txtPrice = findViewById(R.id.textPrice);
                        ImageView imageView = findViewById(R.id.imgProduct);

                        TextView txtSpecs = findViewById(R.id.textSpecifics);
                        TextView txtReview = findViewById(R.id.textComments);
                        ImageView imageRating = findViewById(R.id.imageRating);

                        //Set the parsed data in UI
                        txtSpecs.setText(createString(specsList,1));
                        txtReview.setText(createString(reviewList,2));
                        txtTitle.setText(TitleOfIndividualProduct);
                        txtPrice.setText(PriceOfIndividualProduct);
                        Picasso.with(context).load(ImageOfIndividualProduct).into(imageView);

                        int imageRate = selectRatingImage(Rating);
                        imageRating.setImageResource(imageRate);
                        UtilityLibrary.verifyConnectivity(getApplicationContext());
                    }
                });
            }
        });
        thread.start();

        try {
            thread.join();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    //This is where the scrape magic happens using JSoup
    private void scrapeDataFromWeb(String LinkOfIndividualProduct, String pathToTechSpecificationsIndvProduct,String pathToReviewOfTheProduct, String pathToRatingOfTheProduct,String pathToListOfItemsImage, String pathToListOfItemsTitle,String pathToListOfItemsPrice ,int ID) throws IOException {
        Document doc = Jsoup.connect(LinkOfIndividualProduct).get();//Connect to the website
        Elements linkSpecs = doc.select(pathToTechSpecificationsIndvProduct);
        Element linkRating = doc.selectFirst(pathToRatingOfTheProduct);
        Element linksImage = doc.selectFirst(pathToListOfItemsImage);//Select the information from the website
        Element linksTitle = doc.selectFirst(pathToListOfItemsTitle);

        try {
            TitleOfIndividualProduct=linksTitle.text();
            if(ImageOfIndividualProduct==null)
                 ImageOfIndividualProduct=linksImage.attr("src");
            if (ID == 3) {
                Element linkPrice = doc.select(pathToListOfItemsPrice).last();
                PriceOfIndividualProduct = "Preț: " + linkPrice.text();
            } else {
                Element linkPrice = doc.selectFirst(pathToListOfItemsPrice);
                if (ID == 1)
                    PriceOfIndividualProduct = "Preț: " + UtilityLibrary.addCommaToPrice(linkPrice.text());
                else PriceOfIndividualProduct = "Preț: " + linkPrice.text();
            }
        }catch (NullPointerException e){PriceOfIndividualProduct = "Produs indisponibil";}

		for (Element link : linkSpecs) {
		    if(ID==3) //For PsGarage we need to remove the content from group class
                link.select(".group").remove();
			specsList.add(link.text());
		}

		if(ID!=2) {
            Elements linkReview = doc.select(pathToReviewOfTheProduct);
            for (Element link : linkReview) {
                reviewList.add(link.text());
            }
            //Verify if there is any comments for individual product
            if(reviewList.isEmpty())
                reviewList.add("Niciun comentariu");
        }
		else
            reviewList.add("Niciun comentariu"); //I couldn't get the reviews for MediaGalaxy

        //If the rating is not specified, there is nobody who have granted one -> rating will be from start 0
		if(linkRating!=null)
            Rating = linkRating.text();
		else
		    Rating = "0";
    }

    //Create the string for the comments section and for specs section
    private String createString(ArrayList array,int index){
        String text="";
        for(int i=0; i<array.size();i++) {
            if(index==2) //for comments
                text=text+"* "+array.get(i)+"\n\n\n";
            else  //for reviews
                text=text+array.get(i)+"\n";
        }
        return text;
    }

    //Display one rating image considering the value of the product rating number
    private int selectRatingImage(String rating)
    {
        int ratingImage=R.drawable.rating0;

        try{
            switch (rating.charAt(0)) {
                case '1':
                    ratingImage = R.drawable.rating1;
                    break;
                case '2':
                    ratingImage = R.drawable.rating2;
                    break;
                case '3':
                    ratingImage = R.drawable.rating3;
                    break;
                case '4':
                    ratingImage = R.drawable.rating4;
                    break;
                case '5':
                    ratingImage = R.drawable.rating5;
                    break;
            }
        }catch(NullPointerException e){}

        return ratingImage;
    }

    //Stop the Thread when the back button is pressed, otherwise will still run in the background while entering in other category
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        thread.interrupt();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Refresh list view data.
        finish();
        startActivity(getIntent());
    }

}
