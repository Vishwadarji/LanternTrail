# Verification — Vishwa Darji

## Completed checks

Kotlin compilation against Android SDK 35 passed. Resources and the manifest were compiled/linked with aapt2. A signed debug APK was produced and its signature verified. 32 standalone game-rule checks passed: map solvability, 450-point win, three-collision loss, no post-ending moves, collision penalties, nonadjacent tap handling, no row wrapping, lantern collection and undo, incomplete-exit rejection, reset retaining penalties, saved-state round trips, practice state, and corrupt-state rejection.

Android Studio Gradle sync and assembleDebug also passed on November 12, 2026. The current Gradle debug APK was installed and exercised on an API 35 phone emulator. This is the APK included in deliverables. No crash was observed during the checks below. This does not establish compatibility with every device.

## Device checks

- [x] Gradle sync and assembleDebug succeed in Android Studio.
- [x] Installation and launch work on an API 26+ device/emulator.
- [x] Blank name falls back to Explorer; entered Firefly appears in the journey and unlocks practice mode.
- [ ] Maximum-length player-name input has not been exercised on the emulator.
- [x] All three maps are playable and touch coordinates match displayed tiles.
- [x] Help, Undo, Hint, Restart this trail, and New journey work.
- [x] Three collisions show a loss; completing three maps shows a win.
- [x] Play again starts a fresh run without closing the app.
- [x] Lantern glow and touch ripple work without affecting input.
- [x] Rotation retains name, map, path, hearts, score and lantern count.
- [x] Results-screen rotation and return do not create duplicate result screens.
- [x] Background/relaunch restores an unfinished run.
- [x] Best completed non-practice score persists across restart.
- [x] firefly opens the labeled practice guide with safe-route dots. Best-score exclusion is implemented in source; a second complete practice win was not repeated on the emulator.
- [x] Share journey opens a chooser with the correct result.
- [x] System Back confirmation preserves the run when cancelled.
- [x] Portrait phone controls fit; landscape uses vertical scrolling and preserves state.
- [x] 480 × 800 phone at 240 dpi: home, board, controls and results remain accessible by vertical scrolling; all three trails completed at 450 points.
- [x] Largest Android font-size setting on the small phone: home and result text wrap and scroll; game controls stack vertically with complete labels. Hint and Restart were exercised.
- [ ] Tablets, other Android versions and physical devices have not been exercised.

## Observed results

The clean three-map run ended at 450 points with three hearts. A separate loss run ended after three rock collisions at 81 points. The share chooser displayed the correct 450-point result; no message was sent. Undo removed a collected lantern. A hint deducted 15 points, and restarting retained that deduction. After rotation and after backgrounding, stopping and relaunching the app, the active run retained 119 points, three hearts, one lantern and its route. The 450-point best score persisted. Result rotation followed by Play again returned to one home screen.

Build warnings are limited to deprecated platform/system-bar APIs and the unused AndroidX setting; the build completed without errors.

## Additional emulator verification — November 12, 2026

Two Android 15 / API 35 arm64 virtual phones were used: 1080 × 1920 at 420 dpi, and 480 × 800 at 240 dpi (320 × 533 dp). On the smaller phone, a complete three-map win reached 450 points and all result actions were reachable by scrolling. At the largest system font-size setting, the original horizontal game controls clipped their labels. The layout now stacks those controls when fontScale exceeds 1.2, preserving readable labels and allowing the buttons to grow with their text. The corrected build was installed and its Hint and Restart controls were verified at the largest font setting.

A second Android system version was not installed, so no cross-version runtime claim is made. The minimum supported API level remains 26, as configured in the project.

## Demonstration routes

Tile numbers are zero-based, row-major on a 5 by 5 board. The first number is the start. Tap the remaining numbers in order.

1. Creek Crossing: 20,15,10,11,12,7,2,3,4
2. Pine Hollow: 24,23,22,17,12,11,10,5,0,1,2
3. Moonrise Ridge: 20,21,22,17,12,13,14,9,4,3,2,1,0

Expected clean score: 450. A quick loss: 20 → 21, then tap rock 16 three times.
