package com.example.skingallery

//import android.support.v7.app.AppCompatActivity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray


class MainActivity : AppCompatActivity() {


    lateinit var progress:ProgressBar;
    val res = _servicesData();
    lateinit var recyclerView:RecyclerView;
    lateinit var adapter:RecyclerImg;
    var manager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
    var page = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadShared()

        //  DECORACIONES DEL RECYCLER VIEW
        recyclerView = findViewById<RecyclerView>(R.id.recyclerItem);
        val decoration: SpacesItemDecoration = SpacesItemDecoration(16);
        recyclerView.addItemDecoration(decoration);
        recyclerView.layoutManager = manager;

        val chips = findViewById<ChipGroup>(R.id.chipContainer);
        val filtrar = findViewById<SearchView>(R.id.imgFiltrar);
        val btn = findViewById<Button>(R.id.button);
        val topBar = findViewById<MaterialToolbar>(R.id.topAppBar);
        progress = findViewById<ProgressBar>(R.id.progressBar);

        topBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.barCesta -> {
                    val intento1 = Intent(this, CestaActivity::class.java)
                    startActivity(intento1)
                    true
                }
                else -> false
            }
        }

        chips.setOnCheckedStateChangeListener { group, checkedIds ->
            var filtro = res.getLista();
            if (checkedIds.isNotEmpty()){
                btn.isGone=true;
                filtro = listOf();
                checkedIds.forEach{chipId->
                    val itemchip = group.findViewById<Chip>(chipId);
                     filtro+=res.getLista().filterIndexed { index, imageClass -> imageClass.categoria.contains(itemchip.text.toString()) && !filtro.contains(imageClass)  };
                   // group.clearCheck()
                }
                loadRecycler(filtro);
                return@setOnCheckedStateChangeListener;
            }
            btn.isGone=false;
            page=0;
            loadPage();
            //filtro = res.getLista().filterIndexed { index, imageClass -> listChips.forEach { data->  }  };
        }

        //filtrar.clearFocus();
        filtrar.setOnQueryTextListener (object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {

               if (p0!=null){
                    val filtro = res.getLista().filterIndexed { index, imageClass -> imageClass.referencia.contains(p0,true)  };
                    //val filtro = res.getLista().filterIndexed { index, imageClass -> imageClass.referencia.compareTo(p0,true)  }

                   loadRecycler(filtro)
                    //Toast.makeText(applicationContext,filtro.toString(),Toast.LENGTH_SHORT).show()
                }

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })
        filtrar.setOnSearchClickListener {
            Toast.makeText(this,"onsearch si view",Toast.LENGTH_SHORT).show()
        }

        conex();

        btn.setOnClickListener {
            loadPage()
        }


    }

    fun loadRecycler(xd:List<imageClass>){

        var progressBar = ProgressDialog(this);
        progressBar.setMessage("Cargando");
        progressBar.show();
        adapter = RecyclerImg(xd.toMutableList(),this,0);
        recyclerView.adapter=adapter;
        progressBar.cancel();
    }

    fun conex(){

        var progressBar = ProgressDialog(this);
        progressBar.setMessage("Cargando");
        progressBar.show();

        val gson = Gson();
        val url2="https://marketpandi.000webhostapp.com/data/salidaGallery.php"

        val nuevores = Volley.newRequestQueue(this);
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url2, null,
            { response ->
                val typeToken = object : TypeToken<List<imageClass>>() {}.type
                val imagelist:List<imageClass> = gson.fromJson<List<imageClass>>(response.toString(), typeToken);

                res.listImage=imagelist;

                //loadRecycler(res.listImage);
                loadPage();
                progressBar.cancel();
            },
            { error ->
                Toast.makeText(this," No responde",Toast.LENGTH_SHORT).show();
                progressBar.cancel();
            }
        )
        nuevores.add(jsonArrayRequest);
    }

    fun loadShared(){
        val shared:SharedPreferences = getSharedPreferences("sharedList",Context.MODE_PRIVATE);
        val saveList = shared.getString("data",null)
        val gson = Gson();
        if(saveList!=null){
            val typeToken = object : TypeToken<List<imageClass>>() {}.type
            val imagelist:List<imageClass> = gson.fromJson<List<imageClass>>(saveList, typeToken);
            //Toast.makeText(this,imagelist.toString(),Toast.LENGTH_SHORT).show();
            _servicesData.listCesta=imagelist.toMutableList();
        }




        //Toast.makeText(this,_servicesData.listCesta.toString(),Toast.LENGTH_SHORT).show();
    }

    fun loadPage(){

        val partition = res.getLista().chunked(10);
        if(page<=partition.size){


            if(::adapter.isInitialized && page!=0){
                progress.isGone=false;
                Handler().postDelayed({
                    adapter.addItem(partition[page],page)
                    page++
                    progress.isGone=true;
                },3000)

            }else{
                    adapter = RecyclerImg(partition[page].toMutableList(),this,0);
                    recyclerView.adapter = adapter;
                    page++
            }
            //loading=false

        }
    }

    public override fun onStop() {
        super.onStop()
        val shared:SharedPreferences = getSharedPreferences("sharedList",Context.MODE_PRIVATE);
        val editor:SharedPreferences.Editor = shared.edit();
        val gson = Gson();
        val datjson= gson.toJson(_servicesData.listCesta);
        editor.apply{
            putString("data",datjson.toString())

        }.apply();
      //  Toast.makeText(this,datjson.toString(),Toast.LENGTH_SHORT).show();
    }

}

class SpacesItemDecoration(private val mSpace: Int) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = mSpace
        outRect.right = mSpace
        outRect.bottom = mSpace

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) == 0) outRect.top = mSpace
    }
}