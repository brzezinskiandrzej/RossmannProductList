package com.andrzejbrzezinski.rossmannproductlist.itempackage.productdetailspackage.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.andrzejbrzezinski.rossmannproductlist.ViewPagerAdapter
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityItemBinding
import com.andrzejbrzezinski.rossmannproductlist.itempackage.productdetailspackage.viewModels.ConnectionDetailsViewModel


class ItemActivity : AppCompatActivity() {
    private val viewModel: ConnectionDetailsViewModel by viewModels()
    private lateinit var binding: ActivityItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val productid = intent.getIntExtra("PRODUCT_ID", 0)
        viewModel.loadProduct(productid)
        val imageadapter = ViewPagerAdapter(emptyList())
        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.useWideViewPort = true
        viewModel.products.observe(this, Observer { product ->
            product.let {
                val imageURL = it.productdetailsvdwa.image.map { picture -> picture.medium }
                imageadapter.images = imageURL
                binding.run {
                    DeTitle.text = it.productdetailsvdwa.name
                    DeCaption.text = it.productdetailsvdwa.caption
                    imagepager.adapter = imageadapter
                    if(!it.producthtml?.html.isNullOrEmpty())
                        it.producthtml?.html?.let { it1 -> webView.loadData(it1, "text/html; charset=utf-8", "utf-8") }
                }
            }

        })


    }
}