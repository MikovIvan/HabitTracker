package ru.mikov.habittracker.ui.habit

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.mikov.habittracker.R
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.local.entities.HabitPriority
import ru.mikov.habittracker.data.local.entities.HabitType
import ru.mikov.habittracker.databinding.FragmentHabitBinding

import ru.mikov.habittracker.ui.extentions.hideKeyboard
import ru.mikov.habittracker.ui.extentions.onItemSelectedListener


class HabitFragment : Fragment(R.layout.fragment_habit) {

    companion object {
        const val NUMBER_OF_TAB_KEY = "NUMBER_OF_TAB_KEY"
        const val NUMBER_OF_TAB = "NUMBER_OF_TAB"
    }

    private val args: HabitFragmentArgs by navArgs()
    private val viewBinding: FragmentHabitBinding by viewBinding()
    private val viewModel: HabitViewModel by viewModels()

    private lateinit var adapter: ArrayAdapter<HabitPriority>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        initSpinner()
        addColorPicker(viewBinding.llt)
        with(viewBinding) {
            viewModel.observeState(viewLifecycleOwner) { state ->
                if (!state.isAddingMode) {
                    etHabitName.setText(state.name)
                    etHabitDescription.setText(state.description)
                    etHabitPeriodicity.setText(state.periodicity)
                    etNumberOfExecutions.setText(state.numberOfExecutions)
                    when (state.type) {
                        HabitType.BAD -> rgHabitType.check(R.id.rb_bad)
                        HabitType.GOOD -> rgHabitType.check(R.id.rb_good)
                    }
                    btnSave.text = getString(R.string.btn_update_text)
                    ivSelectedColor.setColorFilter(state.pickedColor)
                    val a = FloatArray(3)
                    Color.colorToHSV(state.pickedColor, a)
                    tvHsv.text = resources.getString(R.string.hsv_formatted, a[0], a[1], a[2])
                    tvRgb.text = resources.getString(
                        R.string.rgb_formatted,
                        Color.red(state.pickedColor),
                        Color.green(state.pickedColor),
                        Color.blue(state.pickedColor)
                    )

                    val spinnerPosition = adapter.getPosition(state.priority)
                    spinnerHabitPriority.setSelection(spinnerPosition)
                } else {
                    btnSave.text = getString(R.string.btn_save_text)
                }

                btnSave.setOnClickListener {
                    if (isValidate()) {
                        if (!state.isAddingMode) updateHabit() else saveHabit()
                        findNavController().navigateUp()
                        hideKeyboard()
                        setFragmentResult(
                            NUMBER_OF_TAB_KEY,
                            bundleOf(NUMBER_OF_TAB to state.type.numOfTab)
                        )
                    }
                }

                rgHabitType.setOnCheckedChangeListener { radioGroup, checkedId ->
                    when (checkedId) {
                        R.id.rb_good -> viewModel.chooseType(HabitType.GOOD)
                        R.id.rb_bad -> viewModel.chooseType(HabitType.BAD)
                    }
                }

                spinnerHabitPriority.onItemSelectedListener { parent, position ->
                    viewModel.choosePriority(
                        HabitPriority.fromString(
                            parent.getItemAtPosition(position).toString()
                        )
                    )
                }
            }
        }
    }

    private fun saveHabit() {
        with(viewBinding) {
            val habit = Habit(
                name = etHabitName.text.toString(),
                description = etHabitDescription.text.toString(),
                priority = viewModel.currentState.priority,
                type = viewModel.currentState.type,
                periodicity = etHabitPeriodicity.text.toString(),
                numberOfExecutions = etNumberOfExecutions.text.toString(),
                color = viewModel.currentState.pickedColor
            )
            viewModel.addHabit(habit)
        }
    }

    private fun updateHabit() {
        with(viewBinding) {
            val updatedHabit = Habit(
                id = args.habitId,
                name = etHabitName.text.toString(),
                description = etHabitDescription.text.toString(),
                priority = viewModel.currentState.priority,
                type = viewModel.currentState.type,
                periodicity = etHabitPeriodicity.text.toString(),
                numberOfExecutions = etNumberOfExecutions.text.toString(),
                color = viewModel.currentState.pickedColor
            )
            viewModel.update(updatedHabit)
        }
    }

    private fun addColorPicker(root: View) {
        val squareOnScreenCount = 4
        val pickerWidth =
            Resources.getSystem().displayMetrics.widthPixels - root.paddingLeft - root.paddingRight
        val squareMarginWidth: Int = pickerWidth / squareOnScreenCount / 6
        val squareWidth: Int = squareMarginWidth * 4

        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(squareMarginWidth)
        layoutParams.width = squareWidth
        layoutParams.height = squareWidth

        root.layoutParams.height = squareWidth + squareMarginWidth * 2

        val spectrumHeight = squareWidth + 2 * squareMarginWidth
        val spectrumWidth = (squareWidth + 2 * squareMarginWidth) * 16

        val bitmap = ResourcesCompat
            .getDrawable(resources, R.drawable.hsv_model, null)!!
            .toBitmap(width = spectrumWidth, height = spectrumHeight)

        val halfSize = squareWidth / 2
        val doubleMarginSide = squareMarginWidth * 2

        val a = FloatArray(3)

        for (i in 0..15) {

            val imageView = ImageView(context)
            imageView.setImageResource(R.drawable.square)
            imageView.layoutParams = layoutParams
            imageView.setBackgroundResource(R.drawable.border_black)

            val pixel = bitmap.getPixel(
                (doubleMarginSide + squareWidth) * i + squareMarginWidth + halfSize,
                squareMarginWidth + halfSize
            )

            val r = Color.red(pixel)
            val g = Color.green(pixel)
            val b = Color.blue(pixel)

            val color = Color.rgb(r, g, b)

            imageView.setColorFilter(color)

            imageView.setOnClickListener {
                viewBinding.ivSelectedColor.colorFilter = imageView.colorFilter
                viewModel.chooseColor(color)
                Color.colorToHSV(color, a)
                with(viewBinding) {
                    tvRgb.text = resources.getString(R.string.rgb_formatted, r, g, b)
                    tvHsv.text =
                        resources.getString(R.string.hsv_formatted, a[0], a[1], a[2])
                }
            }

            (root as LinearLayout).addView(imageView)
        }
    }

    private fun initSpinner() {
        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            HabitPriority.values()
        )
        viewBinding.spinnerHabitPriority.adapter = adapter
    }

    private fun isValidate(): Boolean =
        validateName() && validateDescription() && validatePeriodicity() && validateNumberOfExecutions()

    private fun validateName(): Boolean {
        with(viewBinding) {
            if (etHabitName.text.toString().trim().isEmpty()) {
                tilHabitName.error = "Enter name!"
                etHabitName.requestFocus()
                return false
            } else {
                tilHabitName.isErrorEnabled = false
            }
            return true
        }
    }

    private fun validateDescription(): Boolean {
        with(viewBinding) {
            if (etHabitDescription.text.toString().trim().isEmpty()) {
                tilHabitDescription.error = "Enter description!"
                etHabitDescription.requestFocus()
                return false
            } else {
                tilHabitDescription.isErrorEnabled = false
            }
            return true
        }
    }

    private fun validatePeriodicity(): Boolean {
        with(viewBinding) {
            if (etHabitPeriodicity.text.toString().trim().isEmpty()) {
                tilHabitPeriodicity.error = "Enter periodicity!"
                etHabitPeriodicity.requestFocus()
                return false
            } else {
                tilHabitPeriodicity.isErrorEnabled = false
            }
            return true
        }
    }

    private fun validateNumberOfExecutions(): Boolean {
        with(viewBinding) {
            if (etNumberOfExecutions.text.toString().trim().isEmpty()) {
                tilNumberOfExecutions.error = "Enter number of executions!"
                etNumberOfExecutions.requestFocus()
                return false
            } else {
                tilNumberOfExecutions.isErrorEnabled = false
            }
            return true
        }
    }
}