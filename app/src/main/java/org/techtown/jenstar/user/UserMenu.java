package org.techtown.jenstar.user;

import android.location.Address;
import android.location.Geocoder;
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

import org.techtown.jenstar.marker.user_MarkerAdapter;
import org.techtown.jenstar.database.MarkerDBHelper;
import org.techtown.jenstar.database.UserFavoriteDBHelper;
import org.techtown.jenstar.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserMenu extends Fragment {
    private RecyclerView markerRecyclerView;
    private user_MarkerAdapter user_markerAdapter;
    private UserFavoriteDBHelper userFavoriteDBHelper;
    private MarkerDBHelper markerDBHelper;
    private String userId; // 사용자 ID

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_menu, container, false);

        Geocoder geocoder = new Geocoder(getContext(), Locale.KOREA);
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
            MarkerDBHelper.Marker markerInfo = markerDBHelper.getMarkerById(title);
            if (markerInfo != null) {
                try {
                    List<Address> addresses = geocoder.getFromLocation(markerInfo.lat, markerInfo.lng, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        // 도로명 주소 전체를 가져오기
                        String roadAddress = address.getAddressLine(0);
                        markerInfo.setRoadAddress(roadAddress != null ? roadAddress : "도로명 주소 없음");
                    } else {
                        markerInfo.setRoadAddress("도로명 주소 없음");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    markerInfo.setRoadAddress("주소 변환 실패");
                }
                favoriteMarkers.add(markerInfo);
            }
        }

        // MarkerAdapter를 사용하여 즐겨찾기 마커 리스트 설정
        user_markerAdapter = new user_MarkerAdapter(getContext(), favoriteMarkers, userId);
        markerRecyclerView.setAdapter(user_markerAdapter);

        return view;
    }
}
