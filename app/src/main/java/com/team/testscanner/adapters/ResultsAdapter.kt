package com.team.testscanner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.team.testscanner.R
import com.team.testscanner.models.Quiz

class ResultsAdapter(private val quiz:MutableList<Quiz>):RecyclerView.Adapter<ResultsAdapter.MyViewHolder>() {


    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val textTitle: TextView =itemView.findViewById(R.id.test_title_text)
        val textDesc: TextView =itemView.findViewById(R.id.test_title_desc)
        val resultsButton : Button = itemView.findViewById(R.id.result_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.results_recyclerview,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem=quiz[position]
        holder.textTitle.text=currentItem.title
        holder.textDesc.text=currentItem.id
    }

    override fun getItemCount(): Int {
        return quiz.size
    }
}