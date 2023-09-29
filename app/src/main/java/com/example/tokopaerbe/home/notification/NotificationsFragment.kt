package com.example.tokopaerbe.home.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokopaerbe.databinding.FragmentNotificationsBinding
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: androidx.appcompat.widget.Toolbar = binding.notificationToolbar

        val navigationIcon: View = toolbar

        navigationIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        model.getNotification().observe(viewLifecycleOwner) {
            if (it?.isNotEmpty() == true) {
                binding.imageView5.visibility = GONE
                binding.textView5.visibility = GONE
                binding.descempty.visibility = GONE

                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                val adapter = NotificationAdapter(model)
                binding.recyclerView.adapter = adapter

                adapter.submitList(it.reversed())
            }
        }
    }
}
