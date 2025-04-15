package com.bahaa.chinv

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Invoice : Screen("invoice")
    object Items : Screen("items")
    object Customers : Screen("customers")
    object Reports : Screen("reports")
    object AddItem : Screen("add_item")
    object EditItem : Screen("edit_item")

}
