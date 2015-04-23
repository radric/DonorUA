package ua.andriyantonov.donorua.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ua.andriyantonov.donorua.R;

/**
 * Created by andriy on 28.03.15.
 */
public class Utils {

    public static boolean sIsLogged, sFirstStart;
    public static int sUserBloodType;
    public static String sUserBloodGroup, sUserRhFactor;
    public static String sUserCity, sUserCityId;
    public static int sBloodType;
    public static String[] sCitiesArray, sCitiesIdArray, sCentersLong, sCentersLat,
            sCentersName, sCentersDesc, sCentersAddress, sCentersPhone;

    private static SharedPreferences sShp;

    /**
     * Encrypts keys for API's query;
     */
    public static String getSign(String query, String privateKey, String publicKey) {
        String s = query + privateKey;
        String md5 = MD5(s);
        md5 = md5.replace("\n", "");
        return md5 + publicKey;
    }
    public static String MD5(String md5) {
        byte[] bytesOfMessage = new byte[0];
        try {
            bytesOfMessage = md5.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] thedigest = md.digest(bytesOfMessage);

        return Base64.encodeToString(thedigest, Base64.DEFAULT);
    }


    public static void saveRegStatus(Context context, boolean value) {
        sShp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sShp.edit();
        editor.putBoolean(context.getString(R.string.is_logged_key), value);
        editor.apply();
    }
    /**
     * Converts selected by User bloodType from UI format to API's format
     * and save it to sharedPreferences
     */
    public static void saveUserBloodType(Context context, int bloodGroup, String rhFactor) {
        String pos = context.getString(R.string.rh_factor_pos);
        String neg = context.getString(R.string.rh_factor_neg);
        if (bloodGroup == 1 && rhFactor.equals(pos)) {
            sBloodType = 1;
        } else if (bloodGroup == 1 && rhFactor.equals(neg)) {
            sBloodType = 2;
        } else if (bloodGroup == 2 && rhFactor.equals(pos)) {
            sBloodType = 3;
        } else if (bloodGroup == 2 && rhFactor.equals(neg)) {
            sBloodType = 4;
        } else if (bloodGroup == 3 && rhFactor.equals(pos)) {
            sBloodType = 5;
        } else if (bloodGroup == 3 && rhFactor.equals(neg)) {
            sBloodType = 6;
        } else if (bloodGroup == 4 && rhFactor.equals(pos)) {
            sBloodType = 7;
        } else if (bloodGroup == 4 && rhFactor.equals(neg)) {
            sBloodType = 8;
        }

        sShp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sShp.edit();
        editor.putInt(context.getString(R.string.user_blood_type_key), sBloodType);
        editor.apply();
    }
    /**
     * Saves selected by User city to sharedPreferences
     */
    public static void saveUserCity(Context context, String userCity) {
        sShp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sShp.edit();
        editor.putString(context.getString(R.string.user_city_key), userCity);
        editor.apply();
    }

    public static void saveUserCityId (Context context, String userCityId) {
        sShp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sShp.edit();
        editor.putString(context.getString(R.string.user_city_id_key), userCityId);
        editor.apply();
    }

    public static void saveFirstStartStatus(Context context, Boolean value){
        sShp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sShp.edit();
        editor.putBoolean(context.getString(R.string.first_start_key), value);
        editor.apply();
    }

    public static void loadFirstStartStatus(Context context) {
        sShp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        sFirstStart = sShp.getBoolean(context.getString(R.string.first_start_key), true);
    }

    public static void loadUserBloodGroup(Context context) {
        sShp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        sUserBloodType = sShp.getInt(context.getString(R.string.user_blood_type_key), -1);

        String first = context.getString(R.string.blood_group_1);
        String second = context.getString(R.string.blood_group_2);
        String third = context.getString(R.string.blood_group_3);
        String fourth = context.getString(R.string.blood_group_4);
        String positive = context.getString(R.string.rh_factor_pos);
        String negative = context.getString(R.string.rh_factor_neg);

        switch (sUserBloodType) {
            case 1:
                sUserBloodGroup = first;
                sUserRhFactor = positive;
                break;
            case 2:
                sUserBloodGroup = first;
                sUserRhFactor = negative;
                break;
            case 3:
                sUserBloodGroup = second;
                sUserRhFactor = positive;
                break;
            case 4:
                sUserBloodGroup = second;
                sUserRhFactor = negative;
                break;
            case 5:
                sUserBloodGroup = third;
                sUserRhFactor = positive;
                break;
            case 6:
                sUserBloodGroup = third;
                sUserRhFactor = negative;
                break;
            case 7:
                sUserBloodGroup = fourth;
                sUserRhFactor = positive;
                break;
            case 8:
                sUserBloodGroup = fourth;
                sUserRhFactor = negative;
                break;
        }
    }

    public static String loadUserCity(Context context) {
        sShp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        sUserCity = sShp.getString(context.getString(R.string.user_city_key), "");
        return sUserCity;
    }

    public static void loadUserCityId(Context context) {
        sShp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        sUserCityId = sShp.getString(context.getString(R.string.user_city_id_key), "");
    }


    public static void loadRegStatus(Context context) {
        sShp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        sIsLogged = sShp.getBoolean(context.getString(R.string.is_logged_key), false);
    }


    /**
     * Converts bloodGroup data from Api's format to UI format
     */
    public static String getBloodGroupFormat(Context context, int bloodTypeCode) {
        String bloodGroup = "";
        if (context != null){
            String first = context.getString(R.string.blood_group_1);
            String second = context.getString(R.string.blood_group_2);
            String third = context.getString(R.string.blood_group_3);
            String fourth = context.getString(R.string.blood_group_4);
            String positive = context.getString(R.string.rh_factor_pos);
            String negative = context.getString(R.string.rh_factor_neg);
            switch (bloodTypeCode) {
                case 1:
                    bloodGroup = first + positive;
                    break;
                case 2:
                    bloodGroup = first + negative;
                    break;
                case 3:
                    bloodGroup = second + positive;
                    break;
                case 4:
                    bloodGroup = second + negative;
                    break;
                case 5:
                    bloodGroup = third + positive;
                    break;
                case 6:
                    bloodGroup = third + negative;
                    break;
                case 7:
                    bloodGroup = fourth + positive;
                    break;
                case 8:
                    bloodGroup = fourth + negative;
                    break;
            }
        }
        return bloodGroup;
    }

    /**
     * Converts donationTypes data from Api's format to UI format
     */
    public static String getNeededDonationType(int donationTypes) {
        String returTypes = "";
        String typeOne = "Звичайна кроводача";
        String typeTwo = "Тромбоцитна маса";
        String typeThree = "Плазма крові";

        switch (donationTypes) {
            case 1:
                returTypes = typeOne;
                break;
            case 2:
                returTypes = typeTwo;
                break;
            case 3:
                returTypes = typeThree;
                break;
            case 12:
                returTypes = typeOne + "\n" + typeTwo;
                break;
            case 13:
                returTypes = typeOne + "\n" + typeThree;
                break;
            case 23:
                returTypes = typeTwo + "\n" + typeThree;
                break;
            case 123:
                returTypes = typeOne + "\n" + typeTwo + "\n" + typeThree;
                break;
        }
        return returTypes;
    }

    /**
     * Gets all city Names and Id from dateBase
     */
    public static void getCitiesData(Context context) {
        if (context != null){
            DonorDbHelper donorDbHelper = new DonorDbHelper(context);
            String[] cityColumnId = new String[]{DonorContract.CitiesEntry.COLUMN_CITY_ID};
            Cursor c = donorDbHelper.getReadableDatabase().query(DonorContract.CitiesEntry.TABLE_NAME,
                    cityColumnId,
                    null, null, null, null, null);
            c.moveToFirst();
            int mCount = c.getCount();
            sCitiesIdArray = new String[mCount];
            for (int i = 0; i<mCount ; i++){
                sCitiesIdArray[i] = c.getString(0);
                c.moveToNext();
            }

            String[] cityColumnName = new String[]{DonorContract.CitiesEntry.COLUMN_NAME};
            c = donorDbHelper.getReadableDatabase().query(DonorContract.CitiesEntry.TABLE_NAME,
                    cityColumnName,
                    null, null, null, null, null);
            c.moveToFirst();
            mCount = c.getCount();
            sCitiesArray = new String[mCount];

            for (int i = 0; i<mCount ; i++){
                sCitiesArray[i] = c.getString(0);
                c.moveToNext();
            }

            c.close();
        }
    }

    /**
     * Gets all city Names and Id from dateBase
     */
    public static void getCentersData(Context context) {
       if (context != null){
           DonorDbHelper donorDbHelper = new DonorDbHelper(context);
           String[] selectionLong = new String[]{DonorContract.CentersEntry.COLUMN_LONG};
           Cursor c = donorDbHelper.getReadableDatabase().query(DonorContract.CentersEntry.TABLE_NAME,
                   selectionLong,
                   null, null, null, null, null);
           c.moveToFirst();
           int mCount = c.getCount();
           sCentersLong = new String[mCount];
           for (int i = 0; i<mCount ; i++){
               sCentersLong[i] = c.getString(0);
               c.moveToNext();
           }

           String[] selectionLat = new String[]{DonorContract.CentersEntry.COLUMN_LAT};
           c = donorDbHelper.getReadableDatabase().query(DonorContract.CentersEntry.TABLE_NAME,
                   selectionLat,
                   null, null, null, null, null);
           c.moveToFirst();
           mCount = c.getCount();
           sCentersLat = new String[mCount];
           for (int i = 0; i<mCount ; i++){
               sCentersLat[i] = c.getString(0);
               c.moveToNext();
           }

           String[] selectionName = new String[]{DonorContract.CentersEntry.COLUMN_CENTER_NAME};
           c = donorDbHelper.getReadableDatabase().query(DonorContract.CentersEntry.TABLE_NAME,
                   selectionName,
                   null, null, null, null, null);
           c.moveToFirst();
           mCount = c.getCount();
           sCentersName = new String[mCount];
           for (int i = 0; i<mCount ; i++){
               sCentersName[i] = c.getString(0);
               c.moveToNext();
           }

           String[] selectionDesc = new String[]{DonorContract.CentersEntry.COLUMN_DESC};
           c = donorDbHelper.getReadableDatabase().query(DonorContract.CentersEntry.TABLE_NAME,
                   selectionDesc,
                   null, null, null, null, null);
           c.moveToFirst();
           mCount = c.getCount();
           sCentersDesc = new String[mCount];
           for (int i = 0; i<mCount ; i++){
               sCentersDesc[i] = c.getString(0);
               c.moveToNext();
           }
           String[] selectionAddress = new String[]{DonorContract.CentersEntry.COLUMN_ADDRESS};
           c = donorDbHelper.getReadableDatabase().query(DonorContract.CentersEntry.TABLE_NAME,
                   selectionAddress,
                   null, null, null, null, null);
           c.moveToFirst();
           mCount = c.getCount();
           sCentersAddress = new String[mCount];
           for (int i = 0; i<mCount ; i++){
               sCentersAddress[i] = c.getString(0);
               c.moveToNext();
           }

           String[] selectionPhone = new String[]{DonorContract.CentersEntry.COLUMN_PHONE};
           c = donorDbHelper.getReadableDatabase().query(DonorContract.CentersEntry.TABLE_NAME,
                   selectionPhone,
                   null, null, null, null, null);
           c.moveToFirst();
           mCount = c.getCount();
           sCentersPhone = new String[mCount];
           for (int i = 0; i<mCount ; i++){
               sCentersPhone[i] = c.getString(0);
               c.moveToNext();
           }

           c.close();
       }
    }

    public static String getMobileNum(String mobile){
        mobile = mobile.replace(" ","");
        mobile = mobile.replace("-","");
        mobile = mobile.replace("(","");
        mobile = "tel:" + mobile.replace(")","");
        return mobile;
    }

    public static boolean checkConnection(Context context){
        boolean connection = false;
        if (context != null){
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo i = cm.getActiveNetworkInfo();
            try {
                connection = i != null || i.isConnected() || !i.isAvailable();
            } catch (NullPointerException e){
                connection = false;
            }
        }
        return connection;
    }
}
