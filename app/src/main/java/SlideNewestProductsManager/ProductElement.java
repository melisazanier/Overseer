package SlideNewestProductsManager;

public class ProductElement {

    private String shopName;
    private String image;
    private String title;
    private String url;
    private int day;
    private int month;
    private int year;
    private int hour;

    public ProductElement(String shopName,String image, String title, String url, String date, String hour){
        this.shopName=shopName;
        this.image=image;
        this.title=title;
        this.url=url;
        processDate(date,hour);
    }

    private void processDate(String date,String hour){
        String lastDate=date.substring(date.length() - 8);
        String[]dateList=lastDate.split("/");

        this.hour=Integer.parseInt(hour);

        if(dateList[0].charAt(0)=='0') this.day=Integer.parseInt(dateList[0].charAt(1) +"");
        else this.day=Integer.parseInt(dateList[0]);

        if(dateList[1].charAt(0)=='0') this.month=Integer.parseInt(dateList[1].charAt(1) +"");
        else this.month=Integer.parseInt(dateList[1]);

        this.year=Integer.parseInt(dateList[2]);
    }

    public boolean isNewer(ProductElement element)
    {
        if(this.year > element.year) return true;
        else if(this.year < element.year) return false;

        if(this.month > element.month) return true;
        else if(this.month < element.month) return false;

        if(this.day > element.day) return true;
        else if(this.day < element.day) return false;

        if(this.hour > element.hour) return true;
        else if(this.hour < element.hour) return false;

        return true;
    }

    public String getTitle(){
        return title;
    }

    public String getImage(){
        return image;
    }

    public String getShopName(){
        return shopName;
    }

    public String getUrl(){
        return url;
    }
}
