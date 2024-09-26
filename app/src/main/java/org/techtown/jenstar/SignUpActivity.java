package org.techtown.jenstar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    EditText idEditText, pwEditText;
    Button loginButton;
    DBHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        // DBHelper 초기화
        dbHelper = new DBHelper(this);

        // UI 요소 초기화
        idEditText = findViewById(R.id.ID);
        pwEditText = findViewById(R.id.PW);
        loginButton = findViewById(R.id.SignUp);

        // 로그인 버튼 클릭 리스너 설정
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idEditText.getText().toString().trim();
                String pw = pwEditText.getText().toString().trim();

                // 입력된 값이 비어 있는지 확인
                if (id.isEmpty() || pw.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // DBHelper를 통해 로그인 검증
                    boolean isValidUser = dbHelper.checkUser(id, pw);
                    if (isValidUser) {
                        // 로그인 성공
                        Toast.makeText(SignUpActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        // 메인 액티비티 또는 다른 화면으로 이동
                        Intent intent = new Intent(SignUpActivity.this, FirstActivity.class); // 로그인 후 이동할 액티비티 설정
                        startActivity(intent);
                        finish(); // 현재 액티비티 종료
                    } else {
                        // 로그인 실패
                        Toast.makeText(SignUpActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
