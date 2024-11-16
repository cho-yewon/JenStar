package org.techtown.jenstar.user;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.data.geojson.GeoJsonPoint;
import com.google.maps.android.data.geojson.GeoJsonPointStyle;
import com.google.maps.android.data.geojson.GeoJsonPolygon;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.jenstar.R;
import org.techtown.jenstar.database.MarkerDBHelper;
import org.techtown.jenstar.marker.MarkerAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class UserSearch extends Fragment implements OnMapReadyCallback {

    private static final String TABLE_NAME = "markers";
    private Spinner citySpinner;
    private Spinner districtSpinner;
    private MapView mapView;
    private GoogleMap googleMap;
    private MarkerDBHelper markerDBHelper;
    private Button btnList;
    private RecyclerView markerRecyclerView;
    private MarkerAdapter markerAdapter;
    private List<MarkerDBHelper.Marker> markerList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_search, container, false);

        citySpinner = view.findViewById(R.id.citySpinner);
        districtSpinner = view.findViewById(R.id.districtSpinner);
        mapView = view.findViewById(R.id.user_search_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        btnList = view.findViewById(R.id.btnList);
        markerRecyclerView = view.findViewById(R.id.markerRecyclerView); // fragment_user_menu.xml의 RecyclerView와 연결
        markerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        markerAdapter = new MarkerAdapter(getContext(),markerList, ""); // 이미 있는 MarkerAdapter 사용
        markerRecyclerView.setAdapter(markerAdapter);

        setupCitySpinner();
        markerDBHelper = new MarkerDBHelper(getContext());

        // citySpinner 초기값 설정 - 리스너 등록 전 설정
        citySpinner.setSelection(0, false);
        updateDistrictSpinner(0);

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMarkersInViewArea();
                markerRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private void showMarkersInViewArea() {
        markerList.clear();
        markerList.addAll(showSelectedAreaMarkers("cityName", "districtName")); // showSelectedAreaMarkers 메서드로 현재 영역의 마커 가져오기
        markerAdapter.notifyDataSetChanged(); // 리스트 업데이트
    }

    private void setupCitySpinner() {
        // 시/도 스피너 설정
        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.cities, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    // 도시 인덱스를 정확히 매칭하여 지도에 표시
                    updateDistrictSpinner(position);
                }

                switch (position-1) {
                    case 1: // 서울특별시
                        showSelectedAreaOnMap("서울 전체");
                        break;
                    case 2: // 부산광역시
                        showSelectedAreaOnMap("부산 전체");
                        break;
                    case 3: // 대구광역시
                        showSelectedAreaOnMap("대구 전체");
                        break;
                    case 4: // 인천광역시
                        showSelectedAreaOnMap("인천 전체");
                        break;
                    case 5: // 광주광역시
                        showSelectedAreaOnMap("광주 전체");
                        break;
                    case 6: // 대전광역시
                        showSelectedAreaOnMap("대전 전체");
                        break;
                    case 7: // 울산광역시
                        showSelectedAreaOnMap("울산 전체");
                        break;
                    case 8: // 세종특별자치시
                        showSelectedAreaOnMap("세종 전체");
                        break;
                    case 9: // 경기도
                        showSelectedAreaOnMap("경기도 전체");
                        break;
                    case 10: // 강원도
                        showSelectedAreaOnMap("강원도 전체");
                        break;
                    case 11: // 충청북도
                        showSelectedAreaOnMap("충청북도 전체");
                        break;
                    case 12: // 충청남도
                        showSelectedAreaOnMap("충청남도 전체");
                        break;
                    case 13: // 전라북도
                        showSelectedAreaOnMap("전라북도 전체");
                        break;
                    case 14: // 전라남도
                        showSelectedAreaOnMap("전라남도 전체");
                        break;
                    case 15: // 경상북도
                        showSelectedAreaOnMap("경상북도 전체");
                        break;
                    case 16: // 경상남도
                        showSelectedAreaOnMap("경상남도 전체");
                        break;
                    case 17: // 제주특별자치도
                        showSelectedAreaOnMap("제주 전체");
                        break;
                    default:
                        showSelectedAreaOnMap("서울 전체"); // 기본값은 서울특별시
                        break;
                }
                updateDistrictSpinner(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateDistrictSpinner(int cityPosition) {
        int districtsArrayId;
        Log.d("인덱스","인덱스 값" + cityPosition);
        switch (cityPosition+1) {
            case 1: districtsArrayId = R.array.districts_서울특별시; break;
            case 2: districtsArrayId = R.array.districts_부산광역시; break;
            case 3: districtsArrayId = R.array.districts_대구광역시; break;
            case 4: districtsArrayId = R.array.districts_인천광역시; break;
            case 5: districtsArrayId = R.array.districts_광주광역시; break;
            case 6: districtsArrayId = R.array.districts_대전광역시; break;
            case 7: districtsArrayId = R.array.districts_울산광역시; break;
            case 8: districtsArrayId = R.array.districts_세종특별자치시; break;
            case 9: districtsArrayId = R.array.districts_경기도; break;
            case 10: districtsArrayId = R.array.districts_강원도; break;
            case 11: districtsArrayId = R.array.districts_충청북도; break;
            case 12: districtsArrayId = R.array.districts_충청남도; break;
            case 13: districtsArrayId = R.array.districts_전라북도; break;
            case 14: districtsArrayId = R.array.districts_전라남도; break;
            case 15: districtsArrayId = R.array.districts_경상북도; break;
            case 16: districtsArrayId = R.array.districts_경상남도; break;
            case 17: districtsArrayId = R.array.districts_제주특별자치도; break;
            default: districtsArrayId = R.array.districts_서울특별시; break;
        }

        List<String> districtsList = new ArrayList<>(Arrays.asList(getResources().getStringArray(districtsArrayId)));

        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, districtsList);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtAdapter);

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = citySpinner.getSelectedItem().toString();
                String selectedDistrict = (String) parent.getItemAtPosition(position);
                showSelectedAreaOnMap(selectedDistrict);
                showSelectedAreaMarkers(selectedCity, selectedDistrict);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        // 서울특별시의 디폴트 좌표 설정
        LatLng seoul = new LatLng(37.5665, 126.9780);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 12));
        loadGeoJsonLayer();
    }

    private List<MarkerDBHelper.Marker> showSelectedAreaMarkers(String city, String district) {
        if (googleMap == null) return null;
        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
        SQLiteDatabase db = markerDBHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE lat BETWEEN ? AND ? AND lng BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(bounds.southwest.latitude),
                String.valueOf(bounds.northeast.latitude),
                String.valueOf(bounds.southwest.longitude),
                String.valueOf(bounds.northeast.longitude)
        });

        List<MarkerDBHelper.Marker> markerList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String snippet = cursor.getString(cursor.getColumnIndex("snippet"));
                @SuppressLint("Range") double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                @SuppressLint("Range") double lng = cursor.getDouble(cursor.getColumnIndex("lng"));
                @SuppressLint("Range") Integer state = cursor.getInt(cursor.getColumnIndex("state"));
                MarkerDBHelper.Marker marker = new MarkerDBHelper.Marker(id, title, snippet, lat, lng, state);
                markerList.add(marker);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();


        for (MarkerDBHelper.Marker marker : markerList) {
            LatLng position = new LatLng(marker.lat, marker.lng);
            if (bounds.contains(position)) {
                googleMap.addMarker(new MarkerOptions().position(position).title(marker.title).snippet(marker.snippet));
            }
        }

        return markerList;
    }



    // MarkerDBHelper.java: Add method to get all markers
    public List<MarkerDBHelper.Marker> getAllMarkers() {
        List<MarkerDBHelper.Marker> markerList = new ArrayList<>();
        SQLiteDatabase db = markerDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int columnIndex = cursor.getColumnIndex("id");
                if (columnIndex != -1) {
                    String id = cursor.getString(columnIndex);
                    @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                    @SuppressLint("Range") String snippet = cursor.getString(cursor.getColumnIndex("snippet"));
                    @SuppressLint("Range") Integer state = cursor.getInt(cursor.getColumnIndex("state"));
                    int latIndex = cursor.getColumnIndex("lat");
                    if (latIndex != -1) {
                        double lat = cursor.getDouble(latIndex);
                        int lngIndex = cursor.getColumnIndex("lng");
                        if (lngIndex != -1) {
                            double lng = cursor.getDouble(lngIndex);
                            MarkerDBHelper.Marker marker = new MarkerDBHelper.Marker(id, title, snippet, lat, lng, state);
                            markerList.add(marker);
                        }

                    }

                }
            }while (cursor.moveToNext()) ;
        }

        cursor.close();
        db.close();
        return markerList;
    }



    private void loadGeoJsonLayer() {
        try {
            // raw 폴더에 있는 GeoJSON 파일을 읽어오기
            InputStream inputStream = getResources().openRawResource(R.raw.filtered_data); // 여기서 district_boundaries는 GeoJSON 파일 이름 (확장자 제외)
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            // 파일 내용을 문자열로 변환 후 JSONObject 생성
            String json = new String(buffer, "UTF-8");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GeoJsonLayer currentLayer; // 현재 표시된 구/군 레이어를 추적
    private GeoJsonLayer previousLayer;

    public void showSelectedAreaOnMap(String selectedDistrict) {
        try {
            if (previousLayer != null) {
                Log.d("UserSearch", "Checking feature: " + selectedDistrict);
                for (GeoJsonFeature feature : previousLayer.getFeatures()) {
                    GeoJsonPolygonStyle transparentStyle = new GeoJsonPolygonStyle();
                    transparentStyle.setFillColor(Color.TRANSPARENT);
                    transparentStyle.setStrokeColor(Color.TRANSPARENT);
                    feature.setPolygonStyle(transparentStyle);
                }
                previousLayer.removeLayerFromMap(); // 이전 레이어 제거 (선택 사항)
            }

            // 모든 지도 요소 제거
            Log.d("UserSearch", "Clearing all map elements.");
            googleMap.clear();  // 모든 마커와 레이어를 초기화

            // GeoJSON 파일 로드하여 새로운 레이어 생성
            GeoJsonLayer layer = new GeoJsonLayer(googleMap, R.raw.filtered_data, getContext());
            boolean districtFound = false;

            // 모든 Feature를 기본적으로 투명하게 설정
            GeoJsonPolygonStyle transparentStyle = new GeoJsonPolygonStyle();
            transparentStyle.setFillColor(Color.TRANSPARENT);
            transparentStyle.setStrokeColor(Color.TRANSPARENT);

            LatLng center = getDistrictCoordinates(selectedDistrict); // 선택된 시/구의 중심 좌표
            double radius = 5000; // 예시로 5km 반경 설정

            for (GeoJsonFeature feature : layer.getFeatures()) {
                String districtName = feature.getProperty("name"); // GeoJSON에서 필드명 확인 필요
                LatLng featureCenter = getFeatureCenter(feature);   // 각 feature의 중심 좌표 계산

                if (feature.getGeometry() instanceof com.google.maps.android.data.geojson.GeoJsonPoint) {
                    GeoJsonPointStyle pointStyle = new GeoJsonPointStyle();
                    pointStyle.setVisible(false);
                    feature.setPointStyle(pointStyle);
                } else if (selectedDistrict.equals(districtName) && isWithinRadius(center, featureCenter, radius)) {
                    GeoJsonPolygonStyle selectedStyle = new GeoJsonPolygonStyle();
                    selectedStyle.setStrokeColor(Color.RED);
                    selectedStyle.setFillColor(0x3FFF0000); // 반투명 빨간색
                    feature.setPolygonStyle(selectedStyle);
                    districtFound = true;
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 13));
                } else {
                    feature.setPolygonStyle(transparentStyle);
                }
            }

            // 구/군이 존재하면 레이어 추가 및 현재 레이어로 설정
            if (districtFound) {
                layer.addLayerToMap();
                previousLayer = currentLayer;
                currentLayer = layer; // 현재 레이어 추적
                Log.d("UserSearch", "Layer added to map for district: " + selectedDistrict);
            } else {
                Log.e("UserSearch", "Selected district not found in GeoJSON data.");
            }

        } catch (IOException e) {
            Log.e("UserSearch", "IOException while loading GeoJSON file.", e);
        } catch (JSONException e) {
            Log.e("UserSearch", "JSONException while parsing GeoJSON data.", e);
        } catch (Exception e) {
            Log.e("UserSearch", "Unexpected error occurred: " + e.getMessage(), e);
        }

    }


    private boolean isWithinRadius(LatLng center, LatLng target, double radius) {
        float[] results = new float[1];
        Location.distanceBetween(
                center.latitude, center.longitude,
                target.latitude, target.longitude,
                results
        );
        return results[0] <= radius;
    }


    private LatLng getFeatureCenter(GeoJsonFeature feature) {
        List<LatLng> coordinates = new ArrayList<>();
        if (feature.getGeometry() instanceof GeoJsonPolygon) {
            GeoJsonPolygon polygon = (GeoJsonPolygon) feature.getGeometry();
            for (List<LatLng> polygonCoords : polygon.getCoordinates()) {
                coordinates.addAll(polygonCoords);
            }
        }

        double latSum = 0.0;
        double lngSum = 0.0;
        for (LatLng coord : coordinates) {
            latSum += coord.latitude;
            lngSum += coord.longitude;
        }

        return new LatLng(latSum / coordinates.size(), lngSum / coordinates.size());
    }


    private LatLng getDistrictCoordinates(String district) {
        String cityName = citySpinner.getSelectedItem().toString();

        if (cityName.equals("서울특별시")) {
            switch (district) {
                case "종로구":
                    return new LatLng(37.573, 126.98);
                case "중구":
                    return new LatLng(37.563, 126.997);
                case "용산구":
                    return new LatLng(37.5326, 126.9905);
                case "성동구":
                    return new LatLng(37.5634, 127.0363);
                case "광진구":
                    return new LatLng(37.5384, 127.0822);
                case "동대문구":
                    return new LatLng(37.5744, 127.0396);
                case "중랑구":
                    return new LatLng(37.6063, 127.0927);
                case "성북구":
                    return new LatLng(37.5894, 127.0164);
                case "강북구":
                    return new LatLng(37.6398, 127.0254);
                case "도봉구":
                    return new LatLng(37.6688, 127.0474);
                case "노원구":
                    return new LatLng(37.6544, 127.0563);
                case "은평구":
                    return new LatLng(37.6176, 126.9227);
                case "서대문구":
                    return new LatLng(37.5792, 126.9368);
                case "마포구":
                    return new LatLng(37.5663, 126.9018);
                case "양천구":
                    return new LatLng(37.5164, 126.8665);
                case "강서구":
                    return new LatLng(37.5509, 126.8495);
                case "구로구":
                    return new LatLng(37.4955, 126.8875);
                case "금천구":
                    return new LatLng(37.4567, 126.8958);
                case "영등포구":
                    return new LatLng(37.5263, 126.8962);
                case "동작구":
                    return new LatLng(37.5124, 126.9392);
                case "관악구":
                    return new LatLng(37.4784, 126.9516);
                case "서초구":
                    return new LatLng(37.4836, 127.0327);
                case "강남구":
                    return new LatLng(37.5172, 127.0473);
                case "송파구":
                    return new LatLng(37.5146, 127.1056);
                case "강동구":
                    return new LatLng(37.5301, 127.1238);
                default : return new LatLng(37.5665, 126.9780);
            }
        }
        if (cityName.equals("부산광역시")) {
            switch (district) {
            // 부산광역시
                case "중구": return new LatLng(35.1067, 129.0329);
                case "서구": return new LatLng(35.097, 129.0247);
                case "동구": return new LatLng(35.1296, 129.0454);
                case "영도구": return new LatLng(35.0918, 129.0678);
                case "부산진구": return new LatLng(35.1623, 129.0455);
                case "동래구": return new LatLng(35.205, 129.0787);
                case "남구": return new LatLng(35.1363, 129.0843);
                case "북구": return new LatLng(35.2092, 129.0323);
                case "해운대구": return new LatLng(35.1632, 129.1635);
                case "사하구": return new LatLng(35.1046, 128.9745);
                case "금정구": return new LatLng(35.2429, 129.0929);
                case "강서구": return new LatLng(35.2122, 128.9803);
                case "연제구": return new LatLng(35.1859, 129.0815);
                case "수영구": return new LatLng(35.1428, 129.1131);
                case "사상구": return new LatLng(35.15, 128.99);
                case "기장군": return new LatLng(35.244, 129.2225);
                default: return new LatLng(35.1796, 129.0756);
            }
        }

        if(cityName.equals("대구광역시")) {
            switch (district) {
                case "중구": return new LatLng(35.8694, 128.6062);
                case "동구": return new LatLng(35.8831, 128.6353);
                case "서구": return new LatLng(35.8714, 128.5594);
                case "남구": return new LatLng(35.8451, 128.5932);
                case "북구": return new LatLng(35.8896, 128.5827);
                case "수성구": return new LatLng(35.8589, 128.6302);
                case "달서구": return new LatLng(35.8296, 128.5325);
                case "달성군": return new LatLng(35.7745, 128.4316);
                default: return new LatLng(35.8714, 128.6014);
            }
        }

        if(cityName.equals("인천광역시")) {
            switch (district) {
                case "중구": return new LatLng(37.4736, 126.6218);
                case "동구": return new LatLng(37.4725, 126.6286);
                case "미추홀구": return new LatLng(37.4627, 126.6501);
                case "연수구": return new LatLng(37.4109, 126.6789);
                case "남동구": return new LatLng(37.4472, 126.7318);
                case "부평구": return new LatLng(37.4989, 126.7227);
                case "계양구": return new LatLng(37.5381, 126.7373);
                case "서구": return new LatLng(37.5454, 126.6759);
                case "강화군": return new LatLng(37.7465, 126.4875);
                case "옹진군": return new LatLng(37.4473, 126.6357);
                default: return new LatLng(37.4563, 126.7052);
            }

        }

        if (cityName.equals("광주광역시")) {
            switch (district) {
                case "동구": return new LatLng(35.1446, 126.9235);
                case "서구": return new LatLng(35.1528, 126.8904);
                case "남구": return new LatLng(35.1334, 126.9023);
                case "북구": return new LatLng(35.1743, 126.9119);
                case "광산구": return new LatLng(35.139, 126.7931);
                default: return new LatLng(35.1595, 126.8526);
            }
        }

        if (cityName.equals("대전광역시")) {
            switch (district) {
                case "동구": return new LatLng(36.3372, 127.4545);
                case "중구": return new LatLng(36.3257, 127.4197);
                case "서구": return new LatLng(36.3552, 127.3834);
                case "유성구": return new LatLng(36.3622, 127.3567);
                case "대덕구": return new LatLng(36.3731, 127.4297);
                default: return new LatLng(36.3504, 127.3845);
            }
        }

        if (cityName.equals("울산광역시")) {
            switch (district) {
                case "중구": return new LatLng(35.5681, 129.3321);
                case "남구": return new LatLng(35.5439, 129.3277);
                case "동구": return new LatLng(35.4864, 129.4282);
                case "북구": return new LatLng(35.5824, 129.3611);
                case "울주군": return new LatLng(35.4359, 129.2422);
                default: return new LatLng(35.5384, 129.3114);
            }
        }

        if (cityName.equals("세종특별자치시")) {
            return new LatLng(36.4876, 127.2817);
        }

        if (cityName.equals("경기도")) {
            switch (district) {
                case "수원시": return new LatLng(37.2635, 127.0286);
                case "성남시": return new LatLng(37.4201, 127.1265);
                case "안양시": return new LatLng(37.3943, 126.9568);
                case "안산시": return new LatLng(37.3219, 126.8309);
                case "용인시": return new LatLng(37.2411, 127.1776);
                case "부천시": return new LatLng(37.5034, 126.766);
                case "광명시": return new LatLng(37.4784, 126.8644);
                case "평택시": return new LatLng(36.9921, 127.1122);
                case "과천시": return new LatLng(37.4293, 126.9877);
                case "오산시": return new LatLng(37.1497, 127.0771);
                case "시흥시": return new LatLng(37.3802, 126.8028);
                case "군포시": return new LatLng(37.3617, 126.9356);
                case "의왕시": return new LatLng(37.3445, 126.9682);
                case "하남시": return new LatLng(37.5393, 127.2141);
                case "이천시": return new LatLng(37.2792, 127.4425);
                case "안성시": return new LatLng(37.0089, 127.2795);
                case "김포시": return new LatLng(37.6154, 126.7152);
                case "화성시": return new LatLng(37.1996, 126.831);
                case "광주시": return new LatLng(37.4292, 127.255);
                case "양주시": return new LatLng(37.7854, 127.0457);
                case "포천시": return new LatLng(37.8942, 127.2007);
                case "여주시": return new LatLng(37.2982, 127.6365);
                case "연천군": return new LatLng(38.096, 127.0745);
                case "가평군": return new LatLng(37.8315, 127.509);
                case "양평군": return new LatLng(37.4915, 127.4879);
                default:return new LatLng(37.4138, 127.5183);
            }
        }

        if (cityName.equals("강원도")) {
            switch (district) {
                case "춘천시": return new LatLng(37.8813, 127.7298);
                case "원주시": return new LatLng(37.3422, 127.9195);
                case "강릉시": return new LatLng(37.7519, 128.8761);
                case "동해시": return new LatLng(37.5245, 129.1142);
                case "태백시": return new LatLng(37.1648, 128.9859);
                case "속초시": return new LatLng(38.207, 128.5911);
                case "삼척시": return new LatLng(37.4499, 129.1658);
                case "홍천군": return new LatLng(37.697, 127.888);
                case "횡성군": return new LatLng(37.488, 127.9842);
                case "영월군": return new LatLng(37.1837, 128.4619);
                case "평창군": return new LatLng(37.3704, 128.3904);
                case "정선군": return new LatLng(37.3805, 128.6607);
                case "철원군": return new LatLng(38.1464, 127.3135);
                case "화천군": return new LatLng(38.1061, 127.7081);
                case "양구군": return new LatLng(38.1094, 127.9899);
                case "인제군": return new LatLng(38.0695, 128.1706);
                case "고성군": return new LatLng(38.3808, 128.4677);
                case "양양군": return new LatLng(38.0754, 128.6198);
                default:return new LatLng(37.8228, 128.1555);
            }
        }

        if (cityName.equals("충청북도")) {
            switch (district) {
                case "청주시": return new LatLng(36.6424, 127.4889);
                case "충주시": return new LatLng(36.991, 127.9264);
                case "제천시": return new LatLng(37.1325, 128.1915);
                case "보은군": return new LatLng(36.4897, 127.7299);
                case "옥천군": return new LatLng(36.3064, 127.5712);
                case "영동군": return new LatLng(36.175, 127.7834);
                case "진천군": return new LatLng(36.8573, 127.443);
                case "괴산군": return new LatLng(36.8152, 127.7998);
                case "음성군": return new LatLng(36.9406, 127.6904);
                case "단양군": return new LatLng(36.9841, 128.3658);
                default: return new LatLng(36.6357, 127.4914);
            }
        }

        if (cityName.equals("충청남도")) {
            switch (district) {
                case "천안시": return new LatLng(36.8151, 127.1139);
                case "공주시": return new LatLng(36.4467, 127.119);
                case "보령시": return new LatLng(36.3335, 126.6121);
                case "아산시": return new LatLng(36.7899, 127.001);
                case "서산시": return new LatLng(36.7845, 126.4505);
                case "논산시": return new LatLng(36.1871, 127.098);
                case "계룡시": return new LatLng(36.2744, 127.2485);
                case "당진시": return new LatLng(36.8922, 126.6294);
                case "금산군": return new LatLng(36.1086, 127.4887);
                case "부여군": return new LatLng(36.2755, 126.9094);
                case "서천군": return new LatLng(36.0808, 126.6913);
                case "청양군": return new LatLng(36.4544, 126.8049);
                case "홍성군": return new LatLng(36.6017, 126.6604);
                case "예산군": return new LatLng(36.6827, 126.8507);
                case "태안군": return new LatLng(36.7457, 126.2979);
                default:return new LatLng(36.5184, 126.8002);
            }
        }

        if (cityName.equals("전라북도")) {
            switch (district) {
                case "전주시": return new LatLng(35.8242, 127.147);
                case "군산시": return new LatLng(35.9673, 126.7365);
                case "익산시": return new LatLng(35.9483, 126.9572);
                case "정읍시": return new LatLng(35.5694, 126.8576);
                case "남원시": return new LatLng(35.4164, 127.3903);
                case "김제시": return new LatLng(35.8034, 126.8802);
                case "완주군": return new LatLng(35.9049, 127.1623);
                case "진안군": return new LatLng(35.7917, 127.4246);
                case "무주군": return new LatLng(36.0068, 127.6607);
                case "장수군": return new LatLng(35.6479, 127.5214);
                case "임실군": return new LatLng(35.6172, 127.2835);
                case "순창군": return new LatLng(35.3744, 127.1377);
                case "고창군": return new LatLng(35.4351, 126.7015);
                case "부안군": return new LatLng(35.7315, 126.7335);
                default:return new LatLng(35.7175, 127.153);
            }
        }

        if (cityName.equals("전라남도")) {
            switch (district) {
                case "목포시": return new LatLng(34.8118, 126.392);
                case "여수시": return new LatLng(34.7604, 127.6622);
                case "순천시": return new LatLng(34.9507, 127.4879);
                case "나주시": return new LatLng(35.0151, 126.7115);
                case "광양시": return new LatLng(34.9407, 127.6958);
                case "담양군": return new LatLng(35.3174, 126.9873);
                case "곡성군": return new LatLng(35.2814, 127.2917);
                case "구례군": return new LatLng(35.2034, 127.4629);
                case "고흥군": return new LatLng(34.6079, 127.2849);
                case "보성군": return new LatLng(34.7713, 127.0794);
                case "화순군": return new LatLng(35.0641, 127.0063);
                case "장흥군": return new LatLng(34.681, 126.9074);
                case "강진군": return new LatLng(34.6421, 126.7675);
                case "해남군": return new LatLng(34.571, 126.5987);
                case "영암군": return new LatLng(34.8009, 126.696);
                case "무안군": return new LatLng(34.9902, 126.4813);
                case "함평군": return new LatLng(35.1237, 126.5167);
                case "영광군": return new LatLng(35.2774, 126.5121);
                case "장성군": return new LatLng(35.3017, 126.7844);
                case "완도군": return new LatLng(34.311, 126.7568);
                case "진도군": return new LatLng(34.4849, 126.2635);
                case "신안군": return new LatLng(34.8281, 126.1085);
                default:return new LatLng(34.8679, 126.991);
            }
        }

        if (cityName.equals("경상북도")) {
            switch (district) {
                case "포항시": return new LatLng(36.019, 129.3435);
                case "경주시": return new LatLng(35.8562, 129.2241);
                case "김천시": return new LatLng(36.1394, 128.1136);
                case "안동시": return new LatLng(36.5684, 128.7294);
                case "구미시": return new LatLng(36.1195, 128.3446);
                case "영주시": return new LatLng(36.8057, 128.6241);
                case "영천시": return new LatLng(35.9733, 128.9381);
                case "상주시": return new LatLng(36.415, 128.1594);
                case "문경시": return new LatLng(36.5865, 128.1993);
                case "경산시": return new LatLng(35.8259, 128.7415);
                case "군위군": return new LatLng(36.2397, 128.5728);
                case "의성군": return new LatLng(36.3528, 128.6978);
                case "청송군": return new LatLng(36.4351, 129.0576);
                case "영양군": return new LatLng(36.6645, 129.1124);
                case "영덕군": return new LatLng(36.4157, 129.3656);
                case "청도군": return new LatLng(35.647, 128.7363);
                case "고령군": return new LatLng(35.7283, 128.2621);
                case "성주군": return new LatLng(35.9193, 128.2825);
                case "칠곡군": return new LatLng(35.9959, 128.4019);
                case "예천군": return new LatLng(36.6561, 128.4535);
                case "봉화군": return new LatLng(36.8936, 128.7328);
                case "울진군": return new LatLng(36.993, 129.4002);
                case "울릉군": return new LatLng(37.4864, 130.9045);
                default:return new LatLng(36.5758, 128.5056);
            }
        }

        if(cityName.equals("경상남도")) {
            switch (district) {
                case "창원시": return new LatLng(35.227, 128.6811);
                case "진주시": return new LatLng(35.1802, 128.1076);
                case "통영시": return new LatLng(34.8544, 128.4335);
                case "사천시": return new LatLng(35.0038, 128.0647);
                case "김해시": return new LatLng(35.2285, 128.8898);
                case "밀양시": return new LatLng(35.5037, 128.7474);
                case "거제시": return new LatLng(34.8809, 128.6216);
                case "양산시": return new LatLng(35.335, 129.0374);
                case "의령군": return new LatLng(35.3223, 128.2612);
                case "함안군": return new LatLng(35.272, 128.4064);
                case "창녕군": return new LatLng(35.5436, 128.4927);
                case "고성군": return new LatLng(34.9738, 128.3223);
                case "남해군": return new LatLng(34.8371, 127.8925);
                case "하동군": return new LatLng(35.0674, 127.7506);
                case "산청군": return new LatLng(35.4158, 127.8735);
                case "함양군": return new LatLng(35.5207, 127.7254);
                case "거창군": return new LatLng(35.6865, 127.9095);
                case "합천군": return new LatLng(35.5664, 128.1653);
                default:return new LatLng(35.2383, 128.6928);
            }
        }

        if(cityName.equals("제주특별자치도")) {
            switch (district) {
                case "제주시": return new LatLng(33.4996, 126.5312);
                case "서귀포시": return new LatLng(33.253, 126.5618);
                default:return new LatLng(33.3617, 126.5292);
            }
        }

        return new LatLng(37.5301, 127.1238);
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
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}