package com.example.scopedstoragedemo

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class AlbumAdapter(val context: Context, val imageList: List<Uri>, val imageSize: Int) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.album_image_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = imageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.layoutParams.width = imageSize
        holder.imageView.layoutParams.height = imageSize
        val uri = imageList[position]
        val options = RequestOptions().placeholder(R.drawable.album_loading_bg).override(imageSize, imageSize)
        Glide.with(context).load(uri).apply(options).into(holder.imageView)
    }

}