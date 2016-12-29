package net.huitel.nfccardemulator.utils;

import android.content.Context;
import android.nfc.NfcAdapter;

import net.huitel.nfccardemulator.MainApp;

/**
 * Created by Alan on 18/07/2016.
 */
public class NFCUtils {

    /**
     * @return true is the NFC adapter is enabled, false otherwise
     */
    public static boolean isNfcEnabled() {
        Context context = MainApp.getAppContext();
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter != null)
            return nfcAdapter.isEnabled();
        else
            return false;
    }
}
