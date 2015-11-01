package com.champs21.spellingbee;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by BLACK HAT on 06-May-15.
 */
public class PrefSingleton {

    private static PrefSingleton mInstance;
    private Context mContext;
    //
    private SharedPreferences mMyPreferences;

    private PrefSingleton() {
    }

    public static PrefSingleton getInstance() {
        if (mInstance == null) mInstance = new PrefSingleton();
        return mInstance;
    }

    public void Initialize(Context ctxt) {
        mContext = ctxt;
        mMyPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void savePreference(String key, String value){
        SharedPreferences.Editor e = mMyPreferences.edit();
        e.putString(key, value);
        e.commit();
    }

    public String getPreference(String key)
    {
        String data  = mMyPreferences.getString(key, "");
        return data;
    }

    public boolean containsKey(String key)
    {
        if(mMyPreferences.contains(key))
            return true;
        else
            return false;
    }
}
