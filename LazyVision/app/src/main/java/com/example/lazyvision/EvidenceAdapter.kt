package com.example.lazyvision


import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class EvidenceAdapter(val evidenceList: ArrayList<Evidence>) : RecyclerView.Adapter<EvidenceAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val label = itemView.findViewById<TextView>(R.id.label)
        val img = itemView.findViewById<ImageView>(R.id.imgview)
        val result = itemView.findViewById<TextView>(R.id.result)
        val context = itemView.getContext();
        val generate = itemView.findViewById<Button>(R.id.generate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.evidence_cardview, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return evidenceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evidence: Evidence = evidenceList.get(position)
        holder.label.text = evidence.label
        holder.result.text = evidence.result
        val storage = Firebase.storage
        val url = storage.getReferenceFromUrl(evidence.imgurl)
        var path: String?
        GlideApp.with(holder.context)
            .load(url)
            .placeholder(R.drawable.lazy)
            .into(holder.img)
        holder.generate.setOnClickListener({
                path = pdfCreator(holder, evidence.result)
                if(!path.isNullOrBlank()){

                    val file = File(path)
                    val uri: Uri
                    uri = if (Build.VERSION.SDK_INT < 24) {
                        Uri.fromFile(file)
                    } else {
                        Uri.parse(file.path) // My work-around for new SDKs, doesn't work in Android 10.
                    }
                    val viewFile = Intent(Intent.ACTION_VIEW)
                    viewFile.setDataAndType(uri, "application/pdf")
                    holder.context.startActivity(viewFile)
                }

        })

    }

    private fun pdfCreator(holder: ViewHolder, sometext: String): String? {

        val document = PdfDocument()
        val pageInfo = PageInfo.Builder(400, 300, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val width = canvas.width
        val height = canvas.height

        val paint = Paint()

        val mBackground = BitmapFactory.decodeResource(holder.context.resources,R.drawable.lazy)
        val logoRect = Rect(150, 0, 250, 100)
        canvas.drawBitmap(mBackground, null, logoRect, null)

        val image:Bitmap = holder.img.getDrawable().toBitmap()
        val imgRect = Rect(40, 100, 160, 280)
        canvas.drawBitmap(image, null, imgRect, null)

        paint.setColor(Color.BLACK)
        val list = sometext.split("\n")
        var y = 105F
        paint.textSize = 8F

        for (lines in list){
            canvas.drawText(lines, 200F, y, paint)
            y+=10F;
        }

        document.finishPage(page)

        val path = "/storage/emulated/0/Android/media/com.example.lazyvision/${System.currentTimeMillis()}.pdf"
        val file = File(path)
        val fos = FileOutputStream(file)

        var saved = true

        try {
            document.writeTo(fos)
        } catch (e: IOException) {
            return null
        }

        document.close()
        return path
    }
}