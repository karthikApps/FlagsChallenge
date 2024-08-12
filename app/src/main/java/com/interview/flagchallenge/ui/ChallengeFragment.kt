package com.interview.flagchallenge.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.interview.flagchallenge.databinding.ChallengeFragmentBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ChallengeFragment : Fragment() {

    private var _binding: ChallengeFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewPagerAdapter: ChallengeViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChallengeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getQuestion()
        initViewPager()
        observeQuestion()
    }

    /**
     * Method to get question
     */
    private fun getQuestion() {
        (context as MainActivity).questionViewModel.getQuestions(requireActivity())
    }

    /**
     * Method to observe questions
     */
    private fun observeQuestion() {
        (context as MainActivity).questionViewModel.counter.observe(viewLifecycleOwner) { result ->
            viewPagerAdapter.submitList(result.questions)
        }
    }

    private fun initViewPager() {
        (context as MainActivity).viewPager = binding.viewpager

        // Set up adapter
        viewPagerAdapter = ChallengeViewPagerAdapter(requireActivity())
        (context as MainActivity).viewPager.adapter = viewPagerAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}