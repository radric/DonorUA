package ua.andriyantonov.donorua.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.andriyantonov.donorua.R;
import ua.andriyantonov.donorua.data.Utils;


public class CentersOnMapActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Handler mHandler = new Handler();
    private CameraPosition mCameraPosition;
    private CameraUpdate mCameraUpdate;
    private Boolean mFirstSetUp = true;
    private String[] mCentersPhone, mCentersName, mCentersDesc, mCentersLat, mCentersLong, mCentersAddress;
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
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
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
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                if (mFirstSetUp){
                    setUpMap();
                    mFirstSetUp = false;
                    Runnable getCentersData = new Runnable() {
                        @Override
                        public void run() {
                            Utils.getCentersData(getApplicationContext());
                        }
                    };
                    runOnUiThread(getCentersData);
                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(final Marker marker) {
                            final int centerIndex = Arrays.asList(mCentersName).indexOf(marker.getTitle());
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse(Utils.getMobileNum(mCentersPhone[centerIndex])));
                            startActivity(intent);
                            mFirstSetUp = true;
                        }
                    });
                    }
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mHandler.postDelayed(runnable, 500);
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mCameraPosition = new CameraPosition.Builder()
                .target(new LatLng(49.0139, 31.2858))
                .zoom(5)
                .build();
        mCameraUpdate = CameraUpdateFactory.newCameraPosition(mCameraPosition);
        mMap.animateCamera(mCameraUpdate);
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mCentersPhone = Utils.sCentersPhone;
            mCentersLat = Utils.sCentersLat;
            mCentersLong = Utils.sCentersLong;
            mCentersName = Utils.sCentersName;
            mCentersDesc = Utils.sCentersDesc;
            mCentersAddress = Utils.sCentersAddress;

            if (mCentersPhone == null || mCentersName == null || mCentersDesc == null ||
                    mCentersLat == null || mCentersLong == null || mCentersAddress == null) {
                mHandler.postDelayed(runnable, 500);
                Log.d("111", " + 1 sec");
            } else if (mCentersPhone.length < 0 || mCentersName.length < 0 || mCentersDesc.length <0 ||
                    mCentersLat.length < 0 || mCentersLong.length <0 || mCentersAddress.length < 0){
                mHandler.postDelayed(runnable, 200);
            } else {
                for (int i = 0; i < mCentersLong.length; i++){
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(
                                    Double.parseDouble(mCentersLat[i]),
                                    Double.parseDouble(mCentersLong[i])))
                            .title(mCentersName[i]))
                            .setSnippet(mCentersAddress[i] + "\n" +
                                    mCentersDesc[i] + "\n" +
                                    "тел: " + mCentersPhone[i]);

                    }
                }
            }
        };
}
