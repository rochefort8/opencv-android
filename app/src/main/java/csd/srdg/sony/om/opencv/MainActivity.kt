package csd.srdg.sony.om.opencv

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.SurfaceView
import android.view.WindowManager
import org.opencv.imgproc.Imgproc
import android.R.attr.y
import android.R.attr.x
import android.R.attr.y
import android.R.attr.x
import android.view.Window
import org.opencv.android.*
import org.opencv.core.*


class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2, {

    private var mOpenCvCameraView: CameraBridgeViewBase? = null
    private val mIsJavaCamera = true
    private val mItemSwitchCamera: MenuItem? = null
    private var mRgba: Mat? = null
    private var mRgbaT: Mat? = null
    private var mRgbaF: Mat? = null
    private var mIntermediateMat: Mat? = null
    private var mGray: Mat? = null
    private var mCounter : Int = 0 ;

    private val mLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    Log.i(TAG, "OpenCV loaded successfully")
                    mOpenCvCameraView!!.enableView()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    init {
        Log.i(TAG, "Instantiated new " + this.javaClass)
        mCame
    }

    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "called onCreate")
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_main)

        mOpenCvCameraView = findViewById(R.id.tutorial1_activity_java_surface_view) as CameraBridgeViewBase

        mOpenCvCameraView!!.visibility = SurfaceView.VISIBLE

        //**//
        mOpenCvCameraView!!.setMaxFrameSize(640,480)

        mOpenCvCameraView!!.setCvCameraViewListener(this)

    }

    public override fun onPause() {
        super.onPause()
        if (mOpenCvCameraView != null)
            mOpenCvCameraView!!.disableView()
    }

    public override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback)
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (mOpenCvCameraView != null)
            mOpenCvCameraView!!.disableView();
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        mRgba = Mat(height, width, CvType.CV_8UC4)
        mRgbaT = Mat(height, width, CvType.CV_8UC4)
        mRgbaF = Mat(height, width, CvType.CV_8UC4)
        mIntermediateMat = Mat(height, width, CvType.CV_8UC4)
        mGray = Mat(height, width, CvType.CV_8UC1)

    }

    override fun onCameraViewStopped() {
    }

//    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat? {

//        mCounter++
//        if ((mCounter % 32) != 0) {
//            return mRgba;
//        }

        mRgba = inputFrame.rgba()

        Log.d("HEIGHT=",inputFrame.rgba().size().height.toString())
        Log.d("SIZE=",inputFrame.rgba().size().width.toString())

        var rect_horizontal_margin = 60.0


        var rect_side_length = Math.min(inputFrame.rgba().size().width,inputFrame.rgba().size().height) - (rect_horizontal_margin * 2)


        var pt_lt = Point()
        var pt_rb = Point()

        pt_lt.x = inputFrame.rgba().size().width / 2 - rect_side_length / 2
        pt_lt.y = rect_horizontal_margin
        pt_rb.x = pt_lt.x + rect_side_length
        pt_rb.y = pt_lt.y + rect_side_length

        Imgproc.rectangle(mRgba,pt_lt,pt_rb,Scalar(100.0, 100.0, 0.0),3)


    Imgproc.threshold(inputFrame.gray(),mRgbaT,100.0,255.0,Imgproc.THRESH_BINARY)

        val circles = Mat()

        Imgproc.HoughCircles(mRgbaT, circles, Imgproc.CV_HOUGH_GRADIENT, 1.0, 300.0, 160.0, 50.0, 100)

        val pt = Point()
        val pt1 = Point()
        val pt2 = Point()


        for (i in 0 until circles.cols()) {
            val data = circles.get(0, i)
            pt.x = data[0]
            pt.y = data[1]
            val rho = data[2]
            Imgproc.circle(mRgba, pt, rho.toInt(), Scalar(0.0, 200.0, 0.0), 5)

            pt1.x = pt.x - 50
            pt1.y = pt.y
            pt2.x = pt.x + 50
            pt2.y = pt.y

            Imgproc.line(mRgba, pt1, pt2, Scalar(0.0, 0.0, 200.0), 2)
            pt1.x = pt.x
            pt1.y = pt.y - 50
            pt2.x = pt.x
            pt2.y = pt.y + 50
            Imgproc.line(mRgba, pt1, pt2, Scalar(0.0, 0.0, 200.0), 2)
        }


    return mRgba
    }

    companion object {
        private val TAG = "Tutorial1Activity"
    }
}
