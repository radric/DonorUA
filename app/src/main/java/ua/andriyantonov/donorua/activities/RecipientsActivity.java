package ua.andriyantonov.donorua.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.andriyantonov.donorua.R;
import ua.andriyantonov.donorua.data.Utils;
import ua.andriyantonov.donorua.fragments.RecipientDetailFragment;
import ua.andriyantonov.donorua.fragments.RecipientsFragment;
import ua.andriyantonov.donorua.sync.DonorSyncAdapter;


public class RecipientsActivity extends ActionBarActivity implements RecipientsFragment.Callback{

    private final static String RECIP_DET_SFRAGMENT_TAG = "RDF_TAG";
    private static final String DRAWER_KEY = "drawer key";
    private Drawer.Result mDrawer;
    private boolean mTwoPane, mDrawerIsOpen;
    private String mUserCity;
    private int mBloodType;

    @InjectView(R.id.toolbar_main) Toolbar mToolbar;



    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkIsLogged();

        if (Utils.sIsLogged){
            setContentView(R.layout.activity_recipients);
            ButterKnife.inject(this);
            initNavDrawer();

            prepareMapView();
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            mUserCity = Utils.loadUserCity(this);
            Utils.loadUserBloodGroup(this);
            mBloodType = Utils.sUserBloodType;

            if (savedInstanceState != null && savedInstanceState.containsKey(DRAWER_KEY)){
                mDrawerIsOpen = savedInstanceState.getBoolean(DRAWER_KEY);
                if (mDrawerIsOpen){mDrawer.openDrawer();}
            }

            if (findViewById(R.id.recipient_detail_container) != null){
                mTwoPane = true;

                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recipient_detail_container,new RecipientDetailFragment(), RECIP_DET_SFRAGMENT_TAG)
                            .commit();
                }
            } else {
                mTwoPane = false;
            }
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
        checkUserInfoChanges();
        initHeaderTextFont();
    }

    public void initHeaderTextFont(){
        TextView mHeder = (TextView) findViewById(R.id.heder_textview);

        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/Kotyhoroshko.ttf");
        mHeder.setTypeface(tf,Typeface.BOLD);
    }

    public void checkUserInfoChanges(){
        String userCity = Utils.loadUserCity(this);
        Utils.loadUserBloodGroup(this);
        int bloodType = Utils.sUserBloodType;
        if (userCity != null && !userCity.equals(mUserCity) ||
                bloodType != 0 && bloodType != mBloodType){
            RecipientsFragment recFragment = (RecipientsFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.recipients_list_container);
            if (null != recFragment){
                recFragment.userInfoChanged();
            }
            RecipientDetailFragment recDetFragment = (RecipientDetailFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.recipient_detail_container);
            if (null != recDetFragment){
                recDetFragment.userInfoChanged();
            }
        }
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
                                mDrawer.closeDrawer();
                            }
                            if (iDrawerItem.getIdentifier() == 2) {
                                intent = new Intent(RecipientsActivity.this, CentersOnMapActivity.class);
                                RecipientsActivity.this.startActivity(intent);
                            }
                            if (iDrawerItem.getIdentifier() == 3) {
                                intent = new Intent(RecipientsActivity.this, NeedToKnowActivity.class);
                                RecipientsActivity.this.startActivity(intent);
                            }
                            if (iDrawerItem.getIdentifier() == 4) {
                                intent = new Intent(RecipientsActivity.this, UserInfoActivity.class);
                                RecipientsActivity.this.startActivity(intent);
                            }
                        }
                        if (iDrawerItem instanceof Nameable) {
                            setTitle(RecipientsActivity.this.getString(((Nameable) iDrawerItem).getNameRes()));
                        }
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {
                        InputMethodManager imm = (InputMethodManager) RecipientsActivity.this.
                                getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromInputMethod(RecipientsActivity.this.getCurrentFocus().getWindowToken(), 0);
                        mDrawerIsOpen = true;
                        initHeaderTextFont();
                    }

                    @Override
                    public void onDrawerClosed(View view) {
                        mDrawerIsOpen = false;
                    }
                })
                .withSelectedItem(0)
                .withDrawerWidthRes(R.dimen.nav_drawer_width)
                .build();
        checkFirstStart();
    }

    public void checkFirstStart(){
        Utils.loadFirstStartStatus(this);
        Boolean mFirstStart = Utils.sFirstStart;
        if (mFirstStart){
            mDrawer.openDrawer();
            mFirstStart = false;
            Utils.saveFirstStartStatus(this, mFirstStart);
        }
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



    @Override
    public void onBackPressed() {
        // Закрываем Navigation Drawer по нажатию системной кнопки "Назад" если он открыт
        if (mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Check if user entered his details
     */
    private void checkIsLogged(){
        Utils.loadRegStatus(this);
        if (!Utils.sIsLogged){
            DonorSyncAdapter.initializeSyncAdapter(this);
            Intent intent = new Intent(this,RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Prepares mapView for the firs (more fast) start:
     * initialization of GooglePlay Service make app slowly,
     * so its better to prepare and make initialization before MapActivity starts
     */
    private void prepareMapView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    MapView mapView = new MapView(getApplicationContext());
                    mapView.onCreate(null);
                    mapView.onPause();
                    mapView.onDestroy();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(RecipientDetailFragment.RECIP_DETAIL_URI, contentUri);

            RecipientDetailFragment rdf = new RecipientDetailFragment();
            rdf.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipient_detail_container, rdf)
                    .commit();
        } else {
            Intent intent = new Intent(this, RecipientDetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}
