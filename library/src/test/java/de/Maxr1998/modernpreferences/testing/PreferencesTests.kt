package de.Maxr1998.modernpreferences.testing

import android.content.Context
import com.github.ivanshafran.sharedpreferencesmock.SPMockBuilder
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.helpers.pref
import de.Maxr1998.modernpreferences.helpers.screen
import de.Maxr1998.modernpreferences.helpers.subScreen
import de.Maxr1998.modernpreferences.helpers.switch
import de.Maxr1998.modernpreferences.preferences.SwitchPreference
import de.Maxr1998.modernpreferences.preferences.TwoStatePreference
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.data.blocking.forAll
import io.kotest.data.row
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

    private val contextMock: Context = mockk()

    @BeforeAll
    fun setup() {
        // Setup mocks for SharedPreferences
        val sharedPreferences = SPMockBuilder().createSharedPreferences()
        every { contextMock.packageName } returns "package"
        every { contextMock.getSharedPreferences(any(), any()) } returns sharedPreferences
    }

    @Test
    fun `Non-persistent preferences should return default value or null for all get operations`() {
        runBlocking {
            val pref = Preference(uniqueKeySequence.next()).apply { persistent = false }
            checkAll<Int>(10) { value ->
                pref.getInt(value) shouldBe value
            }
            checkAll(2, Exhaustive.boolean()) { value ->
                pref.getBoolean(value) shouldBe value
            }
            pref.getString() shouldBe null
            pref.getStringSet() shouldBe null
        }
    }

    @Test
    fun `Setting checked before TwoStatePreference is attached should throw`() {
        shouldThrow<IllegalStateException> {
            SwitchPreference(uniqueKeySequence.next()).apply {
                checked = true
            }
        }
    }

    @Test
    fun `TwoStatePreference should respect checked and disableDependents for state`() {
        runBlocking {
            forAll(
                // Basically a XOR, but written out for testing
                row(a = false, b = false, c = false),
                row(a = true, b = false, c = true),
                row(a = false, b = true, c = true),
                row(a = true, b = true, c = false)
            ) { checked: Boolean, disableDependents: Boolean, state: Boolean ->
                lateinit var pref: TwoStatePreference
                screen(contextMock) {
                    pref = switch(uniqueKeySequence.next()) {
                        this.disableDependents = disableDependents
                    }
                }
                pref.checked = checked
                pref.state shouldBe state
            }
        }
    }

    private suspend fun check(dependent: Preference, dependency: TwoStatePreference) {
        // Check initial
        dependent.enabled shouldBe dependency.state

        // Check after changing the state in multiple iterations
        checkAll(4, Exhaustive.boolean()) { value ->
            dependency.checked = value
            dependent.enabled shouldBe dependency.state
        }
    }

    @Test
    fun `Dependents should properly follow their dependency's state`() {
        lateinit var dependent: Preference
        lateinit var dependency: TwoStatePreference

        runBlocking {
            checkAll(2, Exhaustive.boolean()) { disableDependents ->
                screen(contextMock) {
                    val dependencyKey = uniqueKeySequence.next()
                    dependent = pref(uniqueKeySequence.next()) {
                        this.dependency = dependencyKey
                    }
                    dependency = switch(dependencyKey) {
                        this.disableDependents = disableDependents
                    }
                }
                check(dependent, dependency)

                // With inverted order of dependent and dependency
                screen(contextMock) {
                    val dependencyKey = uniqueKeySequence.next()
                    dependency = switch(dependencyKey) {
                        this.disableDependents = disableDependents
                    }
                    dependent = pref(uniqueKeySequence.next()) {
                        this.dependency = dependencyKey
                    }
                }
                check(dependent, dependency)

                // With sub-screens
                screen(contextMock) {
                    val dependencyKey = uniqueKeySequence.next()
                    dependent = pref(uniqueKeySequence.next()) {
                        this.dependency = dependencyKey
                    }
                    subScreen {
                        dependency = switch(dependencyKey) {
                            this.disableDependents = disableDependents
                        }
                    }
                }
                check(dependent, dependency)

                screen(contextMock) {
                    val dependencyKey = uniqueKeySequence.next()
                    subScreen {
                        dependent = pref(uniqueKeySequence.next()) {
                            this.dependency = dependencyKey
                        }
                    }
                    dependency = switch(dependencyKey) {
                        this.disableDependents = disableDependents
                    }
                }
                check(dependent, dependency)

                screen(contextMock) {
                    val dependencyKey = uniqueKeySequence.next()
                    dependency = switch(dependencyKey) {
                        this.disableDependents = disableDependents
                    }
                    subScreen {
                        dependent = pref(uniqueKeySequence.next()) {
                            this.dependency = dependencyKey
                        }
                    }
                }
                check(dependent, dependency)

                screen(contextMock) {
                    val dependencyKey = uniqueKeySequence.next()
                    subScreen {
                        dependency = switch(dependencyKey) {
                            this.disableDependents = disableDependents
                        }
                    }
                    dependent = pref(uniqueKeySequence.next()) {
                        this.dependency = dependencyKey
                    }
                }
                check(dependent, dependency)
            }
        }
    }

    @Test
    fun `Saved state should be empty for adapter without content`() {
        val adapter = createPreferenceAdapter()
        adapter.getSavedState().screenPath.size shouldBe 0
    }
}