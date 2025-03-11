package com.example.newsproject2025

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class TopHeadlinesActivity : AppCompatActivity() {

    private lateinit var categorySpinner: Spinner
    private lateinit var headlinesRecyclerView: RecyclerView
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private lateinit var pageInfoTextView: TextView
    private lateinit var progressBar: ProgressBar

    private var currentPage = 1
    private var totalPages = 1
    private var maxPages = 5

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_top_headlines)

        categorySpinner = findViewById(R.id.categorySpinner)
        headlinesRecyclerView = findViewById(R.id.headlinesRecyclerView)
        previousButton = findViewById(R.id.previousButton)
        nextButton = findViewById(R.id.nextButton)
        pageInfoTextView = findViewById(R.id.pageInfoTextView)
        progressBar = findViewById(R.id.progressBar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupCategorySpinner()
        setupPagination()
    }

    private fun setupRecyclerView() {
        val articles = ArrayList<Article>()
        val articleAdapter = ArticleAdapter(articles)
        headlinesRecyclerView.layoutManager = LinearLayoutManager(this)
        headlinesRecyclerView.adapter = articleAdapter
    }

    private fun setupCategorySpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.news_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }

        val sharedPrefs = getSharedPreferences("News", MODE_PRIVATE)
        val savedCategoryPosition = sharedPrefs.getInt("SelectedHeadlineCategory", 0)
        categorySpinner.setSelection(savedCategoryPosition)

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                sharedPrefs.edit().putInt("SelectedHeadlineCategory", position).apply()

                currentPage = 1
                loadHeadlines()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun setupPagination() {
        previousButton.isEnabled = false

        previousButton.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                updatePaginationUI()
                loadHeadlines()
            }
        }

        nextButton.setOnClickListener {
            if (currentPage < totalPages && currentPage < maxPages) {
                currentPage++
                updatePaginationUI()
                loadHeadlines()
            }
        }
        updatePaginationUI()
    }

    private fun updatePaginationUI() {
        pageInfoTextView.text = "Page $currentPage of $totalPages"

        previousButton.isEnabled = currentPage > 1
        nextButton.isEnabled = currentPage < totalPages && currentPage < maxPages
    }

    private fun loadHeadlines() {
        progressBar.visibility = View.VISIBLE

        val selectedCategory = categorySpinner.selectedItem.toString().lowercase()

        val apiKey = getString(R.string.news_api_key)
        val url = "https://newsapi.org/v2/top-headlines?country=us&category=$selectedCategory&page=$currentPage&apiKey=$apiKey"
        Log.d("TopHeadlines", "Request URL: $url")

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@TopHeadlinesActivity,
                        "Network error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar.visibility = View.GONE
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        if (response.code == 403) {
                            Toast.makeText(
                                this@TopHeadlinesActivity,
                                "API access forbidden (403). Please check your API key.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@TopHeadlinesActivity,
                                "Unexpected response code: ${response.code}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        progressBar.visibility = View.GONE
                    }
                    return
                }
                val responseData = response.body?.string()
                try {
                    val jsonResponse = JSONObject(responseData)
                    val status = jsonResponse.getString("status")
                    if (status == "ok") {
                        val totalResults = jsonResponse.getInt("totalResults")
                        totalPages = (totalResults + 19) / 20 // 20 items per page, rounded up

                        if (totalPages > maxPages) {
                            totalPages = maxPages
                        }

                        val articlesArray = jsonResponse.getJSONArray("articles")
                        val articles = mutableListOf<Article>()

                        for (i in 0 until articlesArray.length()) {
                            val articleObject = articlesArray.getJSONObject(i)
                            val sourceObject = articleObject.getJSONObject("source")
                            val sourceName = sourceObject.getString("name")
                            val title = articleObject.getString("title")
                            val articleUrl = articleObject.getString("url")

                            val content = if (!articleObject.isNull("description")) {
                                articleObject.getString("description")
                            } else {
                                null
                            }

                            val imageUrl = if (!articleObject.isNull("urlToImage")) {
                                articleObject.getString("urlToImage")
                            } else {
                                null
                            }

                            val article = Article(
                                title = title,
                                source = sourceName,
                                content = content,
                                imageUrl = imageUrl,
                                articleUrl = articleUrl
                            )

                            articles.add(article)
                        }

                        runOnUiThread {
                            val adapter = headlinesRecyclerView.adapter as ArticleAdapter
                            adapter.updateArticles(articles)
                            updatePaginationUI()
                            progressBar.visibility = View.GONE
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@TopHeadlinesActivity,
                                "API error: $status",
                                Toast.LENGTH_SHORT
                            ).show()
                            progressBar.visibility = View.GONE
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@TopHeadlinesActivity,
                            "Error parsing data: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressBar.visibility = View.GONE
                    }
                }
            }
        })
    }
}
