package com.avicodes.mongo.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.avicodes.mongo.database.dao.ImageToDeleteDao
import com.avicodes.mongo.database.dao.ImageToUploadDao
import com.avicodes.mongo.database.entitiy.ImageToDelete
import com.avicodes.mongo.database.entitiy.ImageToUpload

@Database(
    entities = [ImageToUpload::class, ImageToDelete::class],
    version = 2,
    exportSchema = false
)
abstract class ImagesDatabase: RoomDatabase() {
    abstract fun imageToUploadDao(): ImageToUploadDao
    abstract fun imageToDeleteDao(): ImageToDeleteDao
}