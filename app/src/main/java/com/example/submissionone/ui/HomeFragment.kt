package com.example.submissionone.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.submissionone.data.response.ListUpcomingEventsItem
import com.example.submissionone.databinding.FragmentHomeBinding
import com.example.submissionone.ui.adapters.FinishedAdapter
import com.example.submissionone.ui.adapters.UpcomingHomeAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var upcomingHomeAdapter: UpcomingHomeAdapter
    private lateinit var finishedAdapter: FinishedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvFirst.layoutManager = GridLayoutManager(context, 2)
            binding.rvSecond.layoutManager = GridLayoutManager(context, 2)
        } else {
            binding.rvFirst.layoutManager = GridLayoutManager(context, 2)
            binding.rvSecond.layoutManager = GridLayoutManager(context, 2)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.upcomingEvents.collect { events ->
                    upcomingHomeAdapter = UpcomingHomeAdapter(events) { event ->
                        openDetailActivity(event)
                    }
                    binding.rvFirst.adapter = upcomingHomeAdapter
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.finishedEvents.collect { events ->
                    finishedAdapter = FinishedAdapter(events)
                    binding.rvSecond.adapter = finishedAdapter
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { errorMsg ->
                    errorMsg?.let {
                        Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                    }
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

        if (!isNetworkAvailable(requireContext())) {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.fetchUpcomingEvents(active = 1)
        viewModel.fetchFinishedEvents(active = 0)
    }

    private fun openDetailActivity(event: ListUpcomingEventsItem) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("eventType", "upcoming")
        intent.putExtra("event", event)
        startActivity(intent)
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