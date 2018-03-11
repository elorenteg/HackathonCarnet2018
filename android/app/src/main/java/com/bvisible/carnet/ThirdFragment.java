package com.bvisible.carnet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bvisible.carnet.controllers.NearSitesController;
import com.bvisible.carnet.utils.Constants;

public class ThirdFragment extends Fragment {
    public static final String TAG = ThirdFragment.class.getSimpleName();

    private View rootView;
    private Button buttonPalau;
    private Button buttonIlla;
    private TextView textView;

    private NearSitesController nearSitesController = null;

    public static ThirdFragment newInstance() {
        return new ThirdFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.third_fragment, container, false);

        setUpElements();
        setUpListeners();

        return rootView;
    }

    public void updateInfo(String text){
        textView.setText(text);
    }

    private void setUpElements() {
        buttonPalau = (Button) rootView.findViewById(R.id.third_fragment_palau);
        buttonIlla = (Button) rootView.findViewById(R.id.third_fragment_illa);
        textView = (TextView) rootView.findViewById(R.id.third_fragment_text);
    }

    private void setUpListeners() {
        buttonPalau.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NearSitesController.getInstance().queryTPGraph(Constants.LAT_PALAU, Constants.LNG_PALAU);
            }
        });
        buttonIlla.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NearSitesController.getInstance().queryTPGraph(Constants.LAT_ILLA, Constants.LNG_ILLA);
            }
        });
    }

    public void setSparkseeController(NearSitesController nearSitesController) {
        this.nearSitesController = nearSitesController;
    }
}
