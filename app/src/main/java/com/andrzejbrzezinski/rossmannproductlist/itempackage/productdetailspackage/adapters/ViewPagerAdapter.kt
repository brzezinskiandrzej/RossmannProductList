package com.andrzejbrzezinski.rossmannproductlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityItemBinding
import com.andrzejbrzezinski.rossmannproductlist.databinding.ItemImageBinding
import com.bumptech.glide.Glide

class ViewPagerAdapter(var images: List<String>): RecyclerView.Adapter<ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {

        return ImageViewHolder(ItemImageBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

}

class ImageViewHolder(private val binding: ItemImageBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(imageUrl: String)
    {
        Glide.with(itemView.context).load(imageUrl).into(binding.imageView)
    }

}