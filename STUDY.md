# STUDY.md

## What this lab is about
- Goal: Build a mobile app that locates Wi-Fi users indoors using a prepared WLAN signal-strength map (RSSI) and nearest-neighbor search.
- Data source: Remote MySQL database (seklys.ila.lt, DB `LDB`, user `stud`, pass `vLXCDmSG6EpEnhXX`). Tables: `matavimai` (grid points x,y), `stiprumai` (RSSI per sensor), `vartotojai` (user MAC readings).
- Provided backend: `MapService-1.0` (Kotlin/http4k) exposes HTTP API endpoints backed by the DB:
  - `GET /size` › map bounds (`minX`, `minY`, `maxX`, `maxY`)
  - `GET /column?x=N` › `{x,y,sensor,strength}` list for each grid cell in column N
  - `GET /wilibox-column?x=N` › aggregated `{x,y,strength1,strength2,strength3}`
- Deliverable: Android app that fetches the map, renders the grid, and locates a user via nearest neighbor (using sensor›RSSI vectors). User can input RSSI values (by MAC/AP) to locate.

## Requirements (from TASK.md / assignment)
- Fetch map data from the provided backend/DB.
- Render the measurement grid; show which cells have readings and highlight computed location.
- Implement nearest-neighbor (K=1) over RSSI vectors to locate a user (sensor alignment, distance metric).
- Allow entering arbitrary MAC/RSSI data and locate it.
- Handle missing data/network gracefully (no crashes).
- For emulator: use `10.0.2.2` to reach host backend.

## How we solved it (code overview)
- Android project (Kotlin, single-activity, Views): `com.example.appmobilegrid`.
- Dependencies: Retrofit + Moshi (JSON), OkHttp logging, coroutines, lifecycle (ViewModel/LiveData), RecyclerView, viewBinding.
- Network setup: base URL `http://10.0.2.2:9000/`; cleartext enabled and INTERNET permission granted.
- API layer: `MapServiceApi` with `getSize()`, `getColumn(x)`, `getWiliboxColumn(x)`; models `MapSize`, `MapCellStrength`, `MapCellWilibox`.
- Repository: loads size, loops columns to build `Map<(x,y), sensor->RSSI>`.
- Nearest-neighbor helper: Euclidean distance over shared sensors; returns closest cell.
- ViewModel: manages UI state (loading/error/data/located), runs load in IO coroutine, locate updates highlight.
- UI: RecyclerView grid (span = width), cells colored if data; highlight nearest cell (yellow). Simple form with three RSSI fields (wiliboxas1/2/3) + Locate button. Loading spinner, error text.

## Run instructions (from zero to working)
1) **Start backend**
   - Open terminal in project root.
   - Run: `MapService-1.0/MapService-1.0/bin/MapService.bat` (Windows). Keep it open. Port 9000.
   - Test (host): `http://localhost:9000/size`.

2) **Android app setup (first time)**
   - Open the project in Android Studio.
   - Ensure dependencies sync (Gradle sync).
   - Base URL is set to `http://10.0.2.2:9000/` for emulator; no change needed.
   - Permissions: INTERNET and cleartext enabled in manifest (already set).

3) **Run on emulator**
   - Create/Cold boot an AVD (API 26+). Device Manager › Create/Cold Boot.
   - Click Run (?). The app installs and launches.
   - Wait for grid to load (backend must be running). If network error, backend may be down.
   - Enter RSSI numbers for `wiliboxas1`, `wiliboxas2`, `wiliboxas3` and tap **Locate**. The nearest grid cell highlights yellow.

4) **If ADB/emulator misbehaves**
   - Restart ADB: `& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" kill-server; & "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" start-server`
   - Cold boot or recreate the AVD.
   - Uninstall old app if needed: `& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" uninstall com.example.appmobilegrid`

## Key files
- Manifest: `app/src/main/AndroidManifest.xml` (INTERNET, cleartext, activity wiring)
- Gradle deps: `app/build.gradle.kts`
- API: `app/src/main/java/com/example/appmobilegrid/api/MapServiceApi.kt`, `Models.kt`, `Network.kt`
- Data: `app/src/main/java/com/example/appmobilegrid/data/MapRepository.kt`, `NearestNeighbor.kt`
- ViewModel/UI: `app/src/main/java/com/example/appmobilegrid/ui/MainViewModel.kt`, `MainViewModelFactory.kt`, `MainActivity.kt`, `GridAdapter.kt`
- Layouts: `app/src/main/res/layout/activity_main.xml`, `item_cell.xml`

## Talking points for defense
- Backend supplied; we run MapService to expose the DB via HTTP (`/size`, `/column`, `/wilibox-column`).
- Client uses Retrofit + Moshi to fetch map bounds and per-column RSSI; builds an in-memory grid keyed by (x,y) with sensor strengths.
- Nearest-neighbor over RSSI vectors (aligned by sensor) finds the closest training point; UI highlights that cell.
- Emulator uses `10.0.2.2` to reach host; cleartext allowed for local HTTP; INTERNET permission required.
- Error handling: loading spinner, error text, no crashes on network failure.

## Quick demo script
1) Start backend (`MapService.bat`).
2) In Android Studio, Run on emulator.
3) Confirm grid appears.
4) Enter sample RSSI: wiliboxas1=20, wiliboxas2=15, wiliboxas3=10 › tap Locate › see highlighted cell.
## Key files (extended)
- Android
  - Manifest: `app/src/main/AndroidManifest.xml` (permissions, cleartext, main activity)
  - Build config: `app/build.gradle.kts` (deps: Retrofit/Moshi, coroutines, lifecycle, RecyclerView, Material, viewBinding)
  - API layer: `api/Models.kt`, `api/MapServiceApi.kt` (HTTP endpoints), `api/Network.kt` (Retrofit client)
  - Data/logic: `data/MapRepository.kt` (fetch map/build grid), `data/NearestNeighbor.kt` (1-NN over RSSI)
  - UI/state: `ui/MainViewModel.kt` (state + load/locate), `ui/MainViewModelFactory.kt`, `ui/MainActivity.kt` (Grid/Add/History sections), `ui/GridAdapter.kt` (grid cells), `ui/ManualEntriesAdapter.kt` (history list)
  - Layouts: `res/layout/activity_main.xml` (toolbar, cards, grid, form, history, bottom nav), `item_cell.xml`, `item_manual_entry.xml`
- Part1 (Python)
  - `config.py` (DB creds), `db.py` (fetch matavimai/stiprumai/vartotojai), `grid.py` (grid assembly), `nn.py` (nearest neighbor), `viz.py` (matplotlib), `main.py` (Typer CLI: fetch-grid, locate, show-grid), `requirements.txt`, `README.md`/`STUDY.md` (setup/notes)
