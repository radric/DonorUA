package ua.andriyantonov.donorua.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import ua.andriyantonov.donorua.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

import static ua.andriyantonov.donorua.data.DonorContract.*;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your WeatherContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {
    static final String TEST_LOCATION = "99705";

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: Use this to create some default weather values for your database tests.
     */
    static ContentValues createRecipientsValues(long locationRowId) {
        ContentValues recipientValues = new ContentValues();
        recipientValues.put(RecipientsEntry.COLUMN_CENTER_KEY, locationRowId);
        recipientValues.put(RecipientsEntry.COLUMN_LAST_NAME, "тест");
        recipientValues.put(RecipientsEntry.COLUMN_FIRST_NAME, "тест");
        recipientValues.put(RecipientsEntry.COLUMN_BLOOD_GROUP_ID, 1);
        recipientValues.put(RecipientsEntry.COLUMN_DONATION_TYPE, 2);
        recipientValues.put(RecipientsEntry.COLUMN_DISEASE, "тест");
        recipientValues.put(RecipientsEntry.COLUMN_PHOTO_IMAGE, "тест");
        recipientValues.put(RecipientsEntry.COLUMN_CONTACT_PERSON, "тест");
        recipientValues.put(RecipientsEntry.COLUMN_CONTACT_PHONE, "тест");
        recipientValues.put(RecipientsEntry.COLUMN_DESC, "тест");

        return recipientValues;
    }

    /*
        Students: You can uncomment this helper function once you have finished creating the
        LocationEntry part of the WeatherContract.
     */
    static ContentValues createCentersValues() {
        // Create a new map of values, where column names are the keys
        ContentValues centerValues = new ContentValues();
        centerValues.put(CentersEntry.COLUMN_CITY_KEY, TEST_LOCATION);
        centerValues.put(CentersEntry.COLUMN_CITY_NAME, "test");
        centerValues.put(CentersEntry.COLUMN_ADDRESS, "test");
        centerValues.put(CentersEntry.COLUMN_CENTER_NAME, "test");
        centerValues.put(CentersEntry.COLUMN_LONG, -147.353);
        centerValues.put(CentersEntry.COLUMN_LAT, 64.7488);
        centerValues.put(CentersEntry.COLUMN_DESC, "test");
        centerValues.put(CentersEntry.COLUMN_WEBSITE, "test");
        centerValues.put(CentersEntry.COLUMN_EMAIL, "test");
        centerValues.put(CentersEntry.COLUMN_PHONE, "test");

        return centerValues;
    }
    static ContentValues createCitiesValues() {
        // Create a new map of values, where column names are the keys
        ContentValues citiesValues = new ContentValues();
        citiesValues.put(CitiesEntry.COLUMN_NAME, "test");
        citiesValues.put(CitiesEntry.COLUMN_REGION, 1);

        return citiesValues;
    }

    /*
        Students: You can uncomment this function once you have finished creating the
        LocationEntry part of the WeatherContract as well as the WeatherDbHelper.
     */
    static long insertNorthPoleLocationValues(Context context) {
        // insert our test records into the database
        DonorDbHelper dbHelper = new DonorDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createCentersValues();

        long locationRowId;
        locationRowId = db.insert(CentersEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
