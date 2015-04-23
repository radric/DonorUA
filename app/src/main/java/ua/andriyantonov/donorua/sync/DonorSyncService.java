package ua.andriyantonov.donorua.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by andriy on 13.04.15.
 */
public class DonorSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static DonorSyncAdapter sDonorSyncAdapter = null;
    public final String LOG_TAG = DonorSyncAdapter.class.getSimpleName();

    @Override
    public void onCreate(){
        Log.d(LOG_TAG, "onCreate - Donor SyncService");
        synchronized (sSyncAdapterLock){
            if (sDonorSyncAdapter == null){
                sDonorSyncAdapter = new DonorSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sDonorSyncAdapter.getSyncAdapterBinder();
    }
}
