package ua.andriyantonov.donorua.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import butterknife.Optional;
import ua.andriyantonov.donorua.R;
import ua.andriyantonov.donorua.fragments.NeedToKnowDetailFragment;
import ua.andriyantonov.donorua.fragments.NeedToKnowFragment;
import ua.andriyantonov.donorua.fragments.RecipientDetailFragment;

public class NeedToKnowActivity extends ActionBarActivity implements NeedToKnowFragment.CallbackToKnow{

    private final static String NEED_TO_KNOW_FRAGMENT_TAG = "NTKF_TAG";
    private final static String NEED_TO_KNOW_DETAIL_FRAGMENT_TAG = "NTKDF_TAG";
    private static final String DRAWER_KEY = "drawer key";
    private Drawer.Result mDrawer;
    private boolean mTwoPane, mDrawerIsOpen;

    @Optional
    @InjectView(R.id.toolbar_need_to_know)Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_to_know);
        ButterKnife.inject(this);

        initNavDrawer();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(DRAWER_KEY)){
            mDrawerIsOpen = savedInstanceState.getBoolean(DRAWER_KEY);
            if (mDrawerIsOpen){mDrawer.openDrawer();}
        }

        if (findViewById(R.id.need_to_know_detail_container) != null){
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.need_to_know_detail_container,new RecipientDetailFragment(),
                                NEED_TO_KNOW_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
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
                                intent = new Intent(NeedToKnowActivity.this, RecipientsActivity.class);
                                NeedToKnowActivity.this.startActivity(intent);
                            }
                            if (iDrawerItem.getIdentifier() == 2) {
                                intent = new Intent(NeedToKnowActivity.this, CentersOnMapActivity.class);
                                NeedToKnowActivity.this.startActivity(intent);
                            }
                            if (iDrawerItem.getIdentifier() == 3) {
                                mDrawer.closeDrawer();
                            }
                            if (iDrawerItem.getIdentifier() == 4) {
                                intent = new Intent(NeedToKnowActivity.this, UserInfoActivity.class);
                                NeedToKnowActivity.this.startActivity(intent);
                            }

                        }
                        if (iDrawerItem instanceof Nameable) {
                            setTitle(NeedToKnowActivity.this.getString(((Nameable) iDrawerItem).getNameRes()));
                        }
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {
                        InputMethodManager imm = (InputMethodManager) NeedToKnowActivity.this.
                                getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromInputMethod(NeedToKnowActivity.this.getCurrentFocus().getWindowToken(), 0);
                        mDrawerIsOpen = true;
                        initHeaderTextFont();
                    }

                    @Override
                    public void onDrawerClosed(View view) {
                        mDrawerIsOpen = false;
                    }
                })
                .withSelectedItem(2)
                .withDrawerWidthRes(R.dimen.nav_drawer_width)
                .build();
    }


    @Override
    public void onBackPressed() {
        // Закрываем Navigation Drawer по нажатию системной кнопки "Назад" если он открыт
        if (mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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


    /**
     * Checks if there are twoPane mode - open fragment in current activity,
     * if no - start new activity
     */
    @Override
    public void onItemSelected(int position) {
        if (mTwoPane){
            Bundle bundle = new Bundle();
            bundle.putInt(NeedToKnowFragment.POSITION_KEY, position);
            NeedToKnowDetailFragment fragment = new NeedToKnowDetailFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.need_to_know_detail_container, fragment,
                            NEED_TO_KNOW_DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, NeedToKnowDetailActivity.class);
            intent.putExtra(NeedToKnowFragment.POSITION_KEY, position);
            startActivity(intent);
        }
    }
}
