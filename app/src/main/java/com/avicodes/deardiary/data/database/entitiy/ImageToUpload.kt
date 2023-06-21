package com.avicodes.deardiary.data.database.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.avicodes.deardiary.utils.Constants.IMAGE_TO_UPLOAD_TABLE

@Entity(tableName = IMAGE_TO_UPLOAD_TABLE)
data class ImageToUpload(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val remoteImagePath: String,
    val imageUri: String,
    val sessionUri: String
)