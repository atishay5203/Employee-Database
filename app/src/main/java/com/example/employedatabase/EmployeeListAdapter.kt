package com.example.employedatabase

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_employee_detail.*
import kotlinx.android.synthetic.main.listsample.*

class EmployeeListAdapter
    (private val listner: (Long)->Unit) :
    ListAdapter<Employee, EmployeeListAdapter.EmployeeViewHolder>(diffUtilCallBack2())
    {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.listsample, parent, false)
            return EmployeeViewHolder(view)
        }

        override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
            holder.bindView(getItem(position))
        }


        inner class EmployeeViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView),
            LayoutContainer {
            init {
                itemView.setOnClickListener {
                    listner.invoke(getItem(adapterPosition).id)
                }
            }



            fun bindView(employee: Employee) {
                name.text= employee.name
                role.text= roles.values().get(employee.role).toString()
                """${employee.age+25} Years""".also { age.text = it }
                gender.text= Gender.values().get(employee.gender).toString()
                with(employee.photo)
                {


                    if(isEmpty())
                    {
                       photo.setImageResource(R.drawable.blank)


                    }
                    else
                    {
                        photo.setImageURI(Uri.parse(this))


                    }
                }

            }

        }



        class diffUtilCallBack2 : DiffUtil.ItemCallback<Employee>() {
            override fun areItemsTheSame(
                oldItem: Employee,
                newItem: Employee
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Employee,
                newItem:Employee
            ): Boolean {
                return oldItem == newItem
            }

        }

}