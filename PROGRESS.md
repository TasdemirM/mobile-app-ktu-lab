# PROGRESS.md

## Current state (backend)
- MapService is provided in `MapService-1.0/MapService-1.0` and is runnable via `bin/MapService.bat` (Windows) or `bin/MapService` (*nix).
- Verified running locally on port 9000. Endpoints confirmed:
  - `GET /size` › map bounds JSON (e.g., `{"minX":-5,"minY":-11,"maxX":6,"maxY":35}`)
  - `GET /column?x=N` › JSON list of `{x,y,sensor,strength}` (per access point)
  - `GET /wilibox-column?x=N` › JSON list of `{x,y,strength1,strength2,strength3}` (aggregated)
- The service uses baked-in DB creds to `seklys.ila.lt` (`stud` / `vLXCDmSG6EpEnhXX`); no extra config needed.

## How to run the backend (for defense/demo)
1) Open a terminal in the project root.
2) Run `MapService-1.0/MapService-1.0/bin/MapService.bat` (Windows). Leave the window open while testing.
   - To stop: close that window or kill the process (see below).
3) Test endpoints (host):
   - `http://localhost:9000/size`
   - `http://localhost:9000/column?x=0`
   - `http://localhost:9000/wilibox-column?x=0`
   From an Android emulator use `http://10.0.2.2:9000/...`.
4) If the process is already running, you’ll see port 9000 listening. To check: `netstat -ano | findstr 9000`. To stop: `taskkill /PID <pid> /F`.

## What we achieved so far
- Identified and started the provided backend service; confirmed DB connectivity and API responses.
- Documented working endpoints and how to reach them from host and emulator.

## Next steps toward finishing Part 1
- Create Android project (Kotlin, single activity).
- Add dependencies: Retrofit + Moshi/Gson, coroutines, ViewModel + LiveData/Flow, OkHttp logging (Room later).
- Define API models and Retrofit interface for `/size`, `/column`, `/wilibox-column` (base URL `http://10.0.2.2:9000/` in emulator).
- ViewModel: fetch size, loop columns to build grid data structure.
- Implement nearest-neighbor on RSS vectors; expose result to UI.
- UI: grid visualization (mark cells with data; highlight computed location), input form for MAC+RSS, error/loading states.
- Testing: verify API calls, grid matches data, nearest-neighbor returns plausible cell.

## Quick talking points for defense
- Backend is supplied; we run `MapService.bat` to serve data from the remote DB. Verified endpoints and sample responses.
- Client will consume `/size` and `/column` to reconstruct the RSS map. Nearest-neighbor uses sensor-aligned RSS vectors to find the closest grid cell.
- Emulators use `10.0.2.2` to reach host services; host uses `localhost`.
- Error handling and UI states ensure no crashes if network/DB is temporarily unavailable.
## Update 2025-12-01
- Backend is running and verified on port 9000 (`/size`, `/column`, `/wilibox-column`).
- Added `client/README.md` with end-to-end Part 1 plan, Retrofit/Moshi setup, data models, repository/ViewModel scaffolds, nearest-neighbor helper, UI outline, and demo/run instructions.
- Next: scaffold the Android project using the provided snippets; point Retrofit to `http://10.0.2.2:9000/` in emulator, build grid UI, and hook the nearest-neighbor call to the MAC/RSSI input form. Test with backend running.
## Update 2025-12-01 (later)
- Android client scaffolded: added Retrofit/Moshi, coroutines, lifecycle, RecyclerView deps and viewBinding.
- Implemented API models/service/provider, repository to load grid from `/size` + `/column`, nearest-neighbor helper, and ViewModel with LiveData state + factory.
- Built minimal UI: RecyclerView grid (colored cells, highlight located), inputs for wiliboxas1-3 RSSI, locate button, loading/error handling.
- Base URL set to `http://10.0.2.2:9000/` for emulator.
- Next: run app with backend on port 9000, verify grid shows and locating works; adjust UI polish if desired.
## Update 2025-12-01 (later)
- Fixed runtime issues on emulator: enabled cleartext traffic for local backend and added INTERNET permission.
- App now loads grid from `http://10.0.2.2:9000/` on emulator with backend running; locate flow works after entering wiliboxas1-3 RSSI.
- Remaining: optional UI polish/testing; otherwise Part 1 client is functional for demo.
