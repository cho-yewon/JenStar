package org.techtown.jenstar.marker;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.techtown.jenstar.R;
import org.techtown.jenstar.company.CompanyAddPageActivity;
import org.techtown.jenstar.database.MarkerDBHelper;

public class MarkerModifyActivity extends AppCompatActivity {

    private Uri imageUri;

    EditText titleEditText, snippetEditText, latEditText, lngEditText;
    Button closeBtn, modifyImageBtn, modifyBtn, deleteBtn;
    ImageView markerImage;

    MarkerDBHelper markerDBHelper;
    CompanyAddPageActivity companyAddPageActivity;

    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri;
                    markerImage.setImageURI(imageUri);
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_modify);

        markerDBHelper = new MarkerDBHelper(this);
        companyAddPageActivity = new CompanyAddPageActivity();

        markerImage = findViewById(R.id.marker_image);
        modifyImageBtn = findViewById(R.id.modify_photo_btn_upload);

        titleEditText = findViewById(R.id.MarkerTitle);
        snippetEditText = findViewById(R.id.MarkerSnippet);
        latEditText = findViewById(R.id.MarkerLat);
        lngEditText = findViewById(R.id.MarkerLng);

        closeBtn = findViewById(R.id.close_button);
        modifyBtn = findViewById(R.id.modify_button);
        deleteBtn = findViewById(R.id.delete_button);

        String markerTitle = getIntent().getStringExtra("markerTitle");
        String userId = getIntent().getStringExtra("userId");

        companyAddPageActivity.loadImageFromFirebase(this, markerTitle, markerImage);

        if (markerTitle != null) {
            MarkerDBHelper.Marker marker = markerDBHelper.getMarkerById(markerTitle);
            if (marker != null) {
                titleEditText.setText(marker.title);
                snippetEditText.setText(marker.snippet);
                latEditText.setText(String.valueOf(marker.lat));
                lngEditText.setText(String.valueOf(marker.lng));
            }
        }

        modifyImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContent.launch("image/*");
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEditText.getText().toString().trim();
                String snippet = snippetEditText.getText().toString().trim();
                String lat = latEditText.getText().toString().trim();
                String lng = lngEditText.getText().toString().trim();

                boolean isUpdate = markerDBHelper.updateMarker(userId, title, snippet, lat, lng);

                if (title.isEmpty() || snippet.isEmpty() || lat.isEmpty() || lng.isEmpty()) {
                    Toast.makeText(MarkerModifyActivity.this, "정보를 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(isUpdate) {
                        companyAddPageActivity.uploadImageToFirebase(MarkerModifyActivity.this, title, imageUri);
                        finish();
                    }
                    else {
                        Toast.makeText(MarkerModifyActivity.this, "업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEditText.getText().toString().trim();

                boolean isUpdate = markerDBHelper.deleteMarker(userId, title);

                if(isUpdate) {
                    Toast.makeText(MarkerModifyActivity.this, "삭제에 성공했습니다.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
                else {
                    Toast.makeText(MarkerModifyActivity.this, "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
