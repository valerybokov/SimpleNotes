package prj.simplenotes

import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import prj.simplenotes.fakes.FakeSettings
import prj.simplenotes.ui.settingsfragment.BACKGROUND_KEY
import prj.simplenotes.ui.settingsfragment.DEFAULT_BACKGROUND_VALUE
import prj.simplenotes.ui.settingsfragment.SettingsViewModel
import prj.simplenotes.ui.settingsfragment.TXT_COLOR_KEY
import prj.simplenotes.ui.settingsfragment.TXT_SIZE_COEFF_KEY


class SettingsViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun testDefaultTextColor() {
        runTestInMain {
            val textColor = Color.GRAY
            val settings = FakeSettings()

            val viewModel = SettingsViewModel(settings)

            viewModel.init(textColor, Color.BLACK, 11f)

            assertEquals(
                "default textColor",
                textColor, viewModel.textColor.value
            )
        }
    }

    @Test
    fun testDefaultBackgroundColor() {
        runTestInMain {
            val backgroundColor = Color.GRAY
            val settings = FakeSettings()

            val viewModel = SettingsViewModel(settings)

            viewModel.init(Color.BLACK, backgroundColor, 11f)

            assertEquals(
                "default backgroundColor",
                backgroundColor, viewModel.backgroundColor.value
            )
        }
    }

    @Test
    fun testRestoreTextColor() {
        runTestInMain {
            val textColor = Color.RED
            val settings = FakeSettings()
            settings.write(TXT_SIZE_COEFF_KEY, 1f)
            settings.write(TXT_COLOR_KEY, textColor)
            settings.write(BACKGROUND_KEY, Color.BLUE)

            val viewModel = SettingsViewModel(settings)

            viewModel.init(Color.YELLOW, Color.BLACK, 11f)

            assertEquals(
                "saved textColor",
                textColor, viewModel.textColor.value)
        }
    }

    @Test
    fun testUpdateTextColor() {
        asyncTestInMain {
            val settings = FakeSettings()
            val viewModel = SettingsViewModel(settings)

            viewModel.init(Color.YELLOW, Color.BLACK, 11f)

            val newTextColor = Color.GREEN
            viewModel.updateTextColor(newTextColor)

            assertEquals(
                "updateTextColor",
                newTextColor,
                settings.readInt(TXT_COLOR_KEY, Color.WHITE)
            )
        }
    }

    @Test
    fun testUpdateBackground() {
        asyncTestInMain {
            val settings = FakeSettings()
            val viewModel = SettingsViewModel(settings)

            viewModel.init(Color.YELLOW, Color.BLACK, 11f)

            val newBackground = Color.GREEN
            viewModel.updateBackground(newBackground)

            assertEquals(
                "updateBackground",
                newBackground,
                settings.readInt(BACKGROUND_KEY, DEFAULT_BACKGROUND_VALUE)
            )
        }
    }
}