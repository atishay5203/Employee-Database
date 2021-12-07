package com.example.employedatabase

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_employee_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

const val READ_FILE_REQ = 3


class EmployeeListFragment : Fragment() {
    private lateinit var viewModel: EmployeeListViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EmployeeListViewModel::class.java)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.simple, menu)
    }

    private fun importEmployees() {
        Intent(Intent.ACTION_GET_CONTENT).also { readFileIntent ->
            readFileIntent.addCategory(Intent.CATEGORY_OPENABLE)
            readFileIntent.type = "text/*"
            readFileIntent.resolveActivity(requireActivity().packageManager).also {
                startActivityForResult(readFileIntent, READ_FILE_REQ)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appBarLayout2.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.import_data -> {
                    Toast.makeText(requireContext(), "Button Clickes", Toast.LENGTH_LONG).show()
                    importEmployees()
                    true
                }
                R.id.export_data -> {
                    GlobalScope.launch {
                        exportData()
                    }

                    true
                }
                else ->
                    false


            }
        }

        with(emplist)
        {
            layoutManager = LinearLayoutManager(activity)
            adapter = EmployeeListAdapter {
                findNavController().navigate(EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeDetailFragment(it))
            }
            viewModel.EmployeeList.observe(viewLifecycleOwner,
                    {
                        (emplist.adapter as EmployeeListAdapter).submitList(it)
                    })

        }
        addemp.setOnClickListener {
            findNavController().navigate(EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeDetailFragment(0L))

        }
    }

    private suspend fun exportData() {
        var file: File? = null
        withContext(Dispatchers.IO)
        {
            file = try {
                createFile(requireContext(), "Documents", "csv")
            } catch (io: IOException) {
                Toast.makeText(requireContext(), "Exception $io occured", Toast.LENGTH_LONG).show()
                null
            }
            file?.printWriter()?.use {
                val exportlist= viewModel.employeelist()
                with(exportlist)
                {
                    if (!this.isEmpty()) {
                        this.forEach { emp ->
                            it.print(emp.name + "," + roles.values().get(emp.role) + "," + (emp.age + 25) + "," + Gender.values().get(emp.gender))

                        }
                    }
                }

            }
            withContext(Dispatchers.Main)
            {
                file?.let {
                    val uri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".fileprovider", it)
                    launchFile(uri, "csv")
                }
            }

        }
    }

    private fun launchFile(uri: Uri, ext: String) {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri,mimeType)
        if(intent.resolveActivity(requireActivity().packageManager)!=null)
        {
            startActivity(intent)
        }
        else
        {
            Toast.makeText(requireContext(),"No such App to show data",Toast.LENGTH_LONG).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == READ_FILE_REQ) {
                GlobalScope.launch {
                    data?.data?.also { uri ->
                        readDataFromURI(uri)

                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private suspend fun readDataFromURI(uri: Uri) {
        try {

            requireActivity().applicationContext.contentResolver.openFileDescriptor(uri, "r")?.use {

                withContext(Dispatchers.IO)
                {
                    try {
                        FileInputStream(it.fileDescriptor).use {
                            parseCSVFile(it)
                        }
                    } catch (io: IOException) {
                        Toast.makeText(requireContext(), "Exception :$io Occurred", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        } catch (fs: FileNotFoundException) {
            Toast.makeText(requireContext(), "File Not Found $fs", Toast.LENGTH_SHORT).show()
        }

    }

    private suspend fun parseCSVFile(stream: FileInputStream) {
        val employees = mutableListOf<Employee>()
        BufferedReader(InputStreamReader(stream)).forEachLine {
            val values = it.split(",")
            val employee = Employee(0, values[0], values[1].toInt(), values[2].toInt(), values[3].toInt(), "")
            employees.add(employee)
        }
        if (employees.isNotEmpty())
            viewModel.insertEmployees(employees)

    }

}