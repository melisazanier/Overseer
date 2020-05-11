package Controller;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import DatabaseManager.DatabaseAccess;
import Utilities.UtilityLibrary;

public class FavoriteProductsListController {
    private Context context;
    private ListOfProducts listOfProducts;
    private String sqlRuleFavShopName,sqlRuleFavURL,sqlRuleFavTitleOfItem,sqlRuleFavPriceOfItem,sqlRuleFavImageOfItem;
    private DatabaseAccess databaseAccess;

    public FavoriteProductsListController(Context context) {
        this.context = context;
        listOfProducts = new ListOfProducts();
    }

    public void deleteArrays(){
        listOfProducts.deleteArrayContent();
    }

    public void getCurrentProductDataFromDB() {
        ArrayList <String> auxPriceList = new ArrayList<>();
        createQueryStrings();
        openDB();

        listOfProducts.getFavShopName().addAll(databaseAccess.getData(sqlRuleFavShopName));
        listOfProducts.getFavURL().addAll(databaseAccess.getData(sqlRuleFavURL));
        listOfProducts.getTitleList().addAll(databaseAccess.getData(sqlRuleFavTitleOfItem)) ;
        auxPriceList.addAll(databaseAccess.getData(sqlRuleFavPriceOfItem)) ;
        listOfProducts.getImageList().addAll(databaseAccess.getData(sqlRuleFavImageOfItem)) ;

        for(int i=0;i<auxPriceList.size();i++) {
            listOfProducts.getPriceList().add("Preț: " + auxPriceList.get(i).substring(auxPriceList.get(i).lastIndexOf('-') + 1) + " LEI");
            Log.d("printDB",listOfProducts.getFavShopName().get(i)+" "+listOfProducts.getFavURL().get(i)+" "+listOfProducts.getTitleList().get(i)+" "+auxPriceList.get(i));

        }
        closeDB();
    }

    private void createQueryStrings(){
         sqlRuleFavShopName = UtilityLibrary.createString(DatabaseAccess.ShopNameFavField,DatabaseAccess.StockPricesTable);
         sqlRuleFavURL = UtilityLibrary.createString(DatabaseAccess.LinkToTheProductField,DatabaseAccess.StockPricesTable);
         sqlRuleFavTitleOfItem = UtilityLibrary.createString(DatabaseAccess.TitleOfTheProductField,DatabaseAccess.StockPricesTable);
         sqlRuleFavPriceOfItem = UtilityLibrary.createString(DatabaseAccess.PricesField,DatabaseAccess.StockPricesTable);
         sqlRuleFavImageOfItem = UtilityLibrary.createString(DatabaseAccess.ImageOfTheProductField,DatabaseAccess.StockPricesTable);

    }

    private void openDB(){
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();//Opens the database
    }

    private void closeDB(){
        databaseAccess.close();//Closes the database
    }

    public ListOfProducts getListOfProducts() {
        return listOfProducts;
    }

}
