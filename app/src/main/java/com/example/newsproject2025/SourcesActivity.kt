package com.example.newsproject2025

import android.os.Bundle
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ArrayAdapter


class SourcesActivity : AppCompatActivity() {
    private lateinit var searchedTextView: TextView
    private lateinit var newsSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sources)

        searchedTextView = findViewById(R.id.searchedForText)
        newsSpinner = findViewById(R.id.newsSpinner)

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}