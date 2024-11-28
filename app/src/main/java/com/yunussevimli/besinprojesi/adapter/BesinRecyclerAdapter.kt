package com.yunussevimli.besinprojesi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.yunussevimli.besinprojesi.databinding.BesinRecyclerRowBinding
import com.yunussevimli.besinprojesi.model.Besin
import com.yunussevimli.besinprojesi.util.gorselIndir
import com.yunussevimli.besinprojesi.util.placeholderYap
import com.yunussevimli.besinprojesi.view.BesinListesiFragmentDirections

class BesinRecyclerAdapter(val besinListesi : ArrayList<Besin>) : RecyclerView.Adapter<BesinRecyclerAdapter.BesinViewHolder>() {

    class BesinViewHolder(var view : BesinRecyclerRowBinding) : RecyclerView.ViewHolder(view.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BesinViewHolder {
        val recyclerRowBinding: BesinRecyclerRowBinding = BesinRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BesinViewHolder(recyclerRowBinding)
    }

    override fun getItemCount(): Int {
        return besinListesi.size // kaç tane besin varsa o kadar dönecek
    }

    fun besinListesiniGuncelle(yeniBesinListesi: List<Besin>){
        besinListesi.clear() // eski listeyi temizle
        besinListesi.addAll(yeniBesinListesi) // yeni listeyi ekle
        notifyDataSetChanged() // adapteri güncelle
    }

    override fun onBindViewHolder(holder: BesinViewHolder, position: Int) {
        holder.view.isim.text = besinListesi.get(position).besinIsim // besin ismini al
        holder.view.kalori.text = besinListesi.get(position).besinKalori // besin kalorisini al
        holder.itemView.setOnClickListener { // tıklanıldığında yapılacak işlem
            val action = BesinListesiFragmentDirections.actionBesinListeFragmentToBesinDetayFragment(besinListesi.get(position).uuid)
            Navigation.findNavController(it).navigate(action)
        }
        holder.view.imageView.gorselIndir(besinListesi.get(position).besinGorsel, placeholderYap(holder.itemView.context)) // besin görselini al ve göster
    }
}