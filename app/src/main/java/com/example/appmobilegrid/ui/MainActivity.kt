package com.example.appmobilegrid.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appmobilegrid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }
    private var adapter = GridAdapter(emptyList(), null)
    private var manualAdapter = ManualEntriesAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Grid uses a staggered column count based on map width (set in observer).
        binding.gridRecycler.adapter = adapter
        binding.manualRecycler.layoutManager = LinearLayoutManager(this)
        binding.manualRecycler.adapter = manualAdapter

        // Bottom nav buttons swap visible section.
        binding.locateButton.setOnClickListener {
            val target = buildTargetMap()
            if (target.isEmpty()) {
                Toast.makeText(this, "Enter at least one RSSI value", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.locate(target)
            }
        }

        binding.navGrid.setOnClickListener { showSection(Section.GRID) }
        binding.navAdd.setOnClickListener { showSection(Section.ADD) }
        binding.navList.setOnClickListener { showSection(Section.LIST) }

        viewModel.state.observe(this) { state ->
            // Loading / error state
            binding.progress.visibility = if (state.loading) View.VISIBLE else View.GONE
            binding.errorText.visibility = if (state.error != null) View.VISIBLE else View.GONE
            binding.errorText.text = state.error ?: ""
            binding.locatedText.text = when (val loc = state.located) {
                null -> "Location: waiting..."
                else -> "Location: (${loc.first}, ${loc.second})"
            }
            manualAdapter.update(state.manualEntries)

            val size = state.size
            if (size != null) {
                val width = size.maxX - size.minX + 1
                binding.gridRecycler.layoutManager = GridLayoutManager(this, width.coerceAtLeast(1))

                // Build cell list for adapter; hasData marks measured cells.
                val cells = mutableListOf<GridAdapter.Cell>()
                for (y in size.minY..size.maxY) {
                    for (x in size.minX..size.maxX) {
                        val hasData = state.grid.containsKey(x to y)
                        cells.add(GridAdapter.Cell(x, y, hasData))
                    }
                }
                adapter.update(cells, state.located)
            }
        }

        viewModel.load()
        showSection(Section.GRID)
    }

    private fun buildTargetMap(): Map<String, Int> {
        // Gather manual RSSI inputs into sensor->value map.
        val map = mutableMapOf<String, Int>()
        binding.sensor1Input.text?.toString()?.takeIf { it.isNotBlank() }?.toIntOrNull()
            ?.let { map["wiliboxas1"] = it }
        binding.sensor2Input.text?.toString()?.takeIf { it.isNotBlank() }?.toIntOrNull()
            ?.let { map["wiliboxas2"] = it }
        binding.sensor3Input.text?.toString()?.takeIf { it.isNotBlank() }?.toIntOrNull()
            ?.let { map["wiliboxas3"] = it }
        return map
    }

    private enum class Section { GRID, ADD, LIST }

    private fun showSection(section: Section) {
        binding.gridRecycler.visibility = if (section == Section.GRID) View.VISIBLE else View.GONE
        binding.statusCard.visibility = if (section == Section.GRID) View.VISIBLE else View.GONE
        binding.inputGroup.visibility = if (section == Section.ADD) View.VISIBLE else View.GONE
        binding.manualRecycler.visibility = if (section == Section.LIST) View.VISIBLE else View.GONE
        binding.listTitle.visibility = if (section == Section.LIST) View.VISIBLE else View.GONE
    }
}
