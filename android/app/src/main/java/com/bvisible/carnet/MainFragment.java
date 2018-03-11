package com.bvisible.carnet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bvisible.carnet.controllers.NearSitesController;

public class MainFragment extends Fragment{
    public static final String TAG = MainFragment.class.getSimpleName();
    private View rootView;
    private Button button;
    private Button button2;

    private NearSitesController nearSitesController = null;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_fragment, container, false);

        setUpElements();
        setUpListeners();

        return rootView;
    }

    private void setUpElements() {
        button = (Button) rootView.findViewById(R.id.main_fragment_palau);
        button2 = (Button) rootView.findViewById(R.id.main_fragment_illa);
    }

    private void setUpListeners() {

    }

    public void setSparkseeController(NearSitesController nearSitesController) {
        this.nearSitesController = nearSitesController;
    }
}
