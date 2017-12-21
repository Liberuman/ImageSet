package com.sxu.smartpicture.album;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Freeman
 * @date 2017/11/17
 */


public class PhotoDirectoryBean {

	public String id;
	public String name;
	public String thumbPath;
	public String createTime;

	public List<String> photoList = new ArrayList<>();

	public PhotoDirectoryBean() {

	}

	public PhotoDirectoryBean(String id, String name, String thumbPath, String createTime) {
		this.id = id;
		this.name = name;
		this.thumbPath = thumbPath;
		this.createTime = createTime;
	}

	public void putPhoto(String photoPath) {
		photoList.add(photoPath);
	}

	@Override
	public boolean equals(Object obj) {
		return id != null && obj != null && id.equals(((PhotoDirectoryBean)obj).id);
	}
}
