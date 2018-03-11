package com.bvisible.carnet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SecondaryFragment extends Fragment {
    private View rootView;
    private Button button;
    private Button button2;

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

    private void setUpElements() {
        button = (Button) rootView.findViewById(R.id.button);
        button2 = (Button) rootView.findViewById(R.id.button2);
    }

    private void setUpListeners() {

    }
}
