package com.vishwa.lanterntrail

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.*

internal object Ui {
    val navy=Color.rgb(20,46,53)
    val paper=Color.rgb(247,245,235)
    val muted=Color.rgb(175,199,193)
    val gold=Color.rgb(234,191,104)
    fun dp(a:Activity,n:Int)=(n*a.resources.displayMetrics.density).toInt()
    fun text(a:Activity,value:String,size:Float,color:Int=paper,bold:Boolean=false)=TextView(a).apply {
        text=value; textSize=size; setTextColor(color); if(bold) setTypeface(typeface,Typeface.BOLD)
        setPadding(0,dp(a,4),0,dp(a,4))
    }
    fun background(color:Int,radius:Float=18f)=GradientDrawable().apply { setColor(color); cornerRadius=radius }
    fun column(a:Activity)=LinearLayout(a).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(a,22),dp(a,12),dp(a,22),dp(a,20)); setBackgroundColor(navy) }
    fun button(a:Activity,label:String,primary:Boolean=false,action:()->Unit)=Button(a).apply {
        text=label; isAllCaps=false; textSize=15f; setTextColor(if(primary)navy else paper)
        background=background(if(primary)gold else Color.rgb(39,77,82),dp(a,13).toFloat())
        minHeight=dp(a,48); minimumHeight=dp(a,48); setPadding(dp(a,12),dp(a,7),dp(a,12),dp(a,7))
        setOnClickListener { action() }
    }
    fun addButton(a:Activity,parent:LinearLayout,label:String,primary:Boolean=false,action:()->Unit) {
        parent.addView(button(a,label,primary,action),LinearLayout.LayoutParams(-1,dp(a,50)).apply { topMargin=dp(a,10) })
    }
    fun insetRoot(a:Activity,root:View) {
        a.window.statusBarColor=navy; a.window.navigationBarColor=navy
        root.setOnApplyWindowInsetsListener { v,ins ->
            if(android.os.Build.VERSION.SDK_INT >= 30) {
                val bars=ins.getInsets(android.view.WindowInsets.Type.systemBars() or android.view.WindowInsets.Type.displayCutout())
                v.setPadding(bars.left,bars.top,bars.right,bars.bottom)
            } else {
                @Suppress("DEPRECATION")
                v.setPadding(ins.systemWindowInsetLeft,ins.systemWindowInsetTop,ins.systemWindowInsetRight,ins.systemWindowInsetBottom)
            }
            ins
        }
        a.setContentView(root); root.requestApplyInsets()
    }
    fun bar(a:Activity,parent:LinearLayout,title:String,help:()->Unit,newJourney:(()->Unit)?=null) {
        val toolbar=Toolbar(android.view.ContextThemeWrapper(a,android.R.style.ThemeOverlay_Material_Dark_ActionBar)).apply {
            this.title=title; setTitleTextColor(paper); setBackgroundColor(navy)
            setContentInsetsAbsolute(0,0)
            menu.add("Help").apply { setShowAsAction(android.view.MenuItem.SHOW_AS_ACTION_ALWAYS) }
            if(newJourney!=null) menu.add("New journey").apply { setShowAsAction(android.view.MenuItem.SHOW_AS_ACTION_NEVER) }
            setOnMenuItemClickListener { item ->
                if(item.title=="New journey") newJourney?.invoke() else help()
                true
            }
        }
        parent.addView(toolbar,LinearLayout.LayoutParams(-1,dp(a,56)))
    }
}
