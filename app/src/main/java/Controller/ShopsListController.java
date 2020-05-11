package Controller;

import android.content.Context;

import com.example.monitorapp_v1.R;

import java.util.ArrayList;
import java.util.List;

import DatabaseManager.DatabaseAccess;
import Utilities.UtilityLibrary;

public class ShopsListController {
    private Context context;
    private List <String> shopsList;
    private List<Integer> logoList;
    private  DatabaseAccess databaseAccess;

    public ShopsListController(Context context) {
        this.context = context;
        shopsList = new ArrayList<>();
        logoList = new ArrayList<>();
    }

    public void setLogoList(){
        logoList.add(R.drawable.logo_emag);
        logoList.add(R.drawable.logo_media_galaxy_2);
        logoList.add(R.drawable.logo_pc_garage);
    }

    private void openDB(){
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();//open database
    }
    private void closeDB(){
        databaseAccess.close();//close database
    }

    private void getDBData(){
        String sqlRule = UtilityLibrary.createString(DatabaseAccess.ShopNameField, DatabaseAccess.ShopListTable);
        ArrayList<String> info = databaseAccess.getData(sqlRule);

        shopsList.clear(); //Delete array content every time before use
        shopsList.addAll(info);
    }

    public void readShopsListThread() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                openDB();
                getDBData();
                closeDB();
            }
        });
        thread.start();

        try { thread.join(); }
        catch (InterruptedException e) { e.printStackTrace();}
    }

    public List<String> getShopsList() {
        return shopsList;
    }

    public List<Integer> getLogoList() {
        return logoList;
    }
}
