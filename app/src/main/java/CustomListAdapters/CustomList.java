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

import java.util.List;

public class CustomList extends ArrayAdapter<String>{

    private final Activity context;
    private final List<String> image,title,price;

    public CustomList(Activity context,
                      List<String> image, List<String> title, List<String> price) {
        super(context, R.layout.list_single, title);
        this.context = context;
        this.title = title;
        this.image = image;
        this.price = price;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_single, null, true);

        TextView txtTitle = rowView.findViewById(R.id.itemTitle);
        TextView txtPrice = rowView.findViewById(R.id.price);
        ImageView imageView = rowView.findViewById(R.id.img);

        txtTitle.setText(title.get(position));
        txtPrice.setText(price.get(position));
        Picasso.with(context).load(image.get(position)).into(imageView);

        return rowView;
    }
}