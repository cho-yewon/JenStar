package org.techtown.jenstar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class CompanyMainActivity extends AppCompatActivity{

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private CompanyMap fragmentMap = new CompanyMap();
    private CompanyMenu fragmentMenu = new CompanyMenu();
    private CompanyAccount fragmentAccount = new CompanyAccount();

    Button add_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_main_screen);

        String savedID = getIntent().getStringExtra("savedID");

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.company_main_frame_layout, fragmentMap).commitAllowingStateLoss();

        add_btn = findViewById(R.id.add_button);

        add_btn.bringToFront();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_main_navigation);
        bottomNavigationView.setOnItemSelectedListener(new ItemSelectedListener());

        View myView = findViewById(R.id.company_main_frame_layout);

        // 디스플레이 크기 가져오기
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;

        // 화면 높이의 10% 계산
        int marginBottom = (int) (screenHeight * 0.095);

        // 마진을 설정
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) myView.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, marginBottom);
        myView.setLayoutParams(params);


        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompanyMainActivity.this, CompanyAddPageActivity.class); // 로그인 후 이동할 액티비티 설정
                intent.putExtra("savedID", savedID);
                startActivity(intent);
            }
        });


    }

    class ItemSelectedListener implements NavigationBarView.OnItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.ic_map:
                    transaction.replace(R.id.company_main_frame_layout, fragmentMap).commitAllowingStateLoss();
                    add_btn.setVisibility(View.VISIBLE);
                    break;

                case R.id.ic_menu:
                    transaction.replace(R.id.company_main_frame_layout, fragmentMenu).commitAllowingStateLoss();
                    add_btn.setVisibility(View.VISIBLE);
                    break;

                case R.id.ic_account:
                    transaction.replace(R.id.company_main_frame_layout, fragmentAccount).commitAllowingStateLoss();
                    add_btn.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }
}