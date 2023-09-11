package com.example.whattocook

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.whattocook.models.EnergyValue
import com.example.whattocook.models.Ingredient
import com.example.whattocook.models.Recipe
import com.example.whattocook.ui.components.BulletList
import com.example.whattocook.ui.theme.WhatToCookTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhatToCookTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TopAppBar(
                            title = { Text(text = "What To Cook?", color = MaterialTheme.colorScheme.surface) },
                            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
                        )
                        IngredientsMenu()
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT > 9) {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }
}

var example_ingredients = getIngredients()
//var example_ingredients = mutableListOf(
//        "Темный шоколад",
//        "Сливочное масло",
//        "Коричневый сахар",
//        "Куриное яйцо",
//        "Пшеничная мука",
//        "Грецкие орехи"
//    )

//@Composable
//fun SearchButton() {
//    //    val recipes = getRecipes(mutableListOf(
////        "Темный шоколад",
////        "Сливочное масло",
////        "Коричневый сахар",
////        "Куриное яйцо",
////        "Пшеничная мука",
////        "Грецкие орехи"
////    ))
////    val selectedIngredients by remember { mutableStateOf(listOf<String>()) }
//    var recipes by remember {
//        mutableStateOf(listOf<Recipe>())
//    }
//
////    val recipes = getRecipes(selectedIngredients)
//
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Button(modifier = Modifier.wrapContentSize(Alignment.TopCenter), onClick = {
//            recipes = getRecipes(selectedIngredients)
//        }) {
//            Text("Найти рецепты!")
//        }
//
//        Log.d("DEBUG", "found " + recipes.size.toString() + " recipes")
//        Log.d("DEBUG", recipes.toString())
//
//        if (recipes.isEmpty()) {
//            Text("По вашему запросу ничего не найдено.\nСходите в магазин :(", fontSize = 25.sp, fontWeight = FontWeight.W900, modifier = Modifier.padding(10.dp))
//        } else{
//            RecipesList(recipes = recipes)
//        }
//    }
////    Button(modifier = Modifier.wrapContentSize(Alignment.TopCenter), onClick = {
////        recipes = getRecipes(selectedIngredients)
////    }) {
////        Text("Найти рецепты!")
////    }
//
//    // TODO: call recipes drawing here
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientsMenu() {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedIngredients by remember { mutableStateOf(listOf<String>()) }  // TODO: may cause problems
    var selectedText by remember { mutableStateOf("") }
    var recipes by remember {
        mutableStateOf(listOf<Recipe>())
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopCenter)
            .padding(all = 5.dp)
    ) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {expanded = !expanded}, modifier = Modifier.heightIn(0.dp, 100.dp)) {
            TextField(
                value = selectedIngredients.joinToString(separator = ", "),
                onValueChange = { selectedText = it },
                label = { Text(text = "Что у вас есть на кухне?") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            val filteredOptions = example_ingredients.filter { it.contains(selectedText, ignoreCase = true) }
            if (filteredOptions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        // We shouldn't hide the menu when the user enters/removes any character
                    }
                ) {
                    filteredOptions.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                var newSelectedIngredients = selectedIngredients.toMutableList()

                                if (item in selectedIngredients) {
                                    newSelectedIngredients.remove(item)
                                }
                                else {
                                    newSelectedIngredients.add(item)
                                }

                                selectedIngredients = newSelectedIngredients
                                expanded = false
                                Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(modifier = Modifier.wrapContentSize(Alignment.TopCenter), onClick = {
            recipes = getRecipes(selectedIngredients)
        }) {
            Text("Найти рецепты!")
        }

        Log.d("DEBUG", "found " + recipes.size.toString() + " recipes")
        Log.d("DEBUG", recipes.toString())

        if (recipes.isEmpty()) {
            Text("По вашему запросу ничего не найдено.\nСходите в магазин :(", fontSize = 25.sp, fontWeight = FontWeight.W900, modifier = Modifier.padding(10.dp))
        } else{
            RecipesList(recipes = recipes)
        }
    }
}

@Composable
fun RecipesList(recipes: List<Recipe>) {
//    val realRecipes by remember {
//        mutableStateOf(listOf<Recipe>())
//    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        recipes.forEach { recipe ->
            RecipeRow(recipe)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RecipeRow(recipe: Recipe) {
    var isPopupVisible by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Card(modifier = Modifier
        .padding(all = 10.dp)
        .clickable {
            isPopupVisible = true
        }
        .fillMaxWidth()) {
        Column(modifier = Modifier.padding(all = 10.dp)) {
            Text(recipe.name, fontSize = 25.sp, fontWeight = FontWeight.W700, modifier = Modifier.padding(10.dp))
//            Text(recipe.servesAmount.toString(), fontSize = 20.sp, fontWeight = FontWeight.W600, modifier = Modifier.padding(10.dp))
        }
    }

    if (isPopupVisible) {
        keyboardController?.hide()
        Popup(
            onDismissRequest = { isPopupVisible = false },
            alignment = Alignment.Center,
        ) {
            // Display your popup window content here
            Column(modifier = Modifier
                .padding(all = 10.dp)
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(5.dp)
                )
                .verticalScroll(rememberScrollState())
            ) {
                Text(recipe.name, fontSize = 25.sp, fontWeight = FontWeight.W900, modifier = Modifier.padding(10.dp))

                Text("Энергетическая ценность", fontSize = 20.sp, fontWeight = FontWeight.W600, modifier = Modifier.padding(10.dp))
                BulletList(style = MaterialTheme.typography.bodyMedium, items = recipe.energyValuePerServing.formatWithPrefixes())

                Text("Вам понадобится на " + recipe.servesAmount + " порций", fontSize = 20.sp, fontWeight = FontWeight.W600, modifier = Modifier.padding(10.dp))
                BulletList(style = MaterialTheme.typography.bodyMedium, items = recipe.ingredients.map { it.format() })
                Text("Шаги", fontSize = 20.sp, fontWeight = FontWeight.W600, modifier = Modifier.padding(10.dp))
                BulletList(style = MaterialTheme.typography.bodyMedium, items = recipe.steps)
//                Column {
//                    Text(recipe.name, fontSize = 25.sp, fontWeight = FontWeight.W900, modifier = Modifier.padding(10.dp))
//                }
//                Text(recipe.name, fontSize = 25.sp, fontWeight = FontWeight.W700, modifier = Modifier.padding(10.dp))
//                Text(recipe.servesAmount.toString(), fontSize = 20.sp, fontWeight = FontWeight.W600, modifier = Modifier.padding(10.dp))
            }
        }
    }
}
