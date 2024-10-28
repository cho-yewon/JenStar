package org.techtown.jenstar;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.techtown.jenstar.database.DBHelper;

public class CreateAccountActivity extends AppCompatActivity {

    EditText idEditText, passwordEditText, checkPasswordEditText, usernameEditText, birthEditText, phoneEditText, emailEditText;
    Button signUpButton, idCheckButton, pwCheckButton;
    DBHelper dbHelper;

    boolean passPW;
    Toast currentToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount_screen);

        // 변수 초기화
        passPW = false;
        currentToast = null;

        // DBHelper 초기화
        dbHelper = new DBHelper(this);

        // UI 요소 초기화
        idEditText = findViewById(R.id.InputID);
        passwordEditText = findViewById(R.id.InputPW);
        checkPasswordEditText = findViewById(R.id.InputCheckPW);
        usernameEditText = findViewById(R.id.InputName);
        birthEditText = findViewById(R.id.InputBirth);
        phoneEditText = findViewById(R.id.InputPhoneNumber);
        emailEditText = findViewById(R.id.InputEmail);
        signUpButton = findViewById(R.id.CreateAccountFinish);
        idCheckButton = findViewById(R.id.CheckID);
        pwCheckButton = findViewById(R.id.CheckPW);

        // 한글 입력 불가능하게
        idEditText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.getType(source.charAt(i)) == Character.OTHER_LETTER) {
                        showToast("아이디에 한글을 입력할 수 없습니다.");
                        return "";
                    }
                }
                return null;
            }
        }});

        // 회원가입 버튼 클릭 리스너
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String checkPassword = checkPasswordEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();
                String birth = birthEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();

                if (!validateId(id)) {
                    showToast("아이디는 7자 이상이어야 합니다.");
                } else if (!validatePassword(password)) {
                    showToast("비밀번호는 영어와 숫자를 포함하여 9자리 이상이어야 합니다.");
                } else if (!password.equals(checkPassword)) {
                    showToast("비밀번호가 일치하지 않습니다.");
                } else if (username.isEmpty()) {
                    showToast("이름을 입력하세요.");
                } else if (!validateBirth(birth)) {
                    showToast("생년월일은 8자리로 입력하세요.");
                } else if (!validatePhone(phone)) {
                    showToast("전화번호는 11자리로 입력하세요.");
                } else if (!validateEmail(email)) {
                    showToast("올바른 이메일 주소를 입력하세요.");
                } else {
                    boolean isInserted = dbHelper.addUser(id, password, username, birth, phone, email);
                    if (isInserted && passPW) {
                        showToast("회원가입 성공");
                        finish();
                    } else {
                        showToast("회원가입 실패");
                    }
                }
            }
        });

        idCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = idEditText.getText().toString().trim();
                if (id.isEmpty()) {
                    showToast("아이디를 입력하세요.");
                } else {
                    boolean isIDCheck = dbHelper.duplicateID(id);
                    if (isIDCheck) {
                        showToast("아이디가 이미 존재합니다.");
                    } else {
                        showToast("아이디 사용 가능합니다.");
                    }
                }
            }
        });

        pwCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String realPW = passwordEditText.getText().toString().trim();
                String checkPW = checkPasswordEditText.getText().toString().trim();
                if (realPW.equals(checkPW) && !realPW.isEmpty()) {
                    showToast("비밀번호가 일치합니다.");
                    passPW = true;
                } else {
                    showToast("비밀번호가 일치하지 않습니다.");
                    passPW = false;
                }
            }
        });
    }

    private boolean validateId(String id) {
        return id.length() >= 7;
    }

    private boolean validatePassword(String password) {
        return password.length() >= 9 && password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$");
    }

    private boolean validateBirth(String birth) {
        return birth.matches("^\\d{8}$");
    }

    private boolean validatePhone(String phone) {
        return phone.matches("^\\d{11}$");
    }

    private boolean validateEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showToast(String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(CreateAccountActivity.this, message, Toast.LENGTH_SHORT);
        currentToast.show();
    }
}
