package com.example.czajbraryapp.Activities.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.czajbraryapp.Firestore.FirestoreClass
import com.example.czajbraryapp.Models.Book
import com.example.czajbraryapp.Models.Cart_Item
import com.example.czajbraryapp.R
import com.example.czajbraryapp.Utils.Constants
import com.example.czajbraryapp.Utils.GlideLoader
import kotlinx.android.synthetic.main.activity_book_details.*

class BookDetailsActivity : BaseActivity(), View.OnClickListener {

    private var mBookId: String = ""
    private lateinit var mBookDetails: Book
    private var mBookOwnerId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_BOOK_ID)) {
            mBookId = intent.getStringExtra(Constants.EXTRA_BOOK_ID)!!
        }

//        var productOwnerId: String = ""

        if (intent.hasExtra(Constants.EXTRA_BOOK_OWNER_ID)) {
            mBookOwnerId = intent.getStringExtra(Constants.EXTRA_BOOK_OWNER_ID)!!
        }

        if (FirestoreClass().getCurrentUserID() == mBookOwnerId) {
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE
        } else {
            btn_add_to_cart.visibility = View.VISIBLE
        }

        getProductDetails()
        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id) {
                R.id.btn_add_to_cart -> {
                    addToCart()
                }
                R.id.btn_go_to_cart -> {
                    startActivity(Intent(this@BookDetailsActivity, CartListActivity::class.java))
                }
            }
        }
    }

    //A function for actionBar Setup.
    private fun setupActionBar() {
        setSupportActionBar(toolbar_book_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_book_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    //getting product details
    private fun getProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this, mBookId)
    }

    //fulfill variables on details product screen
    fun productDetailsSuccess(book: Book) {
        mBookDetails = book

        //load an image
        GlideLoader(this@BookDetailsActivity).loadProductPicture(
            book.image,
            iv_product_detail_image
        )

        //setting details on screen
        tv_product_details_title.text = book.title
        tv_product_details_author.text = book.author
        tv_product_details_price.text = "${book.price} zł"
        tv_product_details_description.text = book.description
        tv_product_details_available_quantity.text = book.stock_quantity

        //if stock == 0 u can not add to cart and show text
        if (book.stock_quantity.toInt() == 0) {
            hideProgressDialog()
            btn_add_to_cart.visibility = View.GONE
            tv_product_details_available_quantity.text =
                resources.getString(R.string.lbl_out_of_stock)

            tv_product_details_available_quantity.setTextColor(
                ContextCompat.getColor(
                    this@BookDetailsActivity,
                    R.color.colorSnackBarError
                )
            )
        } else {
            //checking if user has a product in cart
            if (FirestoreClass().getCurrentUserID() == book.user_id) {
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistInCart(this, mBookId)
            }
        }
    }

    //adding item to cart firestore cloud
    private fun addToCart() {
        val cartItem = Cart_Item(
            FirestoreClass().getCurrentUserID(),
            mBookOwnerId,
            mBookId,
            mBookDetails.title,
            mBookDetails.author,
            mBookDetails.price,
            mBookDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addCartItems(this, cartItem)
    }

    //adding to cart success
    fun addToCartSuccess() {
        // Hide the progress dialog.
        hideProgressDialog()

        Toast.makeText(
            this@BookDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()

        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

    /**
     * A function to notify the success result of item exists in the cart.
     */
    fun productExistsInCart() {
        // Hide the progress dialog.
        hideProgressDialog()

        // Hide the AddToCart button if the item is already in the cart.
        btn_add_to_cart.visibility = View.GONE
        // Show the GoToCart button if the item is already in the cart. User can update the quantity from the cart list screen if he wants.
        btn_go_to_cart.visibility = View.VISIBLE
    }
}