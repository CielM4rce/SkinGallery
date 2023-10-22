package com.example.skingallery

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.google.android.material.floatingactionbutton.FloatingActionButton

@GlideModule

class RecyclerImg(private var listImg: MutableList<imageClass> ,var context: Context, var card: Int) : RecyclerView.Adapter<RecyclerImg.ViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        lateinit var cardHolder:ViewHolder

        when (card){
            0-> {cardHolder= ViewHolder(layoutInflater.inflate(R.layout.card_img,parent,false));}
            1-> {cardHolder= ViewHolder(layoutInflater.inflate(R.layout.card_img_cesta,parent,false));}
        }

        return cardHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listImg[position];
        holder.bind(item,context,card);
    }

    override fun getItemCount(): Int {
        return listImg.size;
    }

    fun deleteItem(adapterPosition: Int) {
        listImg.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition);
        

    }

    fun addItem(list: List<imageClass>,page:Int) {



        listImg.addAll(list)
        //notifyItemRangeInserted(10,19);
        //notifyDataSetChanged()
        notifyItemRangeInserted(page*10,10)

    }

    class ViewHolder(var view: View):RecyclerView.ViewHolder(view){


        fun bind(imgItem: imageClass, context: Context, card: Int) {
            when(card){
                0->{bindCardList(imgItem, context)}
                1->{bindCardCesta(imgItem, context)}
            }
        }

        private fun bindCardCesta(imgItem: imageClass, context: Context) {
            val img: ImageView  = view.findViewById<ImageView>(R.id.imageView2);
            val txtReferencia   = view.findViewById<TextView>(R.id.textReferencia);
            val txtCantidad   = view.findViewById<TextView>(R.id.textCantidad);
            Glide.with(context).load(imgItem.url).into(img);
            txtReferencia.setText(imgItem.referencia);
            txtCantidad.setText("X"+imgItem.cantidad);
        }

        private fun bindCardList(imgItem: imageClass, context: Context) {
            val img: ImageView = view.findViewById<ImageView>(R.id.imageView);
            val btn: FloatingActionButton = view.findViewById<FloatingActionButton>(R.id.btnAgregarImg);

            Glide.with(context).load(imgItem.url).into(img);
            btn.setOnClickListener {
                val exist = _servicesData.listCesta.filter { it.cod == imgItem.cod }
               if(exist.isNotEmpty()){
                //if(_servicesData.listCesta.contains(imgItem)){
                   _servicesData.listCesta.filter { it.cod == imgItem.cod }
                    val indice= _servicesData.listCesta.indexOf(exist[0])
                    _servicesData.listCesta[indice].cantidad+=1;
                    Toast.makeText(context,imgItem.referencia+" +1",Toast.LENGTH_SHORT).show();
                };
                else {
                    imgItem.cantidad=1;
                    _servicesData.listCesta+=imgItem;
                    Toast.makeText(context,"Agregado "+imgItem.referencia,Toast.LENGTH_SHORT).show()
                    //val shared : SharedPreferences = Application.getSharedPreferences("sharedList",Context.MODE_PRIVATE)
                }
            }
        }
    }
}

