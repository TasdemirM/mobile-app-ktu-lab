package com.example.appmobilegrid.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.appmobilegrid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }
    private var adapter = GridAdapter(emptyList(), null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.gridRecycler.adapter = adapter

        binding.locateButton.setOnClickListener {
            val target = buildTargetMap()
            if (target.isEmpty()) {
                Toast.makeText(this, "Enter at least one RSSI value", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.locate(target)
            }
        }

        viewModel.state.observe(this) { state ->
            binding.progress.visibility = if (state.loading) View.VISIBLE else View.GONE
            binding.errorText.visibility = if (state.error != null) View.VISIBLE else View.GONE
            binding.errorText.text = state.error ?: ""

            val size = state.size
            if (size != null) {
                val width = size.maxX - size.minX + 1
                binding.gridRecycler.layoutManager = GridLayoutManager(this, width.coerceAtLeast(1))

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
    }

    private fun buildTargetMap(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        binding.sensor1Input.text?.toString()?.takeIf { it.isNotBlank() }?.toIntOrNull()
            ?.let { map["wiliboxas1"] = it }
        binding.sensor2Input.text?.toString()?.takeIf { it.isNotBlank() }?.toIntOrNull()
            ?.let { map["wiliboxas2"] = it }
        binding.sensor3Input.text?.toString()?.takeIf { it.isNotBlank() }?.toIntOrNull()
            ?.let { map["wiliboxas3"] = it }
        return map
    }
}
