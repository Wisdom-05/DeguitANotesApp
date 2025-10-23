package ph.edu.comteq.notetakingapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao = AppDatabase.getDatabase(application).noteDao()
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

    fun insert(note: Note) = viewModelScope.launch {
        noteDao.insertNote(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        noteDao.updateNote(note)
    }

    fun delete(note: Note) = viewModelScope.launch {
        noteDao.deleteNote(note)
    }
}
