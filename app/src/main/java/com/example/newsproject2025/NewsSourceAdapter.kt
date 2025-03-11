package com.example.newsproject2025

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewsSourceAdapter(var newsSources: List<NewsSource>) : RecyclerView.Adapter<NewsSourceAdapter.ViewHolder>() {

    var onItemClick: ((NewsSource) -> Unit)? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sourceNameText: TextView = itemView.findViewById(R.id.source_name)
        val sourceDescriptionText: TextView = itemView.findViewById(R.id.source_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val rootLayout = layoutInflater.inflate(R.layout.recyler_item_source, parent, false)
        return ViewHolder(rootLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSource = newsSources[position]
        holder.sourceNameText.text = currentSource.name
        holder.sourceDescriptionText.text = currentSource.description

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentSource)
        }
    }

    override fun getItemCount(): Int = newsSources.size

    fun updateSources(newSources: List<NewsSource>) {
        newsSources = newSources
        notifyDataSetChanged()
    }
}