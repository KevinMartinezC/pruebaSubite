package com.ksp.subitesv.actividades.cliente;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.ksp.subitesv.R;
import com.ksp.subitesv.proveedores.ProveedorGeoFire;

public class SolicitarConductorActivity extends AppCompatActivity {

    private LottieAnimationView mAnimation;
    private TextView mTextviewLookingFor;
    private Button mButtonCalcelRequest;
    private ProveedorGeoFire mGeofireProvider;

    private  double mExtraOriginLat;
    private double mExtraOriginLng;
    private LatLng mOriginLanLng;
    private double mRadius = 0.1;
    private boolean mDriverFound = false;
    private String mIdDriverFound = "";
    private LatLng mDriverFoundLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_conductor);

        mAnimation = findViewById(R.id.animacion);
        mTextviewLookingFor = findViewById(R.id.textViewLookingFor);
        mButtonCalcelRequest = findViewById(R.id.btnCancelRequest);

        mAnimation.playAnimation();
        mExtraOriginLat = getIntent().getDoubleExtra("origin_lat",0);
        mExtraOriginLng = getIntent().getDoubleExtra("origin_lng",0);
        mOriginLanLng = new LatLng(mExtraOriginLat,mExtraOriginLng);
        mGeofireProvider = new ProveedorGeoFire();
        getClosestDriver();
    }
    private void getClosestDriver(){
        mGeofireProvider.obtenerConductoresActivos(mOriginLanLng, mRadius).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!mDriverFound){
                    mDriverFound = true;
                    mIdDriverFound = key;
                    mDriverFoundLatLng = new LatLng(location.latitude, location.longitude);
                    mTextviewLookingFor.setText("CONDUCTOR ENCONTRADO\nESPERANDO RESPUESTA");

                    Log.d("DRIVER","ID:"+ mIdDriverFound);

                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //iNGRESA CUANDO TERMINA LA BUSQUEDA DEL CONDUCTOR EN UN RADIO DE 0.1 KM
                if(!mDriverFound){
                    mRadius = mRadius + 0.1;

                    //No encontro ningun conductor
                    if(mRadius>5){
                        mTextviewLookingFor.setText("NO SE ENCONTRO UN CONDUCTOR");
                        Toast.makeText(SolicitarConductorActivity.this, "NO SE ENCONTRO UN CONDUCTOR", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        getClosestDriver();
                    }
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

}