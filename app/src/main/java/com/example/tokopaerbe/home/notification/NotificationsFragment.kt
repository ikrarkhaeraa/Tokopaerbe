package com.example.tokopaerbe.home.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokopaerbe.databinding.FragmentNotificationsBinding
import com.example.tokopaerbe.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val model: ViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: androidx.appcompat.widget.Toolbar = binding.notificationToolbar

        val navigationIcon: View = toolbar

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = NotificationAdapter(
            onNotificationsClick = { notifEntity ->
                if (!notifEntity.isChecked) {
                    model.notifIsChecked(notifEntity.notifId, true)
                } else {
                    // do nothing
                }
            }
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemAnimator?.changeDuration = 0

        navigationIcon.setOnClickListener {
                findNavController().navigateUp()
        }

        lifecycleScope.launch {
            while (true) {
                if (isActive) {
                    model.getNotification().observe(viewLifecycleOwner) {
                        if (it?.isNotEmpty() == true) {
                            binding.imageView5.visibility = GONE
                            binding.textView5.visibility = GONE
                            binding.descempty.visibility = GONE

                            adapter.submitList(it.reversed())
                        }
                    }
                }
                if (!isActive) {
                    break
                }
                delay(1000)
            }
        }
    }
}
