package com.corcow.hw.flagproject.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.adapter.UserFileListAdapter;
import com.corcow.hw.flagproject.fragment.DownloadDialog;
import com.corcow.hw.flagproject.manager.NetworkManager;
import com.corcow.hw.flagproject.manager.UserManager;
import com.corcow.hw.flagproject.model.json.FileInfo;
import com.corcow.hw.flagproject.model.json.UserFile;
import com.corcow.hw.flagproject.model.json.UserPageResult;
import com.yalantis.phoenix.PullToRefreshView;

public class UserPageActivity extends AppCompatActivity implements UserFileListAdapter.OnAdapterDownloadBtnClickListener {

    TextView ownerNameView;
    PullToRefreshView pullToRefreshView;
    ExpandableListView listView;
    UserFileListAdapter mAdapter;

    // Intent Extra define
    public static final String EXTRA_KEY_WHOS_PAGE = "whosPage";

    private static final int REFRESH_DELAY = 1000;

    String loggedInID;
    String pageOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loggedInID = UserManager.getInstance().getUserID();

        Intent intent = getIntent();
        ownerNameView = (TextView)findViewById(R.id.page_owner);
        pageOwner = intent.getStringExtra(EXTRA_KEY_WHOS_PAGE);
        if (isMyPage()) {
            ownerNameView.setText("내가 업로드한 파일");
        } else {
            ownerNameView.setText(pageOwner + " 님이 업로드한 파일");
        }

        listView = (ExpandableListView)findViewById(R.id.fileListView);
        mAdapter = new UserFileListAdapter();
        mAdapter.setOnAdapterBtnClickListener(this);
        listView.setAdapter(mAdapter);
        pullToRefreshView = (PullToRefreshView)findViewById(R.id.pull_to_refresh);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefreshView.setRefreshing(false);
                    }
                }, REFRESH_DELAY);
            }
        });


        NetworkManager.getInstance().userFileList(this, pageOwner, new NetworkManager.OnResultListener<UserPageResult>() {
            @Override
            public void onSuccess(UserPageResult result) {
                mAdapter.setIsMyPage(isMyPage());
                for (UserFile userFile : result.file) {
                    // 내 페이지라면,
                    if (isMyPage()) {
                        mAdapter.add(userFile, userFile.filePrivate, pageOwner);
                        mAdapter.setIsMyPage(isMyPage());
                    }
                    // 내 페이지가 아니라면,
                    else {
                        // public 인 파일들만 담는다.
                        if (userFile.filePrivate.equals("public")) {
                            mAdapter.add(userFile, userFile.filePrivate, pageOwner);
                        }
                    }
                }
                mAdapter.setIsMyPage(isMyPage());
            }
            @Override
            public void onFail(int code) {
                Log.d("UserPageActivity ", "network error/" + code);
            }
        });

    }

    private boolean isMyPage() {
        return (pageOwner.equalsIgnoreCase(loggedInID));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    // Child Item click event listeners
    @Override
    public void onAdapterDownloadBtnClick(final String pageOwnerID, final String flagName) {
        NetworkManager.getInstance().fileInfo(this, pageOwnerID, flagName, new NetworkManager.OnResultListener<FileInfo>() {
            @Override
            public void onSuccess(FileInfo result) {
                DownloadDialog dlg = DownloadDialog.newInstance(pageOwnerID, flagName, result.fileName, result.fileSize);
                dlg.show(UserPageActivity.this.getSupportFragmentManager(), "");
            }

            @Override
            public void onFail(int code) {
                Toast.makeText(UserPageActivity.this, "없는 사용자이거나 FLAG명이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private static final String LABEL_COPY_URL= "DOWNLOAD_URL";
    @Override
    public void onAdapterCopyBtnClick(String copyUrl) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(LABEL_COPY_URL, copyUrl);
        clipboard.setPrimaryClip(clip);
    }
    @Override
    public void onAdapterDeleteBtnClick(final String flagName, final String _id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserPageActivity.this);
        builder.setIcon(R.drawable.icon_warning);
        builder.setTitle("파일을 삭제하시겠습니까?");
        builder.setMessage(flagName + " 파일을 삭제하시겠습니까?\n삭제한 파일은 복구할 수 없습니다.");
        builder.setNeutralButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NetworkManager.getInstance().fileDelete(UserPageActivity.this, _id, new NetworkManager.OnResultListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(UserPageActivity.this, flagName + "파일이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFail(int code) {
                        Toast.makeText(UserPageActivity.this, "파일 삭제 실패:" + code, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
