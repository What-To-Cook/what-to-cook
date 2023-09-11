package com.example.whattocook

import android.util.Log
import com.beust.klaxon.Klaxon
import com.example.whattocook.models.Recipe
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


fun getIngredients(): MutableList<String> {
    val url = URL("http://10.0.2.2:12000/ingredients")
    val buffer = mutableListOf<String>()

    with(url.openConnection() as HttpURLConnection) {
        inputStream.bufferedReader().use {
            it.lines().forEach { line -> buffer.add(line)}
        }
    }

    var line = buffer[0]
    line = line.replace("[", "")
    line = line.replace("]", "")
    line = line.replace("\"", "")

//    with(url.openConnection() as HttpURLConnection) {
//        inputStream.bufferedReader().use {
//            it.lines().forEach { line -> lines.add(line)}
//        }
//    }

    return line.split(",").toMutableList()
}

fun getRecipes(ingredients: List<String>): List<Recipe> {
//    val url = URL("http://127.0.0.1:12000/recipes")
    val url = URL("http://10.0.2.2:12000/recipes")

    val transformedIngredients = ingredients.map { "\"" + it + "\"" }
    val dict = mapOf("ingredients" to transformedIngredients)
    val json = dict.toString().replace("=", ": ").replace("ingredients", "\"ingredients\"")

//    val json = """
//        {
//          "ingredients": [
//            "Темный шоколад",
//            "Сливочное масло",
//            "Коричневый сахар",
//            "Куриное яйцо",
//            "Пшеничная мука",
//            "Грецкие орехи"
//          ]
//        }
//    """.trimIndent()

    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Accept", "application/json")
    connection.allowUserInteraction = false;

    // for sending json
    connection.setRequestProperty("Content-length", json.toByteArray().size.toString());
    connection.doInput = true;
    connection.doOutput = true;
    connection.useCaches = false;

    val outputStream = connection.outputStream
    outputStream.write(json.toByteArray(charset("UTF-8")))
    outputStream.close()

    connection.connect();

    val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
    val sb = StringBuilder()
    var line: String

    try {
        while (bufferedReader.readLine().also { line = it } != null) {
            sb.append(
                """
            $line

            """.trimIndent()
            )
        }
    } catch (ex: Exception) {
        Log.d("DEBUG", "exception: $ex")
    }

    bufferedReader.close()
    line = sb.toString()

    connection.disconnect()

    Log.d("DEBUG", "line: $line")
    val parsed = Klaxon().parseArray<Recipe>(line)
    Log.d("DEBUG", "parsed: " + parsed.toString())

    if (parsed == null) {
        return listOf()
    } else {
        return parsed
    }
}

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            var example_ingredients = listOf(
                "Темный шоколад",
                "Сливочное масло",
                "Коричневый сахар",
                "Куриное яйцо",
                "Пшеничная мука",
                "Грецкие орехи"
            )
//            example_ingredients = example_ingredients.map { "\"" + it + "\"" }
//            val dict = mapOf("ingredients" to example_ingredients)
//            val json = dict.toString().replace("=", ": ").replace("ingredients", "\"ingredients\"")
            val result = getRecipes(example_ingredients)
            println(result)

//            val ingredients = getIngredients()
//            val result2 = getRecipes(mutableListOf())
//            println(result2)
//
//            val result = Klaxon()
//                .parseArray<Recipe>("""
//                [
//  {
//    "name": "Брауни (brownie)",
//    "serves_amount": 6,
//    "steps": [
//      "Шоколад разломать на кусочки и вместе со сливочным маслом растопить на водяной бане, не переставая все время помешивать лопаткой или деревянной ложкой. Получившийся густой шоколадный соус снять с водяной бани и оставить остывать.",
//      "Тем временем смешать яйца со ста граммами коричневого сахара: яйца разбить в отдельную миску и взбить, постепенно добавляя сахар. Взбивать можно при помощи миксера или вручную — как больше нравится, — но не меньше двух с половиной-трех минут.",
//      "Острым ножом на разделочной доске порубить грецкие орехи. Предварительно их можно поджарить на сухой сковороде до появления аромата, но это необязательная опция.",
//      "В остывший растопленный со сливочным маслом шоколад аккуратно добавить оставшийся сахар, затем муку и измельченные орехи и все хорошо перемешать венчиком.",
//      "Затем влить сахарно-яичную смесь и тщательно смешать с шоколадной массой. Цвет у теста должен получиться равномерным, без разводов.",
//      "Разогреть духовку до 200 градусов. Дно небольшой глубокой огнеупорной формы выстелить листом бумаги для выпечки или калькой. Перелить тесто в форму. Поставить в духовку и выпекать двадцать пять — тридцать минут до появления сахарной корочки.",
//      "Готовый пирог вытащить из духовки, дать остыть и нарезать на квадратики острым ножом или ножом для пиццы — так кусочки получатся особенно ровными.",
//      "Подавать брауни можно просто так, а можно посыпать сверху сахарной пудрой или разложить квадратики по тарелкам и украсить каждую порцию шариком ванильного мороженого."
//    ],
//    "ingredients": [
//      {
//        "ingredient": "Темный шоколад",
//        "amount": "100 г"
//      },
//      {
//        "ingredient": "Сливочное масло",
//        "amount": "180 г"
//      },
//      {
//        "ingredient": "Коричневый сахар",
//        "amount": "200 г"
//      },
//      {
//        "ingredient": "Куриное яйцо",
//        "amount": "4 штуки"
//      },
//      {
//        "ingredient": "Пшеничная мука",
//        "amount": "100 г"
//      },
//      {
//        "ingredient": "Грецкие орехи",
//        "amount": "100 г"
//      }
//    ],
//    "energy_value_per_serving": {
//      "calories": 676,
//      "protein": 10,
//      "fat": 46,
//      "carbohydrates": 55
//    }
//  }
//]
//                """
//            )
//            println(result)
        }
    }
}