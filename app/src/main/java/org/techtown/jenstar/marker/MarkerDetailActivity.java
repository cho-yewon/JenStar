package org.techtown.jenstar.marker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.techtown.jenstar.company.CompanyAddPageActivity;
import org.techtown.jenstar.database.DBHelper;
import org.techtown.jenstar.database.MarkerDBHelper;
import org.techtown.jenstar.R;

public class MarkerDetailActivity extends AppCompatActivity {
    private MarkerDBHelper markerDBHelper;
    private DBHelper dbHelper;
    private TextView titleTextView, snippetTextView, latLngTextView;
    private Button closeBtn, modifyBtn;
    private String markerTitle;
    private String userId;

    CompanyAddPageActivity companyAddPageActivity;
    ImageView markerImage;

    // 삭제 후 자신도 종료
    private final ActivityResultLauncher<Intent> modifyActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    finish();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_detail);

        companyAddPageActivity = new CompanyAddPageActivity();

        titleTextView = findViewById(R.id.detailTitle);
        snippetTextView = findViewById(R.id.detailSnippet);
        latLngTextView = findViewById(R.id.detailLatLng);
        closeBtn = findViewById(R.id.closeButton);
        markerImage = findViewById(R.id.marker_image);
        modifyBtn = findViewById(R.id.modifyButton);

        markerDBHelper = new MarkerDBHelper(this);
        dbHelper = new DBHelper(this);


        markerTitle = getIntent().getStringExtra("marker_title");
        userId = getIntent().getStringExtra("userId");

        int authority = dbHelper.checkAuthority(userId);

        if(authority == 1) {
            modifyBtn.setVisibility(View.GONE);
        }
        else {
            modifyBtn.setVisibility(View.VISIBLE);
        }

        loadMarkerDetails();

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MarkerDetailActivity.this, MarkerModifyActivity.class);
                intent.putExtra("markerTitle", markerTitle);
                intent.putExtra("userId", userId);
                modifyActivityResultLauncher.launch(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity가 다시 보일 때 마다 데이터 갱신
        loadMarkerDetails();
    }

    private void loadMarkerDetails() {
        if (markerTitle != null) {
            MarkerDBHelper.Marker marker = markerDBHelper.getMarkerById(markerTitle);
            if (marker != null) {
                companyAddPageActivity.loadImageFromFirebase(this, markerTitle, markerImage);
                titleTextView.setText(marker.title);
                snippetTextView.setText(marker.snippet);
                latLngTextView.setText(marker.lat + ", " + marker.lng);
            }
        }
    }
}
