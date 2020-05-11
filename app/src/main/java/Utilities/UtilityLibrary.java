package Utilities;

import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class UtilityLibrary {

    private UtilityLibrary() {
    }

    //Will create query for the select operation
    public static String createString(String selectedItem, String tableName) {
        return "select " + selectedItem + " from " + tableName;
    }

    public static String createString(String selectedItem, String tableName, String conditionAntet, String conditionBody) {
        return "select " + selectedItem + " from " + tableName + " where " + conditionAntet + " = '" + conditionBody + "'";
    }

    //The method will take only the price (number) from the format: Price: 1.000,99 Lei
    public static String transformPriceExtractNumber(String price) {
        String newPrice = "";

        //I verify every character to be a point or a comma or a digit in order to create the price (the number)
        for (int i = 0; i < price.length(); i++) {
            if (price.charAt(i) == ',' || price.charAt(i) == '.' || Character.isDigit(price.charAt(i)))
                newPrice = newPrice + price.charAt(i);
        }
        return newPrice;
    }

    //The method will take only the price (number) from the format: Price: 1000.99 Lei for chart Display
    public static String transformPriceExtractNumberChart(String price) {
        String newPrice = "";

        //I verify every character to be a point or a comma or a digit in order to create the price (the number)
        for (int i = 0; i < price.length(); i++) {
            if (Character.isDigit(price.charAt(i)))
                newPrice = newPrice + price.charAt(i);
            if (price.charAt(i) == ',')
                newPrice = newPrice + '.';
        }
        return newPrice;
    }

    //Only for Emag, to add comma to the price
    public static String addCommaToPrice(String price) {
        StringBuilder p = new StringBuilder();
        int index = 0;
        for (int i = price.length() - 1; i >= 0; i--) { //Work form end to start to place the comma after the second digit
            if (Character.isDigit(price.charAt(i))) {
                if (index == 2) p.append(","); //Added the necessary comma
                index++;
            }
            p.append(price.charAt(i));
        }
        price = p.reverse().toString();
        return price;
    }

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YY");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getCurrentHour(){
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        return hour+"";
    }

    public static boolean verifyConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (!(nInfo != null && nInfo.isAvailable() && nInfo.isConnected())) {
            Toast.makeText(context, "Conecteaza telefonul la internet.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean searchVerification(String textToVerifyWith, String query) {
        String[] textArray = query.split(" ");
        for (String a : textArray) {
            if (!textToVerifyWith.contains(a))
                return false;
        }
        return true;
    }

    public static String getTypeOfRingerMode(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                return "SILENT";
            case AudioManager.RINGER_MODE_VIBRATE:
                return "VIBRATE";
            case AudioManager.RINGER_MODE_NORMAL:
                return "NORMAL";
        }
        return "NULL";
    }
}
