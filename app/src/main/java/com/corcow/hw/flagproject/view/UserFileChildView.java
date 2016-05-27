package com.corcow.hw.flagproject.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.manager.NetworkManager;
import com.corcow.hw.flagproject.manager.UserManager;
import com.corcow.hw.flagproject.model.UserFileChild;
import com.corcow.hw.flagproject.model.UserFileParent;

/**
 * Created by multimedia on 2016-05-26.
 */
public class UserFileChildView extends FrameLayout {

    String _id;
    String isPublic;
    String logedInID;
    UserFileParent parent;

    LinearLayout downloadButton, copyButton, publicButton, deleteButton;

    public UserFileChildView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.user_file_child_view, this);
        logedInID = UserManager.getInstance().getUserID();

        // 다운로드
        // Private, Public
        downloadButton = (LinearLayout) findViewById(R.id.container_download);
        downloadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // download
                // 다운로드 다이얼로그 출력
                Toast.makeText(getContext(), parent.pageOwner+"의 "+parent.flagName+"플래그 파일 다운로드", Toast.LENGTH_SHORT).show();
            }
        });

        // URL 복사
        // Private, Public
        copyButton = (LinearLayout) findViewById(R.id.container_copy);
        copyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // copy
                Toast.makeText(getContext(), "http://fflag.me/"+parent.pageOwner+"/"+parent.flagName, Toast.LENGTH_SHORT).show();
            }
        });

        // 공개범위 설정
        // only Private
        publicButton = (LinearLayout) findViewById(R.id.container_public);
        publicButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // public
                Toast.makeText(getContext(), _id, Toast.LENGTH_SHORT).show();

                NetworkManager.getInstance().filePrivate(getContext(), _id, new NetworkManager.OnResultListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(getContext(), ""+result, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(int code) {
                        Toast.makeText(getContext(), "공개범위 변경 실패:"+code, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        // 삭제
        // only Private
        deleteButton = (LinearLayout) findViewById(R.id.container_delete);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete
                Toast.makeText(getContext(), _id, Toast.LENGTH_SHORT).show();

                NetworkManager.getInstance().fileDelete(getContext(), _id, new NetworkManager.OnResultListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(getContext(), ""+result, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(int code) {
                        Toast.makeText(getContext(), "파일 삭제 실패:"+code, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }


    public void setChildItem(UserFileChild child, UserFileParent parent) {
        isPublic = child.isPublic;
        this.parent = parent;
        this._id = parent._id;
    }


}
