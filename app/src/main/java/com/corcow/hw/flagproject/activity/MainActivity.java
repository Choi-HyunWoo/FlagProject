package com.corcow.hw.flagproject.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

import com.corcow.hw.flagproject.fragment.FileManagerFragment;
import com.corcow.hw.flagproject.fragment.FlagFragment;
import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.fragment.SettingsFragment;
import com.corcow.hw.flagproject.adapter.TabsAdapter;


public class MainActivity extends AppCompatActivity {

    // Pager tab define
    private static final String TAB_TAG = "currentTab";
    private static final String TAB_ID_FLAG = "tab_flag";
    private static final String TAB_ID_FILEMNG = "tab_filemng";
    private static final String TAB_ID_SETTINGS = "tab_settings";

    // Intent Extra define
    public static final String EXTRA_KEY_WHOS_PAGE = "whosPage";
    public static final String EXTRA_VALUE_MYPAGE = "myPage";

    TabHost tabHost;
    ViewPager pager;
    TabsAdapter mAdapter;

    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        // Fragment Build
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new FileManagerFragment()).commit();
        }
        */

        // fab button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserPageActivity.class);
                intent.putExtra(EXTRA_KEY_WHOS_PAGE, EXTRA_VALUE_MYPAGE);
                startActivity(intent);
            }
        });

        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        pager = (ViewPager)findViewById(R.id.pager);
        mAdapter = new TabsAdapter(this, getSupportFragmentManager(), tabHost, pager);

        mAdapter.addTab(tabHost.newTabSpec(TAB_ID_FLAG).setIndicator("",getResources().getDrawable(R.drawable.icon_tap_flag)), FlagFragment.class, null);
        mAdapter.addTab(tabHost.newTabSpec(TAB_ID_FILEMNG).setIndicator("",getResources().getDrawable(R.drawable.icon_tap_fm)), FileManagerFragment.class, null);
        mAdapter.addTab(tabHost.newTabSpec(TAB_ID_SETTINGS).setIndicator("", getResources().getDrawable(R.drawable.icon_tap_options)), SettingsFragment.class, null);
        setTabColor(tabHost);

        mAdapter.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                setTabColor(tabHost);
                if (tabId.equals(TAB_ID_FLAG)) {
                    //
                } else if (tabId.equals(TAB_ID_FILEMNG)) {
                    // actionBar.setTitle("FileManager에 맞는 Actionbar로");
                } else {
                    // actionBar.setTitle("설정");
                }
            }
        });

    }

    public void setTabColor(TabHost tabhost) {
        for(int i=0;i<tabhost.getTabWidget().getChildCount();i++) {
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.colorDark)); //unselected
        }
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(getResources().getColor(R.color.colorAccent)); // selected
    }



    // Back Pressed 처리 in Fragment
    public interface OnBackPressedListener {
        public void onBackPressed();
    }
    OnBackPressedListener mOnBackPressedListener;

    public void setOnBackPressedListener (OnBackPressedListener listener) {
        mOnBackPressedListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (mOnBackPressedListener != null) {
            mOnBackPressedListener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}