/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2015, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 * For questions related to commercial use licensing, please contact sales@telestax.com.
 *
 */

package org.restcomm.android.olympus;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.restcomm.android.olympus.DatabaseContract.DatabaseVersions;

public class DatabaseHelper extends SQLiteOpenHelper {
   // If you change the database schema, you must increment the database version.
   public static final int DATABASE_VERSION = DatabaseContract.DatabaseVersions.DB_VERSION_DELIVERY_STATUS;
   public static final String DATABASE_NAME = "Olympus.db";

   private static final String TAG = "DatabaseHelper";

   //private static final String TEXT_TYPE = " TEXT";
   //private static final String COMMA_SEP = ",";
   private static final String SQL_CREATE_CONTACT_TABLE =
         "CREATE TABLE " + DatabaseContract.ContactEntry.TABLE_NAME + " (" +
               DatabaseContract.ContactEntry._ID + " INTEGER PRIMARY KEY," +
               DatabaseContract.ContactEntry.COLUMN_NAME_NAME + " TEXT NOT NULL UNIQUE, " +
               DatabaseContract.ContactEntry.COLUMN_NAME_URI + " TEXT NOT NULL" +
               " );";

   private static final String SQL_CREATE_MESSAGE_TABLE =
         "CREATE TABLE " + DatabaseContract.MessageEntry.TABLE_NAME + " (" +
               DatabaseContract.MessageEntry._ID + " INTEGER PRIMARY KEY," +
               DatabaseContract.MessageEntry.COLUMN_NAME_CONTACT_ID + " INTEGER, " +
               DatabaseContract.MessageEntry.COLUMN_NAME_JOB_ID + " TEXT, " +
               DatabaseContract.MessageEntry.COLUMN_NAME_TEXT + " TEXT NOT NULL, " +
               DatabaseContract.MessageEntry.COLUMN_NAME_TYPE + " TEXT NOT NULL, " +
               DatabaseContract.MessageEntry.COLUMN_NAME_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
               DatabaseContract.MessageEntry.COLUMN_NAME_DELIVERY_STATUS + " INTEGER DEFAULT 1, " +   // using default of 'success', so that DB upgrades work smoothly
               "FOREIGN KEY (" + DatabaseContract.MessageEntry.COLUMN_NAME_CONTACT_ID + ") REFERENCES " + DatabaseContract.ContactEntry.TABLE_NAME +
                  "(" + DatabaseContract.ContactEntry._ID + ") " +
               " );";

   // Upgrades statements
   private static final String SQL_UPGRADE_GROUND_ZERO_2_DELIVERY_STATUS_MESSAGES_1 =
           "ALTER TABLE " + DatabaseContract.MessageEntry.TABLE_NAME + " ADD COLUMN " +
                   DatabaseContract.MessageEntry.COLUMN_NAME_JOB_ID + " TEXT;";
   private static final String SQL_UPGRADE_GROUND_ZERO_2_DELIVERY_STATUS_MESSAGES_2 =
           "ALTER TABLE " + DatabaseContract.MessageEntry.TABLE_NAME + " ADD COLUMN " +
                   DatabaseContract.MessageEntry.COLUMN_NAME_DELIVERY_STATUS + " INTEGER DEFAULT 1;";


   private static final String SQL_DELETE_CONTACT_ENTRIES =
         "DROP TABLE IF EXISTS " + DatabaseContract.ContactEntry.TABLE_NAME;
   private static final String SQL_DELETE_MESSAGE_ENTRIES =
         "DROP TABLE IF EXISTS " + DatabaseContract.MessageEntry.TABLE_NAME;

   // Android context
   Context context;

   public DatabaseHelper(Context context)
   {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
      Log.i(TAG, "DatabaseHelper constructor");
      this.context = context;
   }

   public void onCreate(SQLiteDatabase db)
   {
      Log.i(TAG, "Creating table contact: " + SQL_CREATE_CONTACT_TABLE);
      db.execSQL(SQL_CREATE_CONTACT_TABLE);
      Log.i(TAG, "Creating table message: " + SQL_CREATE_MESSAGE_TABLE);
      db.execSQL(SQL_CREATE_MESSAGE_TABLE);
      populateSampleEntries(db);
   }

   // Perform any updates if necessary. Remember that as more upgrade points get added we need to make sure that depending
   // on the oldVersion all intermediate upgrades need to be applied
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
   {
      Log.i(TAG, "onUpgrade from: "+ oldVersion + ", to: " + newVersion);
      switch(oldVersion) {
         case DatabaseVersions.DB_VERSION_GROUND_ZERO:
            if (newVersion== DatabaseVersions.DB_VERSION_DELIVERY_STATUS) {
               // we need to alter message table to add job_id and delivery status columns
               Log.i(TAG, "Upgrading table messages: " + DatabaseContract.MessageEntry.TABLE_NAME);
               Log.d(TAG, "Applying SQL command: " + SQL_UPGRADE_GROUND_ZERO_2_DELIVERY_STATUS_MESSAGES_1);
               db.execSQL(SQL_UPGRADE_GROUND_ZERO_2_DELIVERY_STATUS_MESSAGES_1);
               Log.d(TAG, "Applying SQL command: " + SQL_UPGRADE_GROUND_ZERO_2_DELIVERY_STATUS_MESSAGES_2);
               db.execSQL(SQL_UPGRADE_GROUND_ZERO_2_DELIVERY_STATUS_MESSAGES_2);
            }
         //case DatabaseVersions.DB_VERSION_DELIVERY_STATUS:
         //   ;
      }

      //db.execSQL(SQL_DELETE_CONTACT_ENTRIES);
      //db.execSQL(SQL_DELETE_MESSAGE_ENTRIES);
      //onCreate(db);
   }

   public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
   {
      onUpgrade(db, oldVersion, newVersion);
   }

   // ---- Helpers
   // Populate DB with sample contacts, targeting Restcomm sample applications
   private void populateSampleEntries(SQLiteDatabase db)
   {
      // TODO: used to get exceptions when using this
      // Gets the data repository in write mode
      //SQLiteDatabase db = this.getWritableDatabase();

      db.beginTransaction();
      for (String s: context.getResources().getStringArray(R.array.demo_apps)) {
         String[] parts = s.split(", *");
         String name = parts[0];
         String uri = parts[1];

         // Create a new map of values, where column names are the keys
         ContentValues values = new ContentValues();

         values.put(DatabaseContract.ContactEntry.COLUMN_NAME_NAME, name);
         values.put(DatabaseContract.ContactEntry.COLUMN_NAME_URI, uri);
         db.insert(DatabaseContract.ContactEntry.TABLE_NAME, null, values);
      }
      // veeery important, otherwise transaction is deemed failed and is rolled back
      db.setTransactionSuccessful();
      db.endTransaction();

      //db.close();

      Log.i(TAG, "Populated sample entries");
   }
}
