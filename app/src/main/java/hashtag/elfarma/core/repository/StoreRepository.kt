package hashtag.elfarma.core.repository

import hashtag.elfarma.AllDeliveryApplication
import hashtag.elfarma.core.models.PaymentMethod
import hashtag.elfarma.core.models.Store
import hashtag.elfarma.core.network.StoreApi
import io.reactivex.Observable

class StoreRepository(
    private val dataSource: StoreApi
): IStoreRepository, BaseRepository() {

    override fun getActiveStores(
        indice: Int,
        tamanho: Int,
        lat: Double?,
        lon: Double?,
        tipoOrdenacao: Int
    ): Observable<ArrayList<Store>> {
        val newLat = -30.09488
        val newLon = -60.0462758
        if (lat == null || lon == null){
            return runOnBackground(dataSource.getActiveStores("Bearer " + AllDeliveryApplication.USER?.token, indice,tamanho, newLat, newLon, tipoOrdenacao))
        }
        return runOnBackground(dataSource.getActiveStores("Bearer " + AllDeliveryApplication.USER?.token, indice,tamanho, lat, lon, tipoOrdenacao))
    }

    override fun getPagingStores(
        page: Int?,
        total: Int?,
        lat: Double?,
        lon: Double?,
        tipoordenacao: Int?
    ): Observable<ArrayList<Store>> {
        return runOnBackground(dataSource.getPagingStores("Bearer " + AllDeliveryApplication.USER?.token, page, total, lat, lon, tipoordenacao))
    }

    override fun getStoreLogo(loja: Int?): Observable<Store> {
        return runOnBackground(dataSource.getStoreLogo("Bearer " + AllDeliveryApplication.USER?.token, loja))
    }

    override fun getStoreBanner(loja: Int?): Observable<Store> {
        return runOnBackground(dataSource.getStoreBanner("Bearer " + AllDeliveryApplication.USER?.token, loja))
    }

    override fun getPaymentMethods(loja: Int?): Observable<ArrayList<PaymentMethod>> {
        return  runOnBackground(dataSource.getPaymentMethods("Bearer " + AllDeliveryApplication.USER?.token, loja))
    }
}