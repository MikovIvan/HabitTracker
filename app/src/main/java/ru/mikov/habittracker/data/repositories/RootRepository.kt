package ru.mikov.habittracker.data.repositories

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import ru.mikov.habittracker.data.local.DbManager.db
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.local.entities.HabitType
import ru.mikov.habittracker.data.local.entities.HabitType.GOOD

object RootRepository {
    private var habitsDao = db.habitsDao()

    fun addHabit(habit: Habit) {
        habitsDao.insert(habit)
    }

    fun getHabit(id: Int): Habit {
        return habitsDao.findHabitById(id)
    }

    fun update(updatedHabit: Habit) {
        habitsDao.update(updatedHabit)
    }

    fun rawQueryHabits(filter: HabitFilter): LiveData<List<Habit>> {
        return habitsDao.findArticlesByRaw(SimpleSQLiteQuery(filter.toQuery()))
    }

}

class HabitFilter(
    private val searchQuery: String? = null,
    private val sortBy: Boolean = true,
    private val typeOfSort: Int = -1,
    val type: HabitType = GOOD,
) {
    fun toQuery(): String {
        val qb = QueryBuilder()
        qb.table("habits")
        qb.appendWhere("type LIKE '$type'")

        if (searchQuery != null) qb.appendWhere("name LIKE '%$searchQuery%'")
        if (typeOfSort == 0) qb.orderBy("name") else qb.orderBy("periodicity")
        if (sortBy && typeOfSort != -1 && searchQuery != null) qb.isDesc(false) else qb.isDesc(true)

        return qb.build()
    }
}

class QueryBuilder() {
    private var table: String? = null
    private var selectColumns: String = "*"
    private var joinTables: String? = null
    private var whereCondition: String? = null
    private var order: String? = null
    private var sort: String? = null

    fun build(): String {
        check(table != null) { "table must be not null" }
        val strBuilder = StringBuilder("SELECT ")
            .append("$selectColumns ")
            .append("FROM $table ")

        if (joinTables != null) strBuilder.append(joinTables)
        if (whereCondition != null) strBuilder.append(whereCondition)
        if (order != null) strBuilder.append(order)
        if (sort != null) strBuilder.append(sort)
        return strBuilder.toString()
    }

    fun table(table: String): QueryBuilder = apply { this.table = table }

    fun orderBy(column: String): QueryBuilder = apply {
        order = "ORDER BY $column "
    }

    fun appendWhere(condition: String, logic: String = "AND"): QueryBuilder = apply {
        if (whereCondition.isNullOrEmpty()) whereCondition = "WHERE $condition "
        else whereCondition += "$logic $condition "
    }

    fun isDesc(isDesc: Boolean = true): QueryBuilder = apply {
        sort = if (isDesc) "DESC" else "ASC"
    }
}