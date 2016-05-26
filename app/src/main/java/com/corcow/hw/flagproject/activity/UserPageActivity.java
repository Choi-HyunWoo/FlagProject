package com.corcow.hw.flagproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.adapter.UserFileListAdapter;
import com.corcow.hw.flagproject.manager.NetworkManager;
import com.corcow.hw.flagproject.manager.UserManager;
import com.corcow.hw.flagproject.model.json.UserFile;
import com.corcow.hw.flagproject.model.json.UserPage;
import com.corcow.hw.flagproject.view.libpackage.PullToRefreshView;

public class UserPageActivity extends AppCompatActivity {

    TextView ownerNameView;
    String pageOwner;

    PullToRefreshView pullToRefreshView;
    ExpandableListView listView;
    UserFileListAdapter mAdapter;

    private static final int REFRESH_DELAY = 1000;

    /** TODO 160517
     *
     * 1. 리스트뷰는 Expandable , PullToRefresh 로 구현
     * 2. Expand child item에 copy 버튼 및 public/private 설정 가능하도록...
     *
     */

    String currentUserID;
    String queryUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUserID = UserManager.getInstance().getUserID();
        if (!TextUtils.isEmpty(currentUserID)) {
            queryUserID = currentUserID;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();
        ownerNameView = (TextView)findViewById(R.id.page_owner);
        pageOwner = intent.getStringExtra(MainActivity.EXTRA_KEY_WHOS_PAGE);
        if (pageOwner.equals(MainActivity.EXTRA_VALUE_MYPAGE)) {
            ownerNameView.setText("내가 업로드한 파일");
        } else {
            ownerNameView.setText(pageOwner + " 님이 업로드한 파일");
        }

        listView = (ExpandableListView)findViewById(R.id.fileListView);
        mAdapter = new UserFileListAdapter();
        listView.setAdapter(mAdapter);
        pullToRefreshView = (PullToRefreshView)findViewById(R.id.pull_to_refresh);
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


        NetworkManager.getInstance().userFileList(this, queryUserID, new NetworkManager.OnResultListener<UserPage>() {
            @Override
            public void onSuccess(UserPage result) {
                for (UserFile userFile : result.result.file) {
                    mAdapter.add(userFile, userFile.filePrivate);
                }
/*
                for (NoticeDocs d : result.docsList) {
                    String createdDate = d.created.substring(0, d.created.indexOf("T"));
                    if (d.image_ids.size() != 0) {
                        // mAdapter.add(String createdDate, String title, String content, String imageUrl);
                        mAdapter.add(createdDate, d.title, d.content, d.image_ids.get(0).uri);
                    } else {
                        mAdapter.add(createdDate, d.title, d.content, "");
                    }
                }
*/
            }

            @Override
            public void onFail(int code) {
                Log.d("UserPageActivity ", "network error/" + code);
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
