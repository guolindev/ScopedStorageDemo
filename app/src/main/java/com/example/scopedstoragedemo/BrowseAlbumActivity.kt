package com.example.scopedstoragedemo

import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewTreeObserver.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.scopedstoragedemo.databinding.ActivityBrowseAlbumBinding
import kotlin.concurrent.thread
import android.view.ViewTreeObserver.OnPreDrawListener as OnPreDrawListener1

class BrowseAlbumActivity : AppCompatActivity() {

    private val imageList = ArrayList<Image>()

    private val checkedImages = HashMap<String, Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBrowseAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val pickFiles = intent.getBooleanExtra("pick_files", false)
        title = if (pickFiles) "Pick Images" else "Browse Album"
        binding.recyclerView.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener1 {
            override fun onPreDraw(): Boolean {
                binding.recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                val columns = 3
                val imageSize = binding.recyclerView.width / columns
                val adapter = AlbumAdapter(this@BrowseAlbumActivity, imageList, checkedImages, imageSize, pickFiles)
                binding.recyclerView.layoutManager = GridLayoutManager(this@BrowseAlbumActivity, columns)
                binding.recyclerView.adapter = adapter
                loadImages(adapter)
                return false
            }
        })
        binding.fab.setOnClickListener {
            if (checkedImages.isEmpty()) {
                Toast.makeText(this, "You didn't choose any image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val checkedUris = ArrayList<Uri>()
            for (image in checkedImages.values) {
                checkedUris.add(image.uri)
            }
            val data = Intent()
            data.putExtra("checked_uris", checkedUris)
            setResult(RESULT_OK, data)
            finish()
        }
        if (pickFiles) {
            binding.fab.visibility = View.VISIBLE
        }
    }

    private fun loadImages(adapter: AlbumAdapter) {
        thread {
            val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                null, null, "${MediaStore.MediaColumns.DATE_ADDED} desc")
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageList.add(Image(uri, false))
                }
                cursor.close()
            }
            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        }
    }

}
