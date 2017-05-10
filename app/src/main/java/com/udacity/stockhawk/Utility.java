package com.udacity.stockhawk;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.preference.PreferenceManager;

import com.udacity.stockhawk.sync.QuoteSyncJob;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public final class Utility {
    @SuppressWarnings("ResourceType")
    static public @QuoteSyncJob.StockStatus
    int getStockStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_stock_status_key), QuoteSyncJob.STOCK_STATUS_UNKNOWN);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static String getFormattedPrice(String price) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            DecimalFormat df = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            df.setMaximumFractionDigits(2);
            return df.format(price);
        }
        return price;
    }

    public static String getFormattedDate(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return formatter.format(calendar.getTime());
    }
}
