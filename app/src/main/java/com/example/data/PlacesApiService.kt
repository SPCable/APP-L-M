package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class PlacesResponse(
    @Json(name = "results") val results: List<PlaceResult>,
    @Json(name = "status") val status: String,
    @Json(name = "error_message") val errorMessage: String? = null
)

@JsonClass(generateAdapter = true)
data class PlaceResult(
    @Json(name = "place_id") val placeId: String,
    @Json(name = "name") val name: String,
    @Json(name = "rating") val rating: Double?,
    @Json(name = "user_ratings_total") val userRatingsTotal: Int?,
    @Json(name = "price_level") val priceLevel: Int?,
    @Json(name = "vicinity") val vicinity: String?,
    @Json(name = "photos") val photos: List<PlacePhoto>?,
    @Json(name = "geometry") val geometry: PlaceGeometry?,
    @Json(name = "types") val types: List<String>?
)

@JsonClass(generateAdapter = true)
data class PlacePhoto(
    @Json(name = "photo_reference") val photoReference: String,
    @Json(name = "height") val height: Int,
    @Json(name = "width") val width: Int
)

@JsonClass(generateAdapter = true)
data class PlaceGeometry(
    @Json(name = "location") val location: PlaceLocation
)

@JsonClass(generateAdapter = true)
data class PlaceLocation(
    @Json(name = "lat") val lat: Double,
    @Json(name = "lng") val lng: Double
)

interface PlacesApiService {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun getNearbySearch(
        @Query("location") location: String,
        @Query("radius") radiusInMeters: Int,
        @Query("type") type: String = "restaurant",
        @Query("key") apiKey: String
    ): PlacesResponse

    companion object {
        private const val BASE_URL = "https://maps.googleapis.com/"

        fun create(): PlacesApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
            return retrofit.create(PlacesApiService::class.java)
        }
    }
}
