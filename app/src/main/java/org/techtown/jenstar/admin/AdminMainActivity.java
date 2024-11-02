package org.techtown.jenstar.admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.techtown.jenstar.R;
import org.techtown.jenstar.company.CompanyMap;

public class AdminMainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private AdminApproveMenu fragmentMenu = new AdminApproveMenu();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main_screen);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.company_main_frame_layout, fragmentMenu).commitAllowingStateLoss();
    }
}
