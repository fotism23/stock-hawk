package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

public class StockWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                 /* Do Nothing */
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(Contract.Quote.URI,
                        new String[]{Contract.Quote._ID, Contract.Quote.COLUMN_SYMBOL, Contract.Quote.COLUMN_PRICE, Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
                                Contract.Quote.COLUMN_PERCENTAGE_CHANGE, Contract.Quote.COLUMN_HISTORY},
                        null,
                        null,
                        null
                );
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);
                views.setTextViewText(R.id.widget_symbol, data.getString(Contract.Quote.POSITION_SYMBOL));
                views.setTextViewText(R.id.widget_price, data.getString(Contract.Quote.POSITION_PRICE));

                float percentageChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
                String percentageChangeStr = Float.toString(percentageChange);

                if (percentageChange > 0) {
                    views.setInt(R.id.widget_change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                    percentageChangeStr = "+" + percentageChangeStr;
                } else {
                    views.setInt(R.id.widget_change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }

                views.setTextViewText(R.id.widget_change, percentageChangeStr);

                final Intent fillInIntent = new Intent();
                Uri stockUri = Contract.Quote.makeUriForStock(data.getString(Contract.Quote.POSITION_SYMBOL));
                fillInIntent.putExtra(getString(R.string.extra_stock_selected), stockUri.toString());
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
