package com.example.czajbraryapp.Models

import android.os.Parcel
import android.os.Parcelable

data class Cart_Item(
    val user_id: String = "",
    val product_owner_id: String = "",
    val product_id: String = "",
    val title: String = "",
    val author: String = "",
    val price: String = "",
    val image: String = "",
    var cart_quantity: String = "",
    var stock_quantity: String = "",
    var id: String = ""
) : Parcelable {
    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Cart_Item> {
            override fun createFromParcel(parcel: Parcel) = Cart_Item(parcel)
            override fun newArray(size: Int) = arrayOfNulls<Cart_Item>(size)
        }
    }

    private constructor(parcel: Parcel) : this(
        user_id = parcel.readString().toString(),
        product_owner_id = parcel.readString().toString(),
        product_id = parcel.readString().toString(),
        title = parcel.readString().toString(),
        author = parcel.readString().toString(),
        price = parcel.readString().toString(),
        image = parcel.readString().toString(),
        cart_quantity = parcel.readString().toString(),
        stock_quantity = parcel.readString().toString(),
        id = parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user_id)
        parcel.writeString(product_owner_id)
        parcel.writeString(product_id)
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(price)
        parcel.writeString(image)
        parcel.writeString(cart_quantity)
        parcel.writeString(stock_quantity)
        parcel.writeString(id)
    }

    override fun describeContents() = 0
}