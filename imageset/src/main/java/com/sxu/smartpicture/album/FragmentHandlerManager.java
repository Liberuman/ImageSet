package com.sxu.smartpicture.album;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/*******************************************************************************
 * Description: Fragment的管理类
 *
 * Author: Freeman
 *
 * Date: 2018/12/24
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class FragmentHandlerManager {

	private Fragment parentFragment;
	private FragmentActivity context;
	private FragmentManager fm;

	/**
	 * 添加Fragment操作
	 */
	public final static int OPERATOR_ADD = 0;
	/**
	 * 替换Fragment操作
	 */
	public final static int OPERATOR_REPLACE = 1;


	public FragmentHandlerManager(FragmentActivity context) {
		this(context, null);
	}

	public FragmentHandlerManager(FragmentActivity context, Fragment parentFragment) {
		if (context == null) {
			return;
		}
		this.context = context;
		this.parentFragment = parentFragment;
		this.fm = (parentFragment != null) ? parentFragment.getChildFragmentManager()
				: context.getSupportFragmentManager();
	}

	public FragmentManager getFragmentManager() {
		return fm;
	}

	public void addFragment(int containerId, Fragment fragment) {
		operatorFragment(containerId, OPERATOR_ADD, fragment, null, true);
	}

	public void addFragment(int containerId, Fragment fragment, boolean addToStack) {
		operatorFragment(containerId, OPERATOR_ADD, fragment, null, addToStack);
	}

	public void addFragment(int containerId, Fragment fragment, String tag) {
		operatorFragment(containerId, OPERATOR_ADD, fragment, tag, true);
	}

	public void addFragment(int containerId, Fragment fragment, String tag, boolean addToStack) {
		operatorFragment(containerId, OPERATOR_ADD, fragment, tag, addToStack);
	}

	public void replaceFragment(int containerId, Fragment fragment) {
		operatorFragment(containerId, OPERATOR_REPLACE, fragment, null, true);
	}

	public void replaceFragment(int containerId, Fragment fragment, boolean addToStack) {
		operatorFragment(containerId, OPERATOR_REPLACE, fragment, null, addToStack);
	}

	public void replaceFragment(int containerId, Fragment fragment, String tag) {
		operatorFragment(containerId, OPERATOR_REPLACE, fragment, tag, true);
	}

	public void replaceFragment(int containerId, Fragment fragment, String tag, boolean addToStack) {
		operatorFragment(containerId, OPERATOR_REPLACE, fragment, tag, addToStack);
	}

	public void removeFragment(Fragment fragment) {
		if (fm.findFragmentById(fragment.getId()) == null && fm.findFragmentByTag(fragment.getTag()) == null) {
			return;
		}

		FragmentTransaction transaction = fm.beginTransaction();
		transaction.remove(fragment);
		transaction.commitAllowingStateLoss();
	}

	public void showFragment(Fragment fragment) {
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.show(fragment);
		transaction.commitAllowingStateLoss();
	}

	public void hideFragment(Fragment fragment) {
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.hide(fragment);
		transaction.commitAllowingStateLoss();
	}

	private void operatorFragment(int containerId, int operator, Fragment fragment, String tag, boolean addToStack) {
		if (context == null) {
			return;
		}

		FragmentTransaction transaction = fm.beginTransaction();
		switch (operator) {
			case OPERATOR_ADD:
				transaction.add(containerId, fragment, tag);
				break;
			case OPERATOR_REPLACE:
				transaction.replace(containerId, fragment, tag);
				break;
			default:
				break;
		}

		if (addToStack) {
			transaction.addToBackStack(null);
		}
		transaction.commitAllowingStateLoss();
	}

	/**
	 * Activity重启时恢复Fragment
	 */
	public void resumeFragment() {
		FragmentManager fm = context.getSupportFragmentManager();
		if (fm != null) {
			for (int i = 0, size = fm.getFragments().size(); i < size; i++) {
				Fragment fragment = fm.getFragments().get(i);
				fragment.onAttach((Context)context);
				resumeChildFragment(fragment);
			}
		}
	}

	/**
	 * 恢复嵌套的Fragment
	 */
	public void resumeChildFragment(Fragment fragment) {
		FragmentManager fm = fragment.getFragmentManager();
		if (fm == null) {
			return;
		}

		for (int i = 0, size = fm.getFragments().size(); i < size; i++) {
			fm.getFragments().get(i).onAttach((Context) context);
		}
	}
}
