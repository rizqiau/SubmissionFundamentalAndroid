package com.example.submissionone.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.submissionone.data.response.ListUpcomingEventsItem
import com.example.submissionone.databinding.FragmentUpcomingBinding
import com.example.submissionone.ui.adapters.UpcomingAdapter
import kotlinx.coroutines.launch

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private lateinit var upcomingAdapter: UpcomingAdapter
    private val viewModel: HomeViewModel by viewModels()
    private var upcomingEventsList: List<ListUpcomingEventsItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvUpcoming.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvUpcoming.setHasFixedSize(true)
        upcomingAdapter = UpcomingAdapter(emptyList())
        binding.rvUpcoming.adapter = upcomingAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.upcomingEvents.collect { events ->
                    upcomingEventsList = events
                    upcomingAdapter = UpcomingAdapter(events)
                    binding.rvUpcoming.adapter = upcomingAdapter
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }

        viewModel.fetchUpcomingEvents(active = 1)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideTitle()
                filterEvents(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    showTitle()
                    upcomingAdapter = UpcomingAdapter(upcomingEventsList)
                    binding.rvUpcoming.adapter = upcomingAdapter
                } else {
                    hideTitle()
                    filterEvents(newText)
                }
                return true
            }
        })

        if (!isNetworkAvailable(requireContext())) {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun hideTitle() {
        binding.tvUpcomingTitle.visibility = View.GONE
    }

    private fun showTitle() {
        binding.tvUpcomingTitle.visibility = View.VISIBLE
    }

    private fun filterEvents(query: String?) {
        if (!query.isNullOrEmpty()) {
            viewModel.searchUpcomingEvents(query)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}