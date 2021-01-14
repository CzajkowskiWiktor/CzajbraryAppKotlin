package com.example.czajbraryapp.Activities.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.czajbraryapp.Activities.Adapters.CartItemsListAdapter
import com.example.czajbraryapp.Firestore.FirestoreClass
import com.example.czajbraryapp.Models.Book
import com.example.czajbraryapp.Models.Cart_Item
import com.example.czajbraryapp.R
import com.example.czajbraryapp.Utils.Constants
import kotlinx.android.synthetic.main.activity_cart_list.*

class CartListActivity : BaseActivity() {

    private lateinit var mBooksList: ArrayList<Book>
    private lateinit var mCartListItems: ArrayList<Cart_Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)

        //back button
        setupActionBar()

        btn_checkout.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }
    }

    //showing the cart screen activity
    fun successCartItemsList(cartList: ArrayList<Cart_Item>) {
        hideProgressDialog()

        //checking stock quantity values
        for (book in mBooksList) {
            for (cartItem in cartList){
                if (book.book_id == cartItem.product_id) {
                    cartItem.stock_quantity = book.stock_quantity

                    if (book.stock_quantity.toInt() == 0) {
                        cartItem.cart_quantity = book.stock_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        if (mCartListItems.size > 0) {
            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE

            //positioning the cart items in list
            rv_cart_items_list.layoutManager = LinearLayoutManager(this@CartListActivity)
            rv_cart_items_list.setHasFixedSize(true)

            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, mCartListItems, true)
            rv_cart_items_list.adapter = cartListAdapter

            //calculating the whole price in cart
            var subTotal: Double = 0.0
            for (item in mCartListItems) {
                val availableQuantity = item.stock_quantity.toInt()
                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()

                    subTotal += (price * quantity)
                }
            }

            tv_sub_total.text = "$subTotal zł"
            // Here we have kept Shipping Charge is fixed as 10 zł
            tv_shipping_charge.text = "10.0 zł"

            if (subTotal > 0) {
                ll_checkout.visibility = View.VISIBLE

                val total = subTotal + 10
                tv_total_amount.text = "$total zł"
            } else {
                ll_checkout.visibility = View.GONE
            }
        } else {
            rv_cart_items_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            tv_no_cart_item_found.visibility = View.VISIBLE
        }
    }

    fun successProductsListFromFireStore(booksList: ArrayList<Book>) {
        hideProgressDialog()
        mBooksList = booksList
        getCartItemsList()
    }

    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this)
    }

    private fun getCartItemsList() {
//        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getCartList(this@CartListActivity)
    }

    override fun onResume() {
        super.onResume()
        //firstly check all products, theirs quanmtity and if they exist tne get cart items to screen
        //getCartItemsList()
        getProductList()
    }

    //A function for actionBar Setup.
    private fun setupActionBar() {
        setSupportActionBar(toolbar_cart_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_cart_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    //updating item
    fun itemUpdateSuccess() {
        hideProgressDialog()
        getCartItemsList()
    }

    /**
     * A function to notify the user about the item removed from the cart list.
     */
    fun itemRemovedSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getCartItemsList()
    }
}