package com.example.skingallery

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import java.math.RoundingMode
import java.text.DecimalFormat


class CestaActivity : AppCompatActivity() {

    lateinit var recyclerView:RecyclerView;

    lateinit var topBar:MaterialToolbar;
    lateinit var barBtn:Button;
    lateinit var barText:TextView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cesta)

        topBar  = findViewById<MaterialToolbar>(R.id.topBar);
        barBtn = findViewById<Button>(R.id.appBarBtn);
        barText = findViewById<TextView>(R.id.appBarTxt);
        recyclerView=findViewById(R.id.recyclerCesta);

        val decoration: SpacesItemDecoration = SpacesItemDecoration(16);
        recyclerView.addItemDecoration(decoration);

        loadRecycler();
        loadCal();

        barBtn.setOnClickListener{
            val msj = loadCal();
            if (_servicesData.listCesta.isNotEmpty()){

                val numeroTel = "+51994091623"
                val intent = Intent(Intent.ACTION_VIEW)
                val uri = "whatsapp://send?phone=$numeroTel&text=$msj"
                intent.data = Uri.parse(uri)
                startActivity(intent)
                _servicesData.listCesta.clear()
                loadRecycler()
            }else Toast.makeText(this,"Lista vacia: agregre productos",Toast.LENGTH_SHORT).show();
        }


       //val swipeItem = ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT || ItemTouchHelper.RIGHT)


    }
    fun loadRecycler(){
        recyclerView.layoutManager=GridLayoutManager(this,1)

        //Toast.makeText(this,_servicesData.listCesta.toString(),Toast.LENGTH_SHORT).show();
        val adapter=RecyclerImg(_servicesData.listCesta,this,1);
        recyclerView.adapter= adapter;

        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                   // _servicesData.listCesta.removeAt(viewHolder.adapterPosition)
                    adapter.deleteItem(viewHolder.adapterPosition)
                    loadCal();
                    //_servicesData.listCesta.removeAt(viewHolder.adapterPosition)
                    //adapter.notifyItemRemoved(viewHolder.adapterPosition)
                }
            })

        mIth.attachToRecyclerView(recyclerView);
    }
    fun loadCal():String{

        var msn:String ="Lista de pedido:\n"
        _servicesData.listCesta.forEach { Unit-> msn+=Unit.cantidad.toString()+" cod:"+Unit.cod+" "+Unit.referencia+"\n"}
        var suma = _servicesData.listCesta.sumOf { it.cantidad }
        topBar.setTitle("Cesta("+suma+")")
        barBtn.setText("Pedido("+suma+")")


        val formatDecimal = DecimalFormat("0.00");
        formatDecimal.roundingMode =RoundingMode.DOWN;
        val total = formatDecimal.format(suma*5.50)
        barText.setText("s/.$total")
        msn+="\nUnidades: $suma  Total: s/.$total"

        //Toast.makeText(this,msn,Toast.LENGTH_SHORT).show();
        return msn
    }

    public override fun onStop() {
        super.onStop()
        val shared: SharedPreferences = getSharedPreferences("sharedList", Context.MODE_PRIVATE);
        val editor: SharedPreferences.Editor = shared.edit();
        val gson = Gson();
        val datjson= gson.toJson(_servicesData.listCesta);
        editor.apply{
            putString("data",datjson.toString())

        }.apply();
        //  Toast.makeText(this,datjson.toString(),Toast.LENGTH_SHORT).show();
    }
}

