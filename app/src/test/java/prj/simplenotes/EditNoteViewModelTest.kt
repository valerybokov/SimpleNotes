package prj.simplenotes

import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import prj.simplenotes.data.Note
import prj.simplenotes.fakes.FakeNotesRepository
import prj.simplenotes.fakes.FakeSettings
import prj.simplenotes.ui.editnotefragment.EditNoteViewModel
import prj.simplenotes.ui.editnotefragment.HANDLE_BACKGROUND
import prj.simplenotes.ui.editnotefragment.HANDLE_IS_COMPLETED
import prj.simplenotes.ui.editnotefragment.HANDLE_OPERATION_TYPE
import prj.simplenotes.ui.editnotefragment.HANDLE_TEXT
import prj.simplenotes.ui.editnotefragment.HANDLE_TITLE
import prj.simplenotes.ui.editnotefragment.OPERATION_UPDATE
import prj.simplenotes.ui.settingsfragment.TXT_SIZE_COEFF_KEY


class EditNoteViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun testInit_parameter_is_not_null() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = EditNoteViewModel(
            FakeNotesRepository(), FakeSettings(), savedStateHandle)

        val text = "abcd"
        val title = "title"
        val isCompleted = true
        val background = Color.WHITE
        val note = Note(id = 1, background = background,
            title = title, text = text,
            isCompleted = isCompleted)
        viewModel.init(note)

        assertEquals(
            "isCompleted",
            isCompleted, viewModel.isCompleted.value)

        assertEquals(
            "text",
            text, viewModel.text.value)

        assertEquals(
            "title",
            title, viewModel.title.value)
    }

    @Test
    fun testInit_parameter_is_null() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = EditNoteViewModel(
            FakeNotesRepository(), FakeSettings(), savedStateHandle)

        viewModel.init(null)

        assertEquals(
            "isCompleted",
            false, viewModel.isCompleted.value)

        assertEquals(
            "text",
            "", viewModel.text.value)

        assertEquals(
            "title",
            "", viewModel.title.value)
    }

    @Test
    fun testDefaultTextSize() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = EditNoteViewModel(
            FakeNotesRepository(), FakeSettings(), savedStateHandle)

        assertEquals("getTextSize", 2f, viewModel.getTextSize(2f))
    }

    @Test
    fun testTextSize() {
        val savedStateHandle = SavedStateHandle()
        val settings = FakeSettings()
        settings.write(TXT_SIZE_COEFF_KEY, 4f)

        val viewModel = EditNoteViewModel(
            FakeNotesRepository(), settings, savedStateHandle)

        assertEquals("getTextSize coef 4", 12f, viewModel.getTextSize(3f))
    }

    @Test
    fun testSaveToSavedStateHandle() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = EditNoteViewModel(
            FakeNotesRepository(), FakeSettings(), savedStateHandle)

        viewModel.init(null)

        assertEquals(
            "isCompleted not changed",
            false, viewModel.isCompleted.value)

        viewModel.updateIsCompleted()

        assertEquals(
            "isCompleted changed",
            true, viewModel.isCompleted.value)
    }

    @Test
    fun testRestoreFromSavedStateHandle_HasData() {
        val isCompleted = true
        val text = "abc"
        val title = "title"
        val background = Color.BLUE
        val savedStateHandle = SavedStateHandle(
            mapOf(
                HANDLE_OPERATION_TYPE to OPERATION_UPDATE,
                HANDLE_TITLE to title,
                HANDLE_TEXT to text,
                HANDLE_BACKGROUND to background,
                HANDLE_IS_COMPLETED to isCompleted))
        val viewModel = EditNoteViewModel(
            FakeNotesRepository(), FakeSettings(), savedStateHandle)

        viewModel.init(null)

        assertEquals(
            "restore isCompleted",
            isCompleted, viewModel.isCompleted.value)

        assertEquals(
            "restore title",
            title, viewModel.title.value)

        assertEquals(
            "restore text",
            text, viewModel.text.value)

        assertEquals(
            "restore background",
            background, viewModel.itemBackground)
    }

    @Test
    fun testRestoreFromSavedStateHandle_HasNoText() {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                HANDLE_OPERATION_TYPE to OPERATION_UPDATE,
                HANDLE_IS_COMPLETED to true))
        val viewModel = EditNoteViewModel(
            FakeNotesRepository(), FakeSettings(), savedStateHandle)

        viewModel.init(null)

        assertEquals(
            "restore isCompleted",
            true, viewModel.isCompleted.value)

        assertEquals(
            "restore title",
            "", viewModel.title.value)

        assertEquals(
            "restore text",
            "", viewModel.text.value)
    }
}