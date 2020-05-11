package MonitorService;

import android.app.job.JobParameters;
import android.app.job.JobService;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import DatabaseManager.DatabaseAccess;
import Utilities.UtilityLibrary;

public class MonitorJobService extends JobService {
    private static final String TAG = "ExampleJobService";

    private static ArrayList<String> favShopName = new ArrayList<>();
    private static ArrayList<String> favURL = new ArrayList<>();
    private static ArrayList<String> favItemPrices = new ArrayList<>();
    private static ArrayList<String> favItemDates = new ArrayList<>();
    private static ArrayList<String> favItemTitle = new ArrayList<>();
    private static ArrayList<String> favItemImage = new ArrayList<>();

    private static String pathToPriceItem;
    private static boolean unavailableProduct = false;
    private boolean jobCancelled = false;


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG,"Job started");
        NotificationManager.showNotificationSingle(getApplicationContext());
        doBackgroundWork(jobParameters);
        return true;
    }

    private void doBackgroundWork(final JobParameters jobParameters){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sqlRuleFavShopName = UtilityLibrary.createString(DatabaseAccess.ShopNameFavField,DatabaseAccess.StockPricesTable);
                String sqlRuleFavURL = UtilityLibrary.createString(DatabaseAccess.LinkToTheProductField,DatabaseAccess.StockPricesTable);
                String sqlRuleFavItemPrices = UtilityLibrary.createString(DatabaseAccess.PricesField,DatabaseAccess.StockPricesTable);
                String sqlRuleFavItemDates = UtilityLibrary.createString(DatabaseAccess.DatesField,DatabaseAccess.StockPricesTable);
                String sqlRuleFavItemTitle = UtilityLibrary.createString(DatabaseAccess.TitleOfTheProductField,DatabaseAccess.StockPricesTable);
                String sqlRuleFavItemImage = UtilityLibrary.createString(DatabaseAccess.ImageOfTheProductField,DatabaseAccess.StockPricesTable);
                String sqlRuleSettingsNotificationPrice = UtilityLibrary.createString(DatabaseAccess.PriceNotificationRuleOptionField,DatabaseAccess.AppSettingsTable);

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open(); //databaseAccess.open();

                favShopName = databaseAccess.getData(sqlRuleFavShopName);
                favURL = databaseAccess.getData(sqlRuleFavURL);
                favItemPrices = databaseAccess.getData(sqlRuleFavItemPrices);
                favItemDates = databaseAccess.getData(sqlRuleFavItemDates);
                favItemTitle = databaseAccess.getData(sqlRuleFavItemTitle);
                favItemImage = databaseAccess.getData(sqlRuleFavItemImage);

                int settingsType;
                try {
                    settingsType=Integer.parseInt(databaseAccess.getData(sqlRuleSettingsNotificationPrice).get(0));
                }catch (IndexOutOfBoundsException e){
                    settingsType=1;
                }

                for(int index=0; index<favURL.size();index++){
                    String sqlRulePathToPriceItem = UtilityLibrary.createString(DatabaseAccess.PathToItemPriceField,DatabaseAccess.ShopListTable,DatabaseAccess.ShopNameField,favShopName.get(index));
                    pathToPriceItem = databaseAccess.getData(sqlRulePathToPriceItem).get(0);

                    try {
                        String newPrice = UtilityLibrary.transformPriceExtractNumber(getPriceScrapping(favURL.get(index),pathToPriceItem,favShopName.get(index)));
                        Log.d(TAG,favShopName.get(index)+" "+favURL.get(index)+" "+favItemPrices.get(index)+" "+favItemDates.get(index));
                        boolean priceModified = verifyPriceModification(favItemPrices.get(index),newPrice);
                        boolean priceIsLower;
                        if(settingsType==1)
                            priceIsLower=verifyPriceDecrease(favItemPrices.get(index),newPrice);
                        else priceIsLower=true;

                        //This verification is made so that u can not write to database from 2 different location
                        if(!jobCancelled) {
                            if(priceModified && !unavailableProduct){
                                databaseAccess.updateData(favURL.get(index),priceToUpdate(favItemPrices.get(index),newPrice),favItemDates.get(index));
                                if(priceIsLower)
                                     NotificationManager.sendNotification(getApplicationContext(),favShopName.get(index),favItemImage.get(index),favItemTitle.get(index),favURL.get(index),newPrice,index);
                            }
                        }
                        else return;
                    }
                    catch (IOException e) { e.printStackTrace(); }
                }

                databaseAccess.close();//databaseAccess.close();
                Log.d(TAG,"Job finished");
                jobFinished(jobParameters,false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG,"Job cancelled before completion");
        jobCancelled=true;
        return true;
    }

    private String getPriceScrapping(String URL, String pathToPriceItem,String shopName) throws IOException {
        Document doc = Jsoup.connect(URL).maxBodySize(0).get();//Connect to the website

        try {
            unavailableProduct = false;
            if(shopName.matches("PC Garage")){
                Element linkPrice = doc.select(pathToPriceItem).last();
                return linkPrice.text();
            }
            else{
                Element linkPrice = doc.selectFirst(pathToPriceItem);
                if(shopName.matches("Emag")) return UtilityLibrary.addCommaToPrice(linkPrice.text());
                else return linkPrice.text();
            }
        }catch (NullPointerException e){ // This happens when the product becomes unavailable
            unavailableProduct = true;
            return  "Produs indisponibil";
        }
    }

    private boolean verifyPriceModification(String oldPrice,String newPrice){
        String lastPrice = oldPrice.substring(oldPrice.lastIndexOf('-') + 1);
        if(!lastPrice.equals(newPrice) || newPrice.equals(""))
            return true;
        return false;
    }

    private boolean verifyPriceDecrease(String oldPrice,String newPrice){
        String lastPrice = oldPrice.substring(oldPrice.lastIndexOf('-') + 1);
        if(!newPrice.equals("") &&  Float.parseFloat(UtilityLibrary.transformPriceExtractNumberChart(lastPrice))<Float.parseFloat(UtilityLibrary.transformPriceExtractNumberChart(newPrice))) {
            return false;
        }
        return true;
    }

    private String priceToUpdate(String oldPrice, String newPrice){
        return oldPrice+"-"+newPrice;
    }
}