package com.ytkf2.bahceasist.domain.usecase.notes

import com.ytkf2.bahceasist.domain.model.FieldNote
import com.ytkf2.bahceasist.domain.repository.FieldNoteRepository
import com.ytkf2.bahceasist.domain.repository.LocationRepository
import com.ytkf2.bahceasist.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(
    private val notesRepository: FieldNoteRepository,
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(note: FieldNote): Long {
        // Yeni bir notsa ve henüz sıcaklık atanmadıysa, o anki hava durumunu otomatik ekle
        val enrichedNote = if (note.id == 0L && note.attachedTempC == null) {
            val location = locationRepository.selectedLocation.first()
            val snapshot = location?.let { weatherRepository.observeWeather(it).first() }
            note.copy(
                attachedTempC = snapshot?.current?.temperatureC,
                attachedCondition = snapshot?.current?.condition
            )
        } else {
            note
        }
        return notesRepository.upsert(enrichedNote)
    }
}