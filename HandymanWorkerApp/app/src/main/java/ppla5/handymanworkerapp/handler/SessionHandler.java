package ppla5.handymanworkerapp.handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
/**
 * Created by ASUS on 3/26/2016.
 */
public class SessionHandler {
    private static String TAG = SessionHandler.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "Login";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_CATEGORY = "category";
    public SessionHandler(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    public void setUsername(String username) {

        editor.putString(KEY_USERNAME, username);

        // commit changes
        editor.commit();

        Log.d(TAG, "Username session has been SET!");
    }

    public String getUsername(){
        return pref.getString(KEY_USERNAME, null);
    }
    public void setCategory(String category){
        editor.putString(KEY_CATEGORY,category);
        // commit changes
        editor.commit();

        Log.d(TAG, "User category session has been SET!");
    }
    public String getCategory(){
        return pref.getString(KEY_CATEGORY, null);
    }
}
