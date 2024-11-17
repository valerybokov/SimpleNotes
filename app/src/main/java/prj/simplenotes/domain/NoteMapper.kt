package prj.simplenotes.domain

import prj.simplenotes.data.Note
import prj.simplenotes.data.db.NoteEntity

class NoteMapper {
    fun toEntity(value: Note): NoteEntity {
        return NoteEntity(
            id = value.id,
            background = value.background,
            title = value.title,
            text = value.text,
            isCompleted = value.isCompleted)
    }

    fun toNotes(value: List<NoteEntity>): List<Note> {
        return value.map {
            Note(
                id = it.id,
                background = it.background,
                title = it.title,
                text = it.text,
                isCompleted = it.isCompleted,
                )
        }
    }
}