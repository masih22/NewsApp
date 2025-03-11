package com.example.newsproject2025

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val client = OkHttpClient()
    private lateinit var localNewsAdapter: LocalNewsAdapter
    private lateinit var localNewsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        localNewsRecyclerView = findViewById(R.id.localNewsRecyclerView)
        localNewsAdapter = LocalNewsAdapter(emptyList())
        localNewsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        localNewsRecyclerView.adapter = localNewsAdapter

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val initialLocation = LatLng(0.0, 0.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 2.0f))

        mMap.setOnMapLongClickListener { latLng ->
            mMap.clear()
            lifecycleScope.launch {
                val addresses = getAddressesFromLocation(latLng.latitude, latLng.longitude, this@MapsActivity)
                if (addresses == null) {
                    Toast.makeText(this@MapsActivity, "No address found", Toast.LENGTH_SHORT).show()
                } else {
                    val address = addresses[0]
                    val searchTerm = if (!address.adminArea.isNullOrEmpty()) address.adminArea
                    else address.countryName ?: "Unknown"
                    mMap.addMarker(MarkerOptions().position(latLng).title("Results for $searchTerm"))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7.0f))
                    // Update the header TextView with the search term.
                    val resultsHeaderTextView = findViewById<TextView>(R.id.locationResultsTextView)
                    resultsHeaderTextView.text = "Results for $searchTerm"
                    loadLocalNews(searchTerm)
                }
            }
        }
    }

    private fun loadLocalNews(searchTerm: String) {
        val apiKey = getString(R.string.news_api_key)
        val url = "https://newsapi.org/v2/everything?qInTitle=$searchTerm&apiKey=$apiKey"
        Log.d("LocalNews", "Request URL: $url")

        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MapsActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@MapsActivity, "Unexpected response: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                val responseData = response.body?.string()
                try {
                    val jsonResponse = JSONObject(responseData)
                    val status = jsonResponse.getString("status")
                    if (status == "ok") {
                        val articlesArray = jsonResponse.getJSONArray("articles")
                        val articles = mutableListOf<Article>()
                        for (i in 0 until articlesArray.length()) {
                            val articleObject = articlesArray.getJSONObject(i)
                            val sourceName = articleObject.getJSONObject("source").getString("name")
                            val title = articleObject.getString("title")
                            val articleUrl = articleObject.getString("url")
                            val description = if (!articleObject.isNull("description")) {
                                articleObject.getString("description")
                            } else {
                                ""
                            }
                            val imageUrl = if (!articleObject.isNull("urlToImage")) {
                                articleObject.getString("urlToImage")
                            } else {
                                null
                            }
                            articles.add(Article(title, sourceName, description, imageUrl, articleUrl))
                        }
                        runOnUiThread {
                            localNewsAdapter.updateArticles(articles)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MapsActivity, "API error: $status", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@MapsActivity, "Error parsing data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private suspend fun getAddressesFromLocation(latitude: Double, longitude: Double, context: Context): MutableList<Address>? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context)
                val addresses = geocoder.getFromLocation(latitude, longitude, 5)
                if (addresses != null && addresses.isNotEmpty()) addresses else null
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
}
