package ua.andriyantonov.donorua.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by andriy on 13.04.15.
 */
public class DonorAuthenticatornService extends Service {

    private DonorAuthenticator mAuthenticator;

    @Override
    public void onCreate(){
        mAuthenticator = new DonorAuthenticator(this);
    }
    @Override
    public IBinder onBind(Intent intent) {

        return mAuthenticator.getIBinder();
    }
}
