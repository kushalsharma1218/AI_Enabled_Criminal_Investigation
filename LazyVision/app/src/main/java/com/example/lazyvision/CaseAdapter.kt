package com.example.lazyvision

import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.createDeviceProtectedStorageContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlin.coroutines.coroutineContext

class CaseAdapter(val caseList: ArrayList<Case>) : RecyclerView.Adapter<CaseAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cno = itemView.findViewById<TextView>(R.id.cno)
        val ioff = itemView.findViewById<TextView>(R.id.ioff)
        val status = itemView.findViewById<TextView>(R.id.status)
        val context = itemView.getContext();
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.case_cardview, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return caseList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val case: Case = caseList.get(position)
        holder.cno.text = case.cno
        holder.ioff.text = case.ioff
        holder.status.text = case.status

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.context, Dashboard::class.java)
            intent.putExtra("caseno",case.cno)
            holder.context.startActivity(intent)
        }
    }
}