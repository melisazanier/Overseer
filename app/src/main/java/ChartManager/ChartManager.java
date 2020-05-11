package ChartManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.monitorapp_v1.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

import Utilities.UtilityLibrary;

public class ChartManager extends Activity {
    private static  ArrayList<String> labels = new ArrayList<>();
    private static ArrayList<Entry> nodeValues = new ArrayList<>();

    public void displayChart(Context context, LineChart lineChart, String prices, String dates, String ShopNameAttribute) {
        Description description = new Description();
        description.setText("");

        labels.clear();
        nodeValues.clear();

        lineChart.setDescription(description);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.setScaleEnabled(false);
        lineChart.setDragEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        getValuesForPrice(prices);
        getValuesForDates(dates);

        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        LineDataSet lineDataSet = new LineDataSet(nodeValues, ShopNameAttribute);
        lineDataSet.setValueTextSize(10);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setColors(ColorTemplate.rgb("#FFFFFF"));
        lineDataSet.setCircleColor(ColorTemplate.rgb("#5A4C4A"));
        lineDataSet.setDrawFilled(true);

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = context.getDrawable( R.drawable.chart_fill_gradient);
            lineDataSet.setFillDrawable(drawable);
        }
        else {
            lineDataSet.setFillAlpha(20);
            lineDataSet.setFillColor(ColorTemplate.rgb("#4D744F3A"));
        }

        LineData data = new LineData(lineDataSet);

        if (nodeValues.size() >= 7) { //for more than 7 values, the chart slides to left for older values
            lineChart.setScaleMinima((float) (nodeValues.size()) / 7, 1f);
            lineChart.getXAxis().setAxisMaxValue(nodeValues.size());
            lineChart.getXAxis().setAxisMinValue(0);
            lineChart.getXAxis().setLabelCount(7);
        } else {//for lees than 7 values, the chart is displayed without zoom
            lineChart.setScaleMinima(1f, 1f);
            lineChart.getXAxis().setAxisMaxValue(nodeValues.size());
            lineChart.getXAxis().setAxisMinValue(0);
            if (nodeValues.size() == 1) {//special exception for one value (in order to display it in the middle)
                lineChart.getXAxis().setAxisMaxValue(nodeValues.size() + 1);
                lineChart.getXAxis().setLabelCount(1);
            } else
                lineChart.getXAxis().setLabelCount(nodeValues.size());
        }

        lineChart.moveViewTo(data.getEntryCount(), 7, YAxis.AxisDependency.LEFT);
        lineChart.setData(data);
        lineChart.animateXY(0, 0);
        lineChart.invalidate();
    }

    private void getValuesForPrice(String prices){
        //String dummyPrices = "1.230-1.034-1.243-956-900-456-956-978-67-456-700-1.034-1.243-956-900-456-956-978-67-456";
        String[] priceArray = prices.split("-");
        if (priceArray.length == 1) {
            nodeValues.add(new Entry(1, Float.parseFloat(UtilityLibrary.transformPriceExtractNumberChart(priceArray[0]))));
        } else {
            for (int i = 0; i < priceArray.length; i++) {
                try {
                    nodeValues.add(new Entry(i, Float.parseFloat(UtilityLibrary.transformPriceExtractNumberChart(priceArray[i]))));
                }catch (NumberFormatException exception){
                    nodeValues.add(new Entry(i, Float.parseFloat(UtilityLibrary.transformPriceExtractNumberChart(priceArray[i-1]))));
                }
            }
        }
    }

    private void getValuesForDates(String dates){
        //String dummyDate = "10/02-15/03-24/03-12/06-45/06-10/02-15/03-24/03-12/06-45/06-10/02-15/03-24/03-12/06-45/06-10/02-15/03-24/03-12/6-45/6";
        String[] datesArray = dates.split("-");

        if (nodeValues.size() == 1)
            labels.add("");
        for (int i = 0; i < datesArray.length; i++) {
            datesArray[i]=datesArray[i].substring(0,datesArray[i].length()-3);
            labels.add(datesArray[i]);
        }
    }
}
