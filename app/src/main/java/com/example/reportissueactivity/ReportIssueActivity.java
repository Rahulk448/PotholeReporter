package com.example.reportissueactivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.reportissueactivity.network.ApiService;
import com.example.reportissueactivity.network.RetrofitClient;

public class ReportIssueActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    AutoCompleteTextView issueTypeSpinner;
    EditText issueDescription, otherIssueInput;
    TextInputLayout otherIssueLayout;
    Button submitIssueBtn;
    ImageView issueImage;
    MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;
    private String currentLocationName = "Unknown Location";
    private ArrayList<Issue> issues = new ArrayList<>();
    private CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private DrawerLayout drawerLayout;
    private Bitmap selectedBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ImageView menuBtn = findViewById(R.id.menuBtn);

        menuBtn.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_rate_us) {
                rateApp();
            } else if (id == R.id.menu_mode_change) {
                int currentMode = AppCompatDelegate.getDefaultNightMode();
                if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            } else if (id == R.id.menu_share_app) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Road Guardian App");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this app to report road issues: https://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            } else if (id == R.id.menu_contact_us) {
                showContactDialog();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        issueTypeSpinner = findViewById(R.id.issueTypeSpinner);
        otherIssueLayout = findViewById(R.id.otherIssueLayout);
        otherIssueInput = findViewById(R.id.otherIssueInput);
        issueDescription = findViewById(R.id.issueDescription);
        submitIssueBtn = findViewById(R.id.submitIssueBtn);
        issueImage = findViewById(R.id.issueImage);
        mapView = findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        String[] issueTypes = {"Pothole", "Water on Road", "Traffic Block", "Accident", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, issueTypes);
        issueTypeSpinner.setAdapter(adapter);

        issueTypeSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selection = adapter.getItem(position);
            if ("Others".equals(selection)) {
                otherIssueLayout.setVisibility(View.VISIBLE);
            } else {
                otherIssueLayout.setVisibility(View.GONE);
            }
        });

        // Camera Launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getExtras() != null) {
                            selectedBitmap = (Bitmap) data.getExtras().get("data");
                            issueImage.setImageBitmap(selectedBitmap);
                            issueImage.setPadding(0, 0, 0, 0);
                        }
                    }
                });

        // Gallery Launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri imageUri = data.getData();
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    selectedBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), imageUri));
                                } else {
                                    selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                }
                                issueImage.setImageBitmap(selectedBitmap);
                                issueImage.setPadding(0, 0, 0, 0);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        issueImage.setOnClickListener(v -> showUploadOptions());

        submitIssueBtn.setOnClickListener(v -> submitIssue());
    }

    private void getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                currentLocationName = address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            currentLocationName = "Lat: " + latLng.latitude + ", Lon: " + latLng.longitude;
        }
    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void showContactDialog() {
        String email = "support@roadguardian.com";
        String phone = "8885477996";

        new MaterialAlertDialogBuilder(this)
                .setTitle("Contact Us")
                .setMessage("Email: " + email + "\nPhone: " + phone)
                .setPositiveButton("Email Us", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + email));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - Road Guardian");
                    startActivity(Intent.createChooser(intent, "Send Email"));
                })
                .setNeutralButton("Call Us", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showUploadOptions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_upload_options, null);
        bottomSheetDialog.setContentView(view);

        LinearLayout optionCamera = view.findViewById(R.id.optionCamera);
        LinearLayout optionGallery = view.findViewById(R.id.optionGallery);
        LinearLayout optionCancel = view.findViewById(R.id.optionCancel);

        optionCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(cameraIntent);
            }
            bottomSheetDialog.dismiss();
        });

        optionGallery.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryIntent);
            bottomSheetDialog.dismiss();
        });

        optionCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }


    private void submitIssue() {
        String selectedIssue = issueTypeSpinner.getText().toString();
        String description = issueDescription.getText().toString();

        if (selectedIssue.isEmpty()) {
            Toast.makeText(this, "Please select an issue category", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("Others".equals(selectedIssue)) {
            String otherDetail = otherIssueInput.getText().toString().trim();
            if (otherDetail.isEmpty()) {
                Toast.makeText(this, "Please specify the issue", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedIssue = "Other: " + otherDetail;
        }

        if (currentLocation == null) {
            Toast.makeText(this, "Location not available. Please fetch location first.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> issueData = new HashMap<>();
        issueData.put("title", selectedIssue);
        issueData.put("description", description);
        issueData.put("latitude", currentLocation.latitude);
        issueData.put("longitude", currentLocation.longitude);
        issueData.put("location_name", currentLocationName);

        String issueType = selectedIssue.toLowerCase().replace(" ", "_");
        issueData.put("issue_type", issueType);

        if (selectedBitmap != null) {
            issueData.put("image", encodeImage(selectedBitmap));
        }

        ApiService apiService = RetrofitClient
                .getClient("https://pothole-backend-0je2.onrender.com/")
                .create(ApiService.class);

        apiService.reportIssue(issueData).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ReportIssueActivity.this, "Issue Reported Successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ReportIssueActivity.this, ViewissueActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ReportIssueActivity.this, "Failed to report issue", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(ReportIssueActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void checkAndFetchLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm != null && !lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(this)
                    .setTitle("Location Services Not Enabled")
                    .setMessage("Please enable location services to use this feature.")
                    .setPositiveButton("Go to Settings", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            fetchLocationWithPermissionCheck();
        }
    }

    private void fetchLocationWithPermissionCheck() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            getAddressFromLatLng(currentLocation);
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions().position(currentLocation).title(currentLocationName));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        } else {
                            Toast.makeText(this, "Could not retrieve location. Please ensure location is enabled and try again.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(ReportIssueActivity.this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        checkAndFetchLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (googleMap != null && currentLocation == null) {
            checkAndFetchLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        cancellationTokenSource.cancel();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(cameraIntent);
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkAndFetchLocation();
            } else {
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
