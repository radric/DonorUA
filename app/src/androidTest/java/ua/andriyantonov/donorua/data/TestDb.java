/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ua.andriyantonov.donorua.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

import static ua.andriyantonov.donorua.data.DonorContract.*;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(DonorDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(CentersEntry.TABLE_NAME);
        tableNameHashSet.add(RecipientsEntry.TABLE_NAME);
        tableNameHashSet.add(CitiesEntry.TABLE_NAME);

        mContext.deleteDatabase(DonorDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new DonorDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // centers table
        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + CentersEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> centersColumnHashSet = new HashSet<String>();
        centersColumnHashSet.add(CentersEntry._ID);
        centersColumnHashSet.add(CentersEntry.COLUMN_CITY_KEY);
        centersColumnHashSet.add(CentersEntry.COLUMN_CITY_NAME);
        centersColumnHashSet.add(CentersEntry.COLUMN_CENTER_NAME);
        centersColumnHashSet.add(CentersEntry.COLUMN_ADDRESS);
        centersColumnHashSet.add(CentersEntry.COLUMN_LONG);
        centersColumnHashSet.add(CentersEntry.COLUMN_LAT);
        centersColumnHashSet.add(CentersEntry.COLUMN_DESC);
        centersColumnHashSet.add(CentersEntry.COLUMN_WEBSITE);
        centersColumnHashSet.add(CentersEntry.COLUMN_EMAIL);
        centersColumnHashSet.add(CentersEntry.COLUMN_PHONE);

        int columnNameIndexCenter = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndexCenter);
            centersColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                centersColumnHashSet.isEmpty());

        // cities table
        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + CitiesEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> citiesColumnHashSet = new HashSet<String>();
        citiesColumnHashSet.add(CitiesEntry._ID);
        citiesColumnHashSet.add(CitiesEntry.COLUMN_NAME);
        citiesColumnHashSet.add(CitiesEntry.COLUMN_REGION);

        int columnNameIndexCity = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndexCity);
            citiesColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                citiesColumnHashSet.isEmpty());

        // recipients table
        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + RecipientsEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> recipientsColumnHashSet = new HashSet<String>();
        recipientsColumnHashSet.add(RecipientsEntry._ID);
        recipientsColumnHashSet.add(RecipientsEntry.COLUMN_CENTER_KEY);
        recipientsColumnHashSet.add(RecipientsEntry.COLUMN_LAST_NAME);
        recipientsColumnHashSet.add(RecipientsEntry.COLUMN_FIRST_NAME);
        recipientsColumnHashSet.add(RecipientsEntry.COLUMN_LAST_NAME);
        recipientsColumnHashSet.add(RecipientsEntry.COLUMN_BLOOD_GROUP_ID);
        recipientsColumnHashSet.add(RecipientsEntry.COLUMN_DONATION_TYPE);
        recipientsColumnHashSet.add(RecipientsEntry.COLUMN_DISEASE);
        recipientsColumnHashSet.add(RecipientsEntry.COLUMN_PHOTO_IMAGE);
        recipientsColumnHashSet.add(RecipientsEntry.COLUMN_CONTACT_PERSON);
        recipientsColumnHashSet.add(RecipientsEntry.COLUMN_CONTACT_PHONE);
        recipientsColumnHashSet.add(RecipientsEntry.COLUMN_DESC);

        int columnNameIndexRecipient = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndexRecipient);
            recipientsColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("RECIPIENT TABLE Error: The database doesn't contain all  of the required location entry columns",
                recipientsColumnHashSet.isEmpty());

        db.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public void testCentersTable() {
        insertLocation();

    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testWeatherTable() {

        long centersRowId = insertLocation();
        assertFalse("Error: Location Not Inserted Correctly",centersRowId==-1L);


        DonorDbHelper dbHelper = new DonorDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createRecipientsValues(centersRowId);

        long recipientsRowId = db.insert(RecipientsEntry.TABLE_NAME, null, testValues);

        assertTrue(recipientsRowId!=-1);

        Cursor c = db.query(
                RecipientsEntry.TABLE_NAME,
                null, null, null, null, null,null
        );

        assertTrue("Error: No Records returned from centers query", c.moveToFirst());

        TestUtilities.validateCurrentRecord("testInsertDb recipients failed",c, testValues);

        assertFalse("more than one row", c.moveToNext());

        c.close();
        db.close();
    }

    public void testCitiesTable(){
        DonorDbHelper dbHelper = new DonorDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createCitiesValues();
        long citiesRowId = db.insert(CitiesEntry.TABLE_NAME, null, testValues);

        assertTrue(citiesRowId!=-1);
        Cursor c = db.query(
                CitiesEntry.TABLE_NAME,
                null, null, null, null, null,null
        );

        assertTrue(" no records in this table", c.moveToFirst());

        assertFalse("more than one row in this table", c.moveToNext());

        c.close();
        db.close();
    }


    /*
        Students: This is a helper method for the testWeatherTable quiz. You can move your
        code from testLocationTable to here so that you can call this code from both
        testWeatherTable and testLocationTable.
     */
    public long insertLocation(){
        DonorDbHelper  dbHelper = new DonorDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createCentersValues();

        long centersRowId = db.insert(CentersEntry.TABLE_NAME,null,testValues);

        assertTrue(centersRowId!=-1);

        Cursor c  =db.query(
                CentersEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertTrue("Error: No Records returned from location query", c.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                c, testValues);

        c.close();
        db.close();

        return centersRowId;
    }
}
