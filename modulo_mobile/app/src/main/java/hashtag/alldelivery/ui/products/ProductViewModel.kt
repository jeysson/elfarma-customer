package hashtag.alldelivery.ui.products

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import hashtag.alldelivery.core.models.BusinessEvent
import hashtag.alldelivery.core.models.Group
import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.models.ProductImage
import hashtag.alldelivery.core.repository.IProductRepository
import hashtag.alldelivery.core.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import retrofit2.awaitResponse

class ProductViewModel(private val producRep: IProductRepository) : ViewModel() {

    var groups: MutableLiveData<List<Group>> = MutableLiveData()
    var products: MutableLiveData<List<Product>> = MutableLiveData()
    var images: MutableLiveData<List<ProductImage>> = MutableLiveData()
    var eventoErro = SingleLiveEvent<BusinessEvent>()

    fun getAllProducts(id: Int?): MutableLiveData<List<Product>> {

        producRep.getAllProducts(id).subscribe({
            if(it != null && it.isNotEmpty()) {
                products.postValue(it)
            } else {
                eventoErro.postValue(BusinessEvent("Nenhum produto encontrado."))
            }
        },{
            eventoErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter os produtos para a loja."))
        })

        return products
    }

    fun getGroupProducts(store: Int?, group: Int?) : ArrayList<Product> {
        var resp = producRep.getProductsGroup(store, group).execute()
        return resp.body()!!
    }

    fun getGroupProductsAsync(store: Int?, group: Int?) : MutableLiveData<List<Product>> {
        producRep.getProductsGroupAsync(store, group).subscribe({
                if(it != null && it.isNotEmpty()) {
                    products.postValue(it)
                } else {
                    eventoErro.postValue(BusinessEvent("Nenhum produto encontrado."))
                }
            },{
                eventoErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter os produtos para a loja."))
            })

        return products
    }

    fun getAllGroups(id: Int?): MutableLiveData<List<Group>> {

        producRep.getAllGroups(id).subscribe({
            if(it != null && it.isNotEmpty()) {
                groups.postValue(it)
            } else {
                eventoErro.postValue(BusinessEvent("Nenhum grupo encontrado."))
            }
        },{
            eventoErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter os grupos para a loja."))
        })

        return groups
    }

     fun getImages(id: Int?): List<ProductImage>{
        return producRep.getImages(id).execute().body()!!
    }

    fun getImagesAsync(id: Int?): MutableLiveData<List<ProductImage>>{
        producRep.getImagesAsync(id).subscribe({
            if(it != null && it.isNotEmpty()) {
                images.postValue(it)
            } else {
                eventoErro.postValue(BusinessEvent("Nenhuma imagem encontrada."))
            }
        },{
            eventoErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter as imagens para o produto."))
        })

        return  images
    }

    fun getPagingProducts(store: Int?, group: Int?, page: Int?, total: Int?) : ArrayList<Product> {
        var resp = producRep.getPagingProducts(store, group, page, total).execute()
        return resp.body()!!
    }
}