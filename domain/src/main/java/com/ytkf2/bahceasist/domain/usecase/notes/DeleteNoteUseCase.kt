package com.ytkf2.bahceasist.domain.usecase.notes

import com.ytkf2.bahceasist.domain.repository.FieldNoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val repository: FieldNoteRepository
) {
    suspend operator fun invoke(id: Long) = repository.delete(id)
}