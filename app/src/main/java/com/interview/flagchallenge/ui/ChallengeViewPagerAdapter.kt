package com.interview.flagchallenge.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.interview.flagchallenge.R
import com.interview.flagchallenge.data.Countries
import com.interview.flagchallenge.data.Questions
import com.interview.flagchallenge.databinding.ChallengeItemBinding
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class ChallengeViewPagerAdapter(private val context: Context) :
    ListAdapter<Questions, RecyclerView.ViewHolder>(DiffCallback) {
    private var countdownJob: Job? = null
    private val _timeLeft = MutableLiveData<Long>()

    inner class ProductsItemViewHolder(private val binding: ChallengeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("DefaultLocale")
        fun onBind(data: Questions, position: Int) {
            when (data.country_code) {
                "NZ" -> {
                    binding.imgFlag.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.one_nz
                        )
                    )
                }

                "AW" -> {
                    binding.imgFlag.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.two_aw
                        )
                    )
                }

                "EC" -> {
                    binding.imgFlag.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.three_ec
                        )
                    )
                }

                "PY" -> {
                    binding.imgFlag.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.four_py
                        )
                    )
                }

                "KG" -> {
                    binding.imgFlag.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.five_kg
                        )
                    )
                }

                "PM" -> {
                    binding.imgFlag.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.six_pm
                        )
                    )
                }

                "JP" -> {
                    binding.imgFlag.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.seven_jp
                        )
                    )
                }

                "TM" -> {
                    binding.imgFlag.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.eight_tm
                        )
                    )
                }

                "GA" -> {
                    binding.imgFlag.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.nine_ga
                        )
                    )
                }

                "MQ" -> {
                    binding.imgFlag.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ten_mq
                        )
                    )
                }

                "BZ" -> {
                    binding.imgFlag.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.eleven_bz
                        )
                    )
                }

                "CZ" -> {
                    binding.imgFlag.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.twelve_cz
                        )
                    )
                }

                "AE" -> {
                    binding.imgFlag.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.thirteen_ae
                        )
                    )
                }

                "JE" -> {
                    binding.imgFlag.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.fourteen_je
                        )
                    )
                }

                "LS" -> {
                    binding.imgFlag.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.sixteen_ls
                        )
                    )
                }
            }
            setQuestionToButton(binding, data.countries)
            binding.ans1.setOnClickListener {
                binding.ans1.isEnabled = true
                binding.ans2.isEnabled = false
                binding.ans3.isEnabled = false
                binding.ans4.isEnabled = false
                setButtonAttribute(binding, position, binding.ans1, data.answer_id)

            }
            binding.ans2.setOnClickListener {
                binding.ans1.isEnabled = false
                binding.ans2.isEnabled = true
                binding.ans3.isEnabled = false
                binding.ans4.isEnabled = false
                setButtonAttribute(binding, position, binding.ans2, data.answer_id)
            }
            binding.ans3.setOnClickListener {
                binding.ans1.isEnabled = false
                binding.ans2.isEnabled = false
                binding.ans3.isEnabled = true
                binding.ans4.isEnabled = false
                setButtonAttribute(binding, position, binding.ans3, data.answer_id)
            }
            binding.ans4.setOnClickListener {
                binding.ans1.isEnabled = false
                binding.ans2.isEnabled = false
                binding.ans3.isEnabled = false
                binding.ans4.isEnabled = true
                setButtonAttribute(binding, position, binding.ans4, data.answer_id)
            }
            binding.executePendingBindings()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun setButtonAttribute(
        binding: ChallengeItemBinding,
        position: Int,
        button: Button,
        correctAns: Int
    ) {
        if (button.tag == correctAns) {
            button.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.correctBtn
                )
            )
        } else {
            button.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.wrongBtn
                )
            )
        }
        (context as MainActivity).lifecycleScope.launch(Dispatchers.Main) {
            val totalSeconds = TimeUnit.SECONDS.toSeconds(10)
            val tickSeconds = 1
            for (second in totalSeconds downTo tickSeconds) {
                val time = String.format("%02d:%02d",
                    TimeUnit.SECONDS.toMinutes(second),
                    second - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(second))
                )
                binding.textCount.visibility = View.VISIBLE
                binding.textCount.text = time
                delay(1000)
            }
            context.viewPager.currentItem += 1
            binding.apply {
                ans1.isEnabled = false
                ans1.isSelected = false
                ans2.isEnabled = false
                ans2.isSelected = false
                ans3.isEnabled = false
                ans3.isSelected = false
                ans4.isEnabled = false
                ans4.isSelected = false
            }
        }
    }
    private fun setQuestionToButton(binding: ChallengeItemBinding, countries: List<Countries>) {
        countries.forEachIndexed { index, country ->
            when (index) {
                0 -> {
                    binding.ans1.tag = country.id
                    binding.ans1.text = country.country_name
                }

                1 -> {
                    binding.ans2.tag = country.id
                    binding.ans2.text = country.country_name
                }

                2 -> {
                    binding.ans3.tag = country.id
                    binding.ans3.text = country.country_name
                }

                3 -> {
                    binding.ans4.tag = country.id
                    binding.ans4.text = country.country_name
                }
            }
        }

    }


    object DiffCallback : DiffUtil.ItemCallback<Questions>() {
        override fun areItemsTheSame(oldItem: Questions, newItem: Questions): Boolean {
            return oldItem.answer_id == newItem.answer_id
        }

        override fun areContentsTheSame(oldItem: Questions, newItem: Questions): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ChallengeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductsItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val adjustViewHolder = holder as ProductsItemViewHolder
        val fridgeItem = getItem(position)

        fridgeItem.let {
            adjustViewHolder.onBind(it, position)
        }
    }
}

