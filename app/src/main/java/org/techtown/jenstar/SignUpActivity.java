package org.techtown.jenstar;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.techtown.jenstar.admin.AdminMainActivity;
import org.techtown.jenstar.company.CompanyMainActivity;
import org.techtown.jenstar.database.DBHelper;
import org.techtown.jenstar.user.UserMainActivity;

public class SignUpActivity extends AppCompatActivity {

    String savedID;

    EditText idEditText, pwEditText;
    Button loginButton;
    DBHelper dbHelper;
    Toast currentToast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        // DBHelper 초기화
        dbHelper = new DBHelper(this);
        currentToast = null;

        // UI 요소 초기화
        idEditText = findViewById(R.id.ID);
        pwEditText = findViewById(R.id.PW);
        loginButton = findViewById(R.id.SignUp);

        // 한글 입력 불가능하게
        idEditText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    // 유니코드 범위를 이용해 한글을 감지
                    if (Character.getType(source.charAt(i)) == Character.OTHER_LETTER) {
                        showToast("아이디에 한글을 입력할 수 없습니다.");
                        return ""; // 한글일 경우 입력되지 않도록 함
                    }
                }
                return null; // 한글이 아닐 경우 입력 허용
            }
        }});
        pwEditText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    // 유니코드 범위를 이용해 한글을 감지
                    if (Character.getType(source.charAt(i)) == Character.OTHER_LETTER) {
                        showToast("비밀번호에 한글을 입력할 수 없습니다.");
                        return ""; // 한글일 경우 입력되지 않도록 함
                    }
                }
                return null; // 한글이 아닐 경우 입력 허용
            }
        }});

        // 로그인 버튼 클릭 리스너 설정
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idEditText.getText().toString().trim();
                String pw = pwEditText.getText().toString().trim();

                // 입력된 값이 비어 있는지 확인
                if (id.isEmpty() || pw.isEmpty()) {
                    showToast("아이디와 비밀번호를 입력하세요.");
                } else {
                    // DBHelper를 통해 로그인 검증
                    boolean isValidUser = dbHelper.checkUser(id, pw);
                    int checkAuthority = dbHelper.checkAuthority(id, pw);
                    if (isValidUser) {
                        // 로그인 성공
                        showToast("로그인 성공");
                        savedID = id;
                        // 메인 액티비티 또는 다른 화면으로 이동
                        if (checkAuthority == 0){
                            Intent intent = new Intent(SignUpActivity.this, AdminMainActivity.class); // 로그인 후 이동할 액티비티 설정
                            startActivity(intent);
                        }
                        else if (checkAuthority == 1){
                            Intent intent = new Intent(SignUpActivity.this, UserMainActivity.class); // 로그인 후 이동할 액티비티 설정
                            intent.putExtra("savedID", savedID);
                            startActivity(intent);
                        }
                        else if (checkAuthority == 2) {
                            Intent intent = new Intent(SignUpActivity.this, CompanyMainActivity.class); // 로그인 후 이동할 액티비티 설정
                            intent.putExtra("savedID", savedID);
                            startActivity(intent);
                        }
                        finish(); // 현재 액티비티 종료
                    } else {
                        // 로그인 실패
                        showToast("아이디 또는 비밀번호가 일치하지 않습니다.");
                    }
                }
            }
        });
    }

    private void showToast(String message) {
        if (currentToast != null) {
            currentToast.cancel(); // 이전 Toast가 있으면 취소
        }
        currentToast = Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT);
        currentToast.show();
    }
}
