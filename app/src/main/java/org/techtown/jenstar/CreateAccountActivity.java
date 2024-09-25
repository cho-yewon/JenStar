package org.techtown.jenstar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateAccountActivity extends AppCompatActivity {

    EditText idEditText, passwordEditText, usernameEditText, birthEditText, phoneEditText, emailEditText;
    Button signUpButton;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount_screen);

        // DBHelper 초기화
        dbHelper = new DBHelper(this);

        // UI 요소 초기화
        idEditText = findViewById(R.id.InputID);
        passwordEditText = findViewById(R.id.InputPW);
        usernameEditText = findViewById(R.id.InputName);
        birthEditText = findViewById(R.id.InputBirth);
        phoneEditText = findViewById(R.id.InputPhoneNumber);
        emailEditText = findViewById(R.id.InputEmail);
        signUpButton = findViewById(R.id.CreateAccountFinish);

        // 회원가입 버튼 클릭 리스너
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();
                String birth = birthEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();

                if (id.isEmpty() || password.isEmpty()) {
                    Toast.makeText(CreateAccountActivity.this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    boolean isInserted = dbHelper.addUser(id, password, username, birth, phone, email);
                    if (isInserted) {
                        Toast.makeText(CreateAccountActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                        finish(); // 회원가입 성공 시 화면 종료
                    } else {
                        Toast.makeText(CreateAccountActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
