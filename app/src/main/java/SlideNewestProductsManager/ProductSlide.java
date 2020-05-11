package SlideNewestProductsManager;

public class ProductSlide {

    private ProductElement[] products;
    private int counter = 0;
    private int oldest = -1;

    public ProductSlide(int size){
        products=new ProductElement[size];
    }

    public void addProduct(ProductElement element){
        //if is the first element
        if(counter < 7)
        {
            products[counter] = element;
            counter ++;
            return;
        }

        oldest = 0;

        for(int i=1; i<products.length; i++)
        {
            if(products[oldest].isNewer(products[i])) oldest = i;
        }

        if(!products[oldest].isNewer(element)) products[oldest] = element;
    }

    public ProductElement[] getProducts(){
        return products;
    }

    public int getCounter(){
        return counter;
    }

    public ProductElement[] sortProducts(){
        ProductElement aux;

        for(int i=0;i<products.length/2;i++)
            for(int j=i+1;j<products.length;j++){

                if(!products[i].isNewer(products[j])) {
                    aux = products[i];
                    products[i]=products[j];
                    products[j]=aux;
                }
        }
        return products;
    }
}
