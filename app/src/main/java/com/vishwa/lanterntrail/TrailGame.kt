package com.vishwa.lanterntrail

import kotlin.math.abs

/** The rules are separate from the screen so a rotation cannot reset a run. */
class TrailGame {
    data class Map(val title: String, val route: List<Int>, val lanterns: Set<Int>, val rocks: Set<Int>)
    companion object {
        val maps = listOf(
            Map("Creek Crossing", listOf(20,15,10,11,12,7,2,3,4), setOf(10,12,2), setOf(0,6,9,13,16,18,23)),
            Map("Pine Hollow", listOf(24,23,22,17,12,11,10,5,0,1,2), setOf(22,12,5), setOf(3,6,8,14,16,19,20)),
            Map("Moonrise Ridge", listOf(20,21,22,17,12,13,14,9,4,3,2,1,0), setOf(22,14,2), setOf(5,6,8,10,11,16,18,24))
        )
        fun restore(data: String): TrailGame = TrailGame().apply {
            val parts = data.split(";")
            require(parts.size == 9)
            level = parts[0].toInt(); total = parts[1].toInt(); hearts = parts[2].toInt()
            mistakes = parts[3].toInt(); hints = parts[4].toInt(); practice = parts[5] == "1"
            finished = parts[6] == "1"; won = parts[7] == "1"
            require(level in maps.indices && total >= 0 && hearts in 0..3 && mistakes >= 0 && hints >= 0)
            path.clear(); path.addAll(parts[8].split(",").map { it.toInt() })
            require(path.isNotEmpty() && path.first() == map.route.first() && path.distinct().size == path.size)
            require(path.all { it in 0..24 && it !in map.rocks })
            require(path.zipWithNext().all { (a,b) -> adjacent(a,b) })
        }
        fun adjacent(a: Int, b: Int) = abs(a / 5 - b / 5) + abs(a % 5 - b % 5) == 1
    }
    var level = 0
    var total = 0
    var hearts = 3
    var mistakes = 0
    var hints = 0
    var practice = false
    var finished = false
    var won = false
    val map get() = maps[level]
    val path = mutableListOf(maps[0].route.first())
    val collected get() = path.count { it in map.lanterns }
    val roundScore get() = (120 + collected * 20 - (path.size - 1) * 3 - mistakes * 12 - hints * 15).coerceAtLeast(0)
    val score get() = total + if (finished && won) 0 else roundScore
    fun save() = listOf(level,total,hearts,mistakes,hints,if(practice)1 else 0,if(finished)1 else 0,if(won)1 else 0,path.joinToString(",")).joinToString(";")
    fun move(cell: Int): String {
        if (finished || cell !in 0..24) return ""
        if (cell == path.last()) return "You are here. Choose a neighboring tile."
        if (path.size > 1 && cell == path[path.lastIndex - 1]) { path.removeAt(path.lastIndex); return "One step back." }
        if (!adjacent(path.last(),cell)) return "Choose a tile beside your current position."
        if (cell in map.rocks || cell in path) {
            hearts--; mistakes++
            if (hearts == 0) { finished = true; return "The trail ends here. Try a fresh route." }
            return if(cell in map.rocks) "Rock ahead. You lost one heart." else "That tile is already on your trail. You lost one heart."
        }
        if (cell == map.route.last() && collected != map.lanterns.size) return "Collect every lantern before entering the gate."
        path.add(cell)
        if (cell == map.route.last()) {
            total += roundScore
            if(level == maps.lastIndex) { finished = true; won = true; return "All three trails are lit!" }
            level++; mistakes = 0; hints = 0; path.clear(); path.add(map.route.first())
            return "Trail complete. Next stop: ${map.title}."
        }
        return if (cell in map.lanterns) "Lantern collected. ${collected} of ${map.lanterns.size}." else "Keep going."
    }
    fun undo(): String {
        if(path.size > 1 && !finished) { path.removeAt(path.lastIndex); return "One step back." }
        return "You are at the trailhead."
    }
    fun resetTrail() { if(!finished) { path.clear(); path.add(map.route.first()) } }
    fun useHint(): String { if(!finished) hints++; return "Gold dots show a safe route. Hints cost 15 points each." }
}
