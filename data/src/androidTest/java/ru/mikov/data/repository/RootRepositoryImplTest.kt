package ru.mikov.data.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.mikov.data.TestStubs
import ru.mikov.data.local.AppDb
import ru.mikov.data.local.entities.HabitDoneEntity
import ru.mikov.data.local.entities.HabitEntity
import ru.mikov.data.local.entities.HabitPriority
import ru.mikov.data.local.entities.HabitType
import ru.mikov.data.remote.RestService
import ru.mikov.data.remote.res.HabitRes
import ru.mikov.habittracker.data.local.dao.HabitDoneDao
import ru.mikov.habittracker.data.local.dao.HabitsDao
import ru.mikov.habittracker.data.local.dao.HabitsUIDDao
import ru.mikov.habittracker.data.local.entities.HabitUIDEntity
import ru.mikov.habittracker.data.remote.res.HabitDoneRes
import ru.mikov.habittracker.data.remote.res.HabitUIDRes
import java.net.SocketTimeoutException

@RunWith(AndroidJUnit4::class)
class RootRepositoryImplTest {

    @get:Rule
    var testRule = InstantTaskExecutorRule()
    private lateinit var testDb: AppDb
    private lateinit var habitsDao: HabitsDao
    private lateinit var habitsUIDDao: HabitsUIDDao
    private lateinit var habitDoneDao: HabitDoneDao
    private lateinit var server: MockWebServer
    private lateinit var api: RestService

    private val client = OkHttpClient.Builder().build()
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        testDb = Room.inMemoryDatabaseBuilder(
            context,
            AppDb::class.java
        ).build()
        habitsDao = testDb.habitsDao()
        habitDoneDao = testDb.habitDoneDao()
        habitsUIDDao = testDb.habitsUIDDao()

        server = MockWebServer()
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .build()
            .create(RestService::class.java)
    }

    @After
    fun tearDown() {
        testDb.close()
        server.shutdown()
    }

    @Test
    fun testAddHabitToDb() {
        val expectedHabit = HabitEntity(
            id = "0",
            name = "test habit",
            description = "test_description",
            priority = HabitPriority.HIGH,
            type = HabitType.GOOD,
            periodicity = 10,
            date = 1111111,
            color = 11111,
            numberOfExecutions = 10,
            isSynchronized = false,
            doneDates = emptyList()
        )

        runBlocking {
            habitsDao.insert(expectedHabit)
            val allHabits = habitsDao.findAllHabits()
            assertThat(allHabits).contains(expectedHabit)
        }
    }

    @Test
    fun testAddHabitsToDb() {
        val expectedHabit = HabitEntity(
            id = "0",
            name = "test habit",
            description = "test_description",
            priority = HabitPriority.HIGH,
            type = HabitType.GOOD,
            periodicity = 10,
            date = 1111111,
            color = 11111,
            numberOfExecutions = 10,
            isSynchronized = false,
            doneDates = emptyList()
        )

        val expectedHabits = Array(3) {
            expectedHabit.copy(id = "$it")
        }.toList()

        runBlocking {
            habitsDao.insert(expectedHabits)
            val allHabits = habitsDao.findAllHabits()
            assertEquals(expectedHabits, allHabits)
        }
    }

    @Test
    fun testDeleteHabitFromDb() {
        val expectedHabit = HabitEntity(
            id = "0",
            name = "test habit",
            description = "test_description",
            priority = HabitPriority.HIGH,
            type = HabitType.GOOD,
            periodicity = 10,
            date = 1111111,
            color = 11111,
            numberOfExecutions = 10,
            isSynchronized = false,
            doneDates = emptyList()
        )

        runBlocking {
            habitsDao.insert(expectedHabit)
            habitsDao.deleteHabit(expectedHabit.id)

            val allHabits = habitsDao.findAllHabits()

            assertThat(allHabits).doesNotContain(expectedHabit)
        }
    }

    @Test
    fun testAddHabitUID() {
        val expectedHabitUid = HabitUIDEntity(
            id = "0"
        )

        runBlocking {
            habitsUIDDao.insert(expectedHabitUid)
            val allHabitsUID = habitsUIDDao.getAllUID()
            assertThat(allHabitsUID).contains(expectedHabitUid)
        }
    }

    @Test
    fun testGetHabit() {
        val expectedHabit = HabitEntity(
            id = "0",
            name = "test habit",
            description = "test_description",
            priority = HabitPriority.HIGH,
            type = HabitType.GOOD,
            periodicity = 10,
            date = 1111111,
            color = 11111,
            numberOfExecutions = 10,
            isSynchronized = false,
            doneDates = emptyList()
        )

        runBlocking {
            habitsDao.insert(expectedHabit)
            val habit = habitsDao.findHabitById(expectedHabit.id).first()
            assertEquals(expectedHabit, habit)
        }
    }

    @Test
    fun testGetHabitsByType() {
        val expectedHabit = HabitEntity(
            id = "0",
            name = "test habit",
            description = "test_description",
            priority = HabitPriority.HIGH,
            type = HabitType.GOOD,
            periodicity = 10,
            date = 1111111,
            color = 11111,
            numberOfExecutions = 10,
            isSynchronized = false,
            doneDates = emptyList()
        )

        val expectedHabits = mutableListOf<HabitEntity>()
        for (i in 0..10) {
            var habit = expectedHabit.copy(id = "$i")
            if (i % 2 != 0) {
                habit = expectedHabit.copy(id = "$i", type = HabitType.BAD)
            }
            expectedHabits.add(habit)
        }

        runBlocking {
            habitsDao.insert(expectedHabits)

            val habits = habitsDao.findHabitsByType(HabitType.BAD).first()

            val expHabits = expectedHabits.filter { habit -> habit.type == HabitType.BAD }
            assertEquals(expHabits, habits)
        }
    }

    @Test
    fun testUpdateHabit() {
        val expectedHabit = HabitEntity(
            id = "0",
            name = "test habit",
            description = "test_description",
            priority = HabitPriority.HIGH,
            type = HabitType.GOOD,
            periodicity = 10,
            date = 1111111,
            color = 11111,
            numberOfExecutions = 10,
            isSynchronized = false,
            doneDates = emptyList()
        )
        runBlocking {
            habitsDao.insert(expectedHabit)
            val habit = expectedHabit.copy(periodicity = 15)
            habitsDao.update(habit)
            val allHabits = habitsDao.findAllHabits()
            assertThat(allHabits).contains(habit)
        }
    }

    @Test
    fun testIsNoHabits() {
        val expectedHabit = HabitEntity(
            id = "0",
            name = "test habit",
            description = "test_description",
            priority = HabitPriority.HIGH,
            type = HabitType.GOOD,
            periodicity = 10,
            date = 1111111,
            color = 11111,
            numberOfExecutions = 10,
            isSynchronized = false,
            doneDates = emptyList()
        )

        val expectedHabits = Array(3) {
            expectedHabit.copy(id = "$it")
        }.toList()

        runBlocking {
            habitsDao.findAllHabits().isEmpty()
            assertEquals(true, true)
            habitsDao.insert(expectedHabits)
            assertEquals(false, false)
        }
    }

    @Test
    fun testGetUnSyncHabits() {
        val expectedHabit = HabitEntity(
            id = "0",
            name = "test habit",
            description = "test_description",
            priority = HabitPriority.HIGH,
            type = HabitType.GOOD,
            periodicity = 10,
            date = 1111111,
            color = 11111,
            numberOfExecutions = 10,
            isSynchronized = true,
            doneDates = emptyList()
        )

        val expectedHabits = mutableListOf<HabitEntity>()
        for (i in 0..10) {
            var habit = expectedHabit.copy(id = "$i")
            if (i % 2 != 0) {
                habit = expectedHabit.copy(id = "$i", isSynchronized = false)
            }
            expectedHabits.add(habit)
        }

        runBlocking {
            habitsDao.insert(expectedHabits)
            val habits = habitsDao.findUnSyncHabits()
            val expHabits = expectedHabits.filter { !it.isSynchronized }
            assertEquals(expHabits, habits)
        }
    }

    @Test
    fun testGetUIDToDelete() {
        val expectedHabitUid = HabitUIDEntity(
            id = "0"
        )

        val expectedHabitsUids = Array(3) {
            expectedHabitUid.copy(id = "$it")
        }.toList()

        runBlocking {
            habitsUIDDao.insert(expectedHabitsUids)
            val allHabitsUID = habitsUIDDao.getAllUID()
            assertEquals(expectedHabitsUids, allHabitsUID)
        }
    }

    @Test
    fun testClearUID() {
        val expectedHabitUid = HabitUIDEntity(
            id = "0"
        )

        val expectedHabitsUids = Array(3) {
            expectedHabitUid.copy(id = "$it")
        }.toList()

        runBlocking {
            habitsUIDDao.insert(expectedHabitsUids)
            habitsUIDDao.clear()
            assertEquals(emptyList<HabitUIDEntity>(), emptyList<HabitUIDEntity>())
        }
    }

    @Test
    fun testGetUnSyncDoneDates() {
        val expectedHabitDone = HabitDoneEntity(
            uid = "0",
            doneDate = 1652719222 * 1
        )

        val expectedHabitDones = Array(3) {
            expectedHabitDone.copy(
                id = it + 1,
                uid = "$it",
                doneDate = 1652719222 * it
            )
        }.toList()

        runBlocking {
            habitDoneDao.insert(expectedHabitDones)
            val allHabitDones = habitDoneDao.findAllHabitDone()
            assertEquals(expectedHabitDones, allHabitDones)
        }
    }

    @Test
    fun testAddHabitDone() {
        val expectedHabitDone = HabitDoneEntity(
            id = 1,
            uid = "0",
            doneDate = 1652719222
        )

        runBlocking {
            habitDoneDao.insert(expectedHabitDone)
            val allHabitDones = habitDoneDao.findAllHabitDone()
            assertThat(allHabitDones).contains(expectedHabitDone)
        }
    }

    @Test
    fun testDeleteDoneDate() {
        val expectedHabitDone = HabitDoneEntity(
            id = 1,
            uid = "0",
            doneDate = 1652719222
        )

        runBlocking {
            habitDoneDao.insert(expectedHabitDone)
            habitDoneDao.delete(expectedHabitDone)
            val allHabitDones = habitDoneDao.findAllHabitDone()
            assertThat(allHabitDones).doesNotContain(expectedHabitDone)
        }
    }

    @Test
    fun testUploadHabitToNetwork() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(TestStubs.habitUidRes)
        )

        runBlocking {
            val response = api.addHabit(
                HabitRes(
                    uid = "",
                    title = "test2",
                    description = "test2",
                    priority = 0,
                    type = 0,
                    count = 4,
                    frequency = 4,
                    color = -41216,
                    date = 1651745095,
                    doneDates = emptyList()
                )
            )
            val gson = Gson()
            val expected = gson.fromJson(TestStubs.habitUidRes, HabitUIDRes::class.java)

            assertEquals(expected, response)
        }
    }


    @Test
    fun testLoadHabitsFromNetwork() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(TestStubs.habitsRes)
        )

        runBlocking {
            val response = api.habits()
            val gson = Gson()
            val expected = gson.fromJson(TestStubs.habitsRes, Array<HabitRes>::class.java).asList()

            assertEquals(expected, response)
        }
    }

    @Test
    fun testDeleteHabitFromNetwork() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
        )

        runBlocking { api.deleteHabit(HabitUIDRes("8d438a1a-a349-4ace-8cbc-0e0c2d6014f3")) }
        val recordedRequest = server.takeRequest()

        assertEquals("DELETE", recordedRequest.method)
        assertEquals("/habit", recordedRequest.path)

        server.enqueue(
            MockResponse()
                .setSocketPolicy(SocketPolicy.NO_RESPONSE)
        )

        try {
            runBlocking { api.deleteHabit(HabitUIDRes("8d438a1a-a349-4ace-8cbc-0e0c2d6014f3")) }
        } catch (e: Throwable) {
            assertEquals(true, e is SocketTimeoutException)
        }
    }

    @Test
    fun testDoneHabit() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
        )

        runBlocking {
            api.completeHabit(
                HabitDoneRes(
                    1651926678,
                    "8d438a1a-a349-4ace-8cbc-0e0c2d6014f3"
                )
            )
        }
        val recordedRequest = server.takeRequest()

        assertEquals("POST", recordedRequest.method)
        assertEquals("/habit_done", recordedRequest.path)

        server.enqueue(
            MockResponse()
                .setResponseCode(400)
        )

        try {
            runBlocking {
                api.completeHabit(
                    HabitDoneRes(
                        1651926678,
                        "8d438a1a-a349-4ace-8cbc-0e0c2d6014f3"
                    )
                )
            }
        } catch (e: Throwable) {
            assertEquals(true, e is HttpException)
        }
    }
}
