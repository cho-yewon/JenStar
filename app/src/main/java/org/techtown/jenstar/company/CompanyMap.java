package org.techtown.jenstar.company;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.techtown.jenstar.database.MarkerDBHelper;
import org.techtown.jenstar.marker.MarkerPageActivity;
import org.techtown.jenstar.R;

import java.util.List;

public class CompanyMap extends Fragment implements OnMapReadyCallback {

    private MapView mapView = null;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    MarkerDBHelper markerDBHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_company_map, container, false);
        mapView = (MapView) rootView.findViewById(R.id.company_map);
        mapView.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        markerDBHelper = new MarkerDBHelper(getContext());

        List<MarkerDBHelper.Marker> markers = markerDBHelper.getMarkers();

        for (MarkerDBHelper.Marker marker : markers){
            LatLng MARKER = new LatLng(marker.lat, marker.lng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(MARKER);
            markerOptions.title(marker.title);
            markerOptions.snippet(marker.snippet);
            googleMap.addMarker(markerOptions);
        }

        // 내 위치를 지도에 표시
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        googleMap.setMyLocationEnabled(true); // 내 위치 버튼 활성화

        // 현재 위치로 카메라 이동
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                }
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                // 클릭한 위치가 마커의 실제 범위 내에 있는지 확인하기 위해 거리 계산
                float[] results = new float[1];
                LatLng markerPosition = marker.getPosition();
                LatLng markerLatLng = marker.getPosition(); // marker의 위치를 가져옴

                Location.distanceBetween(markerLatLng.latitude, markerLatLng.longitude,
                        markerPosition.latitude, markerPosition.longitude, results);

                // 클릭한 위치가 마커의 실제 범위 내에 있는지 확인 (반지름을 줄여서 클릭 범위 줄이기)
                float YOUR_DESIRED_RADIUS = 1.0f; // 원하는 클릭 범위 반지름 설정 (단위: 미터)
                if (results[0] < YOUR_DESIRED_RADIUS) {
                    // 클릭된 마커와 연결된 작업 수행
                    markerDBHelper.getMarkers();

                    Bundle markerBundle = new Bundle();
                    markerBundle.putString("marker_title", marker.getTitle());
                    markerBundle.putString("marker_snippet", marker.getSnippet());

                    // BottomSheetDialogFragment 호출
                    MarkerPageActivity bottomSheet = new MarkerPageActivity();
                    bottomSheet.setArguments(markerBundle);
                    bottomSheet.show(getParentFragmentManager(), "markerpageactivity");

                    return true; // 이벤트 소비 - 기본 클릭 동작을 실행하지 않음
                }

                // 클릭한 위치가 마커 범위를 벗어나면 마커 클릭 동작을 처리하지 않음
                return false;
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

}
