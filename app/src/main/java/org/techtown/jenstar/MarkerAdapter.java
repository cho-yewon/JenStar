package org.techtown.jenstar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.MarkerViewHolder> {
    private List<MarkerDBHelper.Marker> markerList;
    private Context context;

    // Constructor
    public MarkerAdapter(Context context, List<MarkerDBHelper.Marker> markerList) {
        this.markerList = markerList;
        this.context = context;
    }

    @NonNull
    @Override
    public MarkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_company_menu_item, parent, false);
        return new MarkerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkerViewHolder holder, int position) {
        MarkerDBHelper.Marker marker = markerList.get(position);
        holder.markerTitle.setText(marker.title);
        holder.markerSnippet.setText(marker.snippet);
        holder.markerLatLng.setText(marker.lat + ", " + marker.lng);


        // 클릭 리스너 설정: 각 마커를 클릭하면 상세 정보 페이지로 이동
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MarkerDetailActivity.class);
            intent.putExtra("marker_title", marker.title);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return markerList.size();
    }

    // ViewHolder class
    public static class MarkerViewHolder extends RecyclerView.ViewHolder {
        public TextView markerTitle, markerSnippet, markerLatLng;
        public View divider;

        public MarkerViewHolder(@NonNull View itemView) {
            super(itemView);
            markerTitle = itemView.findViewById(R.id.markerTitle);
            markerSnippet = itemView.findViewById(R.id.markerSnippet);
            markerLatLng = itemView.findViewById(R.id.markerLatLng);
        }
    }
}
