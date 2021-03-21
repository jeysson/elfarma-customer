package hashtag.alldelivery

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.AllDeliveryApplication.Companion.ADDRESS
import hashtag.alldelivery.AllDeliveryApplication.Companion.Pedido
import hashtag.alldelivery.AllDeliveryApplication.Companion.STORE
import hashtag.alldelivery.AllDeliveryApplication.Companion.changeFragment
import hashtag.alldelivery.core.models.Order
import hashtag.alldelivery.core.models.OrderItem
import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.models.Store
import hashtag.alldelivery.core.utils.OnBackPressedListener
import hashtag.alldelivery.ui.bag.BagFragment
import hashtag.alldelivery.ui.order.OrderFragment
import kotlinx.android.synthetic.main.bag_bar.*
import kotlinx.android.synthetic.main.stores_activity_main.*
import java.text.NumberFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    lateinit var navView: BottomNavigationView
    final val DURATION: Long = 400

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        StatusBarUtil.setTransparent(this)

        setContentView(R.layout.stores_activity_main)

        navView = findViewById(R.id.nav_view)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        //val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_search,
                R.id.navigation_orders,
                R.id.navigation_perfil
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)
        //
        bag_container.setOnClickListener {
            val manager = supportFragmentManager
            manager.beginTransaction()
            manager.commit {
                setCustomAnimations(
                    R.anim.enter_from_up,
                    R.anim.exit_to_down,
                    R.anim.enter_from_down,
                    R.anim.exit_to_up
                )

                addToBackStack(null)
                replace(R.id.nav_host_fragment, BagFragment::class.java, null)
            }

           /* changeFragment(
                supportFragmentManager,
                BagFragment::class.java,
                R.id.bag_checkout_container,
                2
            )*/
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        var fragmentVisible = false
        this.supportFragmentManager.getFragments().forEach {
            if((it as? NavHostFragment) == null && it.isVisible){
                fragmentVisible = true
                (it as? OnBackPressedListener)?.onBackPressed()
            }
        }
        if(!fragmentVisible){
            super.onBackPressed()
        }
    }

    fun hideBottomNavigation() {
        // bottom_navigation is BottomNavigationView
        with(navView) {
            //if (visibility == View.VISIBLE) {
                alpha = 1f
                animate().apply {
                    alpha(0f)
                    duration = DURATION
                    translationY(115f)
                    withEndAction {
                        visibility = View.GONE
                        btSacola.translationY = -95f
                    }
                }
           // }
        }
    }

    fun showBottomNavigation() {
        // bottom_navigation is BottomNavigationView
        with(navView) {
            this.alpha = 0f
          //  if (visibility == View.GONE) {
                translationY = 115f
                animate().apply {
                    alpha(1f)
                    duration = DURATION
                    translationY(0f)
                    withStartAction {
                        visibility = View.VISIBLE
                    }
                }
            //}
        }

        btSacola.translationY = 0f
    }

    fun changeValueBag(prod: Product, value: Int) {
        if (Pedido == null) {
            Pedido = Order()
            Pedido?.address = ADDRESS
            Pedido?.store = Store()
            Pedido?.store?.id = STORE?.id
            Pedido?.store?.nomeFantasia = STORE?.nomeFantasia
            Pedido?.store?.tempoMaximo = STORE?.tempoMaximo
            Pedido?.store?.tempoMinimo = STORE?.tempoMinimo
        }
        //
        var ix = Pedido?.itens?.firstOrNull { p: OrderItem -> p.produto?.id == prod?.id   }

        if(ix == null) {
            Pedido?.itens?.add(OrderItem(prod, value, prod?.preco))
        } else {
            if(value == 0)
                Pedido?.itens?.remove(ix)
            else{

                if(ix.quantity!! < value) {
                    item_added.alpha = 0f
                    item_added.animate().apply {
                        alpha(1f)
                        duration = 1000
                        withStartAction {
                            bag_container.visibility = View.INVISIBLE
                        }

                        withEndAction {
                            item_added.visibility = View.INVISIBLE
                            bag_container.alpha = 0f
                            bag_container.animate().apply {
                                alpha(1f)
                                duration = 600
                            }

                            bag_container.visibility = View.VISIBLE
                        }
                    }

                    item_added.visibility = View.VISIBLE
                }

                ix.quantity = value
            }
        }

        showBag()
    }

    fun showBag(){
        if(Pedido != null) {
            var totalQtd = Pedido!!.itens!!.sumBy { p: OrderItem -> p.quantity!! }
            bag_counter.text = totalQtd.toString()

            bag_total_price.text = NumberFormat.getCurrencyInstance(
                Locale(
                    getString(R.string.language),
                    getString(R.string.country)
                )
            ).format(Pedido!!.itens?.sumByDouble { p -> (p.quantity!! * p.price!!) })

            if(totalQtd < 1) {
                btSacola.alpha = 1f
                btSacola.animate().apply {
                    alpha(0f)
                    duration = 400
                    btSacola.translationY = 0f
                }
                btSacola.visibility = View.GONE
            } else if (btSacola.visibility == View.GONE){
                btSacola.alpha = 0f
                btSacola.animate().apply {
                    alpha(1f)
                    duration = 400
                  //  btSacola.translationY = -95f
                }

                btSacola.visibility = View.VISIBLE
            }
        }
    }

    fun hideBag(){
        btSacola.visibility = View.GONE
    }

    fun select(id: Int) {
        //Volta para o fragmento da loja
        supportFragmentManager.popBackStackImmediate()
        //volta para o fragmento home
        supportFragmentManager.popBackStackImmediate()
        //Exibe os botões de navegação
        showBottomNavigation()
        //Navega para o histórico
        navController.navigate(id)
    }
}

