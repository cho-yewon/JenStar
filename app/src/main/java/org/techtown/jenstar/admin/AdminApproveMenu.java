package org.techtown.jenstar.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.techtown.jenstar.marker.MarkerAdapter;
import org.techtown.jenstar.database.MarkerDBHelper;
import org.techtown.jenstar.R;
import org.techtown.jenstar.marker.admin_MarkerAdapter;

import java.util.List;

public class AdminApproveMenu extends Fragment {
    private RecyclerView markerRecyclerView;
    private admin_MarkerAdapter admin_MarkerAdapter;
    private MarkerDBHelper markerDBHelper;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_approve_menu, container, false);
        markerRecyclerView = view.findViewById(R.id.markerRecyclerView);
        markerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        markerDBHelper = new MarkerDBHelper(getContext());

        if (getArguments() != null) {
            userId = getArguments().getString("userId"); // 번들에서 userId 가져오기
        }

        // 모든 마커 가져오기
        List<MarkerDBHelper.Marker> markerList = markerDBHelper.getMarkers(); // 모든 마커 가져오기
        admin_MarkerAdapter = new admin_MarkerAdapter(getContext(), markerList);
        markerRecyclerView.setAdapter(admin_MarkerAdapter);

        return view;
    }
}
