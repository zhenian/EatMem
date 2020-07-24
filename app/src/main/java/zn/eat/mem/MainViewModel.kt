package zn.eat.mem

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel


class MainViewModel : ViewModel(){
    val memInfo = ObservableField<String>("")

    fun updateShow(ctx: Context){
        val info = L.getMemInfo(ctx)
        this.memInfo.set(info)
        L.i(info)
    }


    val onClickButton: View.OnClickListener = View.OnClickListener{ it ->
        when(it.id){
            R.id.alloc_bitmap_object1 -> {
                L.alloc1BitMap(it.context)
                this.updateShow(it.context)
            }
            R.id.alloc_bitmap_objects -> {
                L.alloc10BitMap(it.context)
                this.updateShow(it.context)
            }
            R.id.update_mem_info -> this.updateShow(it.context)
            else ->{}
        }
    }
}