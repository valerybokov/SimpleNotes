package prj.simplenotes.data.db

import androidx.annotation.ColorInt
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo @ColorInt val background: Int,
    @ColumnInfo val title: String,
    @ColumnInfo val text: String,
    @ColumnInfo val isCompleted: Boolean
)