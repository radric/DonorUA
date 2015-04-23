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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Defines table and column names for the donor database.
 */
public class DonorContract {

    public final static String CONTENT_AUTHORITY = "ua.andriyantonov.donorua";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public final static String PATH_CITIES = "cities";
    public final static String PATH_CENTERS = "centers";
    public final static String PATH_RECIPIENTS = "recipients";

    /*
        Inner class that defines the table contents of the location table
        Students: This is where you will add the strings.  (Similar to what has been
        done for WeatherEntry)
     */
    public static final class CentersEntry implements BaseColumns {

        public final static Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CENTERS).build();
        public final static String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CENTERS;
        public final static String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CENTERS;

        public static final String TABLE_NAME = "centers";
        public static final String COLUMN_CENTER_ID = "center_id";
        public static final String COLUMN_CITY_KEY = "city_id";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_CENTER_NAME = "center_name";
        public static final String COLUMN_LONG = "coord_long";
        public static final String COLUMN_LAT = "coord_lat";
        public static final String COLUMN_DESC = "center_desc";
        public static final String COLUMN_WEBSITE = "center_web";
        public static final String COLUMN_EMAIL = "center_email";
        public static final String COLUMN_PHONE = "center_phone";

        public static Uri buildCenterUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     *  Inner class that defines the table contents of the recipients table
     */
    public static final class RecipientsEntry implements BaseColumns {

        public final static Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPIENTS).build();
        public final static String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPIENTS;
        public final static String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPIENTS;

        public static final String TABLE_NAME = "recipients";
        public static final String COLUMN_RECIPIENT_ID = "recipient_id";
        public static final String COLUMN_CENTER_KEY = "donor_center_id";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_BIRTH_DAY = "birth_day";
        public static final String COLUMN_BLOOD_GROUP_ID = "blood_group_id";
        public static final String COLUMN_DONATION_TYPE = "donation_type_id";
        public static final String COLUMN_DISEASE = "disease";
        public static final String COLUMN_PHOTO_IMAGE = "photo_image";
        public static final String COLUMN_CONTACT_PERSON = "contact_person";
        public static final String COLUMN_CONTACT_PHONE = "person_phone";
        public static final String COLUMN_DESC = "description";
        public static final String COLUMN_NEEDED_TYPES = "needed_types";

        public static Uri buildRecipientUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class CitiesEntry implements BaseColumns{

        public final static Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CITIES).build();
        public final static String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CITIES;
        public final static String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CITIES;

        public static final String TABLE_NAME = "cities";
        public static final String COLUMN_CITY_ID = "city_id";
        public static final String COLUMN_NAME = "city_name";
        public static final String COLUMN_REGION = "region_id";

        public static Uri buildCityUri (long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
