package ninja.irvyne.earthquakes.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Feature(
        @SerializedName("type")
        @Expose
        var type: String? = null,

        @SerializedName("properties")
        @Expose
        var properties: Properties? = null,

        @SerializedName("geometry")
        @Expose
        var geometry: Geometry? = null,

        @SerializedName("id")
        @Expose
        var id: String? = null
)
