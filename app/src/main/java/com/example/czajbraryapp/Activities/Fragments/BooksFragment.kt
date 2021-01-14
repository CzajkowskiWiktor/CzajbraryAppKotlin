package com.example.czajbraryapp.Activities.Fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.czajbraryapp.Activities.Activity.AddBookActivity
import com.example.czajbraryapp.Activities.Adapters.MyBooksListAdapter
import com.example.czajbraryapp.Firestore.FirestoreClass
import com.example.czajbraryapp.Models.Book
import com.example.czajbraryapp.R
import kotlinx.android.synthetic.main.fragment_books.*

class BooksFragment : BaseFragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // If we want to use the option menu in fragment we need to add it.
    setHasOptionsMenu(true)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root = inflater.inflate(R.layout.fragment_books, container, false)
    return root
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.add_product_menu, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id = item.itemId

    when (id) {
      R.id.action_add_product -> {
        startActivity(Intent(activity, AddBookActivity::class.java))
        return true
      }
    }

    return super.onOptionsItemSelected(item)
  }

  //on resume function
  override fun onResume() {
    super.onResume()
    getProductListFromFireStore()
  }

  /**
   * A function to get the successful product list from cloud firestore.
   *
   * @param productsList Will receive the product list from cloud firestore.
   */
  fun successProductsListFromFireStore(booksList: ArrayList<Book>) {
    // Hide Progress dialog.
    hideProgressDialog()

    if (booksList.size > 0) {
      rv_my_product_items.visibility = View.VISIBLE
      tv_no_products_found.visibility = View.GONE

      rv_my_product_items.layoutManager = LinearLayoutManager(activity)
      rv_my_product_items.setHasFixedSize(true)

      val adapterProducts =
              MyBooksListAdapter(requireActivity(), booksList, this@BooksFragment)
      rv_my_product_items.adapter = adapterProducts
    } else {
      rv_my_product_items.visibility = View.GONE
      tv_no_products_found.visibility = View.VISIBLE
    }
  }

  private fun getProductListFromFireStore() {
    // Show the progress dialog.
    showProgressDialog(resources.getString(R.string.please_wait))

    // Call the function of Firestore class.
    FirestoreClass().getProductsList(this@BooksFragment)
  }

  //deleting product from firestore cloud
  fun deleteProduct(productID: String) {
    showAlertDialogToDeleteProduct(productID)
  }

  //success of deleting the product
  fun productDeleteSuccess() {
    hideProgressDialog()

    Toast.makeText(
      requireActivity(),
      resources.getString(R.string.product_delete_success_message),
      Toast.LENGTH_SHORT
    ).show()

    getProductListFromFireStore()
  }

  /**
   * A function to show the alert dialog for the confirmation of delete product from cloud firestore.
   */
  private fun showAlertDialogToDeleteProduct(productID: String) {

    val builder = AlertDialog.Builder(requireActivity())
    //set title for alert dialog
    builder.setTitle(resources.getString(R.string.delete_dialog_title))
    //set message for alert dialog
    builder.setMessage(resources.getString(R.string.delete_dialog_message))
    builder.setIcon(android.R.drawable.ic_dialog_alert)

    //performing positive action
    builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
      // Show the progress dialog.
      showProgressDialog(resources.getString(R.string.please_wait))

      // Call the Deleting function of Firestore class.
      FirestoreClass().deleteProduct(this@BooksFragment, productID)

      dialogInterface.dismiss()
    }

    //performing negative action
    builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

      dialogInterface.dismiss()
    }
    // Create the AlertDialog
    val alertDialog: AlertDialog = builder.create()
    // Set other dialog properties
    alertDialog.setCancelable(false)
    alertDialog.show()
  }
}