package ru.mikov.habittracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.mikov.habittracker.databinding.ActivityHabitBinding

class HabitActivity : AppCompatActivity() {

    private val viewBinding: ActivityHabitBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit)

        viewBinding.btnSave.setOnClickListener {
            finish()
        }
    }
}