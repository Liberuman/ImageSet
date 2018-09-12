package com.sxu.smartpicture.zoomimage;

import android.graphics.Matrix;
import android.graphics.RectF;

import com.facebook.drawee.view.GenericDraweeView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

/*******************************************************************************
 * Description: 适配Fresco的Attacher
 *
 * Author: Freeman
 *
 * Date: 2018/9/12
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class FrescoAttacher extends PhotoViewAttacher {

	private GenericDraweeView imageView;

	public FrescoAttacher(GenericDraweeView image) {
		super(image);
		this.imageView = image;
	}

	@Override
	protected RectF getDisplayRect(Matrix matrix) {
		RectF displayRect = new RectF();
		imageView.getHierarchy().getActualImageBounds(displayRect);
		matrix.mapRect(displayRect);
		return displayRect;
	}
}
