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
import org.techtown.jenstar.database.UserFavoriteDBHelper;

public class UserMainActivity extends AppCompatActivity{

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private UserMap fragmentMap = new UserMap();
    private UserMenu fragmentMenu = new UserMenu();
    private UserAccount fragmentAccount = new UserAccount();
    private UserSearch fragmentSearch = new UserSearch();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        UserFavoriteDBHelper dbHelper = new UserFavoriteDBHelper(this);
        dbHelper.createTableIfNotExists();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main_screen);
        String savedID = getIntent().getStringExtra("savedID");

        Bundle bundle = new Bundle();
        bundle.putString("savedID", savedID);
        fragmentMap.setArguments(bundle);

        Bundle menuBundle = new Bundle();
        menuBundle.putString("userId", savedID); // userId로 사용될 savedID
        fragmentMenu.setArguments(menuBundle);

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

                case R.id.ic_search:
                    transaction.replace(R.id.user_main_frame_layout, fragmentSearch).commitAllowingStateLoss();

                    break;

                case R.id.ic_menu:
                    transaction.replace(R.id.user_main_frame_layout, fragmentMenu).commitAllowingStateLoss();

                    break;

                case R.id.ic_user:
                    transaction.replace(R.id.user_main_frame_layout, fragmentAccount).commitAllowingStateLoss();

                    break;
            }
            return true;
        }
    }
}