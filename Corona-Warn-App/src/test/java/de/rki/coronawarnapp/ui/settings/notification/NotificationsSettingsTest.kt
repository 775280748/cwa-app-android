package de.rki.coronawarnapp.ui.settings.notification

import androidx.core.app.NotificationManagerCompat
import de.rki.coronawarnapp.storage.LocalData
import de.rki.coronawarnapp.storage.preferences.SettingsData
import de.rki.coronawarnapp.ui.settings.notifications.NotificationSettings
import de.rki.coronawarnapp.util.device.ForegroundState
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.verify
import io.mockk.verifySequence
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import testhelpers.BaseTest

class NotificationsSettingsTest : BaseTest() {

    @MockK lateinit var foregroundState: ForegroundState
    @MockK lateinit var notificationManagerCompat: NotificationManagerCompat
    @MockK lateinit var settingsData: SettingsData

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        mockkObject(LocalData)

        every { settingsData.isNotificationsRiskEnabledFlow } returns flow { emit(true) }
        every { settingsData.isNotificationsRiskEnabled = any() } just Runs
        every { settingsData.isNotificationsRiskEnabled } returns true

        every { LocalData.isNotificationsTestEnabledFlow } returns flow { emit(true) }
        every { LocalData.isNotificationsTestEnabled = any() } just Runs
        every { LocalData.isNotificationsTestEnabled } returns true

        every { notificationManagerCompat.areNotificationsEnabled() } returns true
        coEvery { foregroundState.isInForeground } returns flow { emit(true) }
    }

    private fun createInstance() = NotificationSettings(
        foregroundState = foregroundState,
        notificationManagerCompat = notificationManagerCompat,
        settingsData = settingsData
    )

    @Test
    fun isNotificationsEnabled() = runBlockingTest {
        createInstance().apply {
            isNotificationsEnabled.first() shouldBe true
            verifySequence {
                foregroundState.isInForeground
                notificationManagerCompat.areNotificationsEnabled()
            }
        }
    }

    @Test
    fun isNotificationsRiskEnabled() = runBlockingTest {
        createInstance().apply {
            isNotificationsRiskEnabled.first() shouldBe true
        }
    }

    @Test
    fun isNotificationsTestEnabled() = runBlockingTest {
        createInstance().apply {
            isNotificationsTestEnabled.first() shouldBe true
        }
    }

    @Test
    fun toggleNotificationsRiskEnabled() {
        createInstance().toggleNotificationsRiskEnabled()
        verify { settingsData.isNotificationsRiskEnabled = false }
    }

    @Test
    fun toggleNotificationsTestEnabled() {
        createInstance().toggleNotificationsTestEnabled()
        verify { LocalData.isNotificationsTestEnabled = false }
    }
}
