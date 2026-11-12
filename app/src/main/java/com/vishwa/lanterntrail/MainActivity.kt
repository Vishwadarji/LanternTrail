package com.vishwa.lanterntrail

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.*

class MainActivity: Activity() {
    private var game:TrailGame?=null
    private var player=""
    private var message="Collect three lanterns, then reach the gate."
    private var showingResults=false
    private lateinit var score:TextView
    private lateinit var status:TextView
    private lateinit var level:TextView
    private lateinit var board:TrailBoard
    private val prefs get()=getSharedPreferences("trail_progress",MODE_PRIVATE)
    override fun onCreate(state:Bundle?) {
        super.onCreate(state)
        showingResults=state?.getBoolean("showingResults",false) ?: false
        if(android.os.Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(android.window.OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                if(game!=null && !game!!.finished) confirmNew() else finish()
            }
        }
        player=state?.getString("player") ?: prefs.getString("player","").orEmpty()
        message=state?.getString("message") ?: "Collect three lanterns, then reach the gate."
        val saved=state?.getString("run") ?: prefs.getString("run",null)
        game=saved?.let { runCatching { TrailGame.restore(it) }.getOrNull() }
        if(game?.finished==true) { showGame(); openResults() } else if(game!=null) showGame() else showHome()
    }
    override fun onSaveInstanceState(out:Bundle) {
        out.putBoolean("showingResults",showingResults)
        out.putString("player",player); out.putString("message",message); game?.let { out.putString("run",it.save()) }
        super.onSaveInstanceState(out)
    }
    override fun onPause() { super.onPause(); persist() }
    private fun persist() { prefs.edit().putString("player",player).apply { if(game!=null) putString("run",game!!.save()) else remove("run") }.apply() }
    private fun help() { AlertDialog.Builder(this).setTitle("A little light goes a long way")
        .setMessage("Tap a tile next to the mint marker to move. Collect all three lanterns before entering EXIT.\n\nAvoid rocks and tiles already in your trail. Each collision costs one of your three hearts. Nonadjacent taps do not cost hearts.\n\nTap the previous tile or use Undo to step back. Restart trail clears your route but keeps penalties.\n\nEach trail starts at 120 points. Each lantern adds 20; each step costs 3, a collision costs 12, and a hint costs 15. Hearts carry across all three trails.\n\nFinish three trails to win. Run out of hearts and the run ends. You can restart after either ending.")
        .setPositiveButton("Got it",null).show() }
    private fun showHome() {
        val outer=FrameLayout(this).apply { setBackgroundColor(Ui.navy) }
        val scroll=ScrollView(this); val col=Ui.column(this); scroll.addView(col); outer.addView(scroll)
        Ui.bar(this,col,"LANTERN TRAIL",::help)
        col.addView(Ui.text(this,"FOLLOW THE GLOW",12f,Ui.gold,true).apply { setPadding(0,Ui.dp(this@MainActivity,16),0,8) })
        col.addView(Ui.text(this,"Find your way.\nBring the light.",30f,Ui.paper,true))
        col.addView(Ui.text(this,"Three trails. Nine lanterns. Your route.",17f,Ui.muted))
        val preview=TrailBoard(this).apply { game=TrailGame(); isClickable=false; onTile=null }
        col.addView(preview,LinearLayout.LayoutParams(-1,Ui.dp(this,150)).apply { topMargin=Ui.dp(this@MainActivity,8); gravity=Gravity.CENTER_HORIZONTAL })
        col.addView(Ui.text(this,"What should we call you?",15f,Ui.paper,true))
        val name=EditText(this).apply {
            id=android.R.id.edit; hint="Trail name"; setText(player); setTextColor(Ui.paper); setHintTextColor(Ui.muted); isSingleLine=true
            filters=arrayOf(android.text.InputFilter.LengthFilter(24)); inputType=android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS
        }
        col.addView(name,LinearLayout.LayoutParams(-1,Ui.dp(this,52)))
        val best=prefs.getInt("best",0)
        col.addView(Ui.text(this,"Best completed run  ·  $best points",14f,Ui.muted))
        Ui.addButton(this,col,"Start the trail",true) {
            player=name.text.toString().trim().ifBlank { "Explorer" }
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(name.windowToken,0)
            game=TrailGame().apply { practice=player.equals("firefly",true) }
            message=if(game!!.practice) "Firefly guide found! Practice mode shows the safe route." else "$player, your first trail begins here."
            persist(); showGame()
        }
        col.addView(Ui.text(this,"No timer. Take a breath and plan your route.",13f,Ui.muted).apply { gravity=Gravity.CENTER; setPadding(0,8,0,4) })
        Ui.insetRoot(this,outer)
    }
    private fun showGame() {
        val g=game ?: return
        val outer=FrameLayout(this).apply { setBackgroundColor(Ui.navy) }
        val scroll=ScrollView(this); val col=Ui.column(this); scroll.addView(col); outer.addView(scroll)
        Ui.bar(this,col,"LANTERN TRAIL",::help,::confirmNew)
        col.addView(Ui.text(this,"${if(g.practice)"PRACTICE · " else ""}${player}'s journey",13f,Ui.gold,true))
        level=Ui.text(this,"",26f,Ui.paper,true); col.addView(level)
        score=Ui.text(this,"",15f,Ui.muted); col.addView(score)
        board=TrailBoard(this).apply {
            game=g
            onTile={ tile -> message=g.move(tile); hintVisible=false; update(); persist(); if(g.finished) openResults() }
        }
        val width=resources.displayMetrics.widthPixels/resources.displayMetrics.density
        val height=resources.displayMetrics.heightPixels/resources.displayMetrics.density
        val boardSize=if(width>height) minOf(width-48f,260f) else minOf(width-44f,maxOf(230f,height-440f),430f)
        col.addView(board,LinearLayout.LayoutParams(-1,Ui.dp(this,boardSize.toInt())).apply { gravity=Gravity.CENTER })
        col.addView(Ui.text(this,"MINT = YOU     GOLD = LANTERN     OUTLINE = EXIT",11f,Ui.muted).apply { gravity=Gravity.CENTER })
        status=Ui.text(this,"",16f,Ui.paper).apply { minHeight=Ui.dp(this@MainActivity,44); accessibilityLiveRegion=android.view.View.ACCESSIBILITY_LIVE_REGION_POLITE }
        col.addView(status)
        val stacked=resources.configuration.fontScale>1.2f
        val row=LinearLayout(this).apply { orientation=if(stacked) LinearLayout.VERTICAL else LinearLayout.HORIZONTAL }
        fun controlParams()=if(stacked) LinearLayout.LayoutParams(-1,-2).apply { topMargin=Ui.dp(this@MainActivity,8) }
        else LinearLayout.LayoutParams(0,Ui.dp(this,48),1f).apply { marginEnd=Ui.dp(this@MainActivity,6) }
        row.addView(Ui.button(this,"Undo") { message=g.undo(); update(); persist() },controlParams())
        row.addView(Ui.button(this,"Hint −15") { message=g.useHint(); board.hintVisible=true; update(); persist() },controlParams())
        row.addView(Ui.button(this,"Restart") {
            AlertDialog.Builder(this).setTitle("Restart this trail?").setMessage("Your route will clear. Lost hearts and point penalties stay.")
                .setNegativeButton("Keep going",null).setPositiveButton("Restart") { _,_ -> g.resetTrail(); board.hintVisible=false; message="A fresh route from the trailhead."; update(); persist() }.show()
        },controlParams())
        col.addView(row)
        Ui.insetRoot(this,outer); update()
    }
    private fun update() {
        val g=game ?: return
        level.text="${g.level+1} / 3  ·  ${g.map.title}"
        score.text="${g.score} points   ·   ${g.hearts} hearts   ·   ${g.collected}/3 lanterns"
        status.text=message; board.invalidate()
    }
    private fun confirmNew() { AlertDialog.Builder(this).setTitle("Start a new journey?").setMessage("This run will be replaced.")
        .setNegativeButton("Keep playing",null).setPositiveButton("New journey") { _,_ -> game=null; persist(); showHome() }.show() }
    private fun openResults() {
        if(showingResults) return
        val g=game ?: return
        if(g.won && !g.practice && g.total>prefs.getInt("best",0)) prefs.edit().putInt("best",g.total).apply()
        showingResults=true
        startActivityForResult(Intent(this,ResultsActivity::class.java).apply {
            putExtra("player",player); putExtra("score",if(g.won)g.total else g.score); putExtra("won",g.won)
            putExtra("practice",g.practice); putExtra("best",prefs.getInt("best",0)); putExtra("trails",if(g.won)3 else g.level)
        },71)
    }
    @Deprecated("Uses the platform Activity result API to avoid external dependencies")
    override fun onActivityResult(request:Int,result:Int,data:Intent?) {
        super.onActivityResult(request,result,data)
        if(request==71) { showingResults=false; game=null; persist(); showHome() }
    }
    @Deprecated("Uses the platform back navigation callback")
    override fun onBackPressed() { if(game!=null && !game!!.finished) confirmNew() else super.onBackPressed() }
}
