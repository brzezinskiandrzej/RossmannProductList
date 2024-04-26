package com.andrzejbrzezinski.rossmannproductlist.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityMainBinding

import com.andrzejbrzezinski.rossmannproductlist.filmpackage.activities.LoginUserActivity
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.activities.MainFilmsActivity
import com.andrzejbrzezinski.rossmannproductlist.itempackage.productlistpackage.activities.ProductListActivity
import com.andrzejbrzezinski.rossmannproductlist.objects.LoginState
import com.andrzejbrzezinski.rossmannproductlist.objects.LoginStateService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity  : AppCompatActivity() {

    @Inject
    lateinit var loginStateService: LoginStateService

    private lateinit var binding: ActivityMainBinding
    companion object  {
        const val CHANNEL_ID ="upload_channel"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.Productlstbt.setOnClickListener {

            val intent = Intent(binding.root.context, ProductListActivity::class.java)
            binding.root.context.startActivity(intent)

        }

        binding.Filmlstbt.setOnClickListener {

            if(!loginStateService.account!!) {
                val intent = Intent(binding.root.context, LoginUserActivity::class.java)
                binding.root.context.startActivity(intent)
            }
            else {

                val intent = Intent(binding.root.context, MainFilmsActivity::class.java)
                binding.root.context.startActivity(intent)
            }


        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Przesyłanie filmu"
            val descriptionText = "Powiadomienia o przesyłaniu filmu"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }





        /*var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://v5stg.rossmann.pl/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val productApi: ProductApi = retrofit.create(ProductApi::class.java)*/




        //viewModel.loadProductsIfNeeded(pagenb)
        //loadProducts(pagenb)

        /*binding.nextPageButton.setOnClickListener {

            //loadProducts(productApi)
            viewModel.loadProducts()
        }
*/



        /*
        productApi.productList(pagenb).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {


                    //Toast.makeText(this@MainActivity, response.body()?.data?.toString(), Toast.LENGTH_SHORT).show()
                val map = response.body()?.data?.products?.map{
                        Productdetails(
                            it.name,
                            it.pictures.first().medium
                        )
                    }?: listOf()
                    //Toast.makeText(this@MainActivity, map.toString(), Toast.LENGTH_SHORT).show()

                  /*  response.body()?.data?.products?.forEach {
                        Log.i("product", it.toString())
                    }*/

                productAdapter.submitList(map)

            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
*/



    }


   /* private fun loadProducts(productApi:ProductApi){
        lifecycleScope.launch() {
            try {
                val response = productApi.productList(pagenb)
                if (response.isSuccessful) {
                    val map = response.body()?.data?.products?.map {
                        Productdetails(
                            it.name,
                            it.price,
                            it.caption,
                            it.pictures.firstOrNull()?.medium


                        )
                    } ?: listOf()

                        if(pagenb==1)
                            productAdapter.submitList(map)
                        else{
                            val currentProducts = productAdapter.currentList.toMutableList()
                            currentProducts.addAll(map)
                            productAdapter.submitList(currentProducts)
                        }

                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }}
*/


}


