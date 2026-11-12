# Lantern Trail

Vishwa Darji — ISYS 34603 final Android game

Collect every lantern and reach the exit across three maps. Three collisions end the journey. There is no timer. The app works offline and uses Kotlin and Android Views.

## Open

Open this folder in Android Studio. Let Gradle sync, select the app run configuration, and choose an Android phone or emulator at API 26 or newer. The build uses Android Gradle Plugin 9.4.0, Gradle 9.6.0, built-in Kotlin support, and SDK 35. Use the Android Studio Gradle JDK. The first sync may download build dependencies.

Run with the green Run button, or use `./gradlew assembleDebug` (`gradlew.bat assembleDebug` on Windows). The included `deliverables/LanternTrail-debug.apk` is a local debug build, not a Play Store release.

## Rules

Enter a trail name. Tap a horizontal or vertical neighbor of the mint marker. Collect the three gold lanterns before entering EXIT. A rock or revisiting a tile costs a heart. Nonadjacent taps do not cost hearts. Undo retraces one step, including uncollecting a lantern. Restart this trail clears the path but retains penalties. Finish all three trails to win. Play again returns to the start screen without closing the app.

A trail begins with 120 points; lanterns add 20, steps cost 3, collisions cost 12, and hints cost 15. Round scores cannot be negative. A clean completed journey scores 450. Only completed non-practice runs update the saved best score.

Enter `firefly` as the name for a hidden practice guide. It displays safe route dots. Practice scores do not replace the normal best score.

## Source guide

- TrailGame.kt: maps, rules, scoring, saved-state format.
- TrailBoard.kt: custom drawing, tap handling, glow and ripple animation.
- MainActivity.kt: home/game screens, help, saved state and local preferences.
- ResultsActivity.kt: results, replay and implicit score sharing.
- Ui.kt: shared styling, toolbar and system-bar insets.
- checks/TrailGameChecks.kt: standalone game-rule checks.

Review TESTING.md before submission. Canvas requires a repository link pointing to the exact branch to be graded, and says commits after submission will not be considered. Nothing has been submitted to Canvas or uploaded to GitHub.
