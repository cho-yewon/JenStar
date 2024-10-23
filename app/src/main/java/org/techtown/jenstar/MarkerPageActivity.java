package org.techtown.jenstar;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MarkerPageActivity extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marker_screen, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // BottomSheet 높이를 80%로 설정
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheet.getLayoutParams().height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);  // 화면의 80% 높이
            behavior.setPeekHeight((int) (getResources().getDisplayMetrics().heightPixels * 0.8));  // 초기 높이도 80%로 설정
        }
    }
}

