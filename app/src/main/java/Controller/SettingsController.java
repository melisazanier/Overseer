package Controller;

import android.content.Context;

import DatabaseManager.DatabaseAccess;
import Utilities.UtilityLibrary;

public class SettingsController {
    private int settingsInterval;
    private int settingsType;

    private Context context;
    private String sqlRuleSettingsNotificationPrice;
    private String sqlRuleTimeInterval;
    private DatabaseAccess databaseAccess;


    public SettingsController(Context context) {
        this.context = context;
        openDB();
        createQueryToGetDataFromDB();
        settingsInterval = getSettingsIntervalFromBD();
        settingsType = getSettingsTypeFromBD();
        closeDB();
    }

    private void openDB(){
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();//Opens the database
    }

    private void createQueryToGetDataFromDB(){
        sqlRuleSettingsNotificationPrice = UtilityLibrary.createString(
                DatabaseAccess.PriceNotificationRuleOptionField,
                DatabaseAccess.AppSettingsTable);
        sqlRuleTimeInterval = UtilityLibrary.createString(
                DatabaseAccess.ServiceTimeIntervalOptionField,
                DatabaseAccess.AppSettingsTable);
    }

    private int getSettingsIntervalFromBD(){
         return Integer.parseInt(databaseAccess.getData(sqlRuleTimeInterval).get(0));
    }

    private int getSettingsTypeFromBD(){
        return Integer.parseInt(databaseAccess.getData(sqlRuleSettingsNotificationPrice).get(0));
    }

    private void closeDB(){
        databaseAccess.close();
    }

    public void callUpdateInterval(int value){
        openDB();
        databaseAccess.updateSettingsServiceTimeIntervalOption(value);
        closeDB();
    }

    public void callUpdateType(int value){
        openDB();
        databaseAccess.updateSettingsPriceNotificationOption(value);
        closeDB();
    }

    public int getSettingsInterval() {
        return settingsInterval;
    }

    public int getSettingsType() {
        return settingsType;
    }

}
