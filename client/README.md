# Part 1 Client Implementation Guide

This guide gives you an end-to-end blueprint to finish Part 1: build the mobile client that fetches the Wi-Fi map, shows the grid, and locates a user with nearest neighbor.

## Prereqs
- Android Studio (Kotlin)
- Backend running: `MapService-1.0/MapService-1.0/bin/MapService.bat` (port 9000)
- Emulator base URL: `http://10.0.2.2:9000/` (host: `http://localhost:9000/`)

## Project setup (Android)
1. New project › Empty Activity › Kotlin.
2. In `app/build.gradle` add:
```gradle
implementation "com.squareup.retrofit2:retrofit:2.11.0"
implementation "com.squareup.retrofit2:converter-moshi:2.11.0" // or gson
implementation "com.squareup.okhttp3:logging-interceptor:4.12.0"
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1"
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6"
implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.8.6"
```
3. Sync the project.

## Data models (adjust keys if API differs)
```kotlin
data class MapSize(val minX: Int, val minY: Int, val maxX: Int, val maxY: Int)

data class MapCellStrength(
    val x: Int,
    val y: Int,
    val sensor: String,
    val strength: Int
)

data class MapCellWilibox(
    val x: Int,
    val y: Int,
    val strength1: Int,
    val strength2: Int,
    val strength3: Int
)
```

## Retrofit API
```kotlin
interface MapServiceApi {
    @GET("/size")
    suspend fun getSize(): MapSize

    @GET("/column")
    suspend fun getColumn(@Query("x") x: Int): List<MapCellStrength>

    @GET("/wilibox-column")
    suspend fun getWiliboxColumn(@Query("x") x: Int): List<MapCellWilibox>
}

fun provideApi(): MapServiceApi {
    val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    val client = OkHttpClient.Builder().addInterceptor(logging).build()
    return Retrofit.Builder()
        .baseUrl("http://10.0.2.2:9000/") // emulator -> host loopback
        .addConverterFactory(MoshiConverterFactory.create())
        .client(client)
        .build()
        .create(MapServiceApi::class.java)
}
```

## Repository (fetch grid)
```kotlin
class MapRepository(private val api: MapServiceApi) {
    suspend fun loadGrid(): Pair<MapSize, Map<Pair<Int,Int>, Map<String, Int>>> {
        val size = api.getSize()
        val grid = mutableMapOf<Pair<Int,Int>, MutableMap<String, Int>>()
        for (x in size.minX..size.maxX) {
            val col = api.getColumn(x)
            for (cell in col) {
                val key = cell.x to cell.y
                val sensors = grid.getOrPut(key) { mutableMapOf() }
                sensors[cell.sensor] = cell.strength
            }
        }
        return size to grid
    }
}
```

## Nearest-neighbor helper
```kotlin
object NearestNeighbor {
    fun findClosest(
        grid: Map<Pair<Int,Int>, Map<String, Int>>,
        target: Map<String, Int>
    ): Pair<Int,Int>? {
        var best: Pair<Int,Int>? = null
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
            val cVal = cell[sensor] ?: continue // skip missing
            val diff = (tVal - cVal).toDouble()
            sum += diff * diff
            count++
        }
        return if (count == 0) Double.MAX_VALUE else kotlin.math.sqrt(sum)
    }
}
```

## ViewModel sketch
```kotlin
class MainViewModel(private val repo: MapRepository) : ViewModel() {
    data class UiState(
        val loading: Boolean = false,
        val error: String? = null,
        val size: MapSize? = null,
        val grid: Map<Pair<Int,Int>, Map<String, Int>> = emptyMap(),
        val located: Pair<Int,Int>? = null
    )

    private val _state = MutableLiveData(UiState())
    val state: LiveData<UiState> = _state

    fun load() {
        _state.value = _state.value?.copy(loading = true, error = null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val (size, grid) = repo.loadGrid()
                _state.postValue(_state.value?.copy(loading = false, size = size, grid = grid))
            } catch (e: Exception) {
                _state.postValue(_state.value?.copy(loading = false, error = e.message))
            }
        }
    }

    fun locate(targetRssi: Map<String, Int>) {
        val grid = _state.value?.grid ?: return
        val best = NearestNeighbor.findClosest(grid, targetRssi)
        _state.postValue(_state.value?.copy(located = best))
    }
}
```

## UI outline
- Show loading/error states.
- Grid view: RecyclerView with GridLayoutManager (spanCount = width from `size.maxX - size.minX + 1`). Color cells that have data; highlight `located` cell.
- Detail panel: RSSI list for selected cell.
- Input form: sensor/RSSI pairs + MAC field; on submit, build `Map<String,Int>` and call `locate()`; highlight result.

## Running for demo/defense
1. Start backend: `MapService-1.0/MapService-1.0/bin/MapService.bat`; keep it open.
2. Launch emulator; ensure base URL is `http://10.0.2.2:9000/` in Retrofit.
3. Run the app. The grid should load; enter MAC RSSI to locate and see the highlighted cell.
4. If backend stops, you’ll see errors—restart the bat script and retry.

## Talking points
- We reused supplied backend + DB; API calls fetch map size and per-column RSSI.
- Built a grid in memory and applied 1-nearest-neighbor over RSS vectors to locate a user.
- Emulators use `10.0.2.2` to reach host services; host uses `localhost`.
- UI shows grid status, location highlight, and handles loading/errors gracefully.
