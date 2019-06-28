package me.arnoldwho.cleaner

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.Manifest.permission
import android.Manifest.permission.WRITE_CALENDAR
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.Closeable
import java.io.FileNotFoundException
import java.io.FileReader


class MainActivity : AppCompatActivity() {

    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uninstallBtn = findViewById<Button>(R.id.uninstall_btn)

        uninstallBtn.setOnClickListener {
            val list = readFile()
            list!!.forEach {
                val uri = Uri.parse("package:${it}")
                Log.d("Cleaner", uri.toString())
                startActivity(Intent(Intent.ACTION_DELETE, uri))
            }
        }
    }

    fun permissionManager() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
                val permissions: Array<String> = arrayOf("Manifest.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE")
                ActivityCompat.requestPermissions(this, permissions,
                    1)
        }
    }

    fun readFile(): ArrayList<String>? {
        val file = "/sdcard/Cleaner/list.json"
        permissionManager()
        try {
            val fr = FileReader(file)
            var bufReader: BufferedReader? = null
            try {
                bufReader = BufferedReader(fr)
                var result = ""
                var line: String? = bufReader.readLine()
                while (line != null) {
                    result += line
                    line = bufReader.readLine()
                }
                return result.toList<String>()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                closeSilently(fr)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    private fun closeSilently(closeable: Closeable) {
    try {
        closeable.close();
    } catch (e: Exception) {
    }
    }

    private inline fun <reified T> String.toList(): ArrayList<T> {
        return gson.fromJson<ArrayList<T>>(this, object: TypeToken<ArrayList<T>>() {}.type)
    }
}
