package com.sxu.smartpicture.imageloader;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.GenericDraweeView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sxu.smartpicture.R;

/**
 * @author Freeman
 * @date 2017/12/5
 */


public class WrapImageView extends GenericDraweeView {

	private int mPlaceHolder;
	private int mFailureHolder;
	private int mOverlayImageId;
	private int mShape;
	private int mRadius;
	private int mTopLeftRadius;
	private int mTopRightRadius;
	private int mBottomLeftRadius;
	private int mBottomRightRadius;
	private int mBorderWidth;
	private int mBorderColor;
	private boolean mIsBlur;
	private int mBlurRadius;

	public static final int SHAPE_NORMAL = 0;
	public static final int SHAPE_CIRCLE = 1;
	public static final int SHAPE_ROUND = 2;

	public WrapImageView(Context context) {
		this(context, null, 0);
	}

	public WrapImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WrapImageView(Context context, AttributeSet attrs, int theme) {
		super(context, attrs, theme);
		TypedArray arrays = context.obtainStyledAttributes(attrs, R.styleable.WrapImageView);
		mPlaceHolder = arrays.getResourceId(R.styleable.WrapImageView_placeHolder, 0);
		mFailureHolder = arrays.getResourceId(R.styleable.WrapImageView_failureHolder, 0);
		mOverlayImageId = arrays.getResourceId(R.styleable.WrapImageView_overlayImageId, 0);
		mShape = arrays.getInt(R.styleable.WrapImageView_shape, SHAPE_NORMAL);
		mRadius = arrays.getDimensionPixelSize(R.styleable.WrapImageView_radius, 0);
		mTopLeftRadius = arrays.getDimensionPixelSize(R.styleable.WrapImageView_topLeftRadius, 0);
		mTopRightRadius = arrays.getDimensionPixelSize(R.styleable.WrapImageView_topRightRadius, 0);
		mBottomLeftRadius = arrays.getDimensionPixelSize(R.styleable.WrapImageView_bottomLeftRadius, 0);
		mBottomRightRadius = arrays.getDimensionPixelSize(R.styleable.WrapImageView_bottomRightRadius, 0);
		mBorderWidth = arrays.getDimensionPixelSize(R.styleable.WrapImageView_shapeBorderWidth, 0);
		mBorderColor = arrays.getColor(R.styleable.WrapImageView_borderColor, Color.WHITE);
		mIsBlur = arrays.getBoolean(R.styleable.WrapImageView_isBlur, false);
		mBlurRadius = arrays.getInt(R.styleable.WrapImageView_blurRadius, 25);
		arrays.recycle();
	}

	public void setRadius(float radius) {
		this.mRadius = (int) radius;
	}

	public void setRadius(int[] radius) {
		if (radius != null && radius.length == 8) {
			mTopLeftRadius = radius[0] >= radius[1] ? radius[0] : radius[1];
			mTopRightRadius = radius[2] >= radius[3] ? radius[2] : radius[3];
			mBottomRightRadius = radius[4] >= radius[5] ? radius[4] : radius[5];
			mBottomLeftRadius = radius[6] >= radius[7] ? radius[6] : radius[7];
		}
	}

	public void setShape(int shape) {
		this.mShape = shape;
	}

	public void setBorderWidth(int width) {
		this.mBorderWidth = width;
	}

	public void setBorderColor(int color) {
		this.mBorderColor = color;
	}

	public void setPlaceHolder(int placeHolder) {
		this.mPlaceHolder = placeHolder;
	}

	public void setFailureHolder(int failureHolder) {
		this.mFailureHolder = failureHolder;
	}

	public void setOverlayImageId(@DrawableRes int overlayImageId) {
		this.mOverlayImageId = overlayImageId;
	}

	public void setBlur(boolean isBlur) {
		this.mIsBlur = isBlur;
	}

	public void setBlurRadius(int blurRadius) {
		this.mBlurRadius = blurRadius;
	}

	public int getPlaceHolder() {
		return mPlaceHolder;
	}

	public int getFailureHolder() {
		return mFailureHolder;
	}

	public int getOverlayImageId() {
		return mOverlayImageId;
	}

	public int getShape() {
		return mShape;
	}

	public float[] getRadii() {
		if (mTopLeftRadius != 0 || mTopRightRadius != 0 || mBottomRightRadius != 0 || mBottomLeftRadius != 0) {
			float[] radii = {mTopLeftRadius, mTopLeftRadius, mTopRightRadius, mTopRightRadius,
					mBottomRightRadius, mBottomRightRadius, mBottomLeftRadius, mBottomLeftRadius};
			return radii;
		}

		return null;
	}

	public int getRadius() {
		return mRadius;
	}

	public int getBorderWidth() {
		return mBorderWidth;
	}

	public int getBorderColor() {
		return mBorderColor;
	}

	public boolean isBlur() {
		return mIsBlur;
	}

	public int getBlurRadius() {
		return mBlurRadius;
	}

//	@Override
//	public int hashCode() {
//		return super.hashCode();
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj) {
//			return true;
//		} else {
//			return obj != null && obj instanceof WrapImageView
//					&& mPlaceHolder == ((WrapImageView) obj).mPlaceHolder
//					&& mFailureHolder == ((WrapImageView) obj).mFailureHolder
//					&& mShape == ((WrapImageView) obj).mShape
//					&& mRadius == ((WrapImageView) obj).mRadius
//					&& mBorderWidth == ((WrapImageView) obj).mBorderWidth
//					&& mBorderColor == ((WrapImageView) obj).mBorderColor
//					&& mOverlayImageId == ((WrapImageView) obj).mOverlayImageId;
//		}
//
//		// TODO: 2017/12/14  Android系统未对View重写equals方法，采用其他方式来比较配置项
//	}
}
