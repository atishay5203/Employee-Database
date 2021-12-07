package com.example.employedatabase

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EmployeeDetailDao {
    @Query("SELECT * FROM Employees WHERE `id`=:id")
    fun getEmployeeById(id:Long): LiveData<Employee>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployee(employee: Employee):Long

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)
}