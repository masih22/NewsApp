package com.example.newsproject2025

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ResultsActivity : AppCompatActivity() {

    private lateinit var searchHeader: TextView
    private lateinit var resultsRecyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private val client = OkHttpClient()
    private var searchTerm: String = ""
    private var selectedSource: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        searchHeader = findViewById(R.id.searchHeader)
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView)
        articleAdapter = ArticleAdapter(emptyList())
        resultsRecyclerView.layoutManager = LinearLayoutManager(this)
        resultsRecyclerView.adapter = articleAdapter

        searchTerm = intent.getStringExtra("searchTerm") ?: "Unknown"
        selectedSource = intent.getStringExtra("selectedSource") ?: ""

        searchHeader.text = if (selectedSource.isNotEmpty()) {
            "Results for: '$searchTerm' from $selectedSource"
        } else {
            "Results for: '$searchTerm'"
        }

        loadResults()
    }

    private fun loadResults() {
        val apiKey = getString(R.string.news_api_key)
        var url = "https://newsapi.org/v2/everything?q=$searchTerm&apiKey=$apiKey"
        if (selectedSource.isNotEmpty()) {
            url += "&sources=$selectedSource"
        }
        Log.d("ResultsActivity", "Request URL: $url")

        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ResultsActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@ResultsActivity, "Unexpected response: ${response.code}", Toast.LENGTH_SHORT).show()
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
                            } else ""
                            val imageUrl = if (!articleObject.isNull("urlToImage")) {
                                articleObject.getString("urlToImage")
                            } else null
                            articles.add(Article(title, sourceName, description, imageUrl, articleUrl))
                        }
                        runOnUiThread {
                            articleAdapter.updateArticles(articles)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@ResultsActivity, "API error: $status", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@ResultsActivity, "Error parsing data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
