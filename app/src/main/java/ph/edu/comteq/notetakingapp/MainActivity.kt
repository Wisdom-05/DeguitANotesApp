package ph.edu.comteq.notetakingapp

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import ph.edu.comteq.notetakingapp.ui.theme.NoteTakingAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteTakingAppTheme {
                var searchQuery by remember { mutableStateOf("") }
                var isSearchActive by remember { mutableStateOf(false) }
                val notes by viewModel.allNotes.collectAsState(initial = emptyList())

                // simple in-file navigation between list and editor
                var editorNoteId by remember { mutableStateOf<Int?>(null) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if(editorNoteId != null){
                            TopAppBar(
                                title = { Text(if (editorNoteId == 0) "Add Note" else "Edit Note") },
                                navigationIcon = {
                                    IconButton(onClick = { editorNoteId = null }) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            )
                        }
                        else if(isSearchActive){
                            //search mode
                            SearchBar(
                                modifier = Modifier.fillMaxWidth(),
                                inputField = {
                                    SearchBarDefaults.InputField(
                                        query = searchQuery,
                                        onQueryChange = {
                                            searchQuery = it
                                            viewModel.updateSearchQuery(it)
                                        },
                                        onSearch = {},
                                        expanded = true,
                                        onExpandedChange = {
                                            if (!it) {
                                                isSearchActive = false
                                                searchQuery = ""
                                                viewModel.clearSearch()
                                            }
                                        },
                                        placeholder = {Text("Search notes...")},
                                        leadingIcon = {
                                            IconButton(onClick = {
                                                isSearchActive = false
                                                searchQuery = ""
                                                viewModel.clearSearch()
                                            }) {
                                                Icon(
                                                    Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = "Close search"
                                                )
                                            }
                                        },
                                        trailingIcon = {
                                            if (searchQuery.isNotEmpty()) {
                                                IconButton(onClick = {
                                                    searchQuery = ""
                                                    viewModel.clearSearch()
                                                }) {
                                                    Icon(
                                                        Icons.Default.Clear,
                                                        contentDescription = "Clear search"
                                                    )
                                                }
                                            }
                                        }
                                    )
                                },
                                expanded = true,
                                onExpandedChange = {
                                    if (!it) {
                                        isSearchActive = false
                                        searchQuery = ""
                                        viewModel.clearSearch()
                                    }
                                },
                                content = {
                                    //content shown inside the search view(results)
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = PaddingValues(16.dp)
                                    ) {
                                        if (notes.isEmpty()) {
                                            item {
                                                Text(
                                                    text = "No notes found",
                                                    modifier = Modifier.padding(16.dp),
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                                        alpha = 0.6f
                                                    )
                                                )
                                            }
                                        } else {
                                            items(notes) { note ->
                                                NoteCard(note = note)
                                            }
                                        }
                                    }
                                }
                            )
                        }
                        else {
                            TopAppBar(
                                title = { Text("Notes") },
                                actions = {
                                    IconButton(onClick = { isSearchActive = true }) {
                                        Icon(Icons.Filled.Search, contentDescription = "Search")
                                    }
                                }
                            )
                        }
                    },
                    floatingActionButton = {
                        if(editorNoteId == null){
                            FloatingActionButton(onClick = { editorNoteId = 0 }) {
                                Icon(Icons.Filled.Add, contentDescription = "Add Note")
                            }
                        }
                    }
                ) { innerPadding ->
                    if(editorNoteId != null){
                        EditNoteScreen(
                            viewModel = viewModel,
                            noteId = editorNoteId,
                            onDone = { editorNoteId = null },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        NotesListScreen(
                            viewModel = viewModel,
                            onNoteClick = { noteId -> editorNoteId = noteId },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotesListScreen(viewModel: NoteViewModel, onNoteClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    val notesWithTags by viewModel.allNotesWithTags.collectAsState(initial = emptyList())

    LazyColumn(modifier = modifier) {
        items(notesWithTags) { note ->
            NoteCard(note = note.note, tags = note.tags, onClick = { onNoteClick(note.note.id) })
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteCard(
    note: Note,
    tags: List<Tag> = emptyList(),
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
){
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = DateUtils.formatDateTime(note.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = note.category
            )
            Text(
                text = note.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if(tags.isNotEmpty()){
                FlowRow(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    tags.forEach {
                        Text(
                            text = it.name
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditNoteScreen(
    viewModel: NoteViewModel,
    noteId: Int?,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
){
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var originalCreatedAt by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(noteId) {
        if(noteId != null && noteId != 0){
            val existing = viewModel.getNoteById(noteId)
            if(existing != null){
                title = existing.title
                content = existing.content
                category = existing.category
                originalCreatedAt = existing.createdAt
            }
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 6
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = {
                if(title.isNotBlank() || content.isNotBlank()){
                    val now = System.currentTimeMillis()
                    if(noteId != null && noteId != 0){
                        viewModel.update(
                            Note(
                                id = noteId,
                                title = title,
                                content = content,
                                category = category,
                                createdAt = originalCreatedAt ?: now,
                                updatedAt = now
                            )
                        )
                    } else {
                        viewModel.insert(
                            Note(
                                title = title,
                                content = content,
                                category = category
                            )
                        )
                    }
                    onDone()
                } else {
                    onDone()
                }
            }) {
                Text("Save")
            }
            Spacer(modifier = Modifier.width(8.dp))
            if(noteId != null && noteId != 0){
                TextButton(onClick = {
                    viewModel.delete(
                        Note(
                            id = noteId,
                            title = title,
                            content = content,
                            category = category
                        )
                    )
                    onDone()
                }) {
                    Text("Delete")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewNotesListScreen() {
    NoteTakingAppTheme {
        NotesListScreen(viewModel = NoteViewModel(Application()), onNoteClick = {})
    }
}