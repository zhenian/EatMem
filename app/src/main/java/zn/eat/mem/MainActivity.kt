package zn.eat.mem

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import zn.eat.mem.MemService.Companion.SERVICE_ACTION
import zn.eat.mem.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = MainViewModel()
        val mainBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        mainBinding.viewModel = viewModel
        viewModel.updateShow(this)

        val bindIntent = Intent(this, MemService::class.java)
        this.bindService(bindIntent, this.connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        closeService()
    }

    var binder: MemService.MemBinder? = null

    val connection = object: ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            L.i("onServiceDisconnected: ")
        }

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            this@MainActivity.binder = binder as MemService.MemBinder
            this@MainActivity.binder?.updateInfo()
        }
    }

    fun closeService(){
        val mIntent = Intent()
        mIntent.action = SERVICE_ACTION
        mIntent.setPackage(this.packageName) //这里你需要设置你应用的包名
        this.stopService(mIntent)
        this.unbindService(this.connection)
    }
}