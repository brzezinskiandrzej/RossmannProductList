package com.andrzejbrzezinski.rossmannproductlist.itempackage.productlistpackage.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityProductListBinding
import com.andrzejbrzezinski.rossmannproductlist.itempackage.productlistpackage.adapters.UserAdapter
import com.andrzejbrzezinski.rossmannproductlist.itempackage.productlistpackage.viewModels.ConnectionViewModel
import kotlinx.coroutines.*

class ProductListActivity : AppCompatActivity() {
    private val viewModel : ConnectionViewModel by viewModels()
    private lateinit var binding: ActivityProductListBinding
    lateinit var productAdapter: UserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        productAdapter= UserAdapter()
        binding.rv.setHasFixedSize(true)
        binding.rv.apply{
            layoutManager= GridLayoutManager(this@ProductListActivity,2)
            adapter = productAdapter
        }
        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerview: RecyclerView, dx: Int, dy: Int){
                super.onScrolled(recyclerview, dx, dy)
                if(dy>0){
                    val layout = binding.rv.layoutManager as GridLayoutManager
                    val visible = layout.childCount
                    val total = layout.itemCount
                    val past = layout.findFirstVisibleItemPosition()
                    viewModel.checkIfShouldLoadMore(dy,visible,total,past)

                }
            }
        })
        viewModel.products.observe(this, Observer { combinedDataList ->
            productAdapter.submitList(combinedDataList)
            viewModel.setLoadingState(false)


        })
    }
}