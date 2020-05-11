package Controller;

import android.content.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import DatabaseManager.DatabaseAccess;
import Utilities.UtilityLibrary;

public class ShopSubCategoryController {
    private Context context;
    private String ShopNameAttribute;
    private String PositionOfCategory;
    private ArrayList <String> subcategoriesList;
    private ArrayList <String> linkToPass;
    private Thread thread;
    private DatabaseAccess databaseAccess;

    public ShopSubCategoryController(Context context, String ShopNameAttribute, String PositionOfCategory) {
        this.context = context;
        this.ShopNameAttribute = ShopNameAttribute;
        this.PositionOfCategory = PositionOfCategory;
        subcategoriesList = new ArrayList<>();
        linkToPass = new ArrayList<>();
    }

    public void manageSubCategoriesList() {
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                openBD();
                //Create the query strings for getting the information from database latter
                String sqlRuleID = UtilityLibrary.createString(
                        DatabaseAccess.IDField,DatabaseAccess.ShopListTable,
                        DatabaseAccess.ShopNameField,ShopNameAttribute);
                String sqlRuleWebsiteURL = UtilityLibrary.createString(
                        DatabaseAccess.WebsiteURLField,DatabaseAccess.ShopListTable,
                        DatabaseAccess.ShopNameField,ShopNameAttribute);
                String sqlRulePathForProductsSubCategory = UtilityLibrary.createString(
                        DatabaseAccess.PathForProductSubcategoryField,DatabaseAccess.ShopListTable,
                        DatabaseAccess.ShopNameField,ShopNameAttribute);
                String sqlRulePathForProductsCategory = UtilityLibrary.createString(
                        DatabaseAccess.PathForProductCategoryField,DatabaseAccess.ShopListTable,
                        DatabaseAccess.ShopNameField,ShopNameAttribute);

                //Get information from database
                String pathForProductsSubCategory = databaseAccess.getData(sqlRulePathForProductsSubCategory).get(0);
                String pathForProductsCategory = databaseAccess.getData(sqlRulePathForProductsCategory).get(0);
                String websiteURL = databaseAccess.getData(sqlRuleWebsiteURL).get(0);
                int ID = Integer.parseInt(databaseAccess.getData(sqlRuleID).get(0));

                deleteLists();
                //Try to scrape information by calling the scrapeDataFromWeb() method
                try {
                    scrapeDataFromWeb(websiteURL,pathForProductsCategory,pathForProductsSubCategory,ID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                closeBD();
            }
        });
        thread.start();
    }

    private void openBD(){
        databaseAccess =  DatabaseAccess.getInstance(context);
        databaseAccess.open();//opens the database
    }

    private void closeBD(){
        databaseAccess.close();//closes the database
    }

    //This is where the scrape magic happens using JSoup
    private void scrapeDataFromWeb(String websiteURL,String pathForProductsCategory,String pathForProductsSubCategory,int ID) throws IOException {
        Document doc = Jsoup.connect(websiteURL).get();//Connect to the website
        String linkAtr="abs:href";

        if (ID==2) {//Only for MediaGalaxy, I need to go in depth for sub-subcategories
            Element elementSubcategoryWithPosition = doc.select(pathForProductsCategory).get(Integer.parseInt(PositionOfCategory));
            String newURL = elementSubcategoryWithPosition.attr(linkAtr);

            Document doc1 = Jsoup.connect(newURL).get();//Connect to the website (sub-subcategory one)
            Elements elementSubcategory2 = doc1.select(pathForProductsSubCategory);

            //I go in depth for subcategories at MediaGalaxy to avoid multiple activities for iterative subcategories
            for (Element link : elementSubcategory2){
                Document doc2 = Jsoup.connect(link.attr(linkAtr)).get();
                Elements elementSubcategory3 = doc2.select(pathForProductsSubCategory);

                //If I find that a subcategory has another one, it skips the text from the first subcategory and returns the only the depth subcategory text
                if(!elementSubcategory3.isEmpty()){
                    for(Element link1: elementSubcategory3){
                        subcategoriesList.add(link1.text());
                        linkToPass.add((link1.attr(linkAtr)));
                    }
                }
                else {
                    subcategoriesList.add(link.text());
                    linkToPass.add((link.attr(linkAtr)));
                }
            }
        } else {//For the rest of shops
            Element elementSubcategoryWithPosition = doc.select(pathForProductsSubCategory).get(Integer.parseInt(PositionOfCategory));
            int i = 0;
            while (true) {//For PCGarage
                try {
                    subcategoriesList.add(elementSubcategoryWithPosition.child(i).text()); //Get every child element of class while error
                    linkToPass.add(elementSubcategoryWithPosition.child(i).select("a").attr(linkAtr));//for PCGarage links
                } catch (IndexOutOfBoundsException error) {
                    break;
                }
                i++;
            }
            if (subcategoriesList.isEmpty())//Only for Emag. It has some categories without sub ones
            {
                //Go back to the parent for the original category text and for the link
                subcategoriesList.add(elementSubcategoryWithPosition.parent().select("a").text());
                linkToPass.add(elementSubcategoryWithPosition.parent().select("a").attr(linkAtr));
            }
        }
    }

    private void deleteLists(){
        subcategoriesList.clear();
        linkToPass.clear();
    }

    public ArrayList<String> getSubcategoriesList() {
        return subcategoriesList;
    }

    public ArrayList<String> getLinkToPass() {
        return linkToPass;
    }

    public void stopThread(){
        thread.interrupt();
    }

    public void joinThread(){
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
