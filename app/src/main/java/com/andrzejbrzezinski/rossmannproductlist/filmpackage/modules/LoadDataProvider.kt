package com.andrzejbrzezinski.rossmannproductlist.filmpackage.modules

import com.andrzejbrzezinski.rossmannproductlist.filmpackage.interfaces.ILoadData
import javax.inject.Inject
import javax.inject.Named

class LoadDataProvider @Inject constructor(
    @Named("FirebaseData") private val firebaseData: ILoadData,
    @Named("OfflineData") private val offlineData: ILoadData
) {
    fun getLoadData(isOnline: Boolean): ILoadData {
        return if (isOnline) firebaseData else offlineData
    }
}
