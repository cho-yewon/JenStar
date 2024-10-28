package org.techtown.jenstar.marker;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.techtown.jenstar.database.MarkerDBHelper;
import org.techtown.jenstar.R;

public class MarkerDetailActivity extends AppCompatActivity {
    private MarkerDBHelper markerDBHelper;
    private TextView titleTextView, snippetTextView, latLngTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_detail);

        titleTextView = findViewById(R.id.detailTitle);
        snippetTextView = findViewById(R.id.detailSnippet);
        latLngTextView = findViewById(R.id.detailLatLng);

        markerDBHelper = new MarkerDBHelper(this);


        String markerTitle = getIntent().getStringExtra("marker_title");
        if (markerTitle != null) {
            MarkerDBHelper.Marker marker = markerDBHelper.getMarkerById(markerTitle);
            if (marker != null) {
                titleTextView.setText(marker.title);
                snippetTextView.setText(marker.snippet);
                latLngTextView.setText(marker.lat + ", " + marker.lng);
            }
        }
    }
}
