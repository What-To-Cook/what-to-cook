package com.example.whattocook.models

data class EnergyValue(val calories: Int, val protein: Int, val fat: Int, val carbohydrates: Int) {
    fun formatWithPrefixes(): List<String> {
        return listOf(
            "Калории: " + this.calories.toString(),
            "Белки: " + this.protein.toString(),
            "Жиры: " + this.fat.toString(),
            "Углеводы: " + this.carbohydrates.toString(),
        )
    }
}