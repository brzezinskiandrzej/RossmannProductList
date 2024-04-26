package com.andrzejbrzezinski.rossmannproductlist.itempackage.productlistpackage.viewModels

import androidx.lifecycle.*
import com.andrzejbrzezinski.rossmannproductlist.CombinedData
import com.andrzejbrzezinski.rossmannproductlist.Productdetails
import com.andrzejbrzezinski.rossmannproductlist.Productdetailstwo
import com.andrzejbrzezinski.rossmannproductlist.apiProvider.ApiProvider
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.Collections.emptyList


class ConnectionViewModel (): ViewModel() {

    private var pagenb=0
    init {

        pagenb=1
        loadProducts()
        //Log.e("Error","Error")
    }


    private var isLoading = false

    fun checkIfShouldLoadMore(dy: Int, visibleItemCount: Int, totalItemCount: Int, firstVisibleItemPosition: Int) {
        if (!isLoading && dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount-4) {
            loadProducts()
            setLoadingState(true)
        }
    }

    fun setLoadingState(state: Boolean) {
        isLoading = state
    }


    private val _products = MutableLiveData<List<CombinedData>>(emptyList())
    val products: LiveData<List<CombinedData>> = _products
   /* fun loadProductsIfNeeded(pagenb: Int){
        if(_products.value.isNullOrEmpty()){
            loadProducts()
        }
    }*/
    fun loadProducts(){
        viewModelScope.launch() {
            try {
                val response = ApiProvider.productApi.productList(pagenb)

                if (response.isSuccessful) {
                    val map = response.body()?.data?.products?.map {
                        Productdetails(
                            it.name,
                            it.price,
                            it.caption,
                            it.id,
                            it.pictures.firstOrNull()?.medium



                        )


                    } ?: listOf()

                    val combinedList = map.map{ product ->
                        val stockResponse = ApiProvider.productApi.productStock(737,product.id)
                        if(stockResponse.isSuccessful)
                        {
                            val stockmap = stockResponse.body()?.data?.availableQuantity

                            CombinedData(product, Productdetailstwo(stockmap))
                        }else null

                    }.filterNotNull()




                    if(pagenb==1)
                    _products.postValue(combinedList)
                    else{
                        val currentProducts = _products.value ?: emptyList()
                        val updatedProducts = currentProducts + combinedList
                        _products.postValue(updatedProducts)
                    }
                    pagenb++



                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}