package de.Maxr1998.modernpreferences.testing

import android.content.Context
import android.content.SharedPreferences
import de.Maxr1998.modernpreferences.PreferenceScreen
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeAll
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
}