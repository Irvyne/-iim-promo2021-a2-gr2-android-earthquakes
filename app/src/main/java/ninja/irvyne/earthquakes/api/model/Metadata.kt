package ninja.irvyne.earthquakes.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Metadata(
        @SerializedName("generated")
        @Expose
        var generated: Long? = null,

        @SerializedName("url")
        @Expose
        var url: String? = null,

        @SerializedName("title")
        @Expose
        var title: String? = null,

        @SerializedName("status")
        @Expose
        var status: Long? = null,

        @SerializedName("api")
        @Expose
        var api: String? = null,

        @SerializedName("count")
        @Expose
        var count: Long? = null
)
