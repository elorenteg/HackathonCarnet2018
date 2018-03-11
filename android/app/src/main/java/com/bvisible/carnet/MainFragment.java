package com.bvisible.carnet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {
    public static final String TAG = MainFragment.class.getSimpleName();
    private View rootView;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_fragment, container, false);

        setUpElements();
        setUpListeners();

        //TextToSpeechController.getInstance(getContext()).speak("Hola", TextToSpeech.QUEUE_FLUSH);

        return rootView;
    }

    private void setUpElements() {
    }

    private void setUpListeners() {

    }

    public void newVoiceMessage(String message) {
        Log.e(TAG, message);
    }
}
