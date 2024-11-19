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

import org.techtown.jenstar.R;
import org.techtown.jenstar.database.MarkerDBHelper;
import org.techtown.jenstar.marker.MarkerDetailActivity;

import java.util.List;

public class user_MarkerAdapter extends RecyclerView.Adapter<user_MarkerAdapter.UserMarkerViewHolder> {
    private List<MarkerDBHelper.Marker> markerList;
    private Context context;
    private String userId;

    // Constructor
    public user_MarkerAdapter(Context context, List<MarkerDBHelper.Marker> markerList, String userId) {
        this.markerList = markerList;
        this.context = context;
        this.userId = userId;
    }

    @NonNull
    @Override
    public UserMarkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user_menu_item, parent, false);
        return new UserMarkerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserMarkerViewHolder holder, int position) {
        MarkerDBHelper.Marker marker = markerList.get(position);

        // Set marker details
        holder.markerTitle.setText(marker.title);
        holder.markerSnippet.setText(marker.snippet);
        holder.markerLatLng.setText(marker.getRoadAddress() != null ? marker.getRoadAddress() : "도로명 주소 없음");

        // Set image (if applicable, update this to load an image from your storage if needed)
        holder.markerImage.setImageResource(R.drawable.ic_empty);

        // Item click listener
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
    public static class UserMarkerViewHolder extends RecyclerView.ViewHolder {
        public TextView markerTitle, markerSnippet, markerLatLng;
        public ImageView markerImage;

        public UserMarkerViewHolder(@NonNull View itemView) {
            super(itemView);
            markerTitle = itemView.findViewById(R.id.markerTitle);
            markerSnippet = itemView.findViewById(R.id.markerSnippet);
            markerLatLng = itemView.findViewById(R.id.markerLatLng);
            markerImage = itemView.findViewById(R.id.imageView);
        }
    }
}
