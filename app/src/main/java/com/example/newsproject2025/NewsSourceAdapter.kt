package com.example.newsproject2025

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewsSourceAdapter(val newsSources: List<NewsSource>) : RecyclerView.Adapter<NewsSourceAdapter.ViewHolder>() {

    class ViewHolder(rootLayout: View) : RecyclerView.ViewHolder(rootLayout) {
        val sourceNameText: TextView = rootLayout.findViewById(R.id.source_name)
        val sourceDescriptionText: TextView = rootLayout.findViewById(R.id.source_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val rootLayout: View = layoutInflater.inflate(R.layout.recyler_item_source, parent, false)
        return ViewHolder(rootLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSource = newsSources[position]
        holder.sourceNameText.text = currentSource.name
        holder.sourceDescriptionText.text = currentSource.description
    }

    override fun getItemCount(): Int {
        return newsSources.size
    }
}
