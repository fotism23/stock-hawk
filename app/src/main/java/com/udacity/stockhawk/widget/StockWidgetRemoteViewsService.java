package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
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

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(Contract.Quote.URI,
                        new String[]{Contract.Quote._ID,Contract.Quote.COLUMN_SYMBOL, Contract.Quote.COLUMN_PRICE,
                                Contract.Quote.COLUMN_PERCENTAGE_CHANGE},
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
                        R.layout.list_item_quote);
                views.setTextViewText(R.id.symbol, data.getString(Contract.Quote.POSITION_SYMBOL));
                views.setTextViewText(R.id.price, data.getString(Contract.Quote.POSITION_SYMBOL));

                float percentageChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
                if (percentageChange > 0) {
                    views.setInt(R.id.change, "setBackgroundResource",  R.drawable.percent_change_pill_green);
                } else {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }

                views.setTextViewText(R.id.change, Float.toString(percentageChange));

                final Intent fillInIntent = new Intent();
                Uri stockUri = Contract.Quote.makeUriForStock(data.getString(Contract.Quote.POSITION_SYMBOL));
                fillInIntent.setData(stockUri);
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
                return false;
            }
        };
    }
}
