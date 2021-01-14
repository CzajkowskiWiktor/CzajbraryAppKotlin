package com.example.czajbraryapp.Models

import android.os.Parcel
import android.os.Parcelable

class User (
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val gender: String = "",
    val profileCompleted: Int = 0
) : Parcelable {
    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<User> {
            override fun createFromParcel(parcel: Parcel) = User(parcel)
            override fun newArray(size: Int) = arrayOfNulls<User>(size)
        }
    }

    private constructor(parcel: Parcel) : this(
        id = parcel.readString().toString(),
        firstName = parcel.readString().toString(),
        lastName = parcel.readString().toString(),
        email = parcel.readString().toString(),
        image = parcel.readString().toString(),
        mobile = parcel.readLong(),
        gender = parcel.readString().toString(),
        profileCompleted = parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(email)
        parcel.writeString(image)
        parcel.writeLong(mobile)
        parcel.writeString(gender)
        parcel.writeInt(profileCompleted)
    }

    override fun describeContents() = 0
}