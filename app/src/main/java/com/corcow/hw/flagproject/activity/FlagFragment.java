package com.corcow.hw.flagproject.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.corcow.hw.flagproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FlagFragment extends Fragment {

    /** TODO : 160511
     *
     *  툴바추가
     *  다운로드
     *  업로드 UI (Relative)
     *
     *
     *
     */
    public FlagFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_flag, container, false);



        return view;
    }


}
