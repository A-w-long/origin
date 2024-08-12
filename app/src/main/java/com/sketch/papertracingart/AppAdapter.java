//package com.Sketch.papertracingart;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.ApplicationInfo;
//import android.content.pm.PackageManager;
//import android.graphics.drawable.Drawable;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CompoundButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.widget.AppCompatImageView;
//import androidx.appcompat.widget.SwitchCompat;
//import androidx.recyclerview.widget.DiffUtil;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.tool.applock.AppLockSkin;
//import com.tool.applock.R;
//import com.tool.applock.activity.SetPWDActivity;
//import com.tool.applock.fragment.LockFragment;
//import com.tool.applock.ironad.IronSourceAd;
//import com.tool.applock.mydb.Mydata;
//import com.tool.applock.mydb.Mydatabase;
//import com.tool.applock.tools.Mytools;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//import kotlin.Unit;
//import kotlin.jvm.functions.Function1;
//
//public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppVH> {
//    private Context mycontext;
//    private List<Mydata> mydataList = new ArrayList<>();
//    private PackageManager packageManager;
//    private boolean mtost = true;
//    public static SharedPreferences keysp;
//    public static SharedPreferences.Editor keyeditor;
//    private int keyid;
//
//    private Activity activity;
//
//    public AppAdapter(Context context, Activity activity1) {
//        activity = activity1;
//
//        mycontext = context;
//        packageManager = context.getPackageManager();
//        keysp = mycontext.getSharedPreferences("key", Context.MODE_PRIVATE);
//    }
//
//    public void setData(List<Mydata> list, boolean istost) {
//        List<Mydata> old = mydataList;
//        mydataList = list;
//        mtost = istost;
//
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffCallback(old, mydataList));
//
//        diffResult.dispatchUpdatesTo(this);
//
//
//    }
//
//    @NonNull
//    @Override
//    public AppVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(mycontext).inflate(R.layout.item_app, parent, false);
//        return new AppVH(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull AppVH holder, int position) {
//        Mydata mydata = mydataList.get(position);
//        String appname = mydata.getAppName();
//
//
//        Log.d("-------", "-----------------" + position + "---" + appname);
//        boolean lock = mydata.isLock();
//        holder.switchCompat.setSelected(lock);
////        Log.e("1111", String.valueOf(holder.switchCompat.isChecked())+"    "+lock);
////        holder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
////            @Override
////            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                Mytools.runIO(new Runnable() {
////                    @Override
////                    public void run() {
////                        mydata.setLock(isChecked);
////                        Mydatabase.getInstance().mydao().update(mydata);
////
////                    }
////                });
////                String s;
////                if (isChecked) {
////                    s = String.format(mycontext.getString(R.string.text_locked), appname);
////                } else {
////                    s = String.format(mycontext.getString(R.string.text_unlocked), appname);
////                }
////
//////                if (mtost){
////                    Toast.makeText(mycontext, s, Toast.LENGTH_SHORT).show();
//////                }
////
////            }
////        });
//        holder.switchCompat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                keyid = getkey();
//                keyid++;
//                keyeditor = keysp.edit();
//                if (keyid % 5 == 0) {
//                    Log.e("zzj", "111111111111");
//                    IronSourceAd.INSTANCE.showAd(activity, AppLockSkin.place2, new Function1<Boolean, Unit>() {
//                        @Override
//                        public Unit invoke(Boolean aBoolean) {
//                            keyeditor.putInt("key", keyid);
//                            keyeditor.apply();
//                            Log.e("zzj", String.valueOf(keyid));
//                            if (holder.switchCompat.isSelected()) {
//                                holder.switchCompat.setSelected(false);
//                            } else {
//                                holder.switchCompat.setSelected(true);
//                            }
//                            Mytools.runIO(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mydata.setLock(holder.switchCompat.isSelected());
//                                    Mydatabase.getInstance().mydao().update(mydata);
//
//                                }
//                            });
//                            String s;
//                            if (holder.switchCompat.isSelected()) {
//                                s = String.format(mycontext.getString(R.string.text_locked), appname);
//                            } else {
//                                s = String.format(mycontext.getString(R.string.text_unlocked), appname);
//                            }
//
//
//                            Toast.makeText(mycontext, s, Toast.LENGTH_SHORT).show();
//                            return null;
//                        }
//                    });
//                }else {
//                    keyeditor.putInt("key", keyid);
//                    keyeditor.apply();
//                    Log.e("zzj", String.valueOf(keyid));
//                    if (holder.switchCompat.isSelected()) {
//                        holder.switchCompat.setSelected(false);
//                    } else {
//                        holder.switchCompat.setSelected(true);
//                    }
//                    Mytools.runIO(new Runnable() {
//                        @Override
//                        public void run() {
//                            mydata.setLock(holder.switchCompat.isSelected());
//                            Mydatabase.getInstance().mydao().update(mydata);
//
//                        }
//                    });
//                    String s;
//                    if (holder.switchCompat.isSelected()) {
//                        s = String.format(mycontext.getString(R.string.text_locked), appname);
//                    } else {
//                        s = String.format(mycontext.getString(R.string.text_unlocked), appname);
//                    }
//
//
//                    Toast.makeText(mycontext, s, Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
//
//        });
//
//        holder.textView.setText(mydata.getAppName());
//        try {
//            Drawable appLogo = getLogo(mydata.getPackageName());
//            holder.imageView.setImageDrawable(appLogo);
//        } catch (PackageManager.NameNotFoundException e) {
//
//        }
//    }
//
//    public static int getkey() {
//        return keysp.getInt("key", 0);
//    }
//
//    @Override
//    public int getItemCount() {
//        return mydataList.size();
//    }
//
//    private Drawable getLogo(String packageName) throws PackageManager.NameNotFoundException {
//        ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
//        return packageManager.getApplicationIcon(appInfo);
//    }
//
//    public static class AppVH extends RecyclerView.ViewHolder {
//
//        private ImageView imageView;
//        private TextView textView;
//        private AppCompatImageView switchCompat;
//
//        public AppVH(@NonNull View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.logo);
//            textView = itemView.findViewById(R.id.name);
//            switchCompat = itemView.findViewById(R.id.app_switch);
//        }
//
//    }
//
//    private static class MyDiffCallback extends DiffUtil.Callback {
//        private List<Mydata> oldItems;
//        private List<Mydata> newItems;
//
//        MyDiffCallback(List<Mydata> oldItems, List<Mydata> newItems) {
//            this.oldItems = oldItems;
//            this.newItems = newItems;
//        }
//
//        @Override
//        public int getOldListSize() {
//            return oldItems.size();
//        }
//
//        @Override
//        public int getNewListSize() {
//            return newItems.size();
//        }
//
//        @Override
//        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//            return Objects.equals(oldItems.get(oldItemPosition).getPackageName(), newItems.get(newItemPosition).getPackageName());
//        }
//
//        @Override
//        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//            Mydata newItem = newItems.get(newItemPosition);
//            Mydata oldItem = oldItems.get(oldItemPosition);
//            return oldItem.isLock() == newItem.isLock();
//        }
//    }
//
//}
