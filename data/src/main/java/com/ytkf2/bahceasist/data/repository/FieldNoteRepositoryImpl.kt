package com.ytkf2.bahceasist.data.repository

import com.ytkf2.bahceasist.data.local.dao.FieldNoteDao
import com.ytkf2.bahceasist.data.mapper.toDomain
import com.ytkf2.bahceasist.data.mapper.toEntity
import com.ytkf2.bahceasist.domain.model.FieldNote
import com.ytkf2.bahceasist.domain.model.NoteCategory
import com.ytkf2.bahceasist.domain.repository.FieldNoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FieldNoteRepositoryImpl @Inject constructor(
    private val noteDao: FieldNoteDao
) : FieldNoteRepository {

    private val _notesUpdateTrigger = MutableStateFlow(System.currentTimeMillis())

    override fun observeNotes(category: NoteCategory?): Flow<List<FieldNote>> {
        return _notesUpdateTrigger.map {
            withContext(Dispatchers.IO) {
                val entities = noteDao.getAll(category?.name)
                entities.map { it.toDomain() }
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getNote(id: Long): FieldNote? = withContext(Dispatchers.IO) {
        noteDao.getById(id)?.toDomain()
    }

    override suspend fun upsert(note: FieldNote): Long = withContext(Dispatchers.IO) {
        val entity = note.toEntity()
        val resultId = if (note.id == 0L) {
            noteDao.insert(entity)
        } else {
            noteDao.update(entity)
            note.id
        }
        _notesUpdateTrigger.value = System.currentTimeMillis()
        resultId
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        noteDao.delete(id)
        _notesUpdateTrigger.value = System.currentTimeMillis()
    }
}
