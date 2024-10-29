package org.techtown.jenstar.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.techtown.jenstar.marker.MarkerAdapter;
import org.techtown.jenstar.database.MarkerDBHelper;
import org.techtown.jenstar.database.UserFavoriteDBHelper;
import org.techtown.jenstar.R;

import java.util.ArrayList;
import java.util.List;

public class UserMenu extends Fragment {
    private RecyclerView markerRecyclerView;
    private MarkerAdapter markerAdapter;
    private UserFavoriteDBHelper userFavoriteDBHelper;
    private MarkerDBHelper markerDBHelper;
    private String userId; // 사용자 ID

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_menu, container, false);

        markerRecyclerView = view.findViewById(R.id.markerRecyclerView);
        markerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userFavoriteDBHelper = new UserFavoriteDBHelper(getContext());
        markerDBHelper = new MarkerDBHelper(getContext());

        // 사용자의 ID 설정 (예시로 "user123"으로 하였으니 실제 로그인 정보로 변경 필요)
        //userId = "user"; // 로그인 시 사용자의 ID를 받아오는 부분으로 대체 필요
        if (getArguments() != null) {
            userId = getArguments().getString("userId"); // 번들에서 userId 가져오기
        }

        // 사용자 즐겨찾기 마커 가져오기
        List<String> favoriteMarkerTitles = userFavoriteDBHelper.getUserFavorites(userId);

        // 마커 정보를 저장할 리스트 생성
        List<MarkerDBHelper.Marker> favoriteMarkers = new ArrayList<>();
        for (String title : favoriteMarkerTitles) {
            MarkerDBHelper.Marker markerInfo = markerDBHelper.getMarkerById(title); // 제목으로 마커 정보 가져오기
            if (markerInfo != null) {
                favoriteMarkers.add(markerInfo); // 마커 정보를 리스트에 추가
            }
        }

        // MarkerAdapter를 사용하여 즐겨찾기 마커 리스트 설정
        markerAdapter = new MarkerAdapter(getContext(), favoriteMarkers);
        markerRecyclerView.setAdapter(markerAdapter);

        return view;
    }
}
