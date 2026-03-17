package com.example.smart_planner.presentation.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smart_planner.databinding.FragmentNewsBinding
import com.example.smart_planner.presentation.utils.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle


@AndroidEntryPoint
class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsViewModel by viewModels()

    @Inject
    lateinit var imageLoader: ImageLoader

    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()

        // Первоначальная загрузка
        viewModel.refreshNews()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(imageLoader)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshNews()
        }
    }

    private fun observeViewModel() {
        // Наблюдаем за состоянием новостей с помощью корутин
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newsState.collect { state ->
                    when (state) {
                        is NewsViewModel.NewsState.Loading -> {
                            if (newsAdapter.currentList.isEmpty()) {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.recyclerView.visibility = View.GONE
                                binding.emptyState.visibility = View.GONE
                            }
                            binding.swipeRefresh.isRefreshing = false
                        }
                        is NewsViewModel.NewsState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.emptyState.visibility = View.GONE
                            newsAdapter.submitList(state.news)
                            binding.swipeRefresh.isRefreshing = false
                        }
                        is NewsViewModel.NewsState.Empty -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            binding.emptyState.visibility = View.VISIBLE
                            binding.swipeRefresh.isRefreshing = false
                        }
                    }
                }
            }
        }

        // Наблюдаем за состоянием обновления
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isRefreshing.collect { isRefreshing ->
                    binding.swipeRefresh.isRefreshing = isRefreshing
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}