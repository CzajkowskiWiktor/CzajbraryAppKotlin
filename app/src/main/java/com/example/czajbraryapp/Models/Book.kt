package com.example.czajbraryapp.Models

import android.os.Parcel
import android.os.Parcelable

data class Book (
    val user_id: String = "",
    val user_name: String = "",
    val title: String = "",
    val author: String = "",
    val price: String = "",
    val description: String = "",
    val stock_quantity: String = "",
    val image: String = "",
    var book_id: String = ""
) : Parcelable {
    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Book> {
            override fun createFromParcel(parcel: Parcel) = Book(parcel)
            override fun newArray(size: Int) = arrayOfNulls<Book>(size)
        }
    }

    private constructor(parcel: Parcel) : this(
        user_id = parcel.readString().toString(),
        user_name = parcel.readString().toString(),
        title = parcel.readString().toString(),
        author = parcel.readString().toString(),
        price = parcel.readString().toString(),
        description = parcel.readString().toString(),
        stock_quantity = parcel.readString().toString(),
        image = parcel.readString().toString(),
        book_id = parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user_id)
        parcel.writeString(user_name)
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(price)
        parcel.writeString(description)
        parcel.writeString(stock_quantity)
        parcel.writeString(image)
        parcel.writeString(book_id)
    }

    override fun describeContents() = 0
}