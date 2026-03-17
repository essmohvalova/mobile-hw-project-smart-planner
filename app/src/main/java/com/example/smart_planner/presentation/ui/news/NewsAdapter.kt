package com.example.smart_planner.presentation.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_planner.databinding.ItemNewsBinding
import com.example.smart_planner.domain.models.news.News
import com.example.smart_planner.presentation.utils.ImageLoader
import java.text.SimpleDateFormat
import java.util.Locale

class NewsAdapter(
    private val imageLoader: ImageLoader
) : ListAdapter<News, NewsAdapter.NewsViewHolder>(NewsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding, imageLoader)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NewsViewHolder(
        private val binding: ItemNewsBinding,
        private val imageLoader: ImageLoader
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

        fun bind(news: News) {
            binding.apply {
                textTitle.text = news.title
                textAbstract.text = news.abstract
                textSource.text = news.source
                textDate.text = dateFormat.format(news.publishedDate)

                // Загружаем изображение с плейсхолдером
                imageLoader.loadImage(
                    url = news.imageUrl,
                    imageView = imageNews,
                    placeholderResId = android.R.drawable.ic_menu_gallery
                )
            }
        }
    }

    class NewsDiffCallback : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }
    }
}