package com.example.submissionone.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionone.database.FavoriteRoomDatabase
import com.example.submissionone.databinding.FragmentFavoriteBinding
import com.example.submissionone.ui.adapters.FavoriteAdapter
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = FavoriteRoomDatabase.getDatabase(requireContext())
        val dao = db.favoriteEventDao()

        lifecycleScope.launch {
            val favoriteEvents = dao.getAllFavorites()

            val adapter = FavoriteAdapter(favoriteEvents) { event ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("event", event)
                intent.putExtra("eventType", "favorite")
                startActivity(intent)
            }
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
