package com.corcow.hw.flagproject.activity.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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

    ImageView uploadView;

    public FlagFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_flag, container, false);

        uploadView = (ImageView) view.findViewById(R.id.btn_file_upload);
        uploadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 파일 선택 Dialog 출력
                FileSelectDialog dlg = new FileSelectDialog();
                dlg.show(getActivity().getSupportFragmentManager(), "");
            }
        });


        return view;
    }


}
