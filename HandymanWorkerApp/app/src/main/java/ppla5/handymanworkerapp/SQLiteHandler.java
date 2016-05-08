package ppla5.handymanworkerapp;

/**
 * Created by ASUS on 3/26/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "handyman";

    // Login table name
    private static final String TABLE_USER = "worker";

    // Login Table Columns names
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_TAG = "tag";
    private static final String KEY_RATING = "rating";
    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_USERNAME + " varchar(255) PRIMARY KEY,"
                + KEY_PASSWORD + " text," + KEY_NAME + " varchar(255),"
                + KEY_PHOTO + " text," + KEY_ADDRESS + " text,"
                + KEY_LATITUDE + " double," + KEY_LONGITUDE + " double,"
                + KEY_TAG + " text," + KEY_RATING + " float"
                +")";
        db.execSQL(CREATE_LOGIN_TABLE);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String username, String password, String name, String photo, String address, double latitude, double longitude, String tag, double rating) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username); // email
        values.put(KEY_PASSWORD, password); // Password
        values.put(KEY_NAME, name); // Person name
        values.put(KEY_PHOTO, photo); // photo
        values.put(KEY_ADDRESS, address); // address
        values.put(KEY_LATITUDE, latitude); // latitude
        values.put(KEY_LONGITUDE, longitude); // longitude
        values.put(KEY_TAG, tag); // tag
        values.put(KEY_RATING, rating); // rating


        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("username", cursor.getString(1));
            user.put("password", cursor.getString(2));
            user.put("name", cursor.getString(3));
            user.put("photo", cursor.getString(4));
            user.put("address", cursor.getString(5));
            user.put("latitude", cursor.getString(6));
            user.put("longitude", cursor.getString(7));
            user.put("tag", cursor.getString(8));
            user.put("rating", cursor.getString(9));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}
