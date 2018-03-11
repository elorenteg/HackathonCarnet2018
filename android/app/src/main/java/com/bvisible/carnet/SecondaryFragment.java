package com.bvisible.carnet;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class SecondaryFragment extends Fragment {
    public static final String TAG = SecondaryFragment.class.getSimpleName();
    private View rootView;
    private Button buttonPalau;
    private Button buttonIlla;
    private LinearLayout linearLayout;

    public static SecondaryFragment newInstance() {
        return new SecondaryFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.secondary_fragment, container, false);

        setUpElements();
        setUpListeners();

        return rootView;
    }

    public void updateInfo(Context context, String tt){
        ArrayList<BikeLane> bikelanes = NearSitesController.getInstance().getAsyncTaskBikes().getBikes();
        Point p = new Point(Constants.LAT_PALAU, Constants.LNG_PALAU);

        ArrayList<String> lanes = new ArrayList<>();
        for (BikeLane bikelane : bikelanes) {
            if (!lanes.contains(bikelane.getName())) {
                Point pA = new Point(bikelane.getLat1(), bikelane.getLng1());
                Point pB = new Point(bikelane.getLat2(), bikelane.getLng2());
                double distance = PointUtils.pointToLineDistance(pA, pB, p);
                if (distance <= 0.5) {
                    lanes.add(bikelane.getName());
                    String text = bikelane.getName() + " - " + String.format("%.2f", distance) + " km";
                    TextView textView = new TextView(context);
                    linearLayout.addView(textView);
                    textView.setText(text);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                }
            }
        }
    }

    private void setUpElements() {
        buttonPalau = (Button) rootView.findViewById(R.id.secondary_fragment_palau);
        buttonIlla = (Button) rootView.findViewById(R.id.secondary_fragment_illa);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.secondary_fragment_linearlayout);
    }

    private void setUpListeners() {
        buttonPalau.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NearSitesController.getInstance().queryBikesGraph(Constants.LAT_PALAU, Constants.LNG_PALAU);
            }
        });
        buttonIlla.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NearSitesController.getInstance().queryBikesGraph(Constants.LAT_ILLA, Constants.LNG_ILLA);
            }
        });
    }
}
