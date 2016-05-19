package com.corcow.hw.flagproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.corcow.hw.flagproject.R;

public class UserPageActivity extends AppCompatActivity {

    TextView ownerNameView;
    String pageOwner;

    ListView listView;


    /** TODO 160517
     *
     * 1. 리스트뷰는 Expandable , PullToRefresh 로 구현
     * 2. Expand child item에 copy 버튼 및 public/private 설정 가능하도록...
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
