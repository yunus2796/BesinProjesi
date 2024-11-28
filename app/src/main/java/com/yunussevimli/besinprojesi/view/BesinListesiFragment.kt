package com.yunussevimli.besinprojesi.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yunussevimli.besinprojesi.adapter.BesinRecyclerAdapter
import com.yunussevimli.besinprojesi.databinding.FragmentBesinListeBinding
import com.yunussevimli.besinprojesi.viewmodel.BesinListesiViewModel

class BesinListesiFragment : Fragment() {

    private var _binding: FragmentBesinListeBinding? = null
    private val binding get() = _binding!!

    // ViewModel'i tanımlıyoruz. Bu, UI verilerini yönetmek ve yaşam döngüsü değişikliklerine dayanıklı hale getirmek için kullanılır.
    // Fragment'ın yaşam döngüsüne bağlı olarak veri saklama ve geri yükleme işlemlerini kolaylaştırır.
    private lateinit var viewModel : BesinListesiViewModel

    private val recyclerBesinAdapter = BesinRecyclerAdapter(arrayListOf()) // BesinRecyclerAdapter sınıfından bir nesne oluşturuyoruz. Bu nesne, besin listesini gösterecek olan RecyclerView'ın adaptörüdür.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBesinListeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[BesinListesiViewModel::class.java] // ViewModel'i oluşturuyoruz.
        viewModel.refreshData() // ViewModel'deki verileri güncelliyoruz.

        binding.besinListRecycler.layoutManager = LinearLayoutManager(context)
        binding.besinListRecycler.adapter = recyclerBesinAdapter

        binding.swipeRefreshLayout.setOnRefreshListener { // SwipeRefreshLayout ile yenileme işlemi yapılır.
            binding.besinYukleniyor.visibility = View.VISIBLE // Yenileme işlemi başladığında yükleniyor animasyonunu gösterir.
            binding.besinHataMesaji.visibility = View.GONE // Hata mesajını gizler.
            binding.besinListRecycler.visibility = View.GONE // RecyclerView'ı gizler.
            viewModel.refreshFromInternet() // İnternetten verileri yeniler.
            binding.swipeRefreshLayout.isRefreshing = false // Yenileme işlemi bittiğinde animasyonu durdurur.
        }

        observeLiveData() // LiveData'ları gözlemleyen metodu çağırır.
    }

    fun observeLiveData(){

        viewModel.besinler.observe(viewLifecycleOwner) { // Besinler LiveData'sını gözlemleyerek, besin listesini günceller.
            binding.besinListRecycler.visibility = View.VISIBLE
            recyclerBesinAdapter.besinListesiniGuncelle(it) // Besin listesini günceller.
        }

        viewModel.besinHataMesaji.observe(viewLifecycleOwner) { // BesinHataMesaji LiveData'sını gözlemleyerek, hata mesajını gösterir.
            if (it) {
                binding.besinHataMesaji.visibility = View.VISIBLE
                binding.besinListRecycler.visibility = View.GONE
            } else {
                binding.besinHataMesaji.visibility = View.GONE
            }
        }

        viewModel.besinYukleniyor.observe(viewLifecycleOwner) { // BesinYukleniyor LiveData'sını gözlemleyerek, yükleme animasyonunu gösterir.
            if (it) {
                binding.besinListRecycler.visibility = View.GONE
                binding.besinHataMesaji.visibility = View.GONE
                binding.besinYukleniyor.visibility = View.VISIBLE
            } else { // Yükleme işlemi bittiğinde animasyonu durdurur.
                binding.besinYukleniyor.visibility = View.GONE
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}