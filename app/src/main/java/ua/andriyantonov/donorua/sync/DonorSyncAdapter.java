package ua.andriyantonov.donorua.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import ua.andriyantonov.donorua.R;
import ua.andriyantonov.donorua.activities.RecipientsActivity;
import ua.andriyantonov.donorua.data.DonorContract;
import ua.andriyantonov.donorua.data.Utils;

/**
 * Created by andriy on 13.04.15.
 */
public class DonorSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = DonorSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the donorsData, in seconds.
    // 60 seconds (1 minute) * 60 * 24 * 7 = 1 week
    public static final int SYNC_INTERVAL = 60 * 60 * 24 * 7;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/7;
    private static final int DONOR_NOTIFICATION_ID = 7;



    public DonorSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");
        String[] params = {
                getContext().getString(R.string.citiesData),
                getContext().getString(R.string.recipientsData),
                getContext().getString(R.string.centersData) };
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String stringDataJson = null;
        String sign_key = getContext().getString(R.string.donorua_sign_key);
        String public_key = getContext().getString(R.string.donorua_public_key);
        String private_key = getContext().getString(R.string.donorua_private_key);

        for (int i = 0; i<params.length; i++) {
            try {
                String DONORUA_URL = "http://donor.ua";
                String baseUrl = "/api/" + params[i];
                String sign = Utils.getSign(baseUrl, private_key, public_key);
                Log.v(LOG_TAG, "baseUrl: " + baseUrl);

                URL url = new URL(DONORUA_URL + baseUrl + sign_key + sign);
                Log.v(LOG_TAG, "Built URI: " + url.toString());

                // Creating the request to Donor.ua, and open the connection

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    //in case stream will be empty
                    return;
                }
                stringDataJson = buffer.toString();

                if (params[i].equals(params[0])) {
                    getCitiesDataFromJson(stringDataJson);
                } else if (params[i].equals(params[1])) {
                    getRecipientsDataFromJson(stringDataJson);
                } else if (params[i].equals(params[2])) {
                    getCentersDataFromJson(stringDataJson);
                }

                Log.v(LOG_TAG, params[i] + " loaded :" + stringDataJson);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connectivity", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
              {
                if (urlConnection == null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "error closing stream", e);
                    }
                }
            }
        notifyDonor();
    }

    private void notifyDonor() {
        Context context = getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String displayNotificationKey = context.getString(R.string.pref_notification_key);
        String notifStatus = sharedPreferences.getString(displayNotificationKey, "1");

        if (notifStatus.equals( "1")||notifStatus.equals("2")||notifStatus.equals("3")){
            long TIME_TO_NOTIFY;
            if (notifStatus.equals("1")){
                TIME_TO_NOTIFY = (1000 * 60 * 59 * 24) * 7;
            } else if (notifStatus.equals("2")) {
                TIME_TO_NOTIFY = (1000 * 60 * 59 * 24) * 14;
            } else {
                TIME_TO_NOTIFY = (1000 * 60 * 59 * 24) * 30;
            }

            String lastNotificationKey = context.getString(R.string.pref_last_notification);
            long lastSync = sharedPreferences.getLong(lastNotificationKey, 0);
            if (System.currentTimeMillis() - lastSync >= TIME_TO_NOTIFY){

                Uri notifyUri = DonorContract.RecipientsEntry.CONTENT_URI;
                Cursor cursor = context.getContentResolver().query(notifyUri, null, null, null, null);

                int recipientCount = cursor.getCount();

                String title = context.getString(R.string.app_name);
                String contentText = String.format(context.getString(R.string.format_notification),
                        recipientCount);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(contentText)
                        .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS);

                Intent resutlIntent = new Intent(context, RecipientsActivity.class);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntent(resutlIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                        0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);

                NotificationManager mNotificationManager =
                        (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(DONOR_NOTIFICATION_ID, mBuilder.build());


                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(lastNotificationKey, System.currentTimeMillis());
                editor.apply();
                cursor.close();
            }
        }
        }


    private void getCitiesDataFromJson(String dataJson)
            throws JSONException{
        final String CITY_ID = "Id";
        final String CITY_NAME = "Name";
        final String CITY_REGION_ID = "RegionId";

        try{
            JSONArray citiesArray = new JSONArray(dataJson);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(citiesArray.length());

            for (int i = 0; i < citiesArray.length(); i++){
                int id;
                String name;
                int regionId;

                JSONObject currentCity = citiesArray.getJSONObject(i);

                id = currentCity.getInt(CITY_ID);
                name = currentCity.getString(CITY_NAME);
                regionId = currentCity.getInt(CITY_REGION_ID);

                ContentValues cityValue = new ContentValues();
                cityValue.put(DonorContract.CitiesEntry.COLUMN_CITY_ID, id);
                cityValue.put(DonorContract.CitiesEntry.COLUMN_NAME, name);
                cityValue.put(DonorContract.CitiesEntry.COLUMN_REGION, regionId);

                cVVector.add(cityValue);
            }
            if (cVVector.size() > 0){
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver()
                        .bulkInsert(DonorContract.CitiesEntry.CONTENT_URI, cvArray);
            }
            Log.d(LOG_TAG, "getCitiesDataFromJson Complete. " + cVVector.size() + " Inserted");
        }  catch (JSONException e){
            e.printStackTrace();
        }
    }
    private void getRecipientsDataFromJson(String dataJson)
            throws JSONException {

        final String RECIP_ID = "Id";
        final String RECIP_CENTER = "DonorCenterId";
        final String RECIP_LASTNAME = "LastName";
        final String RECIP_FIRSTNAME = "FirstName";
        final String RECIP_DATE_OF_BIRTH = "DateOfBirth";
        final String RECIP_BLOOD_GROUP = "BloodGroupId";
        final String RECIP_NEEDED_TYPES = "NeededTypes";
        final String RECIP_DON_TYPE_ID = "DonationTypeId";
        final String RECIP_DISEASE = "Disease";
        final String RECIP_PHOTO_IMAGE = "PhotoImage";
        final String RECIP_CONTACT_PERSON = "ContactPerson";
        final String RECIP_CONTACT_PHONE = "Phone";
        final String RECIP_DESCRIPTION = "Description";

        try {
            JSONArray recipientsArray = new JSONArray(dataJson);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(recipientsArray.length());
            for (int i = 0; i < recipientsArray.length(); i++) {
                int id;
                int center;
                String lastName;
                String firstName;
                String birthDay;
                int bloodGroupId;
                String donationTypeStr = "";
                int donationTypeInt;
                String disease;
                String photoImage;
                String contactPerson;
                String contactPhone;
                String description;

                JSONObject currentRecipient = recipientsArray.getJSONObject(i);

                JSONArray neededTypesArray = currentRecipient.getJSONArray(RECIP_NEEDED_TYPES);
                for (int j = 0; j < neededTypesArray.length(); j++){
                    JSONObject currentDonationType = neededTypesArray.getJSONObject(j);
                    donationTypeStr = donationTypeStr.concat(currentDonationType.getString(RECIP_DON_TYPE_ID));
                }
                donationTypeInt = Integer.parseInt(donationTypeStr);


                id = currentRecipient.getInt(RECIP_ID);
                center = currentRecipient.getInt(RECIP_CENTER);
                lastName = currentRecipient.getString(RECIP_LASTNAME);
                firstName = currentRecipient.getString(RECIP_FIRSTNAME);
                birthDay = currentRecipient.getString(RECIP_DATE_OF_BIRTH);
                bloodGroupId = currentRecipient.getInt(RECIP_BLOOD_GROUP);
                disease = currentRecipient.getString(RECIP_DISEASE);
                photoImage = currentRecipient.getString(RECIP_PHOTO_IMAGE);
                contactPerson = currentRecipient.getString(RECIP_CONTACT_PERSON);
                contactPhone = currentRecipient.getString(RECIP_CONTACT_PHONE);
                description = currentRecipient.getString(RECIP_DESCRIPTION);

                ContentValues recipientValues = new ContentValues();

                recipientValues.put(DonorContract.RecipientsEntry.COLUMN_RECIPIENT_ID, id);
                recipientValues.put(DonorContract.RecipientsEntry.COLUMN_CENTER_KEY, center);
                recipientValues.put(DonorContract.RecipientsEntry.COLUMN_LAST_NAME, lastName);
                recipientValues.put(DonorContract.RecipientsEntry.COLUMN_FIRST_NAME, firstName);
                recipientValues.put(DonorContract.RecipientsEntry.COLUMN_BIRTH_DAY, birthDay);
                recipientValues.put(DonorContract.RecipientsEntry.COLUMN_BLOOD_GROUP_ID, bloodGroupId);
                recipientValues.put(DonorContract.RecipientsEntry.COLUMN_DONATION_TYPE, donationTypeInt);
                recipientValues.put(DonorContract.RecipientsEntry.COLUMN_DISEASE , disease);
                recipientValues.put(DonorContract.RecipientsEntry.COLUMN_PHOTO_IMAGE , photoImage);
                recipientValues.put(DonorContract.RecipientsEntry.COLUMN_CONTACT_PERSON , contactPerson);
                recipientValues.put(DonorContract.RecipientsEntry.COLUMN_CONTACT_PHONE , contactPhone);
                recipientValues.put(DonorContract.RecipientsEntry.COLUMN_DESC , description);

                cVVector.add(recipientValues);
            }
            if (cVVector.size() > 0){
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                getContext().getContentResolver().bulkInsert(DonorContract.RecipientsEntry.CONTENT_URI, cvArray);
            }
            Log.d(LOG_TAG, "getRecipientsDataFromJson Complete. " + cVVector.size() + " Inserted");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    private void getCentersDataFromJson(String dataJson)
            throws JSONException{
        final String CENTER_ID = "Id";
        final String CENTER_CITY_ID = "CityId";
        final String CENTER_ADDRESS = "Address";
        final String CENTER_NAME = "Name";
        final String CENTER_LONG = "Longitude";
        final String CENTER_LAT = "Latitude";
        final String CENTER_DESC = "Description";
        final String CENTER_WEB = "Website";
        final String CENTER_EMAIL = "Email";
        final String CENTER_PHONE = "Phone";

        try{
            JSONArray centersArray = new JSONArray(dataJson);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(centersArray.length());

            for (int i = 0; i < centersArray.length(); i++){
                int id;
                int cityId;
                String address;
                String name;
                double longitude;
                double latitude;
                String description;
                String web;
                String email;
                String phone;

                JSONObject currentCenter = centersArray.getJSONObject(i);

                id = currentCenter.getInt(CENTER_ID);
                cityId = currentCenter.getInt(CENTER_CITY_ID);
                address = currentCenter.getString(CENTER_ADDRESS);
                name = currentCenter.getString(CENTER_NAME);
                longitude = currentCenter.getDouble(CENTER_LONG);
                latitude = currentCenter.getDouble(CENTER_LAT);
                description = currentCenter.getString(CENTER_DESC);
                web = currentCenter.getString(CENTER_WEB);
                email = currentCenter.getString(CENTER_EMAIL);
                phone = currentCenter.getString(CENTER_PHONE);

                ContentValues centerValue = new ContentValues();
                centerValue.put(DonorContract.CentersEntry.COLUMN_CENTER_ID, id);
                centerValue.put(DonorContract.CentersEntry.COLUMN_CITY_KEY, cityId);
                centerValue.put(DonorContract.CentersEntry.COLUMN_ADDRESS, address);
                centerValue.put(DonorContract.CentersEntry.COLUMN_CENTER_NAME, name);
                centerValue.put(DonorContract.CentersEntry.COLUMN_LONG, longitude);
                centerValue.put(DonorContract.CentersEntry.COLUMN_LAT, latitude);
                centerValue.put(DonorContract.CentersEntry.COLUMN_DESC, description);
                centerValue.put(DonorContract.CentersEntry.COLUMN_WEBSITE, web);
                centerValue.put(DonorContract.CentersEntry.COLUMN_EMAIL, email);
                centerValue.put(DonorContract.CentersEntry.COLUMN_PHONE, phone);

                cVVector.add(centerValue);
            }
            if (cVVector.size() > 0){
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver()
                        .bulkInsert(DonorContract.CentersEntry.CONTENT_URI, cvArray);
            }
            Log.d(LOG_TAG, "getCentersDataFromJson Complete. " + cVVector.size() + " Inserted");
        }  catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        DonorSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }
}
