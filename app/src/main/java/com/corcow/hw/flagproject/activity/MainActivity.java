package com.corcow.hw.flagproject.activity;


import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.corcow.hw.flagproject.fragment.FileManagerFragment;
import com.corcow.hw.flagproject.fragment.FlagFragment;
import com.corcow.hw.flagproject.R;
import com.corcow.hw.flagproject.fragment.SettingsFragment;
import com.corcow.hw.flagproject.adapter.TabsAdapter;
import com.corcow.hw.flagproject.fragment.UserInputDialog;
import com.corcow.hw.flagproject.manager.NetworkManager;
import com.corcow.hw.flagproject.manager.UserManager;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;


public class MainActivity extends AppCompatActivity {

    // Pager tab define
    private static final String TAB_TAG = "currentTab";
    private static final String TAB_ID_FLAG = "tab_flag";
    private static final String TAB_ID_FILEMNG = "tab_filemng";
    private static final String TAB_ID_SETTINGS = "tab_settings";

    // Intent Extra define
    public static final String EXTRA_KEY_WHOS_PAGE = "whosPage";

    // Toolbar
    ImageView toolbarLogo;
    TextView toolbarTitle;
    EditText flagInputField;
    LinearLayout toolbarFlagContainer;
    LinearLayout toolbarNonFlagContainer;

    // FAB
    FloatingActionButton fab;
    FloatingActionMenu fabMenu;

    // Tab Pager
    TabHost tabHost;
    ViewPager pager;
    TabsAdapter mAdapter;

    // logged in User ID
    String loggedInID;

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

        setFloatingBtn();


        // Toolbar setting
        toolbarLogo = (ImageView)findViewById(R.id.toolbar_logo);

        toolbarFlagContainer = (LinearLayout)findViewById(R.id.toolbar_container_flag);

        flagInputField = (EditText)findViewById(R.id.toolbar_download_editText);
        toolbarLogo.setImageResource(R.drawable.icon_cloud);
        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        loggedInID = UserManager.getInstance().getUserID();



        /*
        // fab button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 내가 올린 파일 리스트
                Intent intent = new Intent(MainActivity.this, UserPageActivity.class);
                if (!TextUtils.isEmpty(loggedInID)) {
                    intent.putExtra(EXTRA_KEY_WHOS_PAGE, loggedInID);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "로그인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        */


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
                    fabMenu.close(true);
                } else if (tabId.equals(TAB_ID_FILEMNG)) {
                    toolbarLogo.setImageResource(R.drawable.icon_toolbar_logo_sdcard);
                    toolbarTitle.setText("내 파일");
                    toolbarFlagContainer.setVisibility(View.GONE);
                    toolbarTitle.setVisibility(View.VISIBLE);
                    fabMenu.close(true);
                } else {
                    toolbarLogo.setImageResource(R.drawable.icon_tap_options);
                    toolbarTitle.setText("설정");
                    toolbarFlagContainer.setVisibility(View.GONE);
                    toolbarTitle.setVisibility(View.VISIBLE);
                    fabMenu.close(true);
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

    private void setFloatingBtn() {

        /** Floating Action Btn */
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_fab_plus));

        int fabBtnDiameter = convertToDp(80);
        int fabSubBtnDiameter = convertToDp(65);
        int subBtnPadding = convertToDp(15);
        int radius = convertToDp(75);
        FloatingActionButton.LayoutParams fabParams = new FloatingActionButton.LayoutParams(fabBtnDiameter, fabBtnDiameter);
        fabParams.bottomMargin = convertToDp(60);
        fabParams.rightMargin = convertToDp(10);
        fab = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .setLayoutParams(fabParams)
                .build();

        // FAB sub Mypage
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        ImageView fabMypage = new ImageView(this);
        fabMypage.setImageDrawable(getResources().getDrawable(R.drawable.icon_fab_mypage));
        fabMypage.setPadding(subBtnPadding, subBtnPadding, subBtnPadding, subBtnPadding);
        SubActionButton fabSubBtnMypage = itemBuilder.setContentView(fabMypage)
                .setLayoutParams(new FrameLayout.LayoutParams(fabSubBtnDiameter, fabSubBtnDiameter))
                .build();
        // FAB sub Otherpage
        ImageView fabOtherpage = new ImageView(this);
        fabOtherpage.setImageDrawable(getResources().getDrawable(R.drawable.icon_fab_otherspage));
        fabOtherpage.setPadding(subBtnPadding, subBtnPadding, subBtnPadding, subBtnPadding);
        SubActionButton fabSubBtnOtherpage = itemBuilder.setContentView(fabOtherpage)
                .setLayoutParams(new FrameLayout.LayoutParams(fabSubBtnDiameter, fabSubBtnDiameter))
                .build();

        // sub item setting
        fabMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(fabSubBtnMypage)
                .addSubActionView(fabSubBtnOtherpage)
                .setStartAngle(190)
                .setEndAngle(260)
                .setRadius(radius)
                .attachTo(fab)
                .build();

        fabSubBtnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 내가 올린 파일 리스트
                if (!TextUtils.isEmpty(loggedInID)) {
                    // 로그인 되있다면,
                    Intent intent = new Intent(MainActivity.this, UserPageActivity.class);
                    intent.putExtra(EXTRA_KEY_WHOS_PAGE, loggedInID);
                    startActivity(intent);
                } else {
                    // 로그인 상태가 아니라면,
                    Toast.makeText(MainActivity.this, "내가 올린 파일은 로그인 후 확인 가능합니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        fabSubBtnOtherpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInputDialog dlg = new UserInputDialog();
                dlg.show(getSupportFragmentManager(), "");
            }
        });
    }

    public int convertToDp(int inputDp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, inputDp, getResources().getDisplayMetrics());
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
    protected void onRestart() {
        super.onRestart();
        loggedInID = UserManager.getInstance().getUserID();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loggedInID = UserManager.getInstance().getUserID();
    }

    @Override
    public void onBackPressed() {
        if (fabMenu.isOpen()) {
            fabMenu.close(true);
        } else {
            if (mOnBackPressedListener != null) {
                mOnBackPressedListener.onBackPressed();
            } else {
                super.onBackPressed();
            }
        }
    }
}
