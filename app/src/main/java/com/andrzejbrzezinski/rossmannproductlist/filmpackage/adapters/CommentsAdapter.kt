package com.andrzejbrzezinski.rossmannproductlist.filmpackage.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrzejbrzezinski.rossmannproductlist.CombinedComments
import com.andrzejbrzezinski.rossmannproductlist.CombinedData
import com.andrzejbrzezinski.rossmannproductlist.CommentDetails
import com.andrzejbrzezinski.rossmannproductlist.FilmsDetails
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityFilmsBinding
import com.andrzejbrzezinski.rossmannproductlist.databinding.ItemCommentsBinding
import com.andrzejbrzezinski.rossmannproductlist.databinding.ItemVideoBinding

import com.andrzejbrzezinski.rossmannproductlist.itempackage.productdetailspackage.activities.ItemActivity
import com.bumptech.glide.Glide
import org.w3c.dom.Comment


class CommentsAdapter(var commentsList: List<CombinedComments>? = emptyList()): RecyclerView.Adapter<CommentsAdapter.ViewHolder>(){
    class ViewHolder(private val binding:ItemCommentsBinding): RecyclerView.ViewHolder(binding.root){


        fun bind(commentData: CombinedComments){
            //nametxt.text = pdData.name

            //Picasso.get().load("https:${pdData.image}").into(R.id.productimage)


            binding.run {
              binding.textView.text=commentData.commentdetails.text
                binding.usercomment.text=commentData.commentdetails.user
            }




        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentsAdapter.ViewHolder {
        return CommentsAdapter.ViewHolder(ItemCommentsBinding.inflate(LayoutInflater.from(parent.context),parent,false),
            )
    }

    override fun getItemCount(): Int {
        return commentsList?.size ?: 0
    }

    override fun onBindViewHolder(holder: CommentsAdapter.ViewHolder, position: Int) {
        (commentsList?.get(position) ?: null)?.let { holder.bind(it) }
    }
    fun submitList(newCommentList: List<CombinedComments>?) {
        commentsList = newCommentList
        notifyDataSetChanged()
    }
}