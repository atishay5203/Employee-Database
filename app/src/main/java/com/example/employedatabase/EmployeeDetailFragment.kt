package com.example.employedatabase

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_employee_detail.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

const val PERMISSION_REQUEST_CAMERA = 0
const val REQUEST_IMAGE_CAPTURE = 1
const val GALLERY_PHOTO_REQUEST = 2
var selectedPhotoPath = ""

class EmployeeDetailFragment : Fragment() {
    private lateinit var viewModel: EmployeeDetailViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this).get(EmployeeDetailViewModel::class.java)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val rolevals = mutableListOf<String>()
        roles.values().forEach {
            rolevals.add(it.name)
        }
        val agevals = mutableListOf<String>()
        for (i in 25..60)
            agevals.add(i.toString())
        val roleAdapter = ArrayAdapter(this.requireActivity(), R.layout.support_simple_spinner_dropdown_item, rolevals)

        val ageAdapter = ArrayAdapter(this.requireActivity(), R.layout.support_simple_spinner_dropdown_item, agevals)
        employee_role.adapter = roleAdapter
        employee_age.adapter = ageAdapter
        val id = EmployeeDetailFragmentArgs.fromBundle(requireArguments()).id
        viewModel.setEmployeeId(id)
        if (id != 0L) {
            disableFields()
            viewModel.employee.observe(viewLifecycleOwner,
                    {
                        loadData(it)
                    })

        } else {
            enableFields()
            employee_image.setImageResource(R.drawable.blank)
            employee_image.tag = ""
        }

        appBarLayout1.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {

                R.id.edit_transaction -> {
                    // if the user wants to edit a transaction
                    enableFields()

                    save_employee.setOnClickListener {
                        saveData()

                    }
                    delete_employee.setOnClickListener {
                        viewModel.employee.observe(viewLifecycleOwner,
                                {
                                    viewModel.deleteEmployee(it)
                                    requireActivity().onBackPressed()
                                })
                    }
                    true
                }
                R.id.sharedetails ->
                {
                    shareData()
                    true
                }
                else ->
                    false
            }

        }
        save_employee.setOnClickListener {
            saveData()

        }
        delete_employee.setOnClickListener {
            viewModel.employee.observe(viewLifecycleOwner,
                    {
                        viewModel.deleteEmployee(it)

                    })
            requireActivity().onBackPressed()
        }

        cam_photo.setOnClickListener {
            clickPhotoAfterPermission(it)
        }
        gallery_photo.setOnClickListener {
            pickImage()
        }
    }

    private fun shareData() {
        val name = Employee_Name.editText?.text.toString()
        val role = employee_role.selectedItem.toString()
        val age = employee_age.selectedItemPosition+25
        val gender = if (male_select.isChecked) {
            "Male"
        } else if (female_select.isChecked) {
            "Female"
        } else {
            "Others"
        }
        val details =getString(R.string.employee_detail,name,role,age,gender)
        val intent =Intent().apply {
            action= Intent.ACTION_SEND
            this.putExtra(Intent.EXTRA_TEXT,details)
            type="text/plain"
        }
        val shareintent= Intent.createChooser(intent,"Choose the app to share details")
        startActivity(shareintent)

    }


    private fun clickPhotoAfterPermission(view: View) {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhoto()
        } else {
            requestForPermission(view)
        }
    }

    private fun requestForPermission(view: View) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
            val msg = Snackbar.make(view, "We need your Permission to capture your Image. Please Provie the consent,once asked", Snackbar.LENGTH_INDEFINITE)
            msg.setAction("Ok"
            ) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
            }
            msg.show()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto()
            } else {
                Toast.makeText(requireContext(), "Permission not granted for the desired activity", Toast.LENGTH_SHORT)
                        .show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager
            )?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile(requireActivity(), Environment.DIRECTORY_PICTURES, "jpg")
                } catch (ex: IOException) {
                    Toast.makeText(requireContext(), "Error Occured ${ex}", Toast.LENGTH_SHORT).show()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    selectedPhotoPath = it.absolutePath
                    val photoURI: Uri = FileProvider.getUriForFile(
                            requireContext(),
                            "${BuildConfig.APPLICATION_ID}.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val uri = Uri.fromFile(File(selectedPhotoPath))
                    employee_image.setImageURI(uri)
                    employee_image.tag = uri.toString()


                }
                GALLERY_PHOTO_REQUEST -> {
                    data?.data?.also { uri ->
                        val imageFile: File? =
                                try {
                                    createImageFile(requireActivity(), Environment.DIRECTORY_PICTURES, "jpg")
                                } catch (ex: IOException) {
                                    Toast.makeText(requireContext(), "Error Occured ${ex}", Toast.LENGTH_SHORT).show()
                                    null
                                }
                        imageFile?.also {

                            val resolver = requireActivity().applicationContext.contentResolver
                            resolver.openInputStream(uri).use { inputStream ->
                                val output = FileOutputStream(imageFile)
                                inputStream!!.copyTo(output)

                            }
                            val uri = Uri.fromFile(imageFile)
                            employee_image.setImageURI(uri)
                            employee_image.tag = uri.toString()
                        }
                    }


                }
            }
        }

    }

    private fun saveData() {
        val name = Employee_Name.editText?.text.toString()
        val role = employee_role.selectedItemPosition
        val age = employee_age.selectedItemPosition
        val gender = if (male_select.isChecked) {
            0
        } else if (female_select.isChecked) {
            1
        } else {
            2
        }
        val photo = employee_image.tag.toString()
        val employee = Employee(viewModel._employeeId.value!!, name, age, gender, role, photo)
        viewModel.saveEmployee(employee)
        requireActivity().onBackPressed()

    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.resolveActivity(requireActivity().packageManager)?.also {
            startActivityForResult(intent, GALLERY_PHOTO_REQUEST)
        }
    }

    private fun enableFields() {
        save_employee.isEnabled = true
        delete_employee.isEnabled = true
        employee_age.isEnabled = true
        employee_role.isEnabled = true
        male_select.isEnabled = true
        female_select.isEnabled = true
        others_select.isEnabled = true
        employee_image.isClickable = true
        Employee_Name.isEnabled = true
    }

    private fun disableFields() {
        save_employee.isEnabled = false
        delete_employee.isEnabled = false
        employee_age.isEnabled = false
        employee_role.isEnabled = false
        male_select.isEnabled = false
        female_select.isEnabled = false
        others_select.isEnabled = false
        employee_image.isClickable = false
        Employee_Name.isEnabled = false

    }

    private fun loadData(employee: Employee) {
        with(employee)
        {
            with(this.photo)
            {


                if (isEmpty()) {
                    employee_image.setImageResource(R.drawable.blank)
                    employee_image.tag = ""
                } else {
                    employee_image.setImageURI(Uri.parse(this))

                }
            }
            Employee_Name.editText?.setText(this.name)
            employee_role.setSelection(this.role)
            employee_age.setSelection(this.age)
            if (this.gender == 0)
                male_select.isChecked = true
            else if (this.gender == 1)
                female_select.isChecked = true
            else
                others_select.isChecked = true
        }
    }
}

