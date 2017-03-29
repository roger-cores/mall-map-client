package com.jelliroo.mallmapbeta.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.enums.ClassType;
import com.jelliroo.mallmapbeta.fragments.ClassFragment;
import com.jelliroo.mallmapbeta.fragments.ClassListFragment;
import com.jelliroo.mallmapbeta.fragments.ClassesMapFragment;
import com.jelliroo.mallmapbeta.fragments.HelperClassFragment;
import com.jelliroo.mallmapbeta.fragments.LinkFragment;
import com.jelliroo.mallmapbeta.fragments.SuperClassFragment;


public class ClassesActivity extends AppCompatActivity {

    private FragmentTabHost mTabHost;

    /*
        true: map
        false: list
     */
    private boolean displayType = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTabHost = (FragmentTabHost) findViewById(R.id.fragment_tab_host);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("tab1").setIndicator("Classes", null),
                ClassFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("tab2").setIndicator("Helper Classes", null),
                HelperClassFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("tab3").setIndicator("Links", null),
                LinkFragment.class, null);






    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.class_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        } else if(item.getItemId() == R.id.action_list_or_map){
            displayType = !displayType;
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(mTabHost.getCurrentTabTag());
            if(fragment instanceof SuperClassFragment){
                SuperClassFragment superClassFragment = (SuperClassFragment) fragment;
                superClassFragment.setDisplayType(displayType);
                if(displayType){
                    item.setIcon(R.drawable.ic_pin_drop_black_24dp);
                } else {
                    item.setIcon(R.drawable.ic_format_list_bulleted_black_24dp);
                }
            }

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean getDisplayType() {
        return displayType;
    }
}
