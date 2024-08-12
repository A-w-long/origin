package com.sketch.papertracingart.Import;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sketch.papertracingart.R;
import com.sketch.papertracingart.Room.AppDatabase;
import com.sketch.papertracingart.Room.ImageEntry;
import com.sketch.papertracingart.Room.ImageEntryDao;
import com.sketch.papertracingart.Utils.ItemDecoration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 显示图片列表的 Fragment。
 */
public class ImportListFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST_CODE = 202; // 请求码，用于标识图片选择

    private ImageView btnSelectImage; // 选择图片按钮
    private RecyclerView recyclerView; // 显示图片的 RecyclerView
    private ImportListAdapter importListAdapter; // 图片适配器
    private List<String> imagePaths = new ArrayList<>(); // 图片路径列表
    private AppDatabase appDatabase; // 数据库实例
    private ImageEntryDao imageEntryDao; // 图片条目 DAO
    private LinearLayout linearLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载 Fragment 的布局
        View view = inflater.inflate(R.layout.fragment_import_list, container, false);

        // 初始化控件
        btnSelectImage = view.findViewById(R.id.btn_select_image);
        recyclerView = view.findViewById(R.id.recycler_view);

        linearLayout = view.findViewById(R.id.empty);


        // 设置 RecyclerView 的布局管理器和适配器
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 网格布局，每行显示两个图片\
        ItemDecoration itemDecoration = new ItemDecoration(12, 10, 9);
        recyclerView.addItemDecoration(itemDecoration);
        importListAdapter = new ImportListAdapter(imagePaths, requireActivity()); // 初始化适配器
        recyclerView.setAdapter(importListAdapter); // 设置适配器

        // 设置选择图片按钮的点击事件
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("-----------", "onClick ");
                openImagePicker();
            }
        });

        // 初始化数据库和 DAO
        appDatabase = AppDatabase.getInstance(requireContext());
        imageEntryDao = appDatabase.imageEntryDao();

        // 加载图片路径
        loadImagePaths();

        return view;
    }

    /**
     * 打开图片选择器。
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE); // 启动选择图片的活动
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 处理图片选择结果
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData(); // 获取选中的图片 URI
            if (selectedImageUri != null) {
                saveImageToInternalStorage(selectedImageUri); // 保存图片到内部存储
            }
        }
    }

    /**
     * 将选中的图片保存到内部存储。
     *
     * @param uri 选中的图片 URI
     */
    private void saveImageToInternalStorage(Uri uri) {
        try {
            // 从 URI 获取输入流
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream); // 解码图片

            if (bitmap == null) {
                Toast.makeText(getContext(), "无法加载图片", Toast.LENGTH_SHORT).show();
                return;
            }

            // 将图片保存到内部存储
            File internalStorageDir = requireContext().getFilesDir(); // 获取内部存储目录
            File imageFile = new File(internalStorageDir, System.currentTimeMillis() + ".jpg"); // 创建新图片文件
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // 将图片压缩并保存
            outputStream.close();
            inputStream.close();

            String imagePath = imageFile.getAbsolutePath(); // 获取图片绝对路径

            // 检查图片是否已经存在
            new Thread(() -> {
                if (isImageAlreadyExists(imagePath)) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "该图片已经存在", Toast.LENGTH_SHORT).show());
                    imageFile.delete(); // 删除重复的文件
                    return;
                }
                imagePaths.add(imagePath); // 添加图片路径到列表

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        importListAdapter.updateImagePaths(imagePaths);

                        Log.d("--------1111111---", "imagePaths= +" + imagePaths.size());
                        setView(!imagePaths.isEmpty());
                    }
                }); // 更新 RecyclerView

                // 将图片路径插入数据库
                imageEntryDao.insert(new ImageEntry(imagePath));

            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "保存图片失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检查图片是否已经存在。
     *
     * @param imagePath 图片路径
     * @return 图片是否已存在
     */
    private boolean isImageAlreadyExists(String imagePath) {
        File newImageFile = new File(imagePath);

        // 检查 imagePaths 列表
        for (String path : imagePaths) {
            File existingFile = new File(path);
            if (filesAreIdentical(existingFile, newImageFile)) {
                return true;
            }
        }

        // 检查数据库
        List<ImageEntry> imageEntries = imageEntryDao.getAllImages();
        for (ImageEntry imageEntry : imageEntries) {
            File existingFile = new File(imageEntry.getImagePath());
            if (filesAreIdentical(existingFile, newImageFile)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 比较两个文件是否相同。
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 文件是否相同
     */
    private boolean filesAreIdentical(File file1, File file2) {
        if (file1.length() != file2.length()) {
            return false; // 文件长度不同，则文件不同
        }
        // 可以根据需求进一步比较文件内容（例如，通过 MD5 哈希值或逐字节比较）
        return true;
    }

    /**
     * 加载图片路径列表。
     */
    private void loadImagePaths() {
        new Thread(() -> {
            List<ImageEntry> imageEntries = imageEntryDao.getAllImages();
            for (ImageEntry imageEntry : imageEntries) {
                imagePaths.add(imageEntry.getImagePath()); // 添加路径到列表
            }
            requireActivity().runOnUiThread(() -> {
                importListAdapter.updateImagePaths(imagePaths);

                Log.d("--------22222222---", "imagePaths= +" + imagePaths.size());

                setView(!imagePaths.isEmpty());

            }); // 更新 RecyclerView
        }).start();
    }






    private void setView(Boolean hasData) {
        if (hasData) {
            recyclerView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }

    }

}
