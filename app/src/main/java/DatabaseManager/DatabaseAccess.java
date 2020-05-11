package DatabaseManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import Utilities.UtilityLibrary;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;

    public static final String ShopListTable = "ShopsList";
    public static final String WebsiteURLField = "WebsiteURL";
    public static final String ShopNameField = "ShopName";
    public static final String IDField = "ID";
    public static final String PathForProductCategoryField = "PathForProductCategory";
    public static final String PathForProductSubcategoryField = "PathForProductSubcategory";
    public static final String PathToListOfItemsImageField = "PathToListOfItemsImage";
    public static final String PathToListOfItemsTitleField = "PathToListOfItemsTitle";
    public static final String PathToListOfItemsPriceField = "PathToListOfItemsPrice";
    public static final String PathToListOfItemsImageAtrrField = "PathToListOfItemsImageAtrr";
    public static final String PathToListOfItemsTitleAtrrField = "PathToListOfItemsTitleAtrr";
    public static final String PathToIndividualItemURLField = "PathToIndividualItemURL";
    public static final String PathToItemTitleField = "PathToItemTitle";
    public static final String PathToItemPriceField = "PathToItemPrice";
    public static final String PathToItemImageField = "PathToItemImage";
    public static final String PathToTechSpecificationsIndvProductField = "PathToTechSpecificationsIndvProduct";
    public static final String PathToProductReviewsField = "PathToProductReviews";
    public static final String PathToProductRatingsField = "PathToProductRatings";
    public static final String PathToLastPageNumberField = "PathToLastPageNumber";
    public static final String StartURLToNextPagesField = "StartURLToNextPages";
    public static final String StopURLToNextPagesField = "StopURLToNextPages";

    public static final String StockPricesTable = "StockPrices";
    public static final String LinkToTheProductField = "LinkToTheProduct";
    public static final String TitleOfTheProductField = "TitleOfTheProduct";
    public static final String ImageOfTheProductField = "ImageOfTheProduct";
    public static final String PricesField = "Prices";
    public static final String DatesField = "Dates";
    public static final String HourField = "Hour";
    public static final String ShopNameFavField = "ShopNameFav";

    public static final String AppSettingsTable = "AppSettings";
    public static final String ServiceTimeIntervalOptionField = "ServiceTimeIntervalOption";
    public static final String PriceNotificationRuleOptionField = "PriceNotificationRuleOption";

    //Private constructor so that object creation from outside the class is avoided
    private DatabaseAccess(Context context){
        this.openHelper = new DataBaseHelper(context);
    }

    public static DatabaseAccess getInstance(Context context){
        instance = new DatabaseAccess(context);
        return instance;
    }

    //to open the database
    public void open(){
        this.db = openHelper.getWritableDatabase();
    }

    //closing the database
    public void close() {
        if(db!=null){
            this.db.close();
        }
    }

    public void addData(String link, String price,String shopName,String titleOfIndividualProduct, String imageOfIndividualProduct) {

        ContentValues content = new ContentValues();
        content.put(LinkToTheProductField,link);
        content.put(TitleOfTheProductField,titleOfIndividualProduct);
        content.put(ImageOfTheProductField,imageOfIndividualProduct);
        content.put(PricesField,UtilityLibrary.transformPriceExtractNumber(price));
        content.put(DatesField,UtilityLibrary.getCurrentDate());
        content.put(ShopNameFavField,shopName);
        content.put(HourField,UtilityLibrary.getCurrentHour());
        db.insert(StockPricesTable,null,content);
    }

    public void deleteData(String link){
        db.execSQL(" DELETE  FROM "+StockPricesTable+" WHERE "+LinkToTheProductField+" =\""+link+"\"");
    }

    public void doIt(){
        //db.execSQL("CREATE TABLE AppSettings (ServiceTimeIntervalOption INTEGER,PriceNotificationRuleOption INTEGER);");
//        db.execSQL(" UPDATE AppSettings SET ServiceTimeIntervalOption = 1 ");
//        db.execSQL(" UPDATE AppSettings SET PriceNotificationRuleOption = 1 ");
        ContentValues content = new ContentValues();
        content.put(ServiceTimeIntervalOptionField,3);
        content.put(PriceNotificationRuleOptionField,1);
        db.insert(AppSettingsTable,null,content);

    }

    public boolean verifyDataExistence(String link){
        String queryGetPrice = UtilityLibrary.createString(PricesField,StockPricesTable,LinkToTheProductField,link);
        ArrayList<String> result = getData(queryGetPrice);

        if(!result.isEmpty())
            return true;
        return false;
    }

    public void updateData(String URL,String newPriceToUpdate,String newDateToUpdate){

        ContentValues cv = new ContentValues();
        cv.put(PricesField, newPriceToUpdate);
        cv.put(DatesField,newDateToUpdate+"-"+UtilityLibrary.getCurrentDate());
        cv.put(HourField,UtilityLibrary.getCurrentHour());
        db.update(StockPricesTable, cv, "LinkToTheProduct =\"" + URL+"\"", null);
    }

    public void updateSettingsPriceNotificationOption(int value){
        db.execSQL(" UPDATE AppSettings SET PriceNotificationRuleOption = "+value+" ");
    }

    public void updateSettingsServiceTimeIntervalOption(int value){
        db.execSQL(" UPDATE AppSettings SET ServiceTimeIntervalOption = "+value+" ");
    }

    public ArrayList<String> getData(String sqlRule){
        Cursor c=db.rawQuery(sqlRule, new String[]{});
        ArrayList <String> results = new ArrayList<>();

        while (c.moveToNext()){
            String s = c.getString(0);
            results.add(s);
        }
        c.close();
        return results;
    }
}
