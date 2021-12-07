package com.example.employedatabase

import android.content.Context
import androidx.lifecycle.LiveData

class EmployeeDetailRepo(context: Context) {
    val detailDao:EmployeeDetailDao= EmployeeDatabase.getDatabase(context).employeeDetailDao()
   fun getEmployeeById(id:Long):LiveData<Employee>
   {
       return  detailDao.getEmployeeById(id)
   }
    suspend fun insertEmployee(employee: Employee) :Long
   {
       return detailDao.insertEmployee(employee)
   }
    suspend fun updateEmployee(employee: Employee)
    {
        detailDao.updateEmployee(employee)
    }
    suspend fun deleteEmployee(employee: Employee)
    {
        detailDao.deleteEmployee(employee)
    }
}