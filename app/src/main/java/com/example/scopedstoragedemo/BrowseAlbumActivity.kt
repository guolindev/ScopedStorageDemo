package com.example.scopedstoragedemo

import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.scopedstoragedemo.AlbumAdapter
import com.example.scopedstoragedemo.R
import kotlinx.android.synthetic.main.activity_browse_album.*
import kotlin.concurrent.thread

class BrowseAlbumActivity : AppCompatActivity() {

    val imageList = ArrayList<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_album)
        recyclerView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                val columns = 3
                val imageSize = recyclerView.width / columns
                val adapter = AlbumAdapter(this@BrowseAlbumActivity, imageList, imageSize)
                recyclerView.layoutManager = GridLayoutManager(this@BrowseAlbumActivity, columns)
                recyclerView.adapter = adapter
                loadImages(adapter)
                return false
            }
        })
    }

    private fun loadImages(adapter: AlbumAdapter) {
        thread {
            val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, "${MediaStore.MediaColumns.DATE_ADDED} desc")
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageList.add(uri)
                }
                cursor.close()
            }
            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        }
    }

}
