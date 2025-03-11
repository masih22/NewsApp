package com.example.newsproject2025
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ArticleAdapter(var articles: List<Article>) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    class ViewHolder(rootLayout: View) : RecyclerView.ViewHolder(rootLayout) {
        val articleTitleText: TextView = rootLayout.findViewById(R.id.articleTitleTextView)
        val articleSourceText: TextView = rootLayout.findViewById(R.id.articleSourceTextView)
        val articleContentText: TextView = rootLayout.findViewById(R.id.articleContentTextView)
        val articleImageView: ImageView = rootLayout.findViewById(R.id.articleImageView)
        val charCountText: TextView = rootLayout.findViewById(R.id.charCountTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val rootLayout: View = layoutInflater.inflate(R.layout.item_article, parent, false)
        return ViewHolder(rootLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentArticle = articles[position]

        holder.articleTitleText.text = currentArticle.title
        holder.articleSourceText.text = currentArticle.source

        if (currentArticle.content != null) {
            holder.articleContentText.text = currentArticle.content
            holder.charCountText.text = "[+${currentArticle.content.length} chars]"
        } else {
            holder.articleContentText.text = "No description available"
            holder.charCountText.visibility = View.GONE
        }


        if (!currentArticle.imageUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(currentArticle.imageUrl)
                .into(holder.articleImageView)
        } else {
            holder.articleImageView.setImageDrawable(null)
        }

        holder.itemView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentArticle.articleUrl))
            holder.itemView.context.startActivity(browserIntent)
        }
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    fun updateArticles(newArticles: List<Article>) {

        articles = newArticles.toList()

        notifyDataSetChanged()
    }
}