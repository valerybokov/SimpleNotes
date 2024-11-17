package prj.simplenotes.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val DB_NAME = "notes.db"

@Database(entities = [NoteEntity::class], version = 1)
abstract class NotesDatabase: RoomDatabase() {
    abstract fun notesDao(): NotesDao

    companion object {
        private var _instance: NotesDatabase? = null
        private val _lock = Any()

        fun getInstance(context: Context): NotesDatabase {
            _instance?.let {
                return it
            }

            synchronized(_lock) {
                _instance?.let {
                    return it
                }

                val db = Room.databaseBuilder(
                    context.applicationContext,
                    NotesDatabase::class.java, DB_NAME
                ).build()

                _instance = db

                return db
            }
        }
    }
}