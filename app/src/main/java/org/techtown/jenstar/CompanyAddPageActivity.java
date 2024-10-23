package org.techtown.jenstar;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CompanyAddPageActivity extends AppCompatActivity {

    Button close_btn, save_btn;
    EditText titleEditText, snippetEditText, latEditText, lngEditText;
    MarkerDBHelper markerDBHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_addpage_screen);

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
                        Toast.makeText(CompanyAddPageActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        Toast.makeText(CompanyAddPageActivity.this, "이미 존재하는 마커입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
