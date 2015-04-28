package ua.andriyantonov.donorua.activities;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;
import ua.andriyantonov.donorua.R;
import ua.andriyantonov.donorua.data.Utils;


public class CentersOnMapActivity extends FragmentActivity {

    private GoogleMap mMap;
    private Handler mHandler = new Handler();
    private CameraPosition mCameraPosition;
    private CameraUpdate mCameraUpdate;
    private Boolean mFirstSetUp = true;
    private String[] mCentersPhone, mCentersLat, mCentersLong, mCentersAddress;
    private static final String SELECTED_KEY = "firstSetUpStatus";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centers_on_map);
        ButterKnife.inject(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mFirstSetUp = savedInstanceState.getBoolean(SELECTED_KEY);
        }
        setUpMapIfNeeded();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
            outState.putBoolean(SELECTED_KEY, mFirstSetUp);
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
        mCameraPosition = new CameraPosition.Builder()
                .target(new LatLng(49.0139, 31.2858))
                .zoom(5)
                .build();
        mCameraUpdate = CameraUpdateFactory.newCameraPosition(mCameraPosition);
        mMap.animateCamera(mCameraUpdate);
    }

    /**
     * Prepare information for every center and sets markers on the map
     */
    private void setMarkers(){
        mCentersPhone = Utils.sCentersPhone;
        mCentersLat = Utils.sCentersLat;
        mCentersLong = Utils.sCentersLong;
        mCentersAddress = Utils.sCentersAddress;
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
}
