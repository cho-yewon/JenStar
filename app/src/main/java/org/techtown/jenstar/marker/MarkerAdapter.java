package org.techtown.jenstar.marker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.jenstar.company.CompanyAddPageActivity;
import org.techtown.jenstar.database.MarkerDBHelper;
import org.techtown.jenstar.R;

import java.util.List;

public class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.MarkerViewHolder> {
    private List<MarkerDBHelper.Marker> markerList;
    private Context context;
    private String userId;
    private CompanyAddPageActivity companyAddPageActivity;

    // Constructor
    public MarkerAdapter(Context context, List<MarkerDBHelper.Marker> markerList, String userId) {
        this.markerList = markerList;
        this.context = context;
        this.userId = userId;
        this.companyAddPageActivity = new CompanyAddPageActivity(); // CompanyAddPageActivity 인스턴스 생성
        companyAddPageActivity.initializeFirebaseStorage(); // Firebase 초기화
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
        holder.markerLatLng.setText(marker.getRoadAddress() != null ? marker.getRoadAddress() : "도로명 주소 없음");

        companyAddPageActivity.loadImageFromFirebase(context, marker.title, holder.markerImage);

        // 클릭 리스너 설정: 각 마커를 클릭하면 상세 정보 페이지로 이동
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MarkerDetailActivity.class);
            intent.putExtra("marker_title", marker.title);
            intent.putExtra("userId", userId);
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
        public ImageView markerImage;

        public MarkerViewHolder(@NonNull View itemView) {
            super(itemView);
            markerImage = itemView.findViewById(R.id.markerImage);
            markerTitle = itemView.findViewById(R.id.markerTitle);
            markerSnippet = itemView.findViewById(R.id.markerSnippet);
            markerLatLng = itemView.findViewById(R.id.markerLatLng);
        }
    }


}
