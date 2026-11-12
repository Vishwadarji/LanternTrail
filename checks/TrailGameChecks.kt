package com.vishwa.lanterntrail

fun main() {
    var checks=0
    fun verify(value:Boolean,label:String) { check(value) { label }; checks++ }
    TrailGame.maps.forEach { map ->
        verify(map.route.distinct().size==map.route.size,"Route must not cross itself")
        verify(map.route.none { it in map.rocks },"Route must avoid rocks")
        verify(map.route.containsAll(map.lanterns),"Every lantern must be reachable")
        verify(map.route.zipWithNext().all { (a,b)->TrailGame.adjacent(a,b) },"No diagonal route steps")
    }
    val win=TrailGame()
    TrailGame.maps.forEach { map -> map.route.drop(1).forEach { win.move(it) } }
    verify(win.finished && win.won,"Three completed maps must win")
    verify(win.total==450,"A clean run must score exactly 450")
    verify(win.hearts==3,"Clean run keeps hearts")
    verify(TrailGame.restore(win.save()).save()==win.save(),"Completed run restores exactly")
    val g=TrailGame()
    g.move(15); g.move(10)
    verify(g.collected==1,"Lantern is collected")
    g.undo(); verify(g.collected==0,"Undo removes collected lantern")
    g.move(10); verify(g.collected==1,"Recollection does not duplicate lantern")
    val before=g.save(); val restored=TrailGame.restore(before)
    verify(restored.save()==before,"Active run round trips")
    verify(restored.map.title==g.map.title && restored.path==g.path,"Restored map and path match")
    g.move(0); verify(g.hearts==3,"Nonadjacent tap does not cost hearts")
    g.move(5); g.move(6)
    verify(g.hearts==2 && g.mistakes==1,"Rock consumes one heart")
    g.useHint(); val score=g.roundScore
    g.resetTrail()
    verify(g.hearts==2 && g.mistakes==1 && g.hints==1,"Restart trail retains penalties")
    verify(g.roundScore<=180,"Reset cannot manufacture collected lanterns")
    verify(score>=0,"Score stays nonnegative")
    val loss=TrailGame()
    loss.move(21); loss.move(16); loss.move(16); loss.move(16)
    verify(loss.finished && !loss.won && loss.hearts==0,"Three rocks end the run")
    val ended=loss.save(); loss.move(22); verify(loss.save()==ended,"No moves after ending")
    val early=TrailGame()
    listOf(21,22,17,12,7,2,3).forEach { early.move(it) }
    early.move(4)
    verify(!early.finished && early.level==0 && early.path.last()==3,"Exit rejects missing lanterns")
    val practice=TrailGame().apply { this.practice=true; useHint() }
    verify(TrailGame.restore(practice.save()).practice,"Practice mode survives restoration")
    verify(runCatching { TrailGame.restore("broken") }.isFailure,"Invalid saved data is rejected")
    verify(!TrailGame.adjacent(4,5),"Row edges do not wrap")
    println("PASS: $checks game-rule checks")
}
