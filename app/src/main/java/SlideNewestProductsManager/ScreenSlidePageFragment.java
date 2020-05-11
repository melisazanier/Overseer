package SlideNewestProductsManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.monitorapp_v1.MainActivity;
import com.example.monitorapp_v1.R;
import com.example.monitorapp_v1.ShopIndividualProductDisplay;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import Utilities.UtilityLibrary;

import static android.content.Context.VIBRATOR_SERVICE;

public class ScreenSlidePageFragment extends Fragment {
    private Vibrator vibrator;
    private String url,shopName,image;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);

        vibrator = (Vibrator) container.getContext().getSystemService(VIBRATOR_SERVICE);


        TextView textView = rootView.findViewById(R.id.pagerText);
        String title = getArguments().getString("title");
        image = getArguments().getString("image");
        url = getArguments().getString("url");
        shopName = getArguments().getString("shopName");

        textView.setText(title);
        ImageView imageView = rootView.findViewById(R.id.pagerImage);
        Picasso.with(getContext()).load(image).into(imageView);

        final Button sliderBtn=rootView.findViewById(R.id.sliderBtn);
        sliderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Objects.requireNonNull(UtilityLibrary.getTypeOfRingerMode(container.getContext())).equals("SILENT"))
                    vibrator.vibrate(50);
                MainActivity.setRefreshState(false);
                Intent intent = new Intent(container.getContext(), ShopIndividualProductDisplay.class);
                intent.putExtra("ShopNameAttribute", shopName);
                intent.putExtra("LinkOfIndividualProduct", url);
                intent.putExtra("ImageOfIndividualProduct", image);
                startActivity(intent);
            }
        });
        return rootView;
    }

}
