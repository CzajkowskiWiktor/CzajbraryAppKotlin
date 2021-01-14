package com.example.czajbraryapp.Models

import android.os.Parcel
import android.os.Parcelable

data class Address (
    val user_id: String = "",
    val name: String = "",
    val mobileNumber: String = "",

    val address: String = "",
    val zipCode: String = "",
    val additionalNote: String = "",

    val type: String = "",
    var id: String = ""
) : Parcelable {
    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Address> {
            override fun createFromParcel(parcel: Parcel) = Address(parcel)
            override fun newArray(size: Int) = arrayOfNulls<Address>(size)
        }
    }

    private constructor(parcel: Parcel) : this(
        user_id = parcel.readString().toString(),
        name = parcel.readString().toString(),
        mobileNumber = parcel.readString().toString(),
        address = parcel.readString().toString(),
        zipCode = parcel.readString().toString(),
        additionalNote = parcel.readString().toString(),
        type = parcel.readString().toString(),
        id = parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user_id)
        parcel.writeString(name)
        parcel.writeString(mobileNumber)
        parcel.writeString(address)
        parcel.writeString(zipCode)
        parcel.writeString(additionalNote)
        parcel.writeString(type)
        parcel.writeString(id)
    }

    override fun describeContents() = 0
}