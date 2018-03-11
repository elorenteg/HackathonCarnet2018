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

public class SecondaryFragment extends Fragment {
    public static final String TAG = SecondaryFragment.class.getSimpleName();
    private View rootView;
    private Button buttonPalau;
    private Button buttonIlla;
    private TextView textView;

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

    public void updateInfo(String text){
        textView.setText(text);
    }

    private void setUpElements() {
        buttonPalau = (Button) rootView.findViewById(R.id.secondary_fragment_palau);
        buttonIlla = (Button) rootView.findViewById(R.id.secondary_fragment_illa);
        textView = (TextView) rootView.findViewById(R.id.secondary_fragment_text);
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
