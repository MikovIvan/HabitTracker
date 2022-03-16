package ru.mikov.habittracker.ui.habit

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.setMargins
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.mikov.habittracker.R
import ru.mikov.habittracker.data.entities.Habit
import ru.mikov.habittracker.databinding.ActivityHabitBinding
import ru.mikov.habittracker.ui.main.EXTRA_HABIT
import java.util.*


class HabitActivity : AppCompatActivity() {

    private val viewBinding: ActivityHabitBinding by viewBinding()
    private val viewModel: HabitViewModel by viewModels()
    private var habit: Habit? = null
    private var pickedColor = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit)

        habit = intent.getSerializableExtra(EXTRA_HABIT) as? Habit

        initSpinner(habit)
        addColorPicker(viewBinding.llt)
        initViews(viewBinding)
    }

    private fun initViews(binding: ActivityHabitBinding) {
        with(binding) {
            if (habit != null) {
                etHabitName.setText(habit!!.name)
                etHabitDescription.setText(habit!!.description)
                etHabitPeriodicity.setText(habit!!.periodicity)
                etNumberOfExecutions.setText(habit!!.numberOfExecutions)
                rgHabitType.check((rgHabitType.getChildAt(habit!!.type) as RadioButton).id)
                btnSave.text = getString(R.string.btn_update_text)
                ivSelectedColor.setColorFilter(habit!!.color)
                val a = FloatArray(3)
                Color.colorToHSV(habit!!.color, a)
                tvHsv.text = resources.getString(R.string.hsv_formatted, a[0], a[1], a[2])
                tvRgb.text = resources.getString(
                    R.string.rgb_formatted,
                    Color.red(habit!!.color), Color.green(habit!!.color), Color.blue(habit!!.color)
                )
            } else {
                btnSave.text = getString(R.string.btn_save_text)
            }

            btnSave.setOnClickListener {
                if (isValidate()) {
                    if (habit != null) updateHabit() else saveHabit()
                    finish()
                }
            }
        }
    }

    private fun ActivityHabitBinding.saveHabit() {
        habit = Habit(
            id = UUID.randomUUID().toString(),
            name = etHabitName.text.toString(),
            description = etHabitDescription.text.toString(),
            priority = spinnerHabitPriority.selectedItem.toString(),
            type = rgHabitType.indexOfChild(rgHabitType.findViewById(rgHabitType.checkedRadioButtonId)),
            periodicity = etHabitPeriodicity.text.toString(),
            numberOfExecutions = etNumberOfExecutions.text.toString(),
            color = pickedColor
        )
        viewModel.addHabit(habit!!)
    }

    private fun ActivityHabitBinding.updateHabit() {
        val updatedHabit = habit!!.copy(
            id = habit?.id ?: UUID.randomUUID().toString(),
            name = etHabitName.text.toString(),
            description = etHabitDescription.text.toString(),
            priority = spinnerHabitPriority.selectedItem.toString(),
            type = rgHabitType.indexOfChild(rgHabitType.findViewById(rgHabitType.checkedRadioButtonId)),
            periodicity = etHabitPeriodicity.text.toString(),
            numberOfExecutions = etNumberOfExecutions.text.toString(),
            color = pickedColor
        )
        viewModel.update(updatedHabit)
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

            val imageView = ImageView(this)
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
                pickedColor = color
                Color.colorToHSV(color, a)
                viewBinding.tvRgb.text = resources.getString(R.string.rgb_formatted, r, g, b)
                viewBinding.tvHsv.text =
                    resources.getString(R.string.hsv_formatted, a[0], a[1], a[2])
            }

            (root as LinearLayout).addView(imageView)
        }
    }

    private fun initSpinner(habit: Habit?) {
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.priority,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewBinding.spinnerHabitPriority.adapter = adapter
        if (!habit?.priority.isNullOrBlank()) {
            val spinnerPosition = adapter.getPosition(habit?.priority)
            viewBinding.spinnerHabitPriority.setSelection(spinnerPosition)
        }
    }

    private fun isValidate(): Boolean =
        validateName() && validateDiscription() && validatePeriodicity() && validateNumberOfExecutions()

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

    private fun validateDiscription(): Boolean {
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