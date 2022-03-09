package ru.mikov.habittracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.mikov.habittracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val viewBinding: ActivityMainBinding by viewBinding()

    private val habitsAdapter: HabitAdapter = HabitAdapter {

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
    }
}