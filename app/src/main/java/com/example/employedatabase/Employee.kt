package com.example.employedatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
enum class roles
{
   CEO , President , Manager , Assistant , SDE
}
enum class Gender
{
    Male, Female, Others
}

@Entity(tableName="Employees")
data class Employee(@PrimaryKey(autoGenerate = true)val id:Long,

                    val name:String,
                    val age:Int,
                    val gender:Int,
                    val role:Int,
                    val photo:String)
