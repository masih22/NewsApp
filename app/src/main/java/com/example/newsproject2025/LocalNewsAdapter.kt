package com.example.newsproject2025

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class LocalNewsAdapter(var articles: List<Article>) :
    RecyclerView.Adapter<LocalNewsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val articleTitleText: TextView = itemView.findViewById(R.id.articleTitleTextView)
        val articleSourceText: TextView = itemView.findViewById(R.id.articleSourceTextView)
        val articleImageView: ImageView = itemView.findViewById(R.id.articleImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentArticle = articles[position]

        holder.articleTitleText.text = currentArticle.title
        holder.articleSourceText.text = currentArticle.source

        // Load the image using Picasso if available.
        if (!currentArticle.imageUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(currentArticle.imageUrl)
                .into(holder.articleImageView)
        } else {
            // Optionally clear the image view if no URL is provided.
            holder.articleImageView.setImageDrawable(null)
        }

        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int = articles.size

    fun updateArticles(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }
}