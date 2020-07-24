package zn.eat.mem

import android.app.ActivityManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.storage.StorageManager
import android.util.Log
import java.util.*

object L {
    val allBitmap = mutableListOf<Bitmap>()


    fun decodeResource(ctx: Context, resId: Int): Bitmap? {
        return BitmapFactory.decodeResource(ctx.getResources(), resId)
    }



    fun alloc1BitMap(ctx: Context){
        val b1 = decodeResource(ctx,R.drawable.t1)
        if(b1 != null){
            this.allBitmap.add(b1)
        }
    }
    fun alloc10BitMap(ctx: Context){
        for(i in 0..1){
            val b1 = decodeResource(ctx,R.drawable.t1)
            if(b1 != null){
                this.allBitmap.add(b1)
            }
            val b2 = decodeResource(ctx,R.drawable.t2)
            if(b2 != null){
                this.allBitmap.add(b2)
            }
            val b3 = decodeResource(ctx,R.drawable.t3)
            if(b3 != null){
                this.allBitmap.add(b3)
            }
            val b4 = decodeResource(ctx,R.drawable.t4)
            if(b4 != null){
                this.allBitmap.add(b4)
            }
            val b5 = decodeResource(ctx,R.drawable.t5)
            if(b5 != null){
                this.allBitmap.add(b5)
            }
        }
    }

    fun getMemInfo(ctx: Context): String {
        val activityManager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)

        L.i("memInfo availMem:"+L.getUnit(memInfo.availMem))
        L.i("memInfo threshold:"+L.getUnit(memInfo.threshold)) //low memory threshold
        L.i("memInfo totalMem:"+L.getUnit(memInfo.totalMem))
        L.i("memInfo lowMemory:"+memInfo.lowMemory)  //if current is in low memory

        return "BIG mem object count: ${allBitmap.size}  availMem:${L.getUnit(memInfo.availMem)} percent:${memInfo.availMem*100/memInfo.totalMem}"
    }

    private val units =
        arrayOf("B", "KB", "MB", "GB", "TB")

    /**
     * 进制转换
     */
    fun getUnit(size: Long): String {
        return getUnit(size.toFloat(), 1024f)
    }
    fun getUnit(size: Float, base: Float): String {
        var size = size
        var index = 0
        while (size > base && index < 4) {
            size = size / base
            index++
        }
        return String.format(
            Locale.getDefault(),
            " %.2f %s ",
            size,
            units[index]
        )
    }

    val tag = "[EatMem]"
    fun e(msg: String){
        Log.e(tag, msg)
    }

    fun i(msg: String){
        Log.i(tag, msg)
    }

    fun d(msg: String){
        Log.d(tag, msg)
    }
}