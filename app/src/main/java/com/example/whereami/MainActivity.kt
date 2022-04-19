package com.example.whereami

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatCallback
import androidx.core.app.ActivityCompat
import com.example.whereami.databinding.ActivityMainBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

lateinit var binding : ActivityMainBinding
lateinit var locationRequest: LocationRequest
lateinit var fusedLocationProviderClient: FusedLocationProviderClient
lateinit var locationString : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        binding.getLocationButton.setOnClickListener (){
            // check for self permission
            checkLocationPermission()
        }



    }

    private fun checkLocationPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // permission is allready granted
            checkGPS()

        }else{

            // permission is not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)

        }
    }

    private fun checkGPS(){
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(this.applicationContext).checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                // when GPS is on
                val response = task.getResult(
                    ApiException::class.java
                )

                getUserLocation()

            }catch (e: ApiException){
                // when GPS is off

                e.printStackTrace()

                when(e.statusCode){

                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->try {

                        // here we send a request for enabling the GPS
                        val resolveApiException = e as ResolvableApiException
                        resolveApiException.startResolutionForResult(this, 200)

                    }catch (sendIntentException : IntentSender.SendIntentException){


                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    // when the setting is unavailable
                }
                }

            }

        }



    }

    private fun getUserLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task->

            val location = task.result

            if(location != null){
               try{
                   val geocoder = Geocoder(this, Locale.getDefault())

                   val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                   //show address in textView location_holder
                   locationString = address[0].getAddressLine(0)
                   binding.locationHolder.text = locationString



               }catch (e: IOException){

               }
            }

        }

    }
}