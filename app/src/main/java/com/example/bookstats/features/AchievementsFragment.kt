package com.example.bookstats.features

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.bookstats.app.di.AppComponent.Companion.appComponent
import com.example.bookstats.databinding.FragmentAchievementsBinding
import com.example.bookstats.repository.BookWithSessions
import com.example.bookstats.repository.Repository
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class AchievementsFragment : Fragment() {

    //EVERYTHING RELATED TO EXPORT AND IMPORT IS TEMPORARY IN THIS FRAGMENT

    private var _binding: FragmentAchievementsBinding? = null
    private val binding get() = _binding!!

    val gson = GsonBuilder().setPrettyPrinting().create()
    val dataFile = File(FILE_PATH, FILE_NAME)

    @Inject
    lateinit var repository: Repository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appComponent.inject(this)
        _binding = FragmentAchievementsBinding.inflate(inflater, container, false)

        binding.exportButton.setOnClickListener {
            if (isPermissionsGranted(WRITE_EXTERNAL_STORAGE)) {
                CoroutineScope(Dispatchers.IO).launch {
                    export()
                }
            } else {
                grantPermissions(WRITE_EXTERNAL_STORAGE)
            }
        }

        binding.importButton.setOnClickListener {
            if (isPermissionsGranted(READ_EXTERNAL_STORAGE)) {
                CoroutineScope(Dispatchers.IO).launch {
                    import()
                }
            } else {
                grantPermissions(READ_EXTERNAL_STORAGE)
            }
        }

        binding.deleteAllBooks.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                repository.deleteAllBooks()
            }
        }

        return binding.root
    }

    private suspend fun import() {
        val booksWithSessions = object : TypeToken<List<BookWithSessions>>() {}.type
        val data: String = dataFile.inputStream().bufferedReader().use { it.readText() }
        val bookList = gson.fromJson<List<BookWithSessions>>(data, booksWithSessions)
        bookList.forEach { book ->
            repository.addBookWithSessions(book)
        }
    }

    private suspend fun export() {
        val data = gson.toJson(repository.getBooksWithSessions())
        dataFile.writeText(data)
    }

    private fun grantPermissions(permission: String) {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:" + requireActivity().applicationContext.packageName)
            startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    permission
                ),
                100
            )
        }
    }

    private fun isPermissionsGranted(permission: String): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(requireContext(), permission)
            result == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val FILE_PATH =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        private const val FILE_NAME = "Books.ReadTracker"
    }
}