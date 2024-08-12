package com.sketch.papertracingart.Camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sketch.papertracingart.R;
import com.sketch.papertracingart.Utils.PermissionUtils;
import com.sketch.papertracingart.Utils.Tracing;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity implements View.OnTouchListener, SeekBar.OnSeekBarChangeListener {

    // 请求码常量
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200; // 相机权限请求码
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 201; // 存储权限请求码
    private static final int PICK_IMAGE_REQUEST_CODE = 202; // 选择图片请求码

    // 界面组件
    private PreviewView previewView; // 预览视图
    private ImageView imageView; // 显示图片的视图
    private SeekBar seekBar; // 透明度调节条
    private ImageView selectImageButton; // 选择图片按钮
    private ImageView flashButton; // 闪光灯按钮
    private ImageView btnBack; // 返回按钮

    // 变换矩阵和触摸事件相关变量
    private Matrix matrix = new Matrix(); // 当前变换矩阵
    private Matrix savedMatrix = new Matrix(); // 保存的变换矩阵
    private PointF startPointF = new PointF(); // 触摸起始点
    private float initialDistance; // 初始触摸距离
    private int mode = MODE_NONE; // 当前模式
    private static final int MODE_NONE = 0; // 无模式
    private static final int MODE_DRAG = 1; // 拖动模式
    private static final int MODE_ZOOM = 2; // 缩放模式


    private Bitmap bitmap;
    private boolean isFlashOn = false; // 闪光灯状态
    private Camera camera; // 相机实例

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera); // 设置布局

        initializeViews(); // 初始化界面组件
        setupListeners(); // 设置监听器
        checkPermissionsAndStartCamera(); // 检查权限并启动相机

        // 接收传递的图片路径
        String imagePath = getIntent().getStringExtra("imagePath");
        Log.d("imagepath", "imagepath: " + imagePath);
        if (imagePath != null) {
            displayImage(imagePath); // 显示图片
        }
    }

    private void initializeViews() {
        // 绑定界面组件
        previewView = findViewById(R.id.preview);
        imageView = findViewById(R.id.image);
        seekBar = findViewById(R.id.seekbar);
        selectImageButton = findViewById(R.id.photo);
        flashButton = findViewById(R.id.flash);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupListeners() {
        // 设置监听器
        seekBar.setOnSeekBarChangeListener(this); // 透明度调节条监听
        selectImageButton.setOnClickListener(v -> openImagePicker()); // 选择图片按钮监听
        flashButton.setOnClickListener(v -> toggleFlash()); // 闪光灯按钮监听
        imageView.setOnTouchListener(this); // 图片触摸监听
        btnBack.setOnClickListener(v -> finish()); // 返回按钮监听
    }

    private void checkPermissionsAndStartCamera() {
        // 获取所需的权限
        String[] permissions = getRequiredPermissions();
        // 检查权限并启动相机
        if (PermissionUtils.hasPermissions(this, permissions)) {
            startCamera();
        } else {
            PermissionUtils.requestPermissions(this, permissions, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 处理权限请求结果
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.handlePermissionsResult(grantResults)) {
                startCamera();
            } else {
                Toast.makeText(this, "相机权限被拒绝，请在设置中启用权限", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.handlePermissionsResult(grantResults)) {
                openImagePicker();
            } else {
                Toast.makeText(this, "存储权限被拒绝，请在设置中启用权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String[] getRequiredPermissions() {
        // 根据 Android 版本获取所需权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            return new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
    }

    private void toggleFlash() {
        // 切换闪光灯状态
        if (camera != null) {
            CameraControl cameraControl = camera.getCameraControl();
            isFlashOn = !isFlashOn; // 切换状态
            cameraControl.enableTorch(isFlashOn); // 启用或禁用闪光灯
            flashButton.setImageResource(isFlashOn ? R.drawable.un_light : R.drawable.light); // 更新图标
        }
    }

    private void openImagePicker() {
        // 打开图片选择器
        String[] permissions = PermissionUtils.getStoragePermissions();
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            // 请求存储权限
            ActivityCompat.requestPermissions(this, permissions, STORAGE_PERMISSION_REQUEST_CODE);
        } else {
            // 启动选择图片的 Intent
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 处理选择的图片
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData(); // 获取选中的图片 URI
            if (selectedImageUri != null) {
                imageView.setImageURI(selectedImageUri); // 显示选中的图片
            }
        }
    }

    private void startCamera() {
        // 启动相机
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get(); // 获取相机提供者
                bindPreview(cameraProvider); // 绑定预览
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this)); // 在主线程中执行
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        // 绑定相机预览
        Preview preview = new Preview.Builder().build(); // 创建预览对象
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK) // 选择后置摄像头
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider()); // 设置预览视图
        cameraProvider.unbindAll(); // 解绑所有相机
        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview); // 绑定生命周期

        if(bitmap!= null){
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            onInitIm(width, height); // 初始化图片位置
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix); // 保存当前矩阵
                startPointF.set(event.getX(), event.getY()); // 记录触摸起始点
                mode = MODE_DRAG; // 设置为拖动模式
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                initialDistance = Tracing.getDistance(event); // 计算初始距离
                if (initialDistance > 10f) {
                    savedMatrix.set(matrix); // 保存当前矩阵
                    mode = MODE_ZOOM; // 设置为缩放模式
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == MODE_DRAG) {
                    matrix.set(savedMatrix); // 恢复保存的矩阵
                    matrix.postTranslate(event.getX() - startPointF.x, event.getY() - startPointF.y); // 计算平移
                } else if (mode == MODE_ZOOM) {
                    float newDistance = Tracing.getDistance(event); // 计算新的距离
                    if (newDistance > 10f) {
                        float scale = newDistance / initialDistance; // 计算缩放比例
                        matrix.set(savedMatrix); // 恢复保存的矩阵
                        matrix.postScale(scale, scale, view.getWidth() / 2f, view.getHeight() / 2f); // 应用缩放
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = MODE_NONE; // 重置模式
                break;
        }

        view.setImageMatrix(matrix); // 更新图片矩阵
        return true; // 处理触摸事件
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // 调整图片透明度
        imageView.setAlpha((100 - progress) / 100f); // 根据进度设置透明度
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // 开始拖动调节条
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // 停止拖动调节条
    }

    // 显示本地图片
    private void displayImage(String imagePath) {
        if (imagePath.startsWith("/data/user/")) {
            // 处理设备存储中的图片路径
            displayImageFromStorage(imagePath);
        } else {
            // 处理 assets 文件夹中的图片路径
            displayImageFromAssets(imagePath);
        }
    }

    // 从设备存储中加载图片
    private void displayImageFromStorage(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);


        } else {
            Toast.makeText(this, "加载图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    // 从 assets 文件夹中加载图片
    private void displayImageFromAssets(String imagePath) {
        try {
            // 获取 AssetManager
            AssetManager assetManager = getAssets();
            // 从 assets 中打开文件
            InputStream inputStream = assetManager.open(imagePath);
            // 将 InputStream 转换为 Bitmap
             bitmap = BitmapFactory.decodeStream(inputStream);
            // 显示 Bitmap
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "加载图片失败: " + e.getMessage(), Toast.LENGTH_SHORT).show(); // 显示错误信息
        }
    }


    // 初始化图片的位置
    private void onInitIm(float imW, float imH) {
        Point screen = getScreen(); // 获取屏幕尺寸
        float newX = screen.x / 2f - imW / 2; // 计算图片水平居中位置
        float newY = screen.y / 2f - imH / 2; // 计算图片垂直居中位置
        matrix.postTranslate(newX, newY); // 设置图片初始位置
        imageView.setImageMatrix(matrix); // 应用矩阵

        Log.d("---------------tt", "------startMatrix-----x=" + newX + "------y=" + newY);
    }


    public   Point getScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        Point point = new Point();
        point.x = width;
        point.y = height;
        return point;
    }

}
