package com.bvisible.carnet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bvisible.carnet.controllers.NearSitesController;
import com.bvisible.carnet.utils.Constants;

public class PublicTransportFragment extends Fragment implements AsyncResponse {
    public static final String TAG = PublicTransportFragment.class.getSimpleName();

    private View rootView;
    private Button buttonPalau;
    private Button buttonIlla;
    private TextView textView;

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

    public void updateInfo(String text) {
        textView.setText(text);
    }

    private void setUpElements() {
        buttonPalau = rootView.findViewById(R.id.third_fragment_palau);
        buttonIlla = rootView.findViewById(R.id.third_fragment_illa);
        textView = rootView.findViewById(R.id.third_fragment_text);
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
        String text = "";
        if (typeAsync.equals("STOPS")) {
            update = true;
            text = NearSitesController.getInstance().getTPtext();
        }
        Log.e(TAG, typeAsync);
        Log.e(TAG, text);

        if (update) {
            updateInfo(text);
        }
    }
}
