package com.fritte.myhelsinki.ui.map.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fritte.myhelsinki.R
import com.fritte.myhelsinki.model.event.EventImage

class ImageAdapter(private val context: Context, private var images: List<EventImage>?) : RecyclerView.Adapter<ImageAdapter.ImageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(LayoutInflater.from(context).inflate(R.layout.item_image, parent, false))
    }

    override fun getItemCount(): Int {
        return images!!.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(images?.get(position))
    }

    class ImageHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var ivImage: ImageView? = null

        init {
            ivImage = view.findViewById(R.id.ivImage)
        }

        fun bind(event: EventImage?) {
            Glide.with(ivImage!!.context)
                .load(event!!.url)
                .placeholder(android.R.drawable.stat_notify_error)
                .dontAnimate()
                .into(ivImage!!)
        }
    }
}