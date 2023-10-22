package com.example.skingallery

import android.app.Application
import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class _servicesData : Application() {
    companion object{
        var listCesta :MutableList<imageClass> = mutableListOf()
        set(value) {field=value}
    }
    var listImage: List<imageClass> = listOf()
        get() {
            return field
        }
        set(value) {field=value}
    /* var listImage: List<imageClass>
        {
    fun setListImage(listImage: List<imageClass>){
        this.listImage=listImage;
    }
    fun getListImage():List<imageClass>{
        return listImage;

    }*/

    fun getLista():List<imageClass>{

        return this.listImage
    }

}

