package de.Maxr1998.modernpreferences.testing

import android.content.Context
import android.content.SharedPreferences
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.preferences.SwitchPreference
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.boolean
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PreferencesTests {

    private val prefsMock = mockk<SharedPreferences>()
    private val prefsEditorMock = mockk<SharedPreferences.Editor>()
    private lateinit var testScreen: PreferenceScreen

    @BeforeAll
    fun setup() {
        // Setup mocks for SharedPreferences
        every { prefsMock.edit() } returns prefsEditorMock
        val contextMock = mockk<Context>()
        every { contextMock.packageName } returns "package"
        every { contextMock.getSharedPreferences(any(), any()) } returns prefsMock

        // Create PreferenceScreen for tests
        testScreen = PreferenceScreen.Builder(contextMock).build()
    }

    @Test
    fun `Non-persistent preferences should return default value for all get operations`() {
        runBlocking {
            val pref = Preference(DEFAULT_KEY).apply { persistent = false }
            checkAll<Int>(10) { value ->
                pref.getInt(value) shouldBe value
            }
            checkAll(Exhaustive.boolean()) { value ->
                pref.getBoolean(value) shouldBe value
            }
            checkAll<String>(10) { value ->
                pref.getString(value) shouldBe value
            }
        }
    }

    @Test
    fun `Setting checked before TwoStatePreference is attached should throw`() {
        shouldThrow<IllegalStateException> {
            SwitchPreference(DEFAULT_KEY).apply {
                checked = true
            }
        }
    }
}