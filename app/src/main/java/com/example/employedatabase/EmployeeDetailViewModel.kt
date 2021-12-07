package com.example.employedatabase

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class EmployeeDetailViewModel(context: Application): AndroidViewModel(context) {
    val repo: EmployeeDetailRepo = EmployeeDetailRepo(context)
    val _employeeId= MutableLiveData<Long>(0L)
    private val employeeId :LiveData<Long>
    get() = _employeeId
   val employee:LiveData<Employee> = Transformations.switchMap(_employeeId)
    {
        id ->
        repo.getEmployeeById(id)
    }
     fun saveEmployee(employee: Employee)
    {
        viewModelScope.launch {
            if(_employeeId.value==0L)
            {
                _employeeId.value= repo.insertEmployee(employee)
            }
            else
            {
                repo.updateEmployee(employee)
            }
        }
    }
    fun setEmployeeId(id:Long)
    {
        if(_employeeId.value!=id)
        {
            _employeeId.value=id
        }
    }
  fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            repo.deleteEmployee(employee)
        }
    }

    
}