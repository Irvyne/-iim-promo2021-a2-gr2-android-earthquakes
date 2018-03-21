package ninja.irvyne.earthquakes

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ninja.irvyne.earthquakes.api.EarthquakeService
import ninja.irvyne.earthquakes.api.model.EarthquakeData
import org.jetbrains.anko.longToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mService: EarthquakeService
    private var mEarthquakeChoice: Int = -1
    private var mSelectedEarthquakeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mEarthquakeChoice = intent.getIntExtra(EXTRA_CHOICE, mEarthquakeChoice)
        mSelectedEarthquakeId = intent.getStringExtra(EXTRA_EARTHQUAKE_ID)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Fetch Api
        val retrofit = Retrofit.Builder()
                .baseUrl("https://earthquake.usgs.gov/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        mService = retrofit.create<EarthquakeService>(EarthquakeService::class.java)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        val request = when (mEarthquakeChoice) {
            0 -> mService.listLittleEarthquakes()
            1 -> mService.listMediumEarthquakes()
            2 -> mService.listStrongEarthquakes()
            3 -> mService.listSignificantEarthquakes()
            4 -> mService.listAllEarthquakes()
            else -> throw Exception("No correct value for mEarthquakeChoice, 0..4 accepted but '$mEarthquakeChoice' given!")
        }

        request.enqueue(object : Callback<EarthquakeData> {
            override fun onFailure(call: Call<EarthquakeData>?, t: Throwable?) {
                Log.e(TAG, "An error occurred with listSignificantEarthquakes(), error: $t")
                longToast("Oups, an error occurred 🤟")
            }

            override fun onResponse(call: Call<EarthquakeData>?, response: Response<EarthquakeData>?) {
                Log.d(TAG, "Success, ${response?.body()}")
                longToast("Success 🍾")

                response?.body()?.let {
                    it.features?.forEach { feature ->
                        feature.geometry?.coordinates?.let {

                            val color = if (feature.properties?.mag == null) BitmapDescriptorFactory.HUE_ROSE else when (feature.properties?.mag) {
                                in 0.0..1.0 -> BitmapDescriptorFactory.HUE_GREEN
                                in 1.0..2.5 -> BitmapDescriptorFactory.HUE_AZURE
                                in 2.5..4.5 -> BitmapDescriptorFactory.HUE_BLUE
                                in 4.5..6.5 -> BitmapDescriptorFactory.HUE_ORANGE
                                else -> BitmapDescriptorFactory.HUE_RED
                            }

                            mMap.addMarker(MarkerOptions().position(LatLng(it[1], it[0])).title(feature.properties?.title).icon(BitmapDescriptorFactory.defaultMarker(color))).apply {
                                if (mSelectedEarthquakeId != null && mSelectedEarthquakeId == feature.id) {
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 4f))
                                    showInfoWindow()
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    companion object {
        private const val TAG = "MapsActivity"
        const val EXTRA_CHOICE = "$TAG.choice"
        const val EXTRA_EARTHQUAKE_ID = "$TAG.earthquake_id"
    }
}
