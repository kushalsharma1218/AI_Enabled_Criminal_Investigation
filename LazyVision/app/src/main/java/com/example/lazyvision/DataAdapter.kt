package com.example.lazyvision

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.ThrowOnExtraProperties
import com.squareup.picasso.Picasso
import org.jetbrains.anko.find

class DataAdapter (context:Context,resource: Int ,list : ArrayList<TextToFireBaseBeanClass>): ArrayAdapter<TextToFireBaseBeanClass>(context,resource,list) {

    private var mResource: Int = 0
    private lateinit var mList: ArrayList<TextToFireBaseBeanClass>
    private lateinit var mLayoutInflator: LayoutInflater
    private var mContext: Context = context

    init {
        this.mResource = resource
        this.mList = list
        this.mLayoutInflator =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val returnView: View?
        if (convertView == null) {
            returnView = try {
                mLayoutInflator.inflate(mResource, null)
            } catch (e: Exception) {
                e.printStackTrace()
                View(context)
            }
            setUI(returnView, position)
            return returnView
        }
        setUI(convertView, position)
        return convertView
    }

    private fun setUI(view: View,position: Int)
    {
        val data: TextToFireBaseBeanClass? =if(count > position) getItem(position) else null
        val text:TextView?=view.findViewById(R.id.listview)
        text?.text=data?.textgenerated
        val imgview:ImageView?=view.findViewById(R.id.imageView)
        val url=data?.imageUrl
        Picasso.get().load(url).into(imgview);
    }

}