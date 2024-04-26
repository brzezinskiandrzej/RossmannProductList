package com.andrzejbrzezinski.rossmannproductlist.filmpackage.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andrzejbrzezinski.rossmannproductlist.FilmsDetailsWithThumbnail
import com.andrzejbrzezinski.rossmannproductlist.R
import com.andrzejbrzezinski.rossmannproductlist.databinding.ItemMyVideoBinding

class MyVideosAdapter(private val listener: OnFilmInteractionListener) :
    androidx.recyclerview.widget.ListAdapter<FilmsDetailsWithThumbnail,MyVideosAdapter.ViewHolder>(
        DIFF_CALLBACK) {
    interface OnFilmInteractionListener {
        fun onDeleteFilm(film: FilmsDetailsWithThumbnail)
    }
    class ViewHolder(private val binding: ItemMyVideoBinding) :
        RecyclerView.ViewHolder(binding.root)  {
        fun bind(videoUrl: FilmsDetailsWithThumbnail?){
            binding.run {
                binding.vImage.setImageBitmap(videoUrl?.thumbnail)
                if (videoUrl != null) {
                    binding.videoTitle.text=videoUrl.name
                }

            }


//            binding.deleteVideo.setOnClickListener{
//                val position=adapterPosition
//                if(position!= RecyclerView.NO_POSITION)
//                {
//
//                }
//            }
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVideosAdapter.ViewHolder {
        return MyVideosAdapter.ViewHolder(
            ItemMyVideoBinding.inflate(LayoutInflater.from(parent.context),parent,false),
        )
    }



    override fun onBindViewHolder(holder: MyVideosAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
        val film = getItem(position)
        holder.itemView.tag = film
        holder.itemView.setOnLongClickListener {view->
            val popup = PopupMenu(view.context, view)
            popup.inflate(R.menu.options_menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete -> {
                        val clickedFilm = view.tag as FilmsDetailsWithThumbnail
                        listener.onDeleteFilm(clickedFilm)
                        true
                    }
                    else -> false
                }
            }

            popup.show()
            true
        }
    }
    fun updateList(newVideoList: List<FilmsDetailsWithThumbnail>) {
        submitList(newVideoList)
    }
}
private val DIFF_CALLBACK: DiffUtil.ItemCallback<FilmsDetailsWithThumbnail> = object : DiffUtil.ItemCallback<FilmsDetailsWithThumbnail>() {
    override fun areItemsTheSame(oldItem: FilmsDetailsWithThumbnail, newItem: FilmsDetailsWithThumbnail): Boolean = true

    override fun areContentsTheSame(oldItem: FilmsDetailsWithThumbnail, newItem: FilmsDetailsWithThumbnail): Boolean =
        oldItem == newItem
}