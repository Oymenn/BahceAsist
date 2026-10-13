package com.ytkf2.bahceasist.data.mapper

import com.ytkf2.bahceasist.data.local.entity.FieldNoteEntity
import com.ytkf2.bahceasist.domain.model.FieldNote
import com.ytkf2.bahceasist.domain.model.NoteCategory
import com.ytkf2.bahceasist.domain.model.WeatherCondition

/**
 * Entity <-> Domain Dönüştürücüsü (FieldNote)
 */
fun FieldNoteEntity.toDomain(): FieldNote {
    val cat = try {
        NoteCategory.valueOf(category)
    } catch (_: Exception) {
        NoteCategory.OTHER
    }

    val cond = attachedWeatherCondition?.let {
        try {
            WeatherCondition.valueOf(it)
        } catch (_: Exception) {
            null
        }
    }

    return FieldNote(
        id = id,
        title = title,
        content = content,
        category = cat,
        timestampMillis = timestamp,
        imagePath = imagePath,
        attachedTempC = attachedTemp,
        attachedCondition = cond
    )
}

fun FieldNote.toEntity(): FieldNoteEntity {
    return FieldNoteEntity(
        id = id,
        title = title,
        content = content,
        category = category.name,
        timestamp = timestampMillis,
        imagePath = imagePath,
        attachedTemp = attachedTempC,
        attachedWeatherCondition = attachedCondition?.name
    )
}
