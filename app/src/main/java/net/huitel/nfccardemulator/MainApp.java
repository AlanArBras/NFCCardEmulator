package net.huitel.nfccardemulator;

import android.app.Application;
import android.content.Context;
import android.nfc.NfcAdapter;

/**
 * Created by Alan on 13/07/2016.
 * Application object so we can have common resources between Activities (such as DAOs to access tables in SQLite database)
 */
public class MainApp extends Application {
    private static Context mContext;

    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MainApp.mContext = getApplicationContext();
    }
}
