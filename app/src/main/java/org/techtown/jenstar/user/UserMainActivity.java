package org.techtown.jenstar.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.techtown.jenstar.R;
import org.techtown.jenstar.company.CompanyAccount;
import org.techtown.jenstar.company.CompanyMap;
import org.techtown.jenstar.company.CompanyMenu;

public class UserMainActivity extends AppCompatActivity{

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private CompanyMap fragmentMap = new CompanyMap();
    private CompanyMenu fragmentMenu = new CompanyMenu();
    private CompanyAccount fragmentAccount = new CompanyAccount();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main_screen);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.user_main_frame_layout, fragmentMap).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_main_navigation);
        bottomNavigationView.setOnItemSelectedListener(new ItemSelectedListener());

        View myView = findViewById(R.id.user_main_frame_layout);

        // 디스플레이 크기 가져오기
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;

        // 화면 높이의 10% 계산
        int marginBottom = (int) (screenHeight * 0.09);

        // 마진을 설정
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) myView.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, marginBottom);
        myView.setLayoutParams(params);
    }

    class ItemSelectedListener implements NavigationBarView.OnItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.ic_map:
                    transaction.replace(R.id.user_main_frame_layout, fragmentMap).commitAllowingStateLoss();

                    break;

                case R.id.ic_menu:
                    transaction.replace(R.id.user_main_frame_layout, fragmentMenu).commitAllowingStateLoss();

                    break;

                case R.id.ic_account:
                    transaction.replace(R.id.user_main_frame_layout, fragmentAccount).commitAllowingStateLoss();

                    break;
            }
            return true;
        }
    }
}