package camera.socket.app.gxj.com.socketphoto;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    Button button_switch;
    SurfaceView sView;
    SurfaceHolder surfaceHolder;
    int screenWidth, screenHeight;
    Camera camera; // 定义系统所用的照相机
    boolean isPreview = false; // 是否在浏览中
    private String ipname = "10.2.9.210";


    Camera.CameraInfo cameraInfo;
    int cameraCount;
    boolean isFront = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //拍照过程屏幕一直处于高亮
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        // 继承Activity堆栈中当前Activity下面的那个Activity的方向
        //SCREEN_ORIENTATION_BEHIND：
        // 横屏(风景照) ，显示时宽度大于高度
        //SCREEN_ORIENTATION_LANDSCAPE：
        // 竖屏 (肖像照) ， 显示时高度大于宽度
        //SCREEN_ORIENTATION_PORTRAIT：
        // 由重力感应器来决定屏幕的朝向,它取决于用户如何持有设备,当设备被旋转时方向会随之在横屏与竖屏之间变化
        //SCREEN_ORIENTATION_SENSOR
        // 忽略物理感应器——即显示方向与物理感应器无关，不管用户如何旋转设备显示方向都不会随着改变("unspecified"设置除外)
        //SCREEN_ORIENTATION_NOSENSOR：
        // 未指定，此为默认值，由Android系统自己选择适当的方向，选择策略视具体设备的配置情况而定，因此不同的设备会有不同的方向选择
        //SCREEN_ORIENTATION_UNSPECIFIED：
        // 用户当前的首选方向
        //SCREEN_ORIENTATION_USER：

        setContentView(R.layout.activity_main);

        cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        Toast.makeText(MainActivity.this,"cameraCount == " + cameraCount,Toast.LENGTH_SHORT).show();


        button_switch = (Button) findViewById(R.id.button_switch);
        button_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isFront){
                    initCamera(0);
                    isFront = false;
                }else{
                    initCamera(1);
                    isFront = true;
                }

            }
        });


        screenWidth = 640;
        screenHeight = 480;
        sView = (SurfaceView) findViewById(R.id.sView); // 获取界面中SurfaceView组件
        surfaceHolder = sView.getHolder(); // 获得SurfaceView的SurfaceHolder

        // 为surfaceHolder添加一个回调监听器
        surfaceHolder.addCallback(new Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initCamera(0); // 默认打开后置摄像头
                isFront = false;
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // 如果camera不为null ,释放摄像头
                if (camera != null) {
                    if (isPreview)
                        camera.stopPreview();
                    camera.release();
                    camera = null;
                }
                System.exit(0);
            }
        });
        // 设置该SurfaceView自己不维护缓冲
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();// 停掉原来摄像头的预览
            camera.release();
            camera = null;

            isPreview = false;
        }
    }

    private void initCamera(int index) {
        releaseCamera();

        if (!isPreview) {
            camera = Camera.open(index);
        }
        if (camera != null && !isPreview) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(screenWidth, screenHeight); // 设置预览照片的大小
                parameters.setPreviewFpsRange(20, 30); // 每秒显示20~30帧
                parameters.setPictureFormat(ImageFormat.NV21); // 设置图片格式
                parameters.setPictureSize(screenWidth, screenHeight); // 设置照片的大小
                // camera.setParameters(parameters); // android2.3.3以后不需要此行代码
                camera.setPreviewDisplay(surfaceHolder); // 通过SurfaceView显示取景画面
                camera.setPreviewCallback(new StreamIt(ipname)); // 设置回调的类
                camera.startPreview(); // 开始预览

                //camera.autoFocus(null); // 自动对焦

                if (parameters.getSupportedFocusModes().contains(
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
                camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。


            } catch (Exception e) {
                e.printStackTrace();
            }
            isPreview = true;
        }
    }


    class StreamIt implements Camera.PreviewCallback {
        private String ipname;

        public StreamIt(String ipname) {
            this.ipname = ipname;
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Size size = camera.getParameters().getPreviewSize();
            try {
                // 调用image.compressToJpeg（）将YUV格式图像数据data转为jpg格式
                YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width,
                        size.height, null);
                if (image != null) {
                    ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                    image.compressToJpeg(new Rect(0, 0, size.width, size.height),
                            80, outstream);
                    outstream.flush();
                    // 启用线程将图像数据发送出去
                    Thread th = new MyThread(outstream, ipname);
                    th.start();
                }
            } catch (Exception ex) {
                Log.e("Sys", "Error:" + ex.getMessage());
            }
        }
    }

}
