package com.example.employedatabase

import android.content.Context
import androidx.lifecycle.LiveData

class EmployeeListRepo(context: Context) {
    val database:EmployeeDatabase= EmployeeDatabase.getDatabase(context)
    val listDao = database.employeeListDao()
    fun getEmployees():LiveData<List<Employee>>
    {
        return listDao.getEmployees()
    }
    suspend fun InsertEmployee(employees:List<Employee>)
    {
        listDao.InsertEmployees(employees)
    }
    fun getEmployeesList():List<Employee>
    {
        return listDao.getEmployeesList()
    }
}