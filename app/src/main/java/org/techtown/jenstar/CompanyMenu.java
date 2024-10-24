package org.techtown.jenstar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class CompanyMenu extends Fragment {
    private RecyclerView markerRecyclerView;
    private MarkerAdapter markerAdapter;
    private MarkerDBHelper markerDBHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_menu, container, false);
        markerRecyclerView = view.findViewById(R.id.markerRecyclerView);
        markerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        markerDBHelper = new MarkerDBHelper(getContext());
        List<MarkerDBHelper.Marker> markerList = markerDBHelper.getMarkers();

        markerAdapter = new MarkerAdapter(markerList);
        markerRecyclerView.setAdapter(markerAdapter);

        return view;
    }
}
