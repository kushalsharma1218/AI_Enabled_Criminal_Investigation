package com.example.lazyvision

import android.util.Log
import com.google.firebase.database.*
import java.util.*

object Item: Observable() {
    private var m_valueDataListener: ValueEventListener? = null     // The data listener that gets the data from the database
    private var m_PersonList: ArrayList<TextToFireBaseBeanClass>? = ArrayList()
    private fun getDatabaseRef(): DatabaseReference? {
        val path="caseno/c"+101+"/evidence"

        return FirebaseDatabase.getInstance().reference.child(path)
    }
    init {
        if (m_valueDataListener != null) {

            getDatabaseRef()?.removeEventListener(m_valueDataListener!!)
        }
        m_valueDataListener = null
        Log.e("PersonModel", "dataInit line 27")


        m_valueDataListener = object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    Log.i("Item", "data updated line 28")
                    val data: ArrayList<TextToFireBaseBeanClass> = ArrayList()
                    if (dataSnapshot != null) {
                        for (snapshot: DataSnapshot in dataSnapshot.children) {
                            try {
                                data.add(TextToFireBaseBeanClass(snapshot,"hello"))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        m_PersonList = data
                        Log.i(
                            "PersonModel",
                            "data updated there are " + m_PersonList!!.size + " Person in the list"
                        )
                        setChanged()
                        notifyObservers()
                    } else {
                        throw Exception("data snapshot is null line 31")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                if (p0 != null) {
                    Log.i("PersonModel", "line 33 Data update cancelled, err = ${p0.message}, detail = ${p0.details}")
                }
            }

        }
        getDatabaseRef()?.addValueEventListener(m_valueDataListener as ValueEventListener)
    }

    fun getData(): ArrayList<TextToFireBaseBeanClass>? {
        return m_PersonList
    }


}