package com.sxu.smartpicture.album;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * @author Freeman
 * @date 2017/11/27
 */


public abstract class CommonAdapter<T extends Object> extends BaseAdapter {

	private Context mContext;
	private List<T> mData;
	private int mResId;

	public CommonAdapter(Context context, List<T> data, @LayoutRes int resId) {
		this.mContext = context;
		this.mData = data;
		this.mResId = resId;
	}

	@Override
	public int getCount() {
		return mData != null ? mData.size() : 0;
	}

	@Override
	public T getItem(int i) {
		return mData != null ? mData.get(i) : null;
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder holder = null;
		if (view == null) {
			view = View.inflate(mContext, mResId, null);
			holder = new ViewHolder(view, i);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
			holder.position = i;
		}
		convert(holder , getItem(i));

		return view;
	}

	public abstract void convert(ViewHolder holder, T params);

	public class ViewHolder {

		private int position;
		private View mContentView;
		private SparseArray<View> childViews = new SparseArray<>();

		public ViewHolder(View contentView, int position) {
			this.mContentView = contentView;
			this.position = position;
		}

		public View getView(@IdRes int resId) {
			if (childViews.get(resId) == null) {
				childViews.put(resId, mContentView.findViewById(resId));
			}

			return childViews.get(resId);
		}

		public void setText(@IdRes int resId, String text) {
			((TextView)getView(resId)).setText(text);
		}

		public void setImageResource(@IdRes int resId, @DrawableRes int drawableResId) {
			((ImageView)getView(resId)).setImageResource(drawableResId);
		}

		public View getContentView() {
			return mContentView;
		}

		public int getPosition() {
			return position;
		}
	}
}
