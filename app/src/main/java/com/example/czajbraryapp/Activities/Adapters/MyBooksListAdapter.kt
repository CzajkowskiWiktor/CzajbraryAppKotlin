package com.example.czajbraryapp.Activities.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.czajbraryapp.Activities.Activity.BookDetailsActivity
import com.example.czajbraryapp.Activities.Fragments.BooksFragment
import com.example.czajbraryapp.Models.Book
import com.example.czajbraryapp.R
import com.example.czajbraryapp.Utils.Constants
import com.example.czajbraryapp.Utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*

open class MyBooksListAdapter(
    private val context: Context,
    private var list: ArrayList<Book>,
    private val fragment: BooksFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    /**
     * Inflates the item views which is designed in xml layout file
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            GlideLoader(context).loadProductPicture(model.image, holder.itemView.iv_item_image)

            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_price.text = "${model.price} zł"

            holder.itemView.ib_delete_product.setOnClickListener {
                fragment.deleteProduct(model.book_id)
            }

            holder.itemView.setOnClickListener {
                // Launch Product details screen.
                val intent = Intent(context, BookDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_BOOK_ID, model.book_id)
                intent.putExtra(Constants.EXTRA_BOOK_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}