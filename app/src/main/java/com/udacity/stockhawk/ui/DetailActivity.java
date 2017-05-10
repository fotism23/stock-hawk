package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.Utility;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] STOCK_DETAIL_PROJECTION = {
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_HISTORY,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE
    };

    private static final int INDEX_SYMBOL = 0;
    private static final int INDEX_HISTORY = 1;
    private static final int INDEX_PRICE = 2;
    private static final int INDEX_ABSOLUTE_CHANGE = 3;
    private static final int INDEX_PERCENTAGE_CHANGE = 4;

    private static final int ID_DETAIL_LOADER = 353;

    private Uri mUri;

    @BindView(R.id.detail_price)
    TextView detailPriceTextView;
    @BindView(R.id.detail_symbol)
    TextView detailSymbolTextView;
    @BindView(R.id.chart)
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        getUri();
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }

    private void getUri() {
        if (!getIntent().hasExtra(getString(R.string.extra_stock_selected)))
            throw new NullPointerException("Uri Intent Extra cannot be null.");
        String uriString = getIntent().getStringExtra(getString(R.string.extra_stock_selected));
        mUri = Uri.parse(uriString);
        if (null == mUri)
            throw new NullPointerException("Uri for Stock details cannot be null.");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        mUri,
                        STOCK_DETAIL_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case ID_DETAIL_LOADER:
                showData(data);
                break;
        }
    }

    private void showData(Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
                    /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }
        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }

        detailSymbolTextView.setText(data.getString(INDEX_SYMBOL));
        detailPriceTextView.setText(Utility.getFormattedPrice(data.getString(INDEX_PRICE)));

        String[] history = data.getString(INDEX_HISTORY).split("\n");
        String absoluteChange = data.getString(INDEX_ABSOLUTE_CHANGE);
        String percentageChange = data.getString(INDEX_PERCENTAGE_CHANGE);

        fillGraph(history);
    }

    private void fillGraph(String[] history) {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();

        int count = 0;
        for (String s : history) {
            float value = Float.parseFloat(s.split(",")[1]);
            dates.add(Utility.getFormattedDate(Long.parseLong(s.split(",")[0])));
            entries.add(new Entry(value, count));
            count++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        LineData data = new LineData(dataSet);
        dataSet.setDrawCircles(true);
        dataSet.setDrawFilled(true);
        chart.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
