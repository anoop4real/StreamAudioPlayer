package com.example.anoopmohanan.streamaudioapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import com.example.anoopmohanan.streamaudioapp.player.StreamAudioPlayer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var audioPlayer: StreamAudioPlayer? = null

    private val streamAudioPlayerCallback = object : StreamAudioPlayer.StreamAudioPlayerCallback{
        override fun playerPrepared() {

            show("BUFFERING COMPLETE......")
            showProgress(false)
        }

        override fun playerProgress(offsetInMilliseconds: Long, percent: Float) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun itemComplete() {
            show("FINISHED PLAYING......")
        }

        override fun playerError() {
            show("Error while playing......")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        audioPlayer = StreamAudioPlayer.getInstance(this)
        audioPlayer?.addCallback(streamAudioPlayerCallback)
        show("BUFFERING......")
        showProgress(false)
    }

    fun startStream(view: View){

        //"http://www.robtowns.com/music/blind_willie.mp3"
        //"http://listen.radionomy.com/rockzone-radio.m3u"
        var sourceURL = "http://listen.radionomy.com/rockzone-radio"
        audioPlayer?.playItem(sourceURL)
        showProgress(true)


    }

    fun stopStream(view: View){
        audioPlayer?.stop()
        showProgress(false)
    }

    fun showProgress(status: Boolean){

        if (status){

            this.progressBar.visibility = VISIBLE
        }else{
            this.progressBar.visibility = INVISIBLE
        }
    }

    fun show(message: String){

        var toast = Toast.makeText(this,message,Toast.LENGTH_LONG)
        toast.show()
    }
}
