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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ua.andriyantonov.donorua.data.DonorContract.CentersEntry;
import ua.andriyantonov.donorua.data.DonorContract.RecipientsEntry;

import static ua.andriyantonov.donorua.data.DonorContract.*;

/**
 * Manages a local database for donors data.
 */
public class DonorDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "donor.db";

    public DonorDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_CITIES_TABLE = "CREATE TABLE " + CitiesEntry.TABLE_NAME + " (" +
                CitiesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CitiesEntry.COLUMN_CITY_ID + " INTEGER NOT NULL," +
                CitiesEntry.COLUMN_NAME + " TEXT NOT NULL," +
                CitiesEntry.COLUMN_REGION + " INTEGER NOT NULL, " +
                "UNIQUE ( " + CitiesEntry.COLUMN_CITY_ID + " ) ON CONFLICT REPLACE);";

        final String SQL_CREATE_CENTERS_TABLE = " CREATE TABLE " + CentersEntry.TABLE_NAME + " ("+
                CentersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CentersEntry.COLUMN_CENTER_ID + " INTEGER NOT NULL," +
                CentersEntry.COLUMN_CITY_KEY + " INTEGER NOT NULL," +
                CentersEntry.COLUMN_ADDRESS + " TEXT NOT NULL," +
                CentersEntry.COLUMN_CENTER_NAME + " TEXT NOT NULL," +
                CentersEntry.COLUMN_LONG + " REAL NOT NULL," +
                CentersEntry.COLUMN_LAT + " REAL NOT NULL," +
                CentersEntry.COLUMN_DESC + " TEXT," +
                CentersEntry.COLUMN_WEBSITE + " TEXT," +
                CentersEntry.COLUMN_PHONE + " TEXT," +
                CentersEntry.COLUMN_EMAIL + " TEXT," +

                " FOREIGN KEY (" + CentersEntry.COLUMN_CITY_KEY + ") REFERENCES " +
                CitiesEntry.TABLE_NAME + " (" + CitiesEntry._ID +"), " +
                " UNIQUE (" + CentersEntry.COLUMN_CENTER_ID + ") ON CONFLICT REPLACE );";

        final String SQL_CREATE_RECIPIENTS_TABLE = " CREATE TABLE " + RecipientsEntry.TABLE_NAME + " (" +
                RecipientsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                // the ID of the location entry associated with this weather data
                RecipientsEntry.COLUMN_RECIPIENT_ID + " INTEGER NOT NULL ," +
                RecipientsEntry.COLUMN_CENTER_KEY + " INTEGER NOT NULL, " +
                RecipientsEntry.COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                RecipientsEntry.COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                RecipientsEntry.COLUMN_BIRTH_DAY + " TEXT NOT NULL, " +
                RecipientsEntry.COLUMN_BLOOD_GROUP_ID + " INTEGER NOT NULL," +
                RecipientsEntry.COLUMN_DONATION_TYPE + " INTEGER NOT NULL, " +
                RecipientsEntry.COLUMN_DISEASE + " TEXT NOT NULL, " +
                RecipientsEntry.COLUMN_PHOTO_IMAGE + " TEXT NOT NULL, " +
                RecipientsEntry.COLUMN_CONTACT_PERSON + " TEXT NOT NULL, " +
                RecipientsEntry.COLUMN_CONTACT_PHONE + " TEXT NOT NULL, " +
                RecipientsEntry.COLUMN_DESC + " TEXT NOT NULL, " +
                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + RecipientsEntry.COLUMN_CENTER_KEY + ") REFERENCES " +
                CentersEntry.TABLE_NAME + " (" + CentersEntry._ID +" ), " +
                " UNIQUE (" + RecipientsEntry.COLUMN_RECIPIENT_ID + ") ON CONFLICT REPLACE );";


        sqLiteDatabase.execSQL(SQL_CREATE_CENTERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RECIPIENTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CITIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CentersEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipientsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CitiesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
