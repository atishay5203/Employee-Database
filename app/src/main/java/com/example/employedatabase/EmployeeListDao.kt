package com.example.employedatabase

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EmployeeListDao {
    @Query("SELECT * FROM Employees ORDER BY role ASC")
    fun getEmployees(): LiveData<List<Employee>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun InsertEmployees(employees:List<Employee>)

    @Query("SELECT * FROM Employees ORDER BY role ASC")
    fun getEmployeesList():List<Employee>

}

