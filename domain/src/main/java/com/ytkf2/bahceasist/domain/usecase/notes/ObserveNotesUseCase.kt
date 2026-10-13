package com.ytkf2.bahceasist.domain.usecase.notes

import com.ytkf2.bahceasist.domain.model.FieldNote
import com.ytkf2.bahceasist.domain.model.NoteCategory
import com.ytkf2.bahceasist.domain.repository.FieldNoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNotesUseCase @Inject constructor(
    private val repository: FieldNoteRepository
) {
    operator fun invoke(category: NoteCategory? = null): Flow<List<FieldNote>> =
        repository.observeNotes(category)
}