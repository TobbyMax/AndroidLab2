package com.example.androidlab2

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update

@Entity
data class ShoppingItem(
    @PrimaryKey var uid: Int,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "note") var note: String?,
    @ColumnInfo(name = "checked") var checked: Boolean = false
)

@Dao
interface ItemDao {
    @Query("SELECT * FROM shoppingitem")
    fun getAll(): List<ShoppingItem>

    @Insert
    fun insertAll(vararg users: ShoppingItem)

    @Delete
    fun delete(user: ShoppingItem)

    @Update
    fun update(user: ShoppingItem)

}

@Database(entities = [ShoppingItem::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}