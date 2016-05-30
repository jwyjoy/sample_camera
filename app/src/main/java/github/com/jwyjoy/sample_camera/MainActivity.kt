package github.com.jwyjoy.sample_camera

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment.*
import android.os.PersistableBundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var camera: Camera
    lateinit var preview: CameraPreview

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        // 카메라 인스턴스 생성
        if (checkCameraHardware(applicationContext)) {
            camera = loadCameraInstance()

            // 프리뷰창을 생성하고 액티비티의 레이아웃으로 지정합니다
            preview = CameraPreview(this, camera)
            camera_preview.addView(preview)

            button_capture.setOnClickListener {
                camera.takePicture(null, null,
                        Camera.PictureCallback { bytes, camera ->

                            val pictureFile = getOutputMediaFile()
                            if (pictureFile == null) {
                                Toast.makeText(applicationContext, "Error saving!!", Toast.LENGTH_SHORT).show()
                            }

                            try {
                                val fos = FileOutputStream(pictureFile)
                                fos.write(bytes)
                                fos.close()

                                camera.startPreview()

                            } catch (e: FileNotFoundException) {
                            } catch (e: IOException) {
                            }
                        })
            }

        } else {
            Toast.makeText(applicationContext, "no camera on this device!", Toast.LENGTH_SHORT).show()
        }

    }

    fun loadCameraInstance(): Camera {
        try {
            camera = Camera.open()
        } catch (e: Exception) {
            // 안될때
        }

        return camera
    }

    /** 이미지를 저장할 파일 객체를 생성합니다  */
    private fun getOutputMediaFile(): File? {
        // SD카드가 마운트 되어있는지 먼저 확인해야합니다
        // Environment.getExternalStorageState() 로 마운트 상태 확인 가능합니다

        val mediaStorageDir = File(getExternalStoragePublicDirectory(
                DIRECTORY_PICTURES), "MyCameraApp")
        // 굳이 이 경로로 하지 않아도 되지만 가장 안전한 경로이므로 추천함.

        // 없는 경로라면 따로 생성한다.
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCamera", "failed to create directory")
                return null
            }
        }

        // 파일명을 적당히 생성. 여기선 시간으로 파일명 중복을 피한다.
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val mediaFile: File

        mediaFile = File(mediaStorageDir.path + File.separator + "IMG_" + timeStamp + ".jpg")
        Log.i("MyCamera", "Saved at" + getExternalStoragePublicDirectory(DIRECTORY_PICTURES))

        return mediaFile
    }

    /** 카메라 하드웨어 지원 여부 확인  */
    private fun checkCameraHardware(context: Context): Boolean {
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // 카메라가 최소한 한개 있는 경우 처리
            Log.i("camera", "Number of available camera : " + Camera.getNumberOfCameras())
            return true
        } else {
            // 카메라가 전혀 없는 경우
            Toast.makeText(applicationContext, "No camera found!", Toast.LENGTH_SHORT).show()
            return false
        }
    }

}