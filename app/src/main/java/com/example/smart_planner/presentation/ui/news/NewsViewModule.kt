package com.example.smart_planner.presentation.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_planner.data.repository.news.NewsRepository
import com.example.smart_planner.domain.models.news.News
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _newsState = MutableStateFlow<NewsState>(NewsState.Loading)
    val newsState: StateFlow<NewsState> = _newsState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        observeNews()
        startPeriodicRefresh()
    }

    private fun observeNews() {
        viewModelScope.launch {
            newsRepository.getNewsStream().collect { news ->
                if (news.isEmpty()) {
                    _newsState.value = NewsState.Empty
                } else {
                    _newsState.value = NewsState.Success(news)
                }
            }
        }
    }

    private fun startPeriodicRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(2 * 60 * 1000L) // 2 минуты
                refreshNews()
            }
        }
    }

    fun refreshNews() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                newsRepository.refreshNews()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            newsRepository.clearCache()
        }
    }

    sealed class NewsState {
        object Loading : NewsState()
        object Empty : NewsState()
        data class Success(val news: List<News>) : NewsState()
    }
}