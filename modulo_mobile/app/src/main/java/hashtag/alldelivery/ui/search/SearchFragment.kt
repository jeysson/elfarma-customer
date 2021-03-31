package hashtag.alldelivery.ui.search

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.SEARCH_ALL
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.utils.OnBackPressedListener
import hashtag.alldelivery.ui.products.ProductAdapter
import hashtag.alldelivery.ui.products.ProductViewModel
import kotlinx.android.synthetic.main.product_search_fragment.*
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.android.synthetic.main.search_fragment.loadingSearch
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SearchFragment : Fragment(), OnBackPressedListener {

    private val productViewModel: ProductViewModel by sharedViewModel()
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var page = 1
    var itemsPerPage = 6
    var filter: String? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtil.setLightMode(activity)
        //
        list.layoutManager = GridLayoutManager(context, 2)
        list.setHasFixedSize(true)
        list.addOnScrollListener(object : OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val recyclerLayout = (list.layoutManager as GridLayoutManager)

                if (!isLoading && !isLastPage) {
                    if (recyclerLayout.findLastCompletelyVisibleItemPosition() == (itemsPerPage * page) - 1) {
                        isLoading = true
                        loadingSearch.visibility = View.VISIBLE
                        page += 1
                        if (filter != null)
                            getMoreItems(filter)
                        else
                            getMoreItems()
                    }
                }
            }
        })

        edit_search.focusable = View.FOCUSABLE
        edit_search.doOnTextChanged { text, start, count, after ->
            if (text.isNullOrBlank()) {
                getMoreItems()
                btn_cancel.text = getString(R.string.cancelar)
            } else {
                btn_cancel.text = getString(R.string.limpar)
                if (text.length > 2) {
                    findItemByName(text!!)
                }
            }
        }

        btn_cancel.setOnClickListener {
            if (edit_search.text.isNullOrBlank()) {
                back()
            } else {
                edit_search.setText("")
            }
        }

        initAdapter()
        setObservables()
    }

    fun initAdapter(){
        productViewModel.adapterProduct = ProductAdapter(
            this, SEARCH_ALL, null)
        productViewModel.adapterProduct?.itens = ArrayList<Product>()
        list.adapter = productViewModel.adapterProduct
    }

    fun setObservables(){
         productViewModel.loading.observe(viewLifecycleOwner){

            isLoading = it

            if(it) {
                loadingSearch.visibility = View.VISIBLE

                /* if(page== 1)
                     _recyclerProductList.visibility = View.GONE*/
            }else{
                loadingSearch.visibility = View.INVISIBLE
                //  _recyclerProductList.visibility = View.VISIBLE
            }
        }
    }

    private fun findItemByName(text: CharSequence) {
        /*
        * Procura na lista completa da loja
        */
        page = 1
        if (text != null && text.toString() != "") {
            filter = text.toString()
            getItems(filter)
        }
        else
            getItems()
    }

    private fun getItems(filter: String? = null) {
        page = 1
        isLastPage = false
        //config adapter
        productViewModel.getPagingProducts(
            filter,
            page,
            itemsPerPage
        )
    }

    fun getMoreItems(filter: String? = null) {
        productViewModel.getPagingProducts(
            filter,
            page,
            itemsPerPage
        )
    }

    override fun onBackPressed() {
        back()
    }

    private fun back(){
        activity!!.supportFragmentManager.popBackStack()
        activity!!.supportFragmentManager.beginTransaction()
            .remove(this).commit()
    }
}