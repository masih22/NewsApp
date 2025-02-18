package com.example.newsproject2025

import android.os.Bundle
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class SourcesActivity : AppCompatActivity() {
    private lateinit var searchedTextView: TextView
    private lateinit var newsSpinner: Spinner
    private lateinit var sourcesRecyclerView: RecyclerView
    private lateinit var sourceChangeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sources)

        searchedTextView = findViewById(R.id.searchedForText)
        newsSpinner = findViewById(R.id.newsSpinner)
        sourcesRecyclerView = findViewById(R.id.sourcesRecyclerView)
        sourceChangeButton = findViewById(R.id.sourceChangeButton)

        val searchedText = intent.getStringExtra("searchedText")
        searchedTextView.text = "Searched For: $searchedText"

        ArrayAdapter.createFromResource(
            this,
            R.array.news_categories,  // Your string array defined in strings.xml
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            newsSpinner.adapter = adapter
        }

        val newsSources = getFakeNewsSources()
        val adapter = NewsSourceAdapter(newsSources)
        sourcesRecyclerView.adapter = adapter
        sourcesRecyclerView.layoutManager = LinearLayoutManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getFakeNewsSources(): List<NewsSource> {
        return listOf(
            NewsSource("BBC", "Trusted source for global news and insights"),
            NewsSource("CNN", "Latest breaking news, top stories and analysis"),
            NewsSource("Reuters", "World's largest multimedia news provider"),
            NewsSource("Al Jazeera", "Global news from the Middle East"),
            NewsSource("The Guardian", "World-leading source for current news"),
            NewsSource("The New York Times","A leading American newspaper known for its news and analysis"),
            NewsSource("The Washington Post", "Top U.S. newspaper covering politics and global issues"),
            NewsSource("Fox News", "Leading conservative news and opinion site"),
            NewsSource("NBC News", "News network providing stories on current events worldwide"),
            NewsSource("TIME", "Leading American news magazine covering global issues"),
            NewsSource("National Geographic", "Famous for its coverage of nature, science, and the environment"),
            NewsSource("Sky News", "UK-based news provider offering breaking news and live updates"),
            NewsSource("USA Today", "Popular American news outlet covering a range of topics"),
            NewsSource("The Economist", "Global economics, politics, and business news"),
            NewsSource("Wall Street Journal", "Leading news source for business, economic trends, and finance"),
            NewsSource("ESPN", "Sports news, analysis, scores, and highlights"),
            NewsSource("Sports Illustrated", "Sports news, analysis, and in-depth articles")
            )
    }
}