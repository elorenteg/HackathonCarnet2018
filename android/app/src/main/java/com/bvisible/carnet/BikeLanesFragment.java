package com.bvisible.carnet;

import android.location.Location;
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
import com.bvisible.carnet.utils.Constants;
import com.bvisible.carnet.utils.Point;
import com.bvisible.carnet.utils.PointUtils;

import java.util.ArrayList;

public class BikeLanesFragment extends Fragment implements AsyncResponse {
    public static final String TAG = BikeLanesFragment.class.getSimpleName();
    private View rootView;
    private Button buttonPalau;
    private Button buttonIlla;
    private Button buttonCurrent;
    private LinearLayout linearLayout;

    private AsyncResponse asyncResponse;

    public static BikeLanesFragment newInstance() {
        return new BikeLanesFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.secondary_fragment, container, false);

        setUpElements();
        setUpListeners();

        asyncResponse = this;

        return rootView;
    }

    public void updateInfo(double lat, double lng) {
        ArrayList<BikeLane> bikelanes = NearSitesController.getInstance().getAsyncTaskBikes().getBikes();
        Point p = new Point(lat, lng);

        if (linearLayout.getChildCount() > 0)
            linearLayout.removeAllViews();

        ArrayList<String> lanes = new ArrayList<>();
        for (BikeLane bikelane : bikelanes) {
            if (!lanes.contains(bikelane.getName())) {
                Point pA = new Point(bikelane.getLat1(), bikelane.getLng1());
                Point pB = new Point(bikelane.getLat2(), bikelane.getLng2());
                double distance = PointUtils.pointToLineDistance(pA, pB, p);
                if (distance <= 0.5) {
                    lanes.add(bikelane.getName());
                    String text = bikelane.getName() + " - " + String.format("%.2f", distance) + " km";
                    TextView textView = new TextView(getContext());
                    linearLayout.addView(textView);
                    textView.setText(text);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                }
            }
        }
    }

    private void setUpElements() {
        buttonCurrent = rootView.findViewById(R.id.secondary_fragment_current);
        buttonPalau = rootView.findViewById(R.id.secondary_fragment_palau);
        buttonIlla = rootView.findViewById(R.id.secondary_fragment_illa);
        linearLayout = rootView.findViewById(R.id.secondary_fragment_linearlayout);
    }

    private void setUpListeners() {
        buttonPalau.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NearSitesController.getInstance().queryBikesGraph(Constants.LAT_PALAU, Constants.LNG_PALAU, asyncResponse);
            }
        });
        buttonIlla.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NearSitesController.getInstance().queryBikesGraph(Constants.LAT_ILLA, Constants.LNG_ILLA, asyncResponse);
            }
        });
        buttonCurrent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (((MainActivity) getActivity()).existsLastLocation()) {
                    Location location = ((MainActivity) getActivity()).getLastLocation();
                    NearSitesController.getInstance().queryBikesGraph(location.getLatitude(), location.getLongitude(), asyncResponse);
                }
            }
        });
    }

    @Override
    public void processFinish(String typeAsync) {
        boolean update = false;
        double lat = -1;
        double lng = -1;
        if (typeAsync.equals("BIKES")) {
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
