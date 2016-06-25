package github.com.jwyjoy.sample_camera

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.widget.Toast;
import kotlinx.android.synthetic.main.activity_main.*

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

class MainActivity : Activity(), SurfaceHolder.Callback {

    @SuppressWarnings("deprecation")
    internal var camera: Camera? = null

    @SuppressWarnings("deprecation")
    private lateinit var jpegCallback: Camera.PictureCallback

    @SuppressWarnings("deprecation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_capture.setOnClickListener { camera!!.takePicture(null, null, jpegCallback) }

        window.setFormat(PixelFormat.UNKNOWN)

        camera_view.holder.addCallback(this)
        camera_view.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        jpegCallback = Camera.PictureCallback { data, camera ->
            var outStream: FileOutputStream? = null
            var str: String = ""

            try {
                str = String.format("/sdcard/%d.jpg",
                        System.currentTimeMillis())
                outStream = FileOutputStream(str)

                outStream.write(data)
                outStream.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
            }

            Toast.makeText(applicationContext,
                    "Picture Saved", Toast.LENGTH_LONG).show()
            refreshCamera()

            val intent = Intent(this@MainActivity,
                    ResultActivity::class.java)
            intent.putExtra("strParamName", str)
            startActivity(intent)
        }
    }

    fun refreshCamera() {
        if (camera_view.holder.surface == null) {
            return
        }

        try {
            camera!!.stopPreview()
        } catch (e: Exception) {
        }

        try {
            camera!!.setPreviewDisplay(camera_view.holder)
            camera!!.startPreview()
        } catch (e: Exception) {
        }

    }

    @SuppressWarnings("deprecation")
    override
    fun surfaceCreated(holder: SurfaceHolder) {

        camera = Camera.open()
        camera?.stopPreview()
        val param = camera!!.parameters
        param.setRotation(90)
        camera!!.parameters = param

        try {
            camera!!.setPreviewDisplay(camera_view.holder)
            camera!!.startPreview()
        } catch (e: Exception) {
            System.err.println(e)
            return
        }

    }

    override
    fun surfaceChanged(holder: SurfaceHolder,
                       format: Int, width: Int, height: Int) {
        refreshCamera()
    }

    override
    fun surfaceDestroyed(holder: SurfaceHolder) {
        camera!!.stopPreview()
        camera!!.release()
        camera = null
    }
}