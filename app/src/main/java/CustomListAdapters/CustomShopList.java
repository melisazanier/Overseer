package CustomListAdapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.monitorapp_v1.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomShopList extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> name;
    private final List<Integer>logo;

    public CustomShopList(Activity context,
                          List<Integer> logo, List<String> name) {
        super(context, R.layout.single_list_shop_display, name);
        this.context = context;
        this.name = name;
        this.logo = logo;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.single_list_shop_display, null, true);

        TextView txtTitle = rowView.findViewById(R.id.itemTitleShop);
        ImageView imageView = rowView.findViewById(R.id.imgLogo);

        txtTitle.setText(name.get(position));
        //imageView.setImageResource(logo.get(position));
        Picasso.with(context).load(logo.get(position)).into(imageView);
        return rowView;
    }
}
