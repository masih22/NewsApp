package com.example.newsproject2025

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var searchText: EditText
    private lateinit var searchButton: Button
    private lateinit var localButton: Button
    private lateinit var headlineButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.searchButton)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        searchText=findViewById(R.id.searchText)
        searchButton=findViewById(R.id.button)
        localButton=findViewById(R.id.localButton)
        headlineButton=findViewById(R.id.headlineButton)

        val sharedPrefs = getSharedPreferences("News", MODE_PRIVATE)

        searchText.addTextChangedListener(myTextWatcher)
        val savedSearch=sharedPrefs.getString("Search","")
        searchText.setText(savedSearch)

        searchButton.setOnClickListener {
            val inputtedSearch: String=searchText.text.toString().trim()
            sharedPrefs
                .edit()
                .putString("Search", inputtedSearch)
                .apply()
            val yelpListIntent=Intent(this@MainActivity, SourcesActivity::class.java)
            yelpListIntent.putExtra("searchedText",inputtedSearch)
            startActivity(yelpListIntent)
        }

        localButton.setOnClickListener {
            val localNewsIntent = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(localNewsIntent)
        }

        headlineButton.setOnClickListener {
            val headlinesIntent = Intent(this@MainActivity, TopHeadlinesActivity::class.java)
            startActivity(headlinesIntent)
        }

    }

    private val myTextWatcher: TextWatcher =object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            var inputtedSearch=searchText.text.toString()
            val enableButton: Boolean=inputtedSearch.isNotBlank()
            searchButton.setEnabled(enableButton)
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }
}