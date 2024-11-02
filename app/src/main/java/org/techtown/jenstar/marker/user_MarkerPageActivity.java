package org.techtown.jenstar.marker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.techtown.jenstar.R;
import org.techtown.jenstar.company.CompanyAddPageActivity;
import org.techtown.jenstar.database.UserFavoriteDBHelper;

import java.util.List;

public class user_MarkerPageActivity extends BottomSheetDialogFragment {

    TextView markerTitle, markerSnippet;
    ImageView markerImage;
    ImageButton favoriteButton;
    CompanyAddPageActivity companyAddPageActivity;
    UserFavoriteDBHelper dbHelper;

    boolean isFavorite;
    String save_Id; // 사용자 ID
    String save_markerTitle; // 마커 제목

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 사용자 ID 초기화
        Bundle args = getArguments();
        if (args != null) {
            save_Id = args.getString("savedID");
            save_markerTitle = args.getString("marker_title");
        }

        View view = inflater.inflate(R.layout.user_marker_screen, container, false);

        dbHelper = new UserFavoriteDBHelper(getContext());
        companyAddPageActivity = new CompanyAddPageActivity();

        markerImage = view.findViewById(R.id.marker_image);
        markerTitle = view.findViewById(R.id.marker_title);
        markerSnippet = view.findViewById(R.id.marker_snippet);
        favoriteButton = view.findViewById(R.id.favorite);

        // 즐겨찾기 상태 초기화
        initializeFavoriteState();

        // 즐겨찾기 버튼 클릭 리스너 설정
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 즐겨찾기 추가/삭제
                toggleFavoriteInDatabase(save_Id, save_markerTitle);
            }
        });

        // 마커 정보 가져오기
        Bundle getMarker = getArguments();
        if (getMarker != null) {
            String title = getMarker.getString("marker_title");
            String snippet = getMarker.getString("marker_snippet");
            save_markerTitle = title;

            // Firebase에서 이미지 로드
            companyAddPageActivity.initializeFirebaseStorage();
            companyAddPageActivity.loadImageFromFirebase(getContext(), title, markerImage);
            markerTitle.setText(title);
            markerSnippet.setText(snippet);
        }

        return view;
    }

    // 즐겨찾기 초기화 메서드
    private void initializeFavoriteState() {
        // 데이터베이스에서 현재 사용자의 즐겨찾기를 확인하여 초기 상태 설정
        List<String> favorites = dbHelper.getUserFavorites(save_Id);
        isFavorite = favorites.contains(save_markerTitle); // 마커 ID가 즐겨찾기에 있는지 확인

        // 버튼 이미지 설정
        updateFavoriteButton(); // 초기 상태에 따라 버튼 이미지 설정
    }

    // 즐겨찾기 상태 토글
    private void toggleFavoriteInDatabase(String userId, String markerId) {
        if (isFavorite) {
            // 즐겨찾기 삭제
            boolean removed = dbHelper.removeFavorite(userId, markerId);
            if (removed) {
                Log.d("UserFavorite", "Favorite removed: " + markerId);
            }
        } else {
            // 즐겨찾기 추가
            boolean added = dbHelper.addFavorite(userId, markerId);
            if (added) {
                Log.d("UserFavorite", "Favorite added: " + markerId);
            }
        }

        // isFavorite 상태 업데이트 후 버튼 이미지 업데이트
        isFavorite = !isFavorite; // 상태 토글
        updateFavoriteButton(); // 버튼 이미지 업데이트
    }

    // 즐겨찾기 버튼 업데이트
    private void updateFavoriteButton() {
        if (isFavorite) {
            favoriteButton.setImageResource(android.R.drawable.btn_star_big_on); // 즐겨찾기 추가된 경우
        } else {
            favoriteButton.setImageResource(android.R.drawable.btn_star_big_off); // 즐겨찾기 추가되지 않은 경우
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // BottomSheet 높이를 80%로 설정
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheet.getLayoutParams().height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
            behavior.setPeekHeight((int) (getResources().getDisplayMetrics().heightPixels * 0.8));
        }
    }
}
