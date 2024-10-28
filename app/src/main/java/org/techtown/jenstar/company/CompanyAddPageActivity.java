package org.techtown.jenstar.company;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.jenstar.database.MarkerDBHelper;
import org.techtown.jenstar.R;

public class CompanyAddPageActivity extends AppCompatActivity {

    Button close_btn, save_btn;
    EditText titleEditText, snippetEditText, latEditText, lngEditText;
    MarkerDBHelper markerDBHelper;

    private static final int PICK_IMAGE_FROM_ALBUM = 0;
    private static FirebaseStorage firebaseStorage;
    private Uri imageUri;
    ImageView markerImage;
    Button addMarkerImage;

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
        initializeFirebaseStorage();
        setContentView(R.layout.company_addmarker_screen);

        String savedID = getIntent().getStringExtra("savedID");

        markerDBHelper = new MarkerDBHelper(this);

        titleEditText = findViewById(R.id.MarkerTitle);
        snippetEditText = findViewById(R.id.MarkerSnippet);
        latEditText = findViewById(R.id.MarkerLat);
        lngEditText = findViewById(R.id.MarkerLng);
        close_btn = findViewById(R.id.close_button);
        save_btn = findViewById(R.id.save_marker);


        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEditText.getText().toString().trim();
                String snippet = snippetEditText.getText().toString().trim();
                String lat = latEditText.getText().toString().trim();
                String lng = lngEditText.getText().toString().trim();

                boolean isAdd = markerDBHelper.addMarker(savedID, title, snippet, lat, lng);

                if (title.isEmpty() || snippet.isEmpty() || lat.isEmpty() || lng.isEmpty()) {
                    Toast.makeText(CompanyAddPageActivity.this, "정보를 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(isAdd) {
                        uploadImageToFirebase(title);
                        finish();
                    }
                    else {
                        Toast.makeText(CompanyAddPageActivity.this, "이미 존재하는 마커입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //이미지 처리

        firebaseStorage = FirebaseStorage.getInstance();

        markerImage = findViewById(R.id.add_marker_image);
        addMarkerImage = findViewById(R.id.add_photo_btn_upload);

        addMarkerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContent.launch("image/*");
            }
        }) ;
   }
    private void uploadImageToFirebase(String markerTitle) {
        if (imageUri != null) {
            // 파일 이름을 마커의 타이틀로 설정
            String imageFileName = markerTitle + ".png";

            StorageReference storageRef = firebaseStorage.getReference().child("images").child(imageFileName);

            // 파일 업로드
            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(CompanyAddPageActivity.this, "저장되었습니다.", Toast.LENGTH_LONG).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(CompanyAddPageActivity.this, "이미지 업로드 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        } else {
            Toast.makeText(CompanyAddPageActivity.this, "이미지를 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadImageFromFirebase(Activity activity, Context context, String markerTitle, ImageView Image) {
        // 파일 이름을 마커의 타이틀로 설정
        String imageFileName = markerTitle + ".png";
        StorageReference storageRef = firebaseStorage.getReference().child("images").child(imageFileName);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // 이미지 URI를 통해 이미지 로드
            Glide.with(activity).load(uri).into(Image);
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "이미지를 불러오지 못했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void initializeFirebaseStorage() {
        if (firebaseStorage == null) {
            firebaseStorage = FirebaseStorage.getInstance();
        }
    }


}
