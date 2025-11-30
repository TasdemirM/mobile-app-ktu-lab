package com.example.appmobilegrid.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobilegrid.R

class GridAdapter(
    private var cells: List<Cell>,
    private var located: Pair<Int, Int>? = null
) : RecyclerView.Adapter<GridAdapter.CellViewHolder>() {

    data class Cell(val x: Int, val y: Int, val hasData: Boolean)

    fun update(newCells: List<Cell>, located: Pair<Int, Int>?) {
        this.cells = newCells
        this.located = located
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cell, parent, false)
        return CellViewHolder(view)
    }

    override fun onBindViewHolder(holder: CellViewHolder, position: Int) {
        val cell = cells[position]
        holder.bind(cell, cell.x to cell.y == located)
    }

    override fun getItemCount(): Int = cells.size

    class CellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text: TextView = itemView.findViewById(R.id.cellText)
        fun bind(cell: Cell, isLocated: Boolean) {
            text.text = "${cell.x},${cell.y}"
            val bgColor = when {
                isLocated -> Color.YELLOW
                cell.hasData -> Color.parseColor("#A5D6A7") // light green for data
                else -> Color.LTGRAY
            }
            text.setBackgroundColor(bgColor)
        }
    }
}
