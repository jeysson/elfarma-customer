package hashtag.alldelivery.ui.store

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Store
import kotlinx.android.synthetic.main.store_item_adapter.view.*
import kotlinx.android.synthetic.main.store_list_header.view.*
import java.text.NumberFormat
import java.util.*

class StoresListItemAdapter(
    val act: AppCompatActivity,
    val itens: List<Store>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    private val TYPE_HEADER = 1
    private val TYPE_BODY = 2

    lateinit var listHeader: View
    lateinit var itemClickListener: AdapterView.OnItemClickListener
    var activity = act

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreItemViewHolder {
        if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.store_list_header, parent, false)
            return StoreItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.store_item_adapter, parent, false)
            return StoreItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position > 0) {
            holder as StoreItemViewHolder
            val item = itens[position]

            val name = item.nomeFantasia

            if(!item.disponivel){
                holder.closedItemOverlay.visibility = VISIBLE
                holder.closedText.visibility = VISIBLE
            }

//            Mudando a visibilidade dos itens
            holder.rating.visibility = GONE
            holder.starIcon.visibility = GONE
            holder.categoryType.visibility = GONE
            holder.dividerCategoryDistance.visibility = GONE
            holder.dividerRatingCategory.visibility = GONE


//           Inserindo imagem
            if(item.logo != null){
                val imageBytes = android.util.Base64.decode(item.logo, 0)
                val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.logo.setImageBitmap(image)
            }else {
                holder.logo.setImageResource(R.drawable.ic_medicine)
            }

            var storeFee = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(item.taxaEntrega)
            if (item.taxaEntrega == 0f || item.taxaEntrega == null) {
                holder.deliveryFree.setTextColor(activity?.getColor(R.color.deprecated_forest))
                storeFee = "Gratis"
            }

            holder.name.text = name
            holder.rating.text = "4,5"
            holder.categoryDistance.text = "${item.distancia}"
            holder.deliveryTime.text = "${item.tempoMinimo} - ${item.tempoMaximo} min"
            holder.deliveryFree.text = "$storeFee"
            holder.categoryType.text = "Farmácia"

            holder.card.setTag(position)
            holder.card.setOnClickListener(this)
        }
    }

    override fun getItemCount() = itens.size

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_HEADER;

        } else {
            return TYPE_BODY;
        }
    }

    class StoreHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.store_title
    }

    class StoreItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card = view.card
        val name = view.name
        val rating = view.rating_store_item
        val categoryDistance = view.distance_store_item
        val deliveryTime = view.delivery_time
        val deliveryFree = view.delivery_free
        val categoryType = view.category_store_item
        val starIcon = view.star_icon
        val dividerRatingCategory = view.divider_rating_category
        val dividerCategoryDistance = view.divider_category_distance
        val logo = view.logo
        val closedItemOverlay = view.image_view_store_closed_overlay
        val closedText = view.closed
    }

    override fun onClick(view: View?) {
        if (view is CardView) {
            var position = view!!.tag as Int
            AllDeliveryApplication.STORE = itens[position]
            val manager: FragmentManager = activity.supportFragmentManager
            manager.beginTransaction()
            manager.commit {
                setCustomAnimations(
                    R.anim.enter_from_left,
                    R.anim.exit_to_right,
                    R.anim.enter_from_right,
                    R.anim.exit_to_left
                )
                replace(R.id.nav_host_fragment, StoreFragment::class.java, null)
                addToBackStack(null)
            }
        }
    }
}