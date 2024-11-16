package org.techtown.jenstar.company;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.techtown.jenstar.marker.MarkerAdapter;
import org.techtown.jenstar.database.MarkerDBHelper;
import org.techtown.jenstar.R;

import java.util.List;

public class CompanyMenu extends Fragment {
    private RecyclerView markerRecyclerView;
    private MarkerAdapter markerAdapter;
    private MarkerDBHelper markerDBHelper;
    private String userId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_menu, container, false);
        userId = getArguments() != null ? getArguments().getString("userId") : null;

        initializeRecyclerView(view);
        loadMarkers();

        return view;
    }

    private void initializeRecyclerView(View view) {
        markerRecyclerView = view.findViewById(R.id.markerRecyclerView);
        markerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        markerDBHelper = new MarkerDBHelper(getContext());
    }

    private void loadMarkers() {
        List<MarkerDBHelper.Marker> markerList = markerDBHelper.getMarkers();
        markerAdapter = new MarkerAdapter(getContext(), markerList, userId);
        markerRecyclerView.setAdapter(markerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMarkers(); // 프래그먼트가 다시 화면에 보일 때 마커 데이터를 새로 불러옴
    }
}
