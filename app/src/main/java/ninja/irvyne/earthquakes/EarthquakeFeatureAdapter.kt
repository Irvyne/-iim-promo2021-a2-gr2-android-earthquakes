package ninja.irvyne.earthquakes

import android.content.Context
import android.graphics.Color
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ninja.irvyne.earthquakes.api.model.Feature
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*


class EarthquakeFeatureAdapter(
        private val data: List<Feature>,
        private val listener: OnEarthquakeFeatureAdapterInteraction
) : RecyclerView.Adapter<EarthquakeFeatureAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_earthquake_feature, parent, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.earthquake = this.data[position]


        val backgroundColor = if (holder.earthquake.properties?.mag == null) Color.GRAY else when (holder.earthquake.properties?.mag) {
            in 0.0..1.0 -> Color.GREEN
            in 1.0..2.5 -> Color.CYAN
            in 2.5..4.5 -> Color.BLUE
            in 4.5..6.5 -> Color.MAGENTA
            else -> Color.RED
        }

        DrawableCompat.setTint(holder.magnitude.background, backgroundColor)
        holder.magnitude.text = String.format("%.1f", holder.earthquake.properties?.mag ?: 0F)

        holder.title.text = holder.earthquake.properties?.place ?: "unknown"
        holder.date.text = Instant
                .ofEpochMilli(holder.earthquake.properties?.time ?: 0)
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("E, MMMM d, YYYY 'at' h:m a").withLocale(Locale.getDefault()))

        holder.view.setOnClickListener {
            listener.onEarthquakeSelected(holder.earthquake)
        }
    }

    interface OnEarthquakeFeatureAdapterInteraction {
        fun onEarthquakeSelected(earthquake: Feature)
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var earthquake: Feature

        val magnitude: TextView = view.findViewById(R.id.earthquakeMagnitude)
        val title: TextView = view.findViewById(R.id.earthquakeTitle)
        val date: TextView = view.findViewById(R.id.earthquakeDate)
    }
}