package ua.andriyantonov.donorua.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.andriyantonov.donorua.R;
import ua.andriyantonov.donorua.data.Utils;


public class CentersOnMapActivity extends ActionBarActivity {

    private Drawer.Result mDrawer;
    private GoogleMap mMap;
    private Boolean mFirstSetUp = true, mDrawerIsOpen;
    private static final String SELECTED_KEY = "firstSetUpStatus";
    private static final String DRAWER_KEY = "drawer key";

    @InjectView(R.id.toolbar_map) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centers_on_map);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initNavDrawer();
        Utils.initHeaderTextFont(this);

        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(SELECTED_KEY)){
                mFirstSetUp = savedInstanceState.getBoolean(SELECTED_KEY);
            }
            if (savedInstanceState.containsKey(DRAWER_KEY)){
                mDrawerIsOpen = savedInstanceState.getBoolean(DRAWER_KEY);
                if (mDrawerIsOpen){mDrawer.openDrawer();}
            }
        }
        setUpMapIfNeeded();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mFirstSetUp != null){
            outState.putBoolean(SELECTED_KEY, mFirstSetUp);
        }
        if (mDrawerIsOpen != null){
            outState.putBoolean(DRAWER_KEY, mDrawerIsOpen);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                if (mFirstSetUp){
                    setUpMap();
                    mFirstSetUp = false;
                    Utils.getCentersData(getApplicationContext());
                    }
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                setMarkers();
            }
        }
    }

    /**
     * Setups map in the center of Ukraine
     */
    private void setUpMap() {
        CameraPosition mCameraPosition = new CameraPosition.Builder()
                .target(new LatLng(49.0139, 31.2858))
                .zoom(5)
                .build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(mCameraPosition);
        mMap.animateCamera(mCameraUpdate);
    }

    /**
     * Prepare information for every center and sets markers on the map
     */
    private void setMarkers(){
        String[] mCentersPhone = Utils.sCentersPhone;
        String[] mCentersLat = Utils.sCentersLat;
        String[] mCentersLong = Utils.sCentersLong;
        String[] mCentersAddress = Utils.sCentersAddress;
        if (mCentersPhone == null || mCentersLat == null || mCentersLong == null
                || mCentersAddress == null) {
        } else {
            for (int i = 0; i < mCentersLong.length; i++){
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(
                                Double.parseDouble(mCentersLat[i]),
                                Double.parseDouble(mCentersLong[i])))
                        .title(mCentersAddress[i]))
                        .setSnippet("тел: " + mCentersPhone[i]);
                }
            }

        }

    @Override
    public void onResume(){
        super.onResume();
        mDrawer.setSelection(1);
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
                        new PrimaryDrawerItem().withName(R.string.drawer_item_recipients).withIconColor(Color.WHITE).withTintSelectedIcon(true)
                                .withIcon(FontAwesome.Icon.faw_users).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_centers).withIconColor(Color.WHITE).withTintSelectedIcon(true).
                                withIcon(FontAwesome.Icon.faw_map_marker).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_need_to_know).withIconColor(Color.WHITE).withTintSelectedIcon(true).
                                withIcon(FontAwesome.Icon.faw_bookmark).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_user_info).withIconColor(Color.WHITE).withTintSelectedIcon(true).
                                withIcon(FontAwesome.Icon.faw_user).withIdentifier(4)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                            long id, IDrawerItem iDrawerItem) {
                        if (iDrawerItem != null) {
                            Intent intent = null;
                            if (iDrawerItem.getIdentifier() == 1) {
                                intent = new Intent(CentersOnMapActivity.this, RecipientsActivity.class);
                                CentersOnMapActivity.this.startActivity(intent);
                            }
                            if (iDrawerItem.getIdentifier() == 2) {
                                mDrawer.closeDrawer();
                            }
                            if (iDrawerItem.getIdentifier() == 3) {
                                intent = new Intent(CentersOnMapActivity.this, NeedToKnowActivity.class);
                                CentersOnMapActivity.this.startActivity(intent);
                            }
                            if (iDrawerItem.getIdentifier() == 4) {
                                intent = new Intent(CentersOnMapActivity.this, UserInfoActivity.class);
                                CentersOnMapActivity.this.startActivity(intent);
                            }
                        }
                        if (iDrawerItem instanceof Nameable) {
                            setTitle(CentersOnMapActivity.this.getString(((Nameable) iDrawerItem).getNameRes()));
                        }
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {
                        InputMethodManager imm = (InputMethodManager) CentersOnMapActivity.this.
                                getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromInputMethod(
                                CentersOnMapActivity.this.getCurrentFocus().getWindowToken(), 0);
                        mDrawerIsOpen = true;
                    }
                    @Override
                    public void onDrawerClosed(View view) {
                        mDrawerIsOpen = false;
                    }
                })
                .withSelectedItem(1)
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
