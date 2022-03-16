package ru.mikov.habittracker.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.mikov.habittracker.R
import ru.mikov.habittracker.databinding.ActivityMainBinding
import ru.mikov.habittracker.ui.adapters.HabitAdapter
import ru.mikov.habittracker.ui.habit.HabitActivity

const val EXTRA_HABIT = "HABIT"

class MainActivity : AppCompatActivity() {
    private val viewBinding: ActivityMainBinding by viewBinding()
    private val viewModel: MainViewModel by viewModels()
    private val habitsAdapter: HabitAdapter = HabitAdapter {
        startActivity(Intent(applicationContext, HabitActivity::class.java).apply {
            putExtra(EXTRA_HABIT, it)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        with(viewBinding) {
            fabAddHabit.setOnClickListener {
                startActivity(Intent(applicationContext, HabitActivity::class.java))
            }

            rvHabits.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = habitsAdapter
            }
        }

        viewModel.getHabits().observe(this, Observer {
            habitsAdapter.submitList(it)
        })

    }
}