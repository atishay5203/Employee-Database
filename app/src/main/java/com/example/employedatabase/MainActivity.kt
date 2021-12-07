package com.example.employedatabase

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun createImageFile(context: Context, folder:String,ext:String):File
{
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(folder)
    val file = File(storageDir, "${timeStamp}.$ext")
    file.createNewFile()
    return  file

}
fun createFile(context: Context, folder:String,ext:String):File
{
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(folder)
    val file = File(storageDir, "${timeStamp}.$ext")
    file.createNewFile()
    return  file

}
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}