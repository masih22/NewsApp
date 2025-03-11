package com.example.newsproject2025

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SourcesActivity : AppCompatActivity() {
    private lateinit var searchedTextView: TextView
    private lateinit var newsSpinner: Spinner
    private lateinit var sourcesRecyclerView: RecyclerView
    private lateinit var sourceChangeButton: Button
    private lateinit var newsSourceAdapter: NewsSourceAdapter
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sources)

        searchedTextView = findViewById(R.id.searchedForText)
        newsSpinner = findViewById(R.id.newsSpinner)
        sourcesRecyclerView = findViewById(R.id.sourcesRecyclerView)
        sourceChangeButton = findViewById(R.id.sourceChangeButton)

        val searchTerm = intent.getStringExtra("searchedText") ?: ""
        searchedTextView.text = "Searched For: $searchTerm"

        ArrayAdapter.createFromResource(
            this,
            R.array.news_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            newsSpinner.adapter = adapter
        }

        newsSourceAdapter = NewsSourceAdapter(emptyList())
        sourcesRecyclerView.layoutManager = LinearLayoutManager(this)
        sourcesRecyclerView.adapter = newsSourceAdapter

        newsSourceAdapter.onItemClick = { selectedSource ->
            val intent = android.content.Intent(this, ResultsActivity::class.java)
            intent.putExtra("searchTerm", searchTerm)
            intent.putExtra("selectedSource", selectedSource.id ?: "")
            startActivity(intent)
        }

        sourceChangeButton.setOnClickListener {
            val intent = android.content.Intent(this, ResultsActivity::class.java)
            intent.putExtra("searchTerm", searchTerm)
            intent.putExtra("selectedSource", "")
            startActivity(intent)
        }

        val initialCategory = newsSpinner.selectedItem?.toString()?.toLowerCase() ?: ""
        loadNewsSources(initialCategory)

        newsSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val category = parent.getItemAtPosition(position).toString().toLowerCase()
                loadNewsSources(category)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                loadNewsSources("")
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadNewsSources(category: String) {
        val apiKey = getString(R.string.news_api_key)
        val url = if (category.isEmpty() || category.equals("all", ignoreCase = true)) {
            "https://newsapi.org/v2/top-headlines/sources?apiKey=$apiKey"
        } else {
            "https://newsapi.org/v2/top-headlines/sources?category=$category&apiKey=$apiKey"
        }
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@SourcesActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@SourcesActivity, "Unexpected response: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                val responseData = response.body?.string()
                try {
                    val jsonResponse = JSONObject(responseData)
                    val status = jsonResponse.getString("status")
                    if (status == "ok") {
                        val sourcesArray = jsonResponse.getJSONArray("sources")
                        val sources = mutableListOf<NewsSource>()
                        for (i in 0 until sourcesArray.length()) {
                            val sourceObject = sourcesArray.getJSONObject(i)
                            val id = sourceObject.optString("id")
                            val name = sourceObject.optString("name")
                            val description = sourceObject.optString("description")
                            sources.add(NewsSource(id, name, description))
                        }
                        runOnUiThread {
                            newsSourceAdapter.updateSources(sources)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@SourcesActivity, "API error: $status", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@SourcesActivity, "Error parsing data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}


