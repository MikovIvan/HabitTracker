package ru.mikov.habittracker.presentation.habits

import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

import org.junit.runner.RunWith
import ru.mikov.habittracker.R
import ru.mikov.habittracker.presentation.adapters.HabitViewHolder
import ru.mikov.habittracker.presentation.main.MainActivity

@RunWith(AndroidJUnit4::class)
class HabitsFragmentTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun test_edit_text_habit_name() {
        onView(withId(R.id.fab_add_habit)).perform(click())
        onView(withId(R.id.et_habit_name)).perform(typeText("Test"))

        activityRule.scenario.onActivity {
            val text = it.findViewById<TextInputEditText>(R.id.et_habit_name)
            assertEquals("Test", text.text.toString())
        }
    }

    @Test
    fun test_validation_of_edit_text_habit_name() {
        onView(withId(R.id.fab_add_habit)).perform(click())
        onView(withId(R.id.btn_save)).perform(scrollTo(), click())

        activityRule.scenario.onActivity {
            val text = it.findViewById<TextInputLayout>(R.id.til_habit_name)
            assertEquals("Enter name!", text.error.toString())
        }
    }

    @Test
    fun test_validation_of_color() {
        onView(withId(R.id.fab_add_habit)).perform(click())
        onView(childOf(withId(R.id.llt), 2)).perform(scrollTo(), click())

        activityRule.scenario.onActivity {
            val llt = it.findViewById<LinearLayout>(R.id.llt)
            val ivColor = llt.getChildAt(2) as ImageView
            val actualColor = ivColor.colorFilter

            val ivSelectedColor = it.findViewById<ImageView>(R.id.iv_selected_color)
            val selectedColor = ivSelectedColor.colorFilter
            assertEquals(actualColor, selectedColor)
        }
    }

    @Test
    fun test_delete_hebit() {
        onView(withId(R.id.fab_add_habit)).perform(click())
        onView(withId(R.id.et_habit_name)).perform(typeText("A habit to delete"))
        onView(withId(R.id.et_habit_description)).perform(scrollTo(), typeText("Test"))
        onView(withId(R.id.et_habit_periodicity)).perform(scrollTo(), typeText("10"))
        onView(withId(R.id.et_number_of_executions)).perform(scrollTo(), typeText("10"))
        onView(withId(R.id.btn_save)).perform(scrollTo(), click())

        onView(withId(R.id.rv_habits)).perform(
            RecyclerViewActions.actionOnItem<HabitViewHolder>(
                hasDescendant(withText("A habit to delete")),
                click()
            )
        )

        onView(withId(R.id.action_delete)).perform(click())

        activityRule.scenario.onActivity {
            //https://developer.android.com/training/testing/espresso/recipes#asserting-data-item-not-in-adapter
            onView(withId(R.id.rv_habits)).check(matches(not(withAdaptedData(withText("A habit to delete")))))
        }
    }


}

private fun withAdaptedData(dataMatcher: Matcher<View>): Matcher<View?> {
    return object : TypeSafeMatcher<View?>() {
        override fun describeTo(description: Description) {
            description.appendText("with class name: ")
            dataMatcher.describeTo(description)
        }

        override fun matchesSafely(view: View?): Boolean {
            val adapter: Adapter = view as Adapter
            for (i in 0 until adapter.count) {
                if (dataMatcher.matches(adapter.getItem(i))) {
                    return true
                }
            }
            return false
        }
    }
}

fun childOf(parentMatcher: Matcher<View?>, childPosition: Int): Matcher<View?> {
    return object : TypeSafeMatcher<View?>() {
        override fun describeTo(description: Description?) {
            description?.appendText("position $childPosition of parent ")
            parentMatcher.describeTo(description)
        }

        override fun matchesSafely(view: View?): Boolean {
            if (view?.parent !is ViewGroup) return false
            val parent = view.parent as ViewGroup
            return (parentMatcher.matches(parent)
                    && parent.childCount > childPosition && parent.getChildAt(childPosition) == view)
        }
    }
}