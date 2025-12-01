package com.example.appmobilegrid.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobilegrid.R

class ManualEntriesAdapter(
    private var items: List<MainViewModel.ManualEntry>
) : RecyclerView.Adapter<ManualEntriesAdapter.EntryViewHolder>() {

    // Replace entire list; simple UI so no diff util needed.
    fun update(newItems: List<MainViewModel.ManualEntry>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manual_entry, parent, false)
        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.entryTitle)
        private val detail: TextView = itemView.findViewById(R.id.entryDetail)
        fun bind(entry: MainViewModel.ManualEntry) {
            val locText = entry.located?.let { "(${it.first},${it.second})" } ?: "N/A"
            title.text = "Entry #${entry.id} → $locText"
            val rssiText = entry.inputs.entries.joinToString(", ") { "${it.key}=${it.value}" }
            detail.text = "RSSI: $rssiText • dist=${"%.2f".format(entry.distance)}"
        }
    }
}
