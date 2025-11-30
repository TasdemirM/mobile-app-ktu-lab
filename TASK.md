# TASK.md

## Goal
Build Part 1 mobile client that fetches Wi-Fi map data from the provided MapService/DB, renders the measurement grid, and locates users via nearest-neighbor.

## What you already have
- `MapService-1.0/` runnable HTTP backend (http://localhost:9000) using DB `LDB` on `seklys.ila.lt` (user `stud`, pass `vLXCDmSG6EpEnhXX`).
- Docs: `Part1_Description.pdf`, `Part2_Description.pdf`, `NNSS_MethodTheory-EN.pdf`, `NNSSskaiciavimai-EN-for-students (5).xls`.

## Success criteria (Part 1)
- Can fetch map data (size + columns) from the backend/DB without errors.
- Grid UI shows which cells have measurements; highlights the computed location.
- Nearest-neighbor locates a user from existing MAC data or user-entered MAC+RSS.
- Handles missing data/network failures with clear UI (no crashes).

## Step-by-step
1) **Run backend**: `MapService-1.0/MapService-1.0/bin/MapService.bat`; verify `http://localhost:9000/size`, `/column?x=1`, `/wilibox-column?x=1`.
2) **Create Android project (Kotlin, single activity)**.
3) **Add deps**: Retrofit + Moshi, coroutines, ViewModel + LiveData/Flow (Room later for Part 2), OkHttp logging.
4) **Define models & Retrofit API**:
   - Data classes for `/size`, `/column`, `/wilibox-column` responses.
   - `MapServiceApi` with `getSize()`, `getColumn(x)`, `getWiliboxColumn(x=2)`; base URL `http://10.0.2.2:9000/` (emulator).
5) **ViewModel data load**:
   - Fetch size; loop columns to build grid: cell (x,y) -> sensor->RSS.
   - Expose LiveData/Flow state (loading, error, data).
6) **Nearest-neighbor**:
   - Build RSS vectors per cell; for target MAC vector, compute distance (e.g., Euclidean over shared sensors; skip/penalize missing); pick closest cell.
7) **UI**:
   - Grid display (RecyclerView GridLayoutManager or Compose grid); color cells with data; highlight nearest cell.
   - Show RSS details on tap; simple form to enter MAC with sensor/RSS pairs.
8) **User input flow**: Accept MAC + RSS list, run nearest-neighbor, display location (DB insert optional; keep local if no API endpoint).
9) **Error/empty states**: Loading indicator, retry on failure, message if map not available yet.
10) **Test**: Confirm API calls, grid matches data, nearest-neighbor returns plausible cell for sample MAC/vector, UI stable on rotation.

## Notes
- Retrofit: HTTP client that turns annotated interfaces into suspend functions returning typed models; use Moshi/Gson for JSON.
- Use `viewModelScope.launch(Dispatchers.IO)` for network; post results to LiveData/Flow for the UI.
- For Part 2, add Room caching and Navigation/Material polish.
