package ua.andriyantonov.donorua.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.andriyantonov.donorua.R;
import ua.andriyantonov.donorua.fragments.UserInfoFragment;

public class UserInfoActivity extends ActionBarActivity {

    private static final String DRAWER_KEY = "drawer key";
    private Drawer.Result mDrawer;
    private boolean mDrawerIsOpen;

    @InjectView(R.id.toolbar_user_info) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.inject(this);
        initNavDrawer();


        if (savedInstanceState != null && savedInstanceState.containsKey(DRAWER_KEY)){
            mDrawerIsOpen = savedInstanceState.getBoolean(DRAWER_KEY);
            if (mDrawerIsOpen){mDrawer.openDrawer();}
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle){
        bundle.putBoolean(DRAWER_KEY, mDrawerIsOpen);
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onResume(){
        super.onResume();
        initNavDrawer();
        initHeaderTextFont();
    }

    public void initHeaderTextFont(){
        TextView mHeder = (TextView) findViewById(R.id.heder_textview);

        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/Kotyhoroshko.ttf");
        mHeder.setTypeface(tf,Typeface.BOLD);
    }

    /**
     * Initializes and sets the Navigation Drawer
     */
    private void initNavDrawer(){
        mDrawer = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_recipients).
                                withIcon(FontAwesome.Icon.faw_users).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_centers).
                                withIcon(FontAwesome.Icon.faw_map_marker).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_need_to_know).
                                withIcon(FontAwesome.Icon.faw_bookmark).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_user_info).
                                withIcon(FontAwesome.Icon.faw_user).withIdentifier(4)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                            long id, IDrawerItem iDrawerItem) {
                        if (iDrawerItem != null) {
                            Intent intent = null;
                            if (iDrawerItem.getIdentifier() == 1) {
                                intent = new Intent(UserInfoActivity.this, RecipientsActivity.class);
                                UserInfoActivity.this.startActivity(intent);
                            }
                            if (iDrawerItem.getIdentifier() == 2) {
                                intent = new Intent(UserInfoActivity.this, CentersOnMapActivity.class);
                                UserInfoActivity.this.startActivity(intent);
                            }
                            if (iDrawerItem.getIdentifier() == 3) {
                                intent = new Intent(UserInfoActivity.this, NeedToKnowActivity.class);
                                UserInfoActivity.this.startActivity(intent);
                            }
                            if (iDrawerItem.getIdentifier() == 4) {
                                mDrawer.closeDrawer();
                            }
                        }
                        if (iDrawerItem instanceof Nameable) {
                            setTitle(UserInfoActivity.this.getString(((Nameable) iDrawerItem).getNameRes()));
                        }
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {
                        InputMethodManager imm = (InputMethodManager) UserInfoActivity.this.
                                getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromInputMethod(UserInfoActivity.this.getCurrentFocus().getWindowToken(), 0);
                        mDrawerIsOpen = true;
                        initHeaderTextFont();
                    }

                    @Override
                    public void onDrawerClosed(View view) {
                        mDrawerIsOpen = false;
                    }
                })
                .withSelectedItem(3)
                .withDrawerWidthRes(R.dimen.nav_drawer_width)
                .build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipients_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
