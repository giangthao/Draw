package com.tutorial.draw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context,attrs){
    private var mDrawPath : CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mCanvasPaint:Paint? = null
    private var mDrawPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var color = Color.BLUE
    private var canvas: Canvas? = null
    private val mPaThs = ArrayList<CustomPath>()
    init {
        setupDrawing()
    }
    private fun setupDrawing(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color,mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.CURSOR_AFTER)
        //mBrushSize = 20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw,  oldh)
        mCanvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    /**
     * This method is called when a stroke is drawn on the canvas
     * as a part of the painting.
     */
    override fun onDraw(canvas: Canvas){
        /**
         * Draw the specified bitmap, with its top/left corner at (x,y), using the specified paint,
         * transformed by the current matrix.
         *
         *If the bitmap and canvas have different densities, this function will take care of
         * automatically scaling the bitmap to draw at the same density as the canvas.
         *
         * @param bitmap The bitmap to be drawn
         * @param left The position of the left side of the bitmap being drawn
         * @param top The position of the top side of the bitmap being drawn
         * @param paint The paint used to draw the bitmap (may be null)
         */
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint)
        for(path in mPaThs){
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path!!.color
            canvas.drawPath(path,mDrawPaint!!)
        }
        if(!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!,mDrawPaint!!)
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y
        when(event?.action){
            MotionEvent.ACTION_DOWN ->{
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize
                mDrawPath!!.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.moveTo(touchX,touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                mPaThs.add(mDrawPath!!)
                mDrawPath = CustomPath(color,mBrushSize)
            }
            else -> return false
        }
        invalidate()

        return true
    }
    fun setSizeForBrush(newSize:Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newSize, resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize
    }

    fun setColor(newColor: String){
        color = Color.parseColor(newColor)
    }

    // An inner class for custom path with two params as color and stoke size
    internal inner class CustomPath(var color:Int, var brushThickness: Float) : Path()
}