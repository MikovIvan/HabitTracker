package ru.mikov.habittracker.presentation.dialogs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.mikov.habittracker.R
import ru.mikov.habittracker.app.appComponent
import ru.mikov.habittracker.databinding.BottomSheetBinding
import ru.mikov.habittracker.presentation.extentions.lazyActivityViewModel
import ru.mikov.habittracker.presentation.habits.HabitsViewModel
import ru.mikov.habittracker.presentation.habits.Sort


class FilterDialog : BottomSheetDialogFragment() {

    private val viewBinding: BottomSheetBinding by viewBinding()
    val viewModel: HabitsViewModel by lazyActivityViewModel { stateHandle ->
        requireContext().appComponent.habitsViewModel().create(stateHandle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.subComponent().create().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeState()
        initViews()
    }

    private fun initViews() {
        with(viewBinding) {

            btnApply.setOnClickListener {
                viewModel.handleSearch(etSearch.text.toString())
                dismiss()
            }

            btnCancel.setOnClickListener {
                viewModel.clearFilter()
                dismiss()
            }

            tvAscendingDescending.setOnClickListener {
                viewModel.chooseFilterMode()
            }

            etSearch.setOnEditorActionListener { input, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.handleSearch(input.text.toString())
                    dismiss()
                    return@setOnEditorActionListener true
                }
                false
            }

            tvByName.setOnClickListener {
                viewModel.chooseSortMode(Sort.NAME)
            }

            tvByPeriodicity.setOnClickListener {
                viewModel.chooseSortMode(Sort.PERIODICITY)
            }
        }
    }

    private fun observeState() {
        with(viewBinding) {
            viewModel.observeState(viewLifecycleOwner) {
                etSearch.setText(it.searchQuery)

                when (it.sort) {
                    Sort.NONE -> {
                        tvByName.setTextColor(Color.BLACK)
                        tvByPeriodicity.setTextColor(Color.BLACK)
                    }
                    Sort.NAME -> {
                        tvByName.setTextColor(Color.RED)
                        tvByPeriodicity.setTextColor(Color.BLACK)
                    }
                    Sort.PERIODICITY -> {
                        tvByName.setTextColor(Color.BLACK)
                        tvByPeriodicity.setTextColor(Color.RED)
                    }
                }

                if (it.isAscending) tvAscendingDescending.text = getString(R.string.ascending) else
                    tvAscendingDescending.text = getString(R.string.descending)
            }
        }
    }
}