package com.interview.flagchallenge.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.interview.flagchallenge.R
import com.interview.flagchallenge.databinding.LetsBeginFragmentBinding
import com.interview.flagchallenge.viewmodel.QuestionViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LetsBeginFragment : Fragment() {

    private var _binding: LetsBeginFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView
    private val binding get() = _binding!!

    private val questionViewModel: QuestionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = LetsBeginFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSave.setOnClickListener {
            binding.txtCountdown.visibility = View.VISIBLE
            questionViewModel.startCountdown(binding.timeEditText.text.toString())
            // Observe the countdown time and update the UI
            questionViewModel.timeLeft.observe(requireActivity()) { timeLeft ->
                when {
                    timeLeft.toInt() <= 0 -> {
                        questionViewModel.stopCountdown()
                        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                    }
                    else -> {
                        val time = String.format("%02d:%02d:%02d", timeLeft / 3600,
                            (timeLeft % 3600) / 60, (timeLeft % 60));

                        binding.txtCountdown.text = "Your challenge starts in $time"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}