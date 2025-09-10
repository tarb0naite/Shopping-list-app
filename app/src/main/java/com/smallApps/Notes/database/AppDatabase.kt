package com.smallApps.Notes.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.smallApps.Notes.dao.ItemDao
import com.smallApps.Notes.models.ItemEntity
import com.smallApps.Notes.models.CategoryConverters


@Database(
    entities = [ItemEntity::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(CategoryConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
}