package com.corcow.hw.flagproject.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.adapter.SendViewAdapter;
import com.corcow.hw.flagproject.manager.NetworkManager;
import com.corcow.hw.flagproject.manager.UserManager;
import com.corcow.hw.flagproject.view.libpackage.PullToRefreshView;

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

    LinearLayout selectContainer;
    ImageView uploadView;
    FileSelectDialog dialog;
    String selectedFileName = "";
    String selectedFilePath = "";

    private static final int REFRESH_DELAY = 1000;
    PullToRefreshView pullToRefreshView;
    ListView listView;
    SendViewAdapter mAdpater;

    public FlagFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_flag, container, false);

        // View initialize
        selectContainer = (LinearLayout) view.findViewById(R.id.select_container);
        uploadView = (ImageView) view.findViewById(R.id.btn_file_upload);
        pullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        listView = (ListView) view.findViewById(R.id.containerListView);
        mAdpater = new SendViewAdapter();
        listView.setAdapter(mAdpater);
        mAdpater.setSendItem("", "");

        pullToRefreshView.setVisibility(View.GONE);

        // 파일 선택 버튼
        uploadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 파일 선택 Dialog 출력
                dialog = new FileSelectDialog();
                dialog.setDialogResult(new FileSelectDialog.OnDialogResult() {
                    @Override
                    public void finish(String name, String path) {
                        selectedFileName = name;
                        selectedFilePath = path;
                        Toast.makeText(getContext(), selectedFileName, Toast.LENGTH_SHORT).show();
                        mAdpater.setSendItem(selectedFileName, selectedFilePath);
                        selectContainer.setVisibility(View.GONE);
                        pullToRefreshView.setVisibility(View.VISIBLE);
                    }
                });
                dialog.show(getActivity().getSupportFragmentManager(), "");
            }
        });


        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String userID = UserManager.getInstance().getUserID();
                if (!TextUtils.isEmpty(userID)) {
                    NetworkManager.getInstance().fileUpload(getContext(), selectedFileName, selectedFilePath, "TESTTTTT", false, userID, new NetworkManager.OnResultListener<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Toast.makeText(getContext(), "SUCCESS", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(int code) {
                            Toast.makeText(getContext(), "FAIL" + code, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "로그인해주세요", Toast.LENGTH_SHORT).show();
                }
                pullToRefreshView.setRefreshing(false);

            }
        });




        return view;
    }


}
