package hashtag.alldelivery.core.repository

import hashtag.alldelivery.core.models.Group
import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.models.ProductImage
import hashtag.alldelivery.core.models.Store
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Query

interface IProductRepository {

    fun getAllProducts(id: Int?): Observable<ArrayList<Product>>

    fun getAllGroups(id: Int?): Observable<ArrayList<Group>>

    fun getProductsGroup(store: Int?, group: Int?): Observable<ArrayList<Product>>

    fun getProductsGroupAsync(store: Int?, group: Int?): Observable<ArrayList<Product>>

    fun getImages(id: Int?): Call<ArrayList<ProductImage>>

    fun getImagesAsync(id: Int?): Observable<ArrayList<ProductImage>>

    fun getPagingProducts(store: Int?, group: Int?, page: Int?, total: Int?): Observable<ArrayList<Product>>

    fun getPagingProducts(store: Int?, filter: String?, page: Int?, total: Int?): Observable<ArrayList<Product>>

    fun getImagesGroupo(id: Int?) : Call<ArrayList<ProductImage>>
}