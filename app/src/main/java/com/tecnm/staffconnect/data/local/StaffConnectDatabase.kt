package com.tecnm.staffconnect.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [VacacionEntity::class, NominaEntity::class],
    version = 5,
    exportSchema = false
)
abstract class StaffConnectDatabase : RoomDatabase() {
    abstract fun vacacionDao(): VacacionDao
    abstract fun nominaDao(): NominaDao
}