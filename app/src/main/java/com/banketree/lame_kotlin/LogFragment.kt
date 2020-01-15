package com.banketree.lame_kotlin


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import java.util.ArrayList


class LogFragment : Fragment() {

    private val adapter: LogAdapter by lazy { LogAdapter(ArrayList()) }
    var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_log, container, false)
        recyclerView = rootView.findViewById<View>(R.id.logrecyclerview) as RecyclerView
        val llm = LinearLayoutManager(activity)
        recyclerView!!.layoutManager = llm
        recyclerView!!.adapter = adapter

        return rootView
    }

    fun addLog(log: String) {
        adapter.addLog(log)
    }

    private inner class LogAdapter(var logList: MutableList<String>?) :
        RecyclerView.Adapter<LogAdapter.ItemHolder>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ItemHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_log_item, null)
            return ItemHolder(v)
        }

        override fun onBindViewHolder(itemHolder: ItemHolder, i: Int) {
            itemHolder.logText.text = logList!![i]
        }

        override fun getItemCount(): Int {
            return if (null != logList) logList!!.size else 0
        }

        inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
            var logText: TextView = view.findViewById<View>(R.id.text) as TextView
        }

        fun addLog(log: String) {
            logList!!.add(log)
            notifyDataSetChanged()
            recyclerView!!.scrollToPosition(logList!!.size - 1)
        }
    }
}
