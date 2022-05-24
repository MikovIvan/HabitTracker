package ru.mikov.habittracker.presentation.habit

import android.content.res.Resources
import android.graphics.Color
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.core.view.setMargins
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.mikov.data.local.entities.HabitPriority
import ru.mikov.data.local.entities.HabitType
import ru.mikov.domain.models.Habit
import ru.mikov.habittracker.R
import ru.mikov.habittracker.app.appComponent
import ru.mikov.habittracker.databinding.FragmentHabitBinding
import ru.mikov.habittracker.presentation.base.BaseFragment
import ru.mikov.habittracker.presentation.extentions.hideKeyboard
import ru.mikov.habittracker.presentation.extentions.lazyViewModel
import ru.mikov.habittracker.presentation.extentions.onItemSelectedListener
import java.util.*


class HabitFragment : BaseFragment<HabitState, HabitViewModel>(R.layout.fragment_habit) {

    companion object {
        const val NUMBER_OF_TAB_KEY = "NUMBER_OF_TAB_KEY"
        const val NUMBER_OF_TAB = "NUMBER_OF_TAB"
    }

    private val args: HabitFragmentArgs by navArgs()
    override val viewModel: HabitViewModel by lazyViewModel { stateHandle ->
        requireContext().appComponent.habitViewModel()
            .create(stateHandle, args.habitId)
    }
    private val viewBinding: FragmentHabitBinding by viewBinding()

    override fun renderUi(data: HabitState) {
        with(viewBinding) {
            if (!data.isAddingMode) {
                etHabitName.setText(data.name)
                etHabitDescription.setText(data.description)
                etHabitPeriodicity.setText(data.periodicity)
                etNumberOfExecutions.setText(data.numberOfExecutions)
                when (data.type) {
                    HabitType.BAD -> rgHabitType.check(R.id.rb_bad)
                    HabitType.GOOD -> rgHabitType.check(R.id.rb_good)
                }
                btnSave.text = getString(R.string.btn_update_text)
                ivSelectedColor.setColorFilter(data.pickedColor)
                val a = FloatArray(3)
                Color.colorToHSV(data.pickedColor, a)
                tvHsv.text = resources.getString(R.string.hsv_formatted, a[0], a[1], a[2])
                tvRgb.text = resources.getString(
                    R.string.rgb_formatted,
                    Color.red(data.pickedColor),
                    Color.green(data.pickedColor),
                    Color.blue(data.pickedColor)
                )

                spinnerHabitPriority.setSelection(data.priority.id)
            } else {
                btnSave.text = getString(R.string.btn_save_text)
            }
            updateOptionsMenu()
            if (data.isHabitLoaded || data.isHabitDeleted) findNavController().navigateUp()
        }
    }

    override fun setupViews() {
        addColorPicker(viewBinding.llt)
        initViews()

        viewBinding.viewmodel = viewModel
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.habit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) {
            viewModel.deleteHabit(args.habitId)
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_delete)
        item.isVisible = !viewModel.currentState.isAddingMode
    }

    private fun initViews() {
        with(viewBinding) {
            btnSave.setOnClickListener {
                if (isValidate()) {
                    if (!viewModel.currentState.isAddingMode) updateHabit() else saveHabit()
                    hideKeyboard()
                    setFragmentResult(
                        NUMBER_OF_TAB_KEY,
                        bundleOf(NUMBER_OF_TAB to viewModel.currentState.type.numOfTab)
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
                    HabitPriority.getById(position)
                )
            }
        }
    }

    private fun saveHabit() {
        with(viewBinding) {
            val habit = Habit(
                uid = "",
                title = etHabitName.text.toString(),
                description = etHabitDescription.text.toString(),
                priority = viewModel.currentState.priority.id,
                type = viewModel.currentState.type.id,
                frequency = etHabitPeriodicity.text.toString().toInt(),
                count = etNumberOfExecutions.text.toString().toInt(),
                color = viewModel.currentState.pickedColor,
                date = (Date().time / 1000).toInt()
            )
            viewModel.addHabit(habit)
        }
    }

    private fun updateHabit() {
        with(viewBinding) {
            val updatedHabit = Habit(
                uid = args.habitId,
                title = etHabitName.text.toString(),
                description = etHabitDescription.text.toString(),
                priority = viewModel.currentState.priority.id,
                type = viewModel.currentState.type.id,
                frequency = etHabitPeriodicity.text.toString().toInt(),
                count = etNumberOfExecutions.text.toString().toInt(),
                color = viewModel.currentState.pickedColor,
                date = (Date().time / 1000).toInt()
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

    private fun updateOptionsMenu() {
        requireActivity().invalidateOptionsMenu()
    }

    override fun init() {
        super.init()
        setHasOptionsMenu(true)
        requireContext().appComponent.subComponent().create().inject(this)
    }
}