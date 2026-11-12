package com.vishwa.lanterntrail

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.view.Gravity
import android.widget.*

class ResultsActivity:Activity() {
    override fun onCreate(state:Bundle?) {
        super.onCreate(state)
        val player=intent.getStringExtra("player") ?: "Explorer"
        val score=intent.getIntExtra("score",0)
        val won=intent.getBooleanExtra("won",false)
        val practice=intent.getBooleanExtra("practice",false)
        val best=intent.getIntExtra("best",0)
        val trails=intent.getIntExtra("trails",0)
        val outer=FrameLayout(this).apply { setBackgroundColor(Ui.navy) }
        val scroll=ScrollView(this); val col=Ui.column(this); scroll.addView(col); outer.addView(scroll)
        col.gravity=Gravity.CENTER_HORIZONTAL
        col.addView(Ui.text(this,"LANTERN TRAIL  /  JOURNEY NOTES",12f,Ui.gold,true).apply { setPadding(0,Ui.dp(this@ResultsActivity,38),0,20) })
        col.addView(Ui.text(this,if(won)"You brought\nthe light home." else "Every trail teaches\nyou something.",34f,Ui.paper,true).apply { gravity=Gravity.CENTER })
        col.addView(Ui.text(this,"Well explored, $player.",18f,Ui.muted))
        col.addView(Ui.text(this,score.toString(),76f,Ui.gold,true))
        col.addView(Ui.text(this,if(practice)"PRACTICE POINTS" else "POINTS",13f,Ui.muted,true))
        col.addView(Ui.text(this,"$trails of 3 trails completed",18f,Ui.paper,true).apply { setPadding(0,20,0,8) })
        col.addView(Ui.text(this,if(practice)"Guide runs do not change your best score." else "Best completed run: $best points",15f,Ui.muted))
        col.addView(Ui.text(this,if(won)"Nine lanterns are glowing along your route." else "Try a different route and keep an eye on your hearts.",16f,Ui.paper).apply { gravity=Gravity.CENTER; setPadding(0,24,0,24) })
        Ui.addButton(this,col,"Play again",true) { setResult(RESULT_OK); finish() }
        Ui.addButton(this,col,"Share journey") {
            val text="$player ${if(won)"completed all three trails" else "completed $trails trails"} in Lantern Trail with $score points${if(practice)" in practice mode" else ""}."
            val share=Intent(Intent.ACTION_SEND).apply { type="text/plain"; putExtra(Intent.EXTRA_TEXT,text) }
            try { startActivity(Intent.createChooser(share,"Share your journey")) }
            catch(_:android.content.ActivityNotFoundException) { Toast.makeText(this,"No sharing app is installed.",Toast.LENGTH_SHORT).show() }
        }
        Ui.insetRoot(this,outer)
    }
}
