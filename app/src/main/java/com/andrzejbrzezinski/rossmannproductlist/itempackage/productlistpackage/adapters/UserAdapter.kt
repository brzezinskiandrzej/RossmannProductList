package com.andrzejbrzezinski.rossmannproductlist.itempackage.productlistpackage.adapters

import android.content.Intent
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andrzejbrzezinski.rossmannproductlist.CombinedData
import com.andrzejbrzezinski.rossmannproductlist.databinding.ItemUserBinding
import com.andrzejbrzezinski.rossmannproductlist.itempackage.productdetailspackage.activities.ItemActivity
import com.bumptech.glide.Glide

class UserAdapter: ListAdapter<CombinedData, UserAdapter.UserViewHolder>(CombinedDataDiffCallback()) {

    class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root){

        //var nametxt: TextView = iv.findViewById<TextView>(R.id.productname)

        fun bindData(pdData: CombinedData){
            //nametxt.text = pdData.name

            //Picasso.get().load("https:${pdData.image}").into(R.id.productimage)


            binding.run {
                productname.text = pdData.productdetails.name
                price.text="${pdData.productdetails.price} zł"
                productdescription.text=pdData.productdetails.caption
                Glide.with(itemView.context).load(pdData.productdetails.image).into(pimage)
                quantity.text="Pieces available: ${pdData.productdetailstwo.availableQuantity}"
            }
            binding.root.setOnClickListener{
                val intent = Intent(binding.root.context, ItemActivity::class.java)
                intent.putExtra("PRODUCT_ID" , pdData.productdetails.id)
                binding.root.context.startActivity(intent)
            }


        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
       holder.bindData(getItem(position))
    }




   /* class UserDiffUtil: DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Productdetails, newItem: Productdetails): Boolean {
            return newItem.name == oldItem.name
        }

        override fun areContentsTheSame(oldItem: Productdetails, newItem: Productdetails): Boolean {
            return areItemsTheSame(oldItem,newItem)
        }

    }*/

    class CombinedDataDiffCallback : DiffUtil.ItemCallback<CombinedData>() {
        override fun areItemsTheSame(oldItem: CombinedData, newItem: CombinedData): Boolean {
            return oldItem.productdetails.name == newItem.productdetails.name
        }

        override fun areContentsTheSame(oldItem: CombinedData, newItem: CombinedData): Boolean {
            return oldItem == newItem
        }
    }

}
