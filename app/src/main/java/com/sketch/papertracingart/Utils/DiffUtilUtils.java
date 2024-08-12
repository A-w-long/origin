package com.sketch.papertracingart.Utils;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class DiffUtilUtils {

    /**
     * 创建一个 DiffUtil.Callback 实例
     *
     * @param oldList 旧数据列表
     * @param newList 新数据列表
     * @return DiffUtil.Callback 实例
     */
    public static DiffUtil.Callback createImageDiffCallback(List<String> oldList, List<String> newList) {
        return new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }
        };
    }
}
