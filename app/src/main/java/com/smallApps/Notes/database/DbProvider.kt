package com.smallApps.Notes.database

import android.content.Context
import androidx.room.Room
import com.smallApps.Notes.repository.ItemRepository
import com.smallApps.Notes.repository.ItemRepositoryImpl

object DbProvider {

    @Volatile
    private var db: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return db ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "shopping_db"
            )

                .fallbackToDestructiveMigration()
                .build()
            db = instance
            instance
        }
    }

    fun getRepository(context: Context): ItemRepository {
        val database = getDatabase(context)
        return ItemRepositoryImpl(database.itemDao())
    }
}