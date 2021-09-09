package com.arsenosov.weatherapp.city

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CityItem(
    @SerializedName("name")
    val name: String,
    @SerializedName("country")
    val country: String
): Parcelable {
    override fun toString(): String = "$name, $country"
}
