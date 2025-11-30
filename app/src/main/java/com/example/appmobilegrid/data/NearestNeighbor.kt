package com.example.appmobilegrid.data

object NearestNeighbor {
    fun findClosest(
        grid: Map<Pair<Int, Int>, Map<String, Int>>,
        target: Map<String, Int>
    ): Pair<Int, Int>? {
        var best: Pair<Int, Int>? = null
        var bestDist = Double.MAX_VALUE
        for ((coord, sensors) in grid) {
            val d = distance(sensors, target)
            if (d < bestDist) {
                bestDist = d
                best = coord
            }
        }
        return best
    }

    private fun distance(cell: Map<String, Int>, target: Map<String, Int>): Double {
        var sum = 0.0
        var count = 0
        for ((sensor, tVal) in target) {
            val cVal = cell[sensor] ?: continue
            val diff = (tVal - cVal).toDouble()
            sum += diff * diff
            count++
        }
        return if (count == 0) Double.MAX_VALUE else kotlin.math.sqrt(sum)
    }
}
