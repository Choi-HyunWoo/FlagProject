package com.corcow.hw.flagproject.view;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.manager.NetworkManager;
import com.corcow.hw.flagproject.manager.UserManager;
import com.corcow.hw.flagproject.model.UserFileChild;
import com.corcow.hw.flagproject.model.UserFileParent;
import com.corcow.hw.flagproject.model.json.FileInfo;

/**
 * Created by multimedia on 2016-05-26.
 */
public class UserFileChildView extends FrameLayout {

    String _id;
    String isPublic;
    String logedInID;
    UserFileParent parent;
    boolean isMyPage;

    LinearLayout downloadButton, copyButton, publicButton, deleteButton;
    ImageView publicImage;

    public UserFileChildView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.user_file_child_view, this);
        logedInID = UserManager.getInstance().getUserID();

        publicImage = (ImageView) findViewById(R.id.btn_public);

        // 다운로드
        // Private, Public
        downloadButton = (LinearLayout) findViewById(R.id.container_download);
        downloadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // download
                // 다운로드 다이얼로그 출력
                Toast.makeText(getContext(), parent.pageOwner+" 님의 "+parent.flagName+" 파일 다운로드", Toast.LENGTH_SHORT).show();
                final String pageOwnerID = parent.pageOwner;
                final String flagName = parent.flagName;
                NetworkManager.getInstance().fileInfo(getContext(), pageOwnerID, flagName, new NetworkManager.OnResultListener<FileInfo>() {
                    @Override
                    public void onSuccess(FileInfo result) {
                        mListener.onDownloadBtnClick(pageOwnerID, flagName);
                    }

                    @Override
                    public void onFail(int code) {
                        Toast.makeText(getContext(), "없는 사용자이거나 FLAG명이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // URL 복사
        // Private, Public
        copyButton = (LinearLayout) findViewById(R.id.container_copy);
        copyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // copy
                // 클립보드 복사
                Toast.makeText(getContext(), "http://fflag.me/" + parent.pageOwner + "/" + parent.flagName + " URL이 복사되었습니다.\n" +
                        "친구에게 URL로 파일을 공유하세요!", Toast.LENGTH_SHORT).show();
                mListener.onCopyBtnClick("http://fflag.me/" + parent.pageOwner + "/" + parent.flagName);
            }
        });

        // 공개범위 설정
        // only Private
        publicButton = (LinearLayout) findViewById(R.id.container_public);
        publicButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // public
                // Toast로 변경상태 알림
                NetworkManager.getInstance().filePrivate(getContext(), _id, new NetworkManager.OnResultListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // 공개 상태일 때 눌렸다면,
                        if (isPublic.equals("public")) {
                            Toast.makeText(getContext(), parent.flagName + " 파일을 공개하지 않습니다.", Toast.LENGTH_SHORT).show();
                            isPublic = "private";
                            mListener.onPublicBtnClick(isPublic, parent.position);

                        } else {
                            Toast.makeText(getContext(), parent.flagName + " 파일을 공개합니다.", Toast.LENGTH_SHORT).show();
                            isPublic = "public";
                            mListener.onPublicBtnClick(isPublic, parent.position);
                        }
                    }

                    @Override
                    public void onFail(int code) {
                        Toast.makeText(getContext(), "공개범위 변경 실패:" + code, Toast.LENGTH_SHORT).show();
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
                mListener.onDeleteBtnClick(parent.flagName, _id, parent.position);
            }
        });
        if (!isMyPage) {
            deleteButton.setVisibility(GONE);
            publicButton.setVisibility(GONE);
        } else {
            deleteButton.setVisibility(VISIBLE);
            publicButton.setVisibility(VISIBLE);
        }
    }


    public void setChildItem(UserFileChild child, UserFileParent parent) {
        isPublic = child.isPublic;
        this.parent = parent;
        this._id = parent._id;
        if (isPublic.equals("public")) {
            publicImage.setImageResource(R.drawable.icon_public);
        } else {
            publicImage.setImageResource(R.drawable.icon_private);
        }
        if (!isMyPage) {
            deleteButton.setVisibility(GONE);
            publicButton.setVisibility(GONE);
        } else {
            deleteButton.setVisibility(VISIBLE);
            publicButton.setVisibility(VISIBLE);
        }
    }

    public void setIsMyPage(boolean isMyPage) {
        this.isMyPage = isMyPage;
    }

    public interface OnChildBtnClickListener {
        public void onDownloadBtnClick(String pageOwnerID, String flagName);
        public void onCopyBtnClick(String copyUrl);
        public void onPublicBtnClick(String isPublic, int position);
        public void onDeleteBtnClick(String flagName, String _id, int position);
        // 다른 Button click methods 추가 가능
    }
    OnChildBtnClickListener mListener;
    public void setOnChildBtnClickListener(OnChildBtnClickListener listener) {
        mListener = listener;
    }

}
