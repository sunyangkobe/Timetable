package edu.cmu.cs.vlis.timetable.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/*
 * This class extends a third-party class called "SQLiteAssetHelper", gitHub link:
 * https://github.com/jgilfelt/android-sqlite-asset-helper
 * 
 * This SQLiteAssetHelper helper class helps to manage database creation and version management
 * using an application's raw asset files, and provides developers with a simple way to ship their
 * Android app with an existing SQLite database (which may be pre-populated with data) and to manage
 * its initial creation and any upgrades required with subsequent version releases.
 * 
 * We pre-store this "US_university_metadata" database in the asset directory, and this class will
 * help us to migrate the database to the application database directory
 * (/data/data/<package_name>/<dataabse_name>) when initialized, so we don't need to execute a bunch
 * of SQL insert statements at runtime.
 */

public class USUniversityNameDatabase extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "USUniversity";
    private static final int DATABASE_VERSION = 1;

    public USUniversityNameDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Simply retrieve all records in the table "US_university_names", return a Cursor
    public Cursor getAllUSUniversityNames() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = { "name" };
        String sqlTables = "USUniversityName";

        qb.setTables(sqlTables);
        Cursor cursor = qb.query(db, sqlSelect, null, null, null, null, null);

        cursor.moveToFirst();
        return cursor;
    }
}
