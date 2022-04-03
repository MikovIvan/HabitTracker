package ru.mikov.habittracker.ui.dialogs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.mikov.habittracker.R
import ru.mikov.habittracker.databinding.BottomSheetBinding
import ru.mikov.habittracker.ui.habits.HabitsViewModel

class FilterDialog : BottomSheetDialogFragment() {

    private val viewBinding: BottomSheetBinding by viewBinding()
    private val viewModel: HabitsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding) {
            viewModel.state.observe(viewLifecycleOwner) {
                etSearch.setText(it.searchQuery)

                when (it.typeOfSort) {
                    0 -> tvByName.setTextColor(Color.RED)
                    1 -> tvByPeriodicity.setTextColor(Color.RED)
                    else -> {
                        tvByName.setTextColor(Color.BLACK)
                        tvByPeriodicity.setTextColor(Color.BLACK)
                    }
                }

                when (it.sortBy) {
                    true -> tvAscendingDescending.text = getString(R.string.ascending)
                    else -> tvAscendingDescending.text = getString(R.string.descending)
                }
            }


            btnApply.setOnClickListener {
                viewModel.search(etSearch.text.toString())
                viewModel.setTypeOfSort(viewModel.sortParam)
                viewModel.setOrder(!viewModel.isAscending)
                dismiss()
            }

            btnCancel.setOnClickListener {
                viewModel.search("")
                viewModel.sortParam = -1
                viewModel.isAscending = false
                viewModel.setTypeOfSort(viewModel.sortParam)
                viewModel.setOrder(!viewModel.isAscending)
                dismiss()
            }

            tvAscendingDescending.setOnClickListener {
                when (viewModel.isAscending) {
                    true -> {
                        tvAscendingDescending.text = getString(R.string.ascending)
                        viewModel.isAscending = false
                    }
                    else -> {
                        tvAscendingDescending.text = getString(R.string.descending)
                        viewModel.isAscending = true
                    }
                }
            }

            etSearch.setOnEditorActionListener { input, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.search(input.text.toString())
                    dismiss()
                    return@setOnEditorActionListener true
                }
                false
            }

            tvByName.setOnClickListener {
                tvByName.setTextColor(Color.RED)
                tvByPeriodicity.setTextColor(Color.BLACK)
                viewModel.sortParam = 0
            }

            tvByPeriodicity.setOnClickListener {
                tvByName.setTextColor(Color.BLACK)
                tvByPeriodicity.setTextColor(Color.RED)
                viewModel.sortParam = 1
            }
        }
    }
}