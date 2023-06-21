package com.avicodes.deardiary.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.avicodes.deardiary.data.database.dao.ImageToDeleteDao
import com.avicodes.deardiary.data.database.dao.ImageToUploadDao
import com.avicodes.deardiary.data.database.entitiy.ImageToDelete
import com.avicodes.deardiary.data.database.entitiy.ImageToUpload

@Database(
    entities = [ImageToUpload::class, ImageToDelete::class],
    version = 2,
    exportSchema = false
)
abstract class ImagesDatabase: RoomDatabase() {
    abstract fun imageToUploadDao(): ImageToUploadDao
    abstract fun imageToDeleteDao(): ImageToDeleteDao
}