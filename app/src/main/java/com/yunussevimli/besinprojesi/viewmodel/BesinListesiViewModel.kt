package com.yunussevimli.besinprojesi.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yunussevimli.besinprojesi.model.Besin
import com.yunussevimli.besinprojesi.roomdb.BesinDatabase
import com.yunussevimli.besinprojesi.service.BesinAPIServis
import com.yunussevimli.besinprojesi.util.OzelSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BesinListesiViewModel(application: Application) : AndroidViewModel(application) {
    val besinler = MutableLiveData<List<Besin>>() //LiveData, MutableLiveData'nin üst sınıfıdır. MutableLiveData, LiveData'nın genişletilmiş sürümüdür ve verileri değiştirmek için kullanılır.
    val besinHataMesaji = MutableLiveData<Boolean>()
    val besinYukleniyor = MutableLiveData<Boolean>()
    private var guncellemeZamani = 10 * 60 * 1000 * 1000 * 1000L // 10 dakika

    private val besinApiServis = BesinAPIServis() //Retrofit ile oluşturduğumuz servis sınıfını çağırıyoruz
    private val ozelSharedPreferences = OzelSharedPreferences(getApplication()) //SharedPreferences sınıfını çağırıyoruz

    fun refreshData() { //Verileri yenileme fonksiyonu oluşturuyoruz. Bu fonksiyon, verileri internetten alır ve SQLite'a kaydeder. Ayrıca, 10 dakika içinde uygulama açıldığında verileri SQLite'tan alır. Eğer 10 dakikadan fazla süre geçtiyse, verileri internetten alır.

        val kaydedilmeZamani = ozelSharedPreferences.zamaniAl()
        if (kaydedilmeZamani != null && kaydedilmeZamani != 0L && System.nanoTime() - kaydedilmeZamani < guncellemeZamani){
            //Sqlite'tan çek
            verileriSQLitetanAl()
        } else {
            verileriInternettenAl()
        }
    }

    fun refreshFromInternet(){ //Bu fonksiyon, verileri internetten alır ve SQLite'a kaydeder.
        verileriInternettenAl()
    }

    private fun verileriSQLitetanAl(){ //Bu fonksiyon, verileri SQLite'tan alır ve gösterir.
        besinYukleniyor.value = true

        viewModelScope.launch {

            val besinListesi = BesinDatabase(getApplication()).besinDao().getAllBesin()
            besinleriGoster(besinListesi)
            Toast.makeText(getApplication(),"Besinleri Room'dan Aldık",Toast.LENGTH_LONG).show()
        }
    }

    private fun verileriInternettenAl(){ //Bu fonksiyon, verileri internetten alır ve SQLite'a kaydeder.
        besinYukleniyor.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val besinler = besinApiServis.getData()
            withContext(Dispatchers.Main) {
                besinYukleniyor.value = false
                sqliteSakla(besinler)
                Toast.makeText(getApplication(),"Besinleri Internet'ten Aldık",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun besinleriGoster(besinlerListesi : List<Besin>){ //Bu fonksiyon, verileri gösterir.
        besinler.value = besinlerListesi
        besinHataMesaji.value = false
        besinYukleniyor.value = false
    }

    private fun sqliteSakla(besinListesi: List<Besin>){ //Bu fonksiyon, verileri SQLite'a kaydeder.

        viewModelScope.launch {

            val dao = BesinDatabase(getApplication()).besinDao()
            dao.deleteAllBesin()
            val uuidListesi = dao.insertAll(*besinListesi.toTypedArray())
            var i = 0
            while (i < besinListesi.size){
                besinListesi[i].uuid = uuidListesi[i].toInt()
                i = i + 1
            }
            besinleriGoster(besinListesi)
        }

        ozelSharedPreferences.zamaniKaydet(System.nanoTime())
    }

}