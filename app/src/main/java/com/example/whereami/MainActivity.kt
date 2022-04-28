package com.example.whereami

import android.Manifest
import android.content.DialogInterface
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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


        binding.getLocationButton.setOnClickListener {
            // check for self permission
            checkLocationPermission()
        }

        binding.getContacts.setOnClickListener {
            checkContactPermission()
        }


    }
    private fun checkContactPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "contact list available", Toast.LENGTH_SHORT).show()
            // permission is allready granted
            //getContacts()

        }else{
            requestContactPermission()
        }
    }


    private fun requestContactPermission(){
        if((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) or (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED)){
            Toast.makeText(this, "you have denied permission to location", Toast.LENGTH_SHORT).show()
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Permission needed")
            builder.setMessage("This permission is needed to get current location and send it later on.")
            builder.setPositiveButton("ok", DialogInterface.OnClickListener { dialogInterface, witch ->
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_CONTACTS), 100)
            })
            builder.setNegativeButton("cancel", DialogInterface.OnClickListener { dialogInterface, witch ->
                dialogInterface.dismiss()
            })
            builder.create().show()
        }
        else{
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_CONTACTS), 100)
        }
    }

    private fun getContacts() {
        TODO("Not yet implemented")

    }
    private fun checkLocationPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "location available", Toast.LENGTH_SHORT).show()
            // permission is allready granted
            checkGPS()

        }else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission(){
        if((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) or (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)){
            Toast.makeText(this, "you have denied permission to location", Toast.LENGTH_SHORT).show()
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Permission needed")
            builder.setMessage("This permission is needed to get current location and send it later on.")
            builder.setPositiveButton("ok", DialogInterface.OnClickListener { dialogInterface, witch ->
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            })
            builder.setNegativeButton("cancel", DialogInterface.OnClickListener { dialogInterface, witch ->
                dialogInterface.dismiss()
            })
            builder.create().show()
        }
        else{
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
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

            val location = task.result  // location is the lastLocation given by the client

            if(location != null){ // check that a location was given i.e not null
               try{ // translate location to address using geocoder object set to default Locale for the phone
                   val geocoder = Geocoder(this, Locale.getDefault())

                   val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                   //show address in textView location_holder and save address to the lateint var for later use
                   locationString = address[0].getAddressLine(0)
                   binding.locationHolder.text = locationString



               }catch (e: IOException){

               }
            }

        }

    }
}