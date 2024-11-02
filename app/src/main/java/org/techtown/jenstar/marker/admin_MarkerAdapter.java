package org.techtown.jenstar.marker;

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

public class admin_MarkerAdapter extends RecyclerView.Adapter<admin_MarkerAdapter.MarkerViewHolder> {
    private List<MarkerDBHelper.Marker> markerList;
    private Context context;
    private String userId;
    private CompanyAddPageActivity companyAddPageActivity;

    // Constructor
    public admin_MarkerAdapter(Context context, List<MarkerDBHelper.Marker> markerList) {
        this.markerList = markerList;
        this.context = context;
        this.companyAddPageActivity = new CompanyAddPageActivity(); // CompanyAddPageActivity 인스턴스 생성
        companyAddPageActivity.initializeFirebaseStorage(); // Firebase 초기화
    }

    @NonNull
    @Override
    public MarkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_admin_approve_menu_item, parent, false);

        return new MarkerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkerViewHolder holder, int position) {
        MarkerDBHelper.Marker marker = markerList.get(position);
        holder.markerTitle.setText(marker.title);
        holder.markerSnippet.setText(marker.snippet);
        holder.markerLatLng.setText(marker.lat + ", " + marker.lng);

        companyAddPageActivity.loadImageFromFirebase(context, marker.title, holder.markerImage);

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
