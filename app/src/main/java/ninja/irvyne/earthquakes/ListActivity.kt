package ninja.irvyne.earthquakes

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_main.*
import ninja.irvyne.earthquakes.api.EarthquakeService
import ninja.irvyne.earthquakes.api.model.EarthquakeData
import ninja.irvyne.earthquakes.api.model.Feature
import org.jetbrains.anko.longToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListActivity :
        AppCompatActivity(),
        EarthquakeFeatureAdapter.OnEarthquakeFeatureAdapterInteraction {

    private lateinit var mService: EarthquakeService
    private var mEarthquakeChoice: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        mEarthquakeChoice = intent.getIntExtra(EXTRA_CHOICE, mEarthquakeChoice)

        listRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ListActivity)
        }

        // Fetch Api
        val retrofit = Retrofit.Builder()
                .baseUrl("https://earthquake.usgs.gov/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        mService = retrofit.create<EarthquakeService>(EarthquakeService::class.java)

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

                val earthquakes = response?.body()?.features ?: arrayListOf()

                listRecyclerView.apply {
                    adapter = EarthquakeFeatureAdapter(context, earthquakes, this@ListActivity)
                }
            }
        })
    }

    override fun onEarthquakeSelected(earthquake: Feature) {
        Log.d(TAG, "Click on $earthquake")

        startActivity(Intent(this, MapsActivity::class.java).apply {
            putExtra(MapsActivity.EXTRA_CHOICE, mEarthquakeChoice)
            putExtra(MapsActivity.EXTRA_EARTHQUAKE_ID, earthquake.id!!)
        })
    }

    companion object {
        private const val TAG = "ListActivity"
        const val EXTRA_CHOICE = "$TAG.choice"
    }
}
