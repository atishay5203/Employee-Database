package com.example.employedatabase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class EmployeeListViewModel(context: Application):AndroidViewModel(context) {
    val repo:EmployeeListRepo= EmployeeListRepo(context)
    val EmployeeList:LiveData<List<Employee>> = repo.getEmployees()
        suspend fun insertEmployees(employees:List<Employee>)
        {
            repo.InsertEmployee(employees)
        }
    fun employeelist():List<Employee>
    {
        return  repo.getEmployeesList()
    }

}