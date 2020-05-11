package Controller;

import android.content.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import DatabaseManager.DatabaseAccess;
import Utilities.UtilityLibrary;

public class ShopCategoriesListController {
    private Context context;
    private List<String> categoriesList;
    private String ShopNameAttribute;
    private DatabaseAccess databaseAccess;
    private Thread thread;

    public ShopCategoriesListController(Context context, String ShopNameAttribute) {
        this.context = context;
        this.ShopNameAttribute = ShopNameAttribute;
        categoriesList = new ArrayList<>();
    }

    public void displayCategoriesList() {

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                openBD();
                createQueryAndGetData();
                closeBD();
            }
        });
        thread.start();

        try { thread.join();
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    private void openBD(){
        databaseAccess =  DatabaseAccess.getInstance(context);
        databaseAccess.open();//Opens the database
    }

    private void closeBD(){
        databaseAccess.close();//Closes the database
    }

    private void createQueryAndGetData(){
        String sqlRulePathForProductsCategory = UtilityLibrary.createString(
                DatabaseAccess.PathForProductCategoryField,DatabaseAccess.ShopListTable,
                DatabaseAccess.ShopNameField,ShopNameAttribute);
        String sqlRuleWebsiteURL = UtilityLibrary.createString(
                DatabaseAccess.WebsiteURLField, DatabaseAccess.ShopListTable,
                DatabaseAccess.ShopNameField,ShopNameAttribute);

        String pathForProductsCategory = databaseAccess.getData(sqlRulePathForProductsCategory).get(0);
        String websiteURL = databaseAccess.getData(sqlRuleWebsiteURL).get(0);

        categoriesList.clear();
        try {
            categoriesList = scrapeDataFromWeb(pathForProductsCategory,websiteURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //This is where the scrape magic happens using JSoup
    private ArrayList<String> scrapeDataFromWeb(String pathForProductsCategory, String websiteURL) throws IOException {
        ArrayList <String> CategoriesResults = new ArrayList<>();
        Document doc = Jsoup.connect(websiteURL).get();//Connect to the website
        Elements links = doc.select(pathForProductsCategory);//Select the information from the website

        for (Element link : links)
            if (!link.text().matches("Servicii"))//For PCGarage to avoid Servicii category
                CategoriesResults.add(link.text());
        return CategoriesResults;
    }

    public void stopThread(){
     thread.interrupt();
    }

    public List<String> getCategoriesList() {
        return categoriesList;
    }
}
