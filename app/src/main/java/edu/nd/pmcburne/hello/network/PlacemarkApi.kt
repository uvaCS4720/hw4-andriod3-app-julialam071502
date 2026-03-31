package edu.nd.pmcburne.hello.network

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class PlacemarkResponse(
    @SerializedName("id")            val id: Int,
    @SerializedName("name")          val name: String,
    @SerializedName("tag_list")      val tagList: List<String>,
    @SerializedName("description")   val description: String?,
    @SerializedName("visual_center") val visualCenter: VisualCenter?
)

data class VisualCenter(
    @SerializedName("latitude")  val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)

interface PlacemarkApiService {
    @GET("placemarks.json")
    suspend fun getPlacemarks(): List<PlacemarkResponse>
}

object RetrofitInstance {
    val api: PlacemarkApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.cs.virginia.edu/~wxt4gm/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlacemarkApiService::class.java)
    }
}