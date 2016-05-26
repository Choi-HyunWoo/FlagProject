package com.corcow.hw.flagproject.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

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

    // Toolbar
    ImageView toolbarLogo;
    TextView toolbarTitle;
    EditText flagInputField;
    LinearLayout toolbarFlagContainer;
    LinearLayout toolbarNonFlagContainer;

    // FAB
    FloatingActionButton fab;

    // Tab Pager
    TabHost tabHost;
    ViewPager pager;
    TabsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        // Fragment Build
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.tabContainer, new FileManagerFragment()).commit();
        }
        */

        // Toolbar setting
        toolbarLogo = (ImageView)findViewById(R.id.toolbar_logo);

        toolbarFlagContainer = (LinearLayout)findViewById(R.id.toolbar_container_flag);

        flagInputField = (EditText)findViewById(R.id.toolbar_download_editText);
        toolbarLogo.setImageResource(R.drawable.icon_cloud);
        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);



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

        mAdapter.addTab(tabHost.newTabSpec(TAB_ID_FLAG).setIndicator("",getResources().getDrawable(R.drawable.icon_cloud)), FlagFragment.class, null);
        mAdapter.addTab(tabHost.newTabSpec(TAB_ID_FILEMNG).setIndicator("",getResources().getDrawable(R.drawable.icon_tap_fm)), FileManagerFragment.class, null);
        mAdapter.addTab(tabHost.newTabSpec(TAB_ID_SETTINGS).setIndicator("", getResources().getDrawable(R.drawable.icon_tap_options)), SettingsFragment.class, null);
        setTabColor(tabHost);

        mAdapter.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                setTabColor(tabHost);
                if (tabId.equals(TAB_ID_FLAG)) {
                    toolbarLogo.setImageResource(R.drawable.icon_cloud);
                    toolbarFlagContainer.setVisibility(View.VISIBLE);
                    toolbarTitle.setVisibility(View.GONE);
                } else if (tabId.equals(TAB_ID_FILEMNG)) {
                    toolbarLogo.setImageResource(R.drawable.icon_toolbar_logo_sdcard);
                    toolbarTitle.setText("내 파일");
                    toolbarFlagContainer.setVisibility(View.GONE);
                    toolbarTitle.setVisibility(View.VISIBLE);
                } else {
                    toolbarLogo.setImageResource(R.drawable.icon_tap_options);
                    toolbarTitle.setText("설정");
                    toolbarFlagContainer.setVisibility(View.GONE);
                    toolbarTitle.setVisibility(View.VISIBLE);
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
