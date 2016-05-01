package ppl.handyman;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by ASUS on 3/26/2016.
 */
public class SessionHandler {
    private static String TAG = SessionHandler.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;
    SQLiteHandler sql;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "Login";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    public SessionHandler(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void setUsername(String username){
        editor.putString("username",username);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
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

    public void setPickedCategory(TreeSet<String> pickedCategory){
        editor.putStringSet("Picked Category",pickedCategory);
        editor.commit();
    }
    public  String[] getPickedCategory(){
        TreeSet<String> set = new TreeSet<>();
        return  pref.getStringSet("Picked Category",set).toArray(new String[set.size()]);
    }
    public Set getCategoryAsSet(){
        return pref.getStringSet("Picked Category",new TreeSet<String>());
    }
}
