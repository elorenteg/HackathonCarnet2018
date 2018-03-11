package com.bvisible.carnet;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bvisible.carnet.controllers.NearSitesController;
import com.bvisible.carnet.models.BikeLane;
import com.bvisible.carnet.models.RouteTime;
import com.bvisible.carnet.models.StopNextRoutes;
import com.bvisible.carnet.utils.Constants;
import com.bvisible.carnet.utils.DateUtils;
import com.bvisible.carnet.utils.Point;
import com.bvisible.carnet.utils.PointUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PublicTransportFragment extends Fragment implements AsyncResponse {
    public static final String TAG = PublicTransportFragment.class.getSimpleName();

    private View rootView;
    private Button buttonPalau;
    private Button buttonIlla;
    private LinearLayout linearLayout;

    private AsyncResponse asyncResponse;

    public static PublicTransportFragment newInstance() {
        return new PublicTransportFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.third_fragment, container, false);

        setUpElements();
        setUpListeners();

        asyncResponse = this;

        return rootView;
    }

    public void updateInfo(double lat, double lng){
        ArrayList<StopNextRoutes> nextRoutes = NearSitesController.getInstance().getAsyncTaskTP().getNextRoutes(lat, lng);

        if(((LinearLayout) linearLayout).getChildCount() > 0)
            ((LinearLayout) linearLayout).removeAllViews();

        for (StopNextRoutes stopNextRoutes : nextRoutes) {
            String text = stopNextRoutes.getStopname() + " - " + String.format("%.2f", stopNextRoutes.getDistance()) + " km";

            TextView textView = new TextView(getContext());
            linearLayout.addView(textView);
            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            for (RouteTime routeTime : stopNextRoutes.getRoutes()) {
                Calendar now = Calendar.getInstance();
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minute = now.get(Calendar.MINUTE);
                Date dateNow = DateUtils.parseDate(hour + ":" + minute);

                long different = dateNow.getTime() - routeTime.getDate().getTime();
                int elapsedHours = (int) different / (1000 * 60 * 60);
                if (elapsedHours >= 0 && elapsedHours < 1 && dateNow.before(routeTime.getDate())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String text2 = "  " + routeTime.getName() + " " + sdf.format(routeTime.getDate());
                    TextView textView2 = new TextView(getContext());
                    linearLayout.addView(textView2);
                    textView2.setText(text2);
                    textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    Log.e(TAG, dateNow.toString() + "   ///    " + sdf.format(routeTime.getDate()));
                }
            }
        }
    }

    private void setUpElements() {
        buttonPalau = (Button) rootView.findViewById(R.id.third_fragment_palau);
        buttonIlla = (Button) rootView.findViewById(R.id.third_fragment_illa);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.third_fragment_linearlayout);
    }

    private void setUpListeners() {
        buttonPalau.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NearSitesController.getInstance().queryTPGraph(Constants.LAT_PALAU, Constants.LNG_PALAU, asyncResponse);
            }
        });
        buttonIlla.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NearSitesController.getInstance().queryTPGraph(Constants.LAT_ILLA, Constants.LNG_ILLA, asyncResponse);
            }
        });
    }

    @Override
    public void processFinish(String typeAsync) {
        boolean update = false;
        double lat = -1;
        double lng = -1;
        if (typeAsync.equals("STOPS")) {
            update = true;
            lat = NearSitesController.getInstance().getLatitude();
            lng = NearSitesController.getInstance().getLongitude();
        }
        Log.e(TAG, typeAsync);

        if (update) {
            updateInfo(lat, lng);
        }
    }
}
