package github.com.jwyjoy.sample_camera

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

import java.io.IOException

/**
 * Created by U02 on 2016-05-30.
 */

class CameraPreview(context: Context, private var mCamera: Camera?) : SurfaceView(context), SurfaceHolder.Callback {
    internal var TAG = "CAMERA"
    private val mHolder: SurfaceHolder

    init {
        // SurfaceHolder 가 가지고 있는 하위 Surface가 파괴되거나 업데이트 될경우 받을 콜백을 세팅한다
        mHolder = holder
        mHolder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Surface가 생성되었으니 프리뷰를 어디에 띄울지 지정해준다. (holder 로 받은 SurfaceHolder에 뿌려준다.
        try {
            val parameters = mCamera?.parameters
            if (resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters?.set("orientation", "portrait")
                mCamera?.setDisplayOrientation(90)
                parameters?.setRotation(90)
            } else {
                parameters?.set("orientation", "landscape")
                mCamera?.setDisplayOrientation(0)
                parameters?.setRotation(0)
            }
            mCamera?.parameters = parameters

            mCamera?.setPreviewDisplay(holder)
            mCamera?.startPreview()
        } catch (e: IOException) {
            Log.d(TAG, "Error setting camera preview: " + e.message)
        }

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // 프리뷰 제거시 카메라 사용도 끝났다고 간주하여 리소스를 전부 반환한다
        mCamera?.stopPreview()
        mCamera?.release()
    }

    private fun getBestPreviewSize(width: Int, height: Int): Camera.Size {
        var result: Camera.Size? = null
        val p = mCamera?.parameters
        for (size in p!!.supportedPreviewSizes) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size
                } else {
                    val resultArea = result.width * result.height
                    val newArea = size.width * size.height

                    if (newArea > resultArea) {
                        result = size
                    }
                }
            }
        }
        return result!!
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
        // 프리뷰를 회전시키거나 변경시 처리를 여기서 해준다.
        // 프리뷰 변경시에는 먼저 프리뷰를 멈춘다음 변경해야한다.

        if (mHolder.surface == null) {
            // 프리뷰가 존재하지 않을때
            return
        }

        // 우선 멈춘다
        try {
            mCamera?.stopPreview()
        } catch (e: Exception) {
            // 프리뷰가 존재조차 하지 않는 경우다
        }


        // 프리뷰 변경, 처리 등을 여기서 해준다.
        val parameters = mCamera?.parameters
        val size = getBestPreviewSize(w, h)
        parameters?.setPreviewSize(size.width, size.height)
        mCamera?.parameters = parameters
        // 새로 변경된 설정으로 프리뷰를 재생성한다
        try {
            mCamera?.setPreviewDisplay(mHolder)
            mCamera?.startPreview()

        } catch (e: Exception) {
            Log.d(TAG, "Error starting camera preview: " + e.message)
        }
    }
}
