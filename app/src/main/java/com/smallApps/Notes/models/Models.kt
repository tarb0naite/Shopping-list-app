package com.smallApps.Notes.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class Category {
    Daržovės,
    Pieno_produktai,
    Mėsos_produktai,
    Grudiniai_produktai,
    Kuno_produktai,
    Plaukų_produktai,
    Veido_produktai,
    Vitaminai,
    Dideli_pirkiniai
}

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "category")
    val category: Category,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "checked")
    val checked: Boolean = false
)

class CategoryConverters  {
    @TypeConverter
    fun fromCategory(c: Category): String = c.name

    @TypeConverter
    fun toCategory(value: String): Category = Category.valueOf(value)
}