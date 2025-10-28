package ph.edu.comteq.notetakingapp

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * this class represents a NOTE with all associated tags
 */
data class NoteWithTags(
    @Embedded
    val note: Note,
    @Relation(
        parentColumn = "id", //note id
        entityColumn = "id", //tag id
        associateBy = Junction(
            value = NoteTagCrossRef::class,
            parentColumn = "note_id",
            entityColumn = "tag_id"
        )
    )
    val tags: List<Tag>
)
