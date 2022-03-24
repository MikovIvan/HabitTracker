package ru.mikov.habittracker.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import ru.mikov.habittracker.R
import ru.mikov.habittracker.data.entities.HabitType
import ru.mikov.habittracker.databinding.FragmentViewPagerBinding
import ru.mikov.habittracker.ui.adapters.ViewPagerAdapter
import ru.mikov.habittracker.ui.habits.HabitsFragment


class ViewPagerFragment : Fragment(R.layout.fragment_view_pager) {

    private val viewBinding: FragmentViewPagerBinding by viewBinding()
    private val args: ViewPagerFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentList = arrayListOf<Fragment>(
            HabitsFragment.newInstance("Good", HabitType.GOOD),
            HabitsFragment.newInstance("Bad", HabitType.BAD)
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        with(viewBinding) {


            viewPager.adapter = adapter



            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> HabitType.GOOD.name
                    else -> HabitType.BAD.name
                }
            }.attach()

            when (args.type) {
                HabitType.GOOD -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        viewPager.setCurrentItem(0, true)
                    }, 100)
                }
                HabitType.BAD -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        viewPager.setCurrentItem(1, true)
                    }, 100)
                }
            }
        }
    }


}