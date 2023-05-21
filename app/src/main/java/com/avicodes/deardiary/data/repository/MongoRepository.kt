package com.avicodes.deardiary.data.repository

import com.avicodes.deardiary.model.Diary
import com.avicodes.deardiary.utils.RequestState
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

typealias Diaries = RequestState<Map<LocalDate,List<Diary>>>

interface MongoRepository {
    fun configureTheRealm()
    fun getAllDiaries(): Flow<Diaries>
}