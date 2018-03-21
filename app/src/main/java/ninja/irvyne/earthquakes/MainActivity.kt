package ninja.irvyne.earthquakes

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainSpinner.adapter = ArrayAdapter.createFromResource(this, R.array.earthquakes_list, android.R.layout.simple_spinner_item).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        mainButtonList.setOnClickListener {
            Log.d(TAG, "Click on element '${mainSpinner.selectedItem}' at position ${mainSpinner.selectedItemPosition}")

            startActivity(Intent(this, ListActivity::class.java).apply {
                putExtra(ListActivity.EXTRA_CHOICE, mainSpinner.selectedItemPosition)
            })
        }

        mainButtonMaps.setOnClickListener {
            Log.d(TAG, "Click on element '${mainSpinner.selectedItem}' at position ${mainSpinner.selectedItemPosition}")

            startActivity(Intent(this, MapsActivity::class.java).apply {
                putExtra(MapsActivity.EXTRA_CHOICE, mainSpinner.selectedItemPosition)
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        Log.d(TAG, "onSaveInstanceState")
        outState?.putInt(STATE_SPINNER_POSITION, mainSpinner.selectedItemPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        Log.d(TAG, "onRestoreInstanceState")
        savedInstanceState?.getInt(STATE_SPINNER_POSITION)?.let { mainSpinner?.setSelection(it) }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val STATE_SPINNER_POSITION = "$TAG.state.spinner_position"
    }
}
