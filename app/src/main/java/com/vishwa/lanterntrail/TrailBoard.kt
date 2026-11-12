package com.vishwa.lanterntrail

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

/** A drawn board keeps the same geometry for painting and hit testing. */
class TrailBoard(context: Context) : View(context) {
    var game = TrailGame()
    var hintVisible = false
    var onTile: ((Int) -> Unit)? = null
    private val ink = Paint(Paint.ANTI_ALIAS_FLAG)
    private var cell = 0f
    private var left = 0f
    private var top = 0f
    private var pulse = 0f
    private var ripple = 0f
    private var rippleAlpha = 0f
    private var touched = -1
    private var glow: ValueAnimator? = null
    private var tapAnimation: AnimatorSet? = null
    private val gold = Color.rgb(234,191,104)
    private val mint = Color.rgb(108,192,167)
    init { isFocusable = true; contentDescription = "Lantern trail puzzle board"; setLayerType(LAYER_TYPE_SOFTWARE,null) }
    override fun onMeasure(w: Int,h: Int) {
        val size = min(MeasureSpec.getSize(w),MeasureSpec.getSize(h)).coerceAtLeast(1)
        setMeasuredDimension(size,size)
    }
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!android.animation.ValueAnimator.areAnimatorsEnabled()) return
        glow = ValueAnimator.ofFloat(0f,1f).apply {
            duration=1800; repeatCount=ValueAnimator.INFINITE; repeatMode=ValueAnimator.REVERSE
            addUpdateListener { pulse=it.animatedValue as Float; invalidate() }; start()
        }
    }
    override fun onDetachedFromWindow() { glow?.cancel(); tapAnimation?.cancel(); super.onDetachedFromWindow() }
    private fun center(i: Int) = PointF(left+(i%5+.5f)*cell,top+(i/5+.5f)*cell)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        cell = min(width,height)/5.15f; left=(width-cell*5)/2; top=(height-cell*5)/2
        ink.style=Paint.Style.FILL
        for(i in 0..24) {
            val x=left+(i%5)*cell; val y=top+(i/5)*cell; val gap=cell*.065f
            ink.color=if(i in game.path) Color.rgb(41,82,82) else Color.rgb(29,59,66)
            canvas.drawRoundRect(x+gap,y+gap,x+cell-gap,y+cell-gap,cell*.16f,cell*.16f,ink)
        }
        // The path is drawn below the objects so markers remain legible.
        if(game.path.size>1) {
            ink.color=mint; ink.strokeWidth=cell*.105f; ink.strokeCap=Paint.Cap.ROUND
            game.path.zipWithNext().forEach { (a,b) -> val p=center(a); val q=center(b); canvas.drawLine(p.x,p.y,q.x,q.y,ink) }
        }
        if(hintVisible || game.practice) game.map.route.forEach {
            if(it !in game.path && it !in game.map.lanterns && it!=game.map.route.last()) {
                val p=center(it); ink.color=gold; ink.alpha=140; canvas.drawCircle(p.x,p.y,cell*.045f,ink); ink.alpha=255
            }
        }
        for(i in game.map.rocks) {
            val p=center(i); ink.color=Color.rgb(93,115,119)
            val shape=Path().apply { moveTo(p.x-cell*.23f,p.y+cell*.15f); lineTo(p.x-cell*.18f,p.y-cell*.12f); lineTo(p.x+cell*.03f,p.y-cell*.23f); lineTo(p.x+cell*.23f,p.y-cell*.03f); lineTo(p.x+cell*.20f,p.y+cell*.19f); close() }
            canvas.drawPath(shape,ink); ink.color=Color.rgb(120,140,140); ink.strokeWidth=cell*.025f
            canvas.drawLine(p.x-cell*.12f,p.y-cell*.06f,p.x+cell*.02f,p.y-cell*.13f,ink)
        }
        for(i in game.map.lanterns) {
            val p=center(i); val picked=i in game.path
            if(!picked) { ink.color=gold; ink.alpha=(22+14*pulse).toInt(); canvas.drawCircle(p.x,p.y,cell*(.29f+.025f*pulse),ink); ink.alpha=255 }
            ink.color=if(picked) mint else gold
            val w=cell*.17f; val h=cell*.19f
            canvas.drawRoundRect(p.x-w,p.y-h,p.x+w,p.y+h,cell*.045f,cell*.045f,ink)
            ink.style=Paint.Style.STROKE; ink.strokeWidth=cell*.035f
            canvas.drawArc(p.x-w*.65f,p.y-h*1.7f,p.x+w*.65f,p.y-h*.6f,180f,180f,false,ink)
            ink.style=Paint.Style.FILL; ink.color=Color.rgb(255,243,207)
            canvas.drawRoundRect(p.x-w*.50f,p.y-h*.6f,p.x+w*.50f,p.y+h*.6f,cell*.02f,cell*.02f,ink)
            if(picked) { ink.color=Color.rgb(20,46,53); ink.textSize=cell*.24f; ink.textAlign=Paint.Align.CENTER; canvas.drawText("✓",p.x,p.y+cell*.08f,ink) }
        }
        val gate=center(game.map.route.last()); ink.style=Paint.Style.STROKE; ink.color=gold; ink.strokeWidth=cell*.055f
        canvas.drawRoundRect(gate.x-cell*.22f,gate.y-cell*.23f,gate.x+cell*.22f,gate.y+cell*.23f,cell*.07f,cell*.07f,ink)
        ink.style=Paint.Style.FILL; ink.textAlign=Paint.Align.CENTER; ink.textSize=cell*.19f
        canvas.drawText("EXIT",gate.x,gate.y+cell*.06f,ink)
        val start=center(game.map.route.first()); ink.color=mint; ink.textSize=cell*.17f
        if(game.path.size>1) canvas.drawText("START",start.x,start.y+cell*.06f,ink)
        val current=center(game.path.last()); ink.color=mint
        canvas.drawCircle(current.x,current.y,cell*.16f,ink); ink.color=Color.rgb(239,248,235)
        canvas.drawCircle(current.x,current.y,cell*.065f,ink)
        if(touched>=0 && rippleAlpha>0) {
            val p=center(touched); ink.style=Paint.Style.STROKE; ink.strokeWidth=cell*.04f; ink.color=gold; ink.alpha=(255*rippleAlpha).toInt()
            canvas.drawCircle(p.x,p.y,cell*(.15f+ripple*.32f),ink); ink.alpha=255; ink.style=Paint.Style.FILL
        }
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(event.action==MotionEvent.ACTION_DOWN) return true
        if(event.action==MotionEvent.ACTION_UP) {
            if(cell<=0f || event.x<left || event.y<top || event.x>=left+cell*5 || event.y>=top+cell*5) return true
            touched=((event.y-top)/cell).toInt()*5+((event.x-left)/cell).toInt()
            tapAnimation?.cancel()
            val expand=ValueAnimator.ofFloat(0f,1f).apply { duration=160; addUpdateListener { ripple=it.animatedValue as Float; rippleAlpha=1f; invalidate() } }
            val fade=ValueAnimator.ofFloat(1f,0f).apply { duration=200; addUpdateListener { rippleAlpha=it.animatedValue as Float; invalidate() } }
            tapAnimation=AnimatorSet().apply { playSequentially(expand,fade); start() }
            onTile?.invoke(touched); performClick(); return true
        }
        return true
    }
    override fun performClick(): Boolean { super.performClick(); return true }
}
