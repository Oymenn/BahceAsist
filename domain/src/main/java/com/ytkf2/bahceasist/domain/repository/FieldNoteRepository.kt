package com.ytkf2.bahceasist.domain.repository

import com.ytkf2.bahceasist.domain.model.FieldNote
import com.ytkf2.bahceasist.domain.model.NoteCategory
import kotlinx.coroutines.flow.Flow

interface FieldNoteRepository {
    fun observeNotes(category: NoteCategory? = null): Flow<List<FieldNote>>
    suspend fun getNote(id: Long): FieldNote?
    suspend fun upsert(note: FieldNote): Long
    suspend fun delete(id: Long)
}