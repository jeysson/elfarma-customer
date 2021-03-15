package hashtag.alldelivery.ui.products

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.PRODUCT
import hashtag.alldelivery.AllDeliveryApplication.Companion.Pedido
import hashtag.alldelivery.MainActivity
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.OrderItem
import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.utils.OnBackPressedListener
import hashtag.alldelivery.core.utils.OnChangedValueListener
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.product_details_content.*
import kotlinx.android.synthetic.main.product_item_info.*
import java.text.NumberFormat
import java.util.*

class ProductDetail : Fragment(), OnBackPressedListener, OnChangedValueListener {

    private lateinit var product: Product
    private lateinit var item: OrderItem
    private var quantity: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.product_details_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AllDeliveryApplication.productDetailFragment = this
        activity!!.supportFragmentManager.beginTransaction().show(this).commit()
        StatusBarUtil.setLightMode(activity)
        product = PRODUCT!!
        btMinusPlus.open = true
        btMinusPlus.animado = false
        btMinusPlus.produto = product
        btMinusPlus.addOnChangeValueListener(this)

        if(Pedido != null) {
            item = Pedido?.itens?.firstOrNull { p -> p?.produto?.id!! == product.id }!!

            if(item != null)
                btMinusPlus.total = item.quantity!!
        }

        topbar_title.text = getString(R.string.product_details)

        back_button.setOnClickListener {
            back()
        }

        if(product.productImages?.size!! > 0) {
            val imageBytes = android.util.Base64.decode(product.productImages!![0].fotoBase64, 0)
            val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            item_image.setImageBitmap(image)
            item_image.setOnClickListener {
                val intent = Intent(activity, ProductView::class.java)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity,
                    item_image, "robot").toBundle())
            }
        }

        item_title.text = product.nome
        item_unit_description.text = product.descricao
        //
        item_weighable_price.text = getFormatedPrice(product.preco)
    }

    fun getFormatedPrice(valor: Double?): String{
        return NumberFormat.getCurrencyInstance(
            Locale(
                getString(R.string.language),
                getString(R.string.country)
            )
        ).format(valor)
    }


    override fun OnChangedValue(prod: Product, value: Int){
        (activity as MainActivity).changeValueBag(prod, value)
    }

    override fun onBackPressed() {
        back()
    }

    private fun back(){
        StatusBarUtil.setLightMode(activity)
        activity!!.supportFragmentManager.popBackStackImmediate()
        activity!!.supportFragmentManager.beginTransaction()
            .remove(this).commit()
    }
}