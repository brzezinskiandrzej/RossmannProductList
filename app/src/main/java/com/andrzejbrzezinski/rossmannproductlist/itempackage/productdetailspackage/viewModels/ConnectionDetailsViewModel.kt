package com.andrzejbrzezinski.rossmannproductlist.itempackage.productdetailspackage.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.lang.Exception
import androidx.lifecycle.*
import com.andrzejbrzezinski.rossmannproductlist.CombinedProductDetails
import com.andrzejbrzezinski.rossmannproductlist.ProductHtml
import com.andrzejbrzezinski.rossmannproductlist.Productdetailsvdwa
import com.andrzejbrzezinski.rossmannproductlist.apiProvider.ApiProvider
import com.andrzejbrzezinski.rossmannproductlist.productApi.ProductApi



class ConnectionDetailsViewModel(): ViewModel() {


    private val _products = MutableLiveData<CombinedProductDetails>()
    val products: LiveData<CombinedProductDetails> = _products


fun loadProduct(productID: Int){
    viewModelScope.launch() {
        try {
            val response = ApiProvider.productApi.productDetailsApi(productID)

            if (response.isSuccessful) {
                val data = response.body()?.data
                if(data!=null){
                    val productDetailone = Productdetailsvdwa(
                        data.name,
                        data.price,
                        data.caption,
                        data.id,
                        data.pictures


                    )
                    val response2 = ApiProvider.productApi.productWebView(productID)
                    if (response2.isSuccessful){
                        val data2= response2.body()?.data
                        if(data2!=null)
                        {
                            val producthtml= ProductHtml(
                                data2.html
                            )
                            val combinedList = CombinedProductDetails(productDetailone,producthtml)
                            _products.postValue((combinedList))

                        }else{
                            val combinedList = CombinedProductDetails(productDetailone,null)
                            _products.postValue(combinedList)
                        }

                    }





                }











            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

}