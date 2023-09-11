package com.example.whattocook.models

import com.beust.klaxon.Json

data class Ingredient(
    @Json(name = "ingredient")
    val name: String,
    val amount: String
) {
    fun format(): String {
        return this.name + ": " + this.amount
    }
}
