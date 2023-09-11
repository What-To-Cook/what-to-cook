package com.example.whattocook.models

import com.beust.klaxon.Json

data class Recipe(
    val name: String,
    @Json(name = "serves_amount")
    val servesAmount: Int,
    val steps: List<String>,
    val ingredients: List<Ingredient>,
    @Json(name = "energy_value_per_serving")
    val energyValuePerServing: EnergyValue,
)