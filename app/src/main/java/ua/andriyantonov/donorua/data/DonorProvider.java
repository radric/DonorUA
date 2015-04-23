package ua.andriyantonov.donorua.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;


import static ua.andriyantonov.donorua.data.DonorContract.*;

public class DonorProvider extends ContentProvider {

    private static final String LOG_TAG = "CONTENT PROVIDER";

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private DonorDbHelper mDonorDBHelper;

    static final int RECIPIENTS = 100;
    static final int RECIPIENT_ID = 101;
    static final int CENTERS = 200;
    static final int CENTER_ID = 201;
    static final int CITIES = 300;
    static final int CITY_ID = 301;

    private static final SQLiteQueryBuilder sRecipientWithCenterQueryBuilder;

    static{
        sRecipientWithCenterQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //recipient INNER JOIN centers ON recipients.centers_id = centers._id
        sRecipientWithCenterQueryBuilder.setTables(
                RecipientsEntry.TABLE_NAME + " INNER JOIN " +
                        CentersEntry.TABLE_NAME +
                        " ON " + RecipientsEntry.TABLE_NAME +
                        "." + RecipientsEntry.COLUMN_CENTER_KEY +
                        " = " + CentersEntry.TABLE_NAME +
                        "." + CentersEntry.COLUMN_CENTER_ID);
    }

    private Cursor getAllRecipientsWithCenter
            (Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){

        Utils.loadUserCityId(getContext());
        Utils.loadUserBloodGroup(getContext());
        String userBloodType = String.valueOf(Utils.sUserBloodType);
        String userCityId = Utils.sUserCityId;

        if (!userCityId.isEmpty()) {
            selection = CentersEntry.TABLE_NAME + "." + CentersEntry.COLUMN_CITY_KEY +
                    " = " + userCityId + " AND " +
                    RecipientsEntry.TABLE_NAME + "." + RecipientsEntry.COLUMN_BLOOD_GROUP_ID +
                    " = " + userBloodType;
        }

        return sRecipientWithCenterQueryBuilder.query(
                mDonorDBHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getCurrentRecipientWithCenter
            (Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){

        String id = uri.getLastPathSegment();
        selection = RecipientsEntry.TABLE_NAME + "." + RecipientsEntry._ID + " = " + id;

        return sRecipientWithCenterQueryBuilder.query(
                mDonorDBHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DonorContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PATH_RECIPIENTS, RECIPIENTS);
        matcher.addURI(authority, PATH_RECIPIENTS + "/#", RECIPIENT_ID);

        matcher.addURI(authority, PATH_CENTERS, CENTERS);
        matcher.addURI(authority, PATH_CENTERS + "/#", CENTER_ID);

        matcher.addURI(authority, PATH_CITIES, CITIES);
        matcher.addURI(authority, PATH_CITIES + "/#", CITY_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDonorDBHelper = new DonorDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder){
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case RECIPIENTS:
            {
                retCursor = getAllRecipientsWithCenter(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case RECIPIENT_ID:
            {
                retCursor = getCurrentRecipientWithCenter(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case CENTERS:
            {
                retCursor = mDonorDBHelper.getReadableDatabase().query(
                        CentersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CITIES:
            {
                retCursor = mDonorDBHelper.getReadableDatabase().query(
                        CitiesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }



    @Override
    public Uri insert(Uri uri, ContentValues values){
        Log.d(LOG_TAG, "insert, " + uri.toString());

        final SQLiteDatabase db = mDonorDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;
        long _id;

        switch (match){
            case RECIPIENTS:
                _id = db.insert(RecipientsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = RecipientsEntry.buildRecipientUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case CENTERS:
                _id = db.insert(CentersEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = CentersEntry.buildCenterUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case CITIES:
                _id = db.insert(CitiesEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = CitiesEntry.buildCityUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete (Uri uri, String selection, String[] selectionArgs){
        Log.d(LOG_TAG, "delete, " + uri.toString());
        final SQLiteDatabase db = mDonorDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if (null == selection) selection = "1";
        switch (match){
            case RECIPIENTS:
                rowsDeleted = db.delete(RecipientsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CENTERS:
                rowsDeleted = db.delete(CentersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CITIES:
                rowsDeleted = db.delete(CitiesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDonorDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match){
            case RECIPIENTS:
                rowsUpdated = db.update(RecipientsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CENTERS:
                rowsUpdated = db.update(CentersEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CITIES:
                rowsUpdated = db.update(CitiesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case RECIPIENTS:
                return RecipientsEntry.CONTENT_TYPE;
            case RECIPIENT_ID:
                return RecipientsEntry.CONTENT_ITEM_TYPE;
            case CENTERS:
                return CentersEntry.CONTENT_TYPE;
            case CENTER_ID:
                return CentersEntry.CONTENT_ITEM_TYPE;
            case CITIES:
                return CitiesEntry.CONTENT_TYPE;
            case CITY_ID:
                return CitiesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDonorDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case RECIPIENTS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RecipientsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case CENTERS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(CentersEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case CITIES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(CitiesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mDonorDBHelper.close();
        super.shutdown();
    }
}