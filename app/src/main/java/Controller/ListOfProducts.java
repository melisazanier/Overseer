package Controller;

import java.util.ArrayList;
import java.util.List;

public class ListOfProducts {

    private List<String> imageList,titleList,priceList,favShopName,favURL;

    ListOfProducts() {
        imageList = new ArrayList<>();
        titleList = new ArrayList<>();
        priceList = new ArrayList<>();
        favShopName = new ArrayList<>();
        favURL = new ArrayList<>();
    }

    public List<String> getImageList() {
        return imageList;
    }

    public List<String> getTitleList() {
        return titleList;
    }

    public List<String> getPriceList() {
        return priceList;
    }

    public List<String> getFavShopName() {
        return favShopName;
    }

    public List<String> getFavURL() {
        return favURL;
    }


    void deleteArrayContent(){
        imageList.clear();
        titleList.clear();
        priceList.clear();
        favShopName.clear();
        favURL.clear();
    }
}
