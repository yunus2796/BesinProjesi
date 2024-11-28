package com.yunussevimli.besinprojesi.util

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.yunussevimli.besinprojesi.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

// Placeholder : Yüklenirken gösterilecek resim
// ImageView.gorselIndir : Glide kütüphanesi ile resim yükleme işlemi yapar. ImageView'a ek özellikler ekleyebiliriz.
fun ImageView.gorselIndir(url: String?, placeholder : CircularProgressDrawable){ 

    val options = RequestOptions().placeholder(placeholder).error(R.mipmap.ic_launcher_round) // Hata durumunda gösterilecek resim

    Glide.with(context).setDefaultRequestOptions(options).load(url).into(this) // Resmi yükleme işlemi
}

fun placeholderYap(context: Context) : CircularProgressDrawable { // Yüklenir iken gösterilecek resim
    return CircularProgressDrawable(context).apply {
        strokeWidth = 8f // Çemberin kalınlığı
        centerRadius = 40f // Çemberin yarıçapı
        start() // Çemberi başlat
    }
}