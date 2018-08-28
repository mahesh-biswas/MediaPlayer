package com.mahesh.mediaplayer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var mediaPlayer: MediaPlayer
    lateinit var thread: Thread
    lateinit var metadata: MediaMetadataRetriever
    var rid: Int = 0
    var exit: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rid = R.raw.girls_like_you
        mediaPlayer = MediaPlayer.create(this, rid)
//SeekBar's Properties{---
        seekBar.max = mediaPlayer.duration
        seekBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
//  ---}
        var str: String? = this!!.getAlbumName()
        if(str==null){
            str = "untitled!!"
        }
        song_name.text = str
        setText()
        play.setOnClickListener(View.OnClickListener {
            if(!thread.isAlive){
                thread.start()
            }
            if(isPlaying()){
                mediaPlayer.pause()
            }else{
                mediaPlayer.start()
            }
            setText()
            Toast.makeText(this,"${mediaPlayer.duration} | ${mediaPlayer.currentPosition}",Toast.LENGTH_LONG).show()

        })
        thread = Thread({
            try {
                while (!exit) {
                    Thread.sleep(100)
                        if (mediaPlayer != null ) {
                            if(mediaPlayer.isPlaying) {
                                println("mediaPlayer Log: |: ${mediaPlayer.duration} | ${mediaPlayer.currentPosition}")
                                seekBar.progress = mediaPlayer.currentPosition
                                if(seekBar.progress == mediaPlayer.duration){

                                }
                            }
                        }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
            println("thread ended")
        })
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                print("")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                print("")
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(mediaPlayer!=null && fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        exit = true;
        Thread.sleep(1000)
        mediaPlayer.release()
    }
    fun getMeta(){
        metadata = MediaMetadataRetriever()
        var tmp = "android.resource://${packageName}/${rid}";
        metadata.setDataSource(this,Uri.parse(tmp))
        println("mediaPlayer meta: ${tmp} |")
    }
    fun getAlbumName(): String? {
        getMeta()
        var string: String? = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        var byte: ByteArray? = metadata.embeddedPicture
        var bitmap: Bitmap?
        if(byte!=null) {
             bitmap = BitmapFactory.decodeByteArray(byte!!, 0, byte!!.size)
        }else{
            bitmap = BitmapFactory.decodeResource(resources,R.drawable.music)
        }
        imageView.setImageBitmap(bitmap)
        println("byte: $byte :: $bitmap")
        return string
    }
    fun setText():Unit{
        if(isPlaying()){
            play.text = "Pause"
        }else{
            play.text = "Play"
        }
    }
    fun isPlaying():Boolean{
        if(mediaPlayer.isPlaying){
            return true
        }
        return false
    }


}
