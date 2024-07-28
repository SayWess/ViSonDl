package com.example.visondl.data

import android.util.Log
import com.example.visondl.JSON_DATA_FILE_PATH
import com.example.visondl.checkSpellTitle
import com.example.visondl.model.Item
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

private const val TAG = "DataManager"


class DataManager {


    fun addItem(url: String, title: String, ) {
        if (validUrl(url)) {
            val id = getId(url = url)
            if (!(items.any { item -> item.id == id }))
                items.add(Item(url = url, id = id, title = checkSpellTitle(title), isPlaylist = isPlaylist(url)))
        }

    }

    fun deleteItemById(itemId: String) {
        items.removeIf { it.id == itemId }
    }

    fun getItemById(itemId: String): Item {
        return items.first { it.id == itemId }
    }

    private fun validUrl(url: String) : Boolean {
        return url.contains("playlist") || url.contains("v=")
    }

    private fun isPlaylist(url: String) : Boolean {
        return url.contains("playlist")
    }

    private fun getId(url: String) : String {
        val start = url.indexOfFirst { char -> char == '=' } + 1
        return url.substring(start, url.lastIndexOf('&'))
    }

    fun getItems(): MutableList<Item> {
        return items
    }


    fun saveData(): Boolean {
        val gson = Gson()
        val elementsListJSON = gson.toJson(items)
        val jsonFile = File(JSON_DATA_FILE_PATH)
        val fos: FileOutputStream
        try {
            fos = FileOutputStream(jsonFile)
            fos.write(elementsListJSON.toByteArray())
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    companion object {
        val items : MutableList<Item> = initData()

        private fun initData(): MutableList<Item> {
            val gson = Gson()
            var itemsList = mutableListOf<Item>()
            try {
                // Récupération du contenu du fichier .json en String
                val strContent = FileInputStream(JSON_DATA_FILE_PATH).bufferedReader().use { it.readText() }
                Log.d("Data Manager", "Contenue du fichier JSON : $strContent")
                itemsList = gson.fromJson(strContent, Array<Item>::class.java).toMutableList()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return itemsList
        }
    }
}