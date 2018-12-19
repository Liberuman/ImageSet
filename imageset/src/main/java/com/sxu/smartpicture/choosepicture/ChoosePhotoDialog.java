package com.sxu.smartpicture.choosepicture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sxu.smartpicture.R;

import java.util.List;

/*******************************************************************************
 * FileName: CommonChooseDialog
 * <p>
 * Description:
 * <p>
 * Author: Freeman
 * <p>
 * Version: v1.0
 * <p>
 * Date: 16/10/19
 * <p>
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/

public class ChoosePhotoDialog extends AlertDialog {

    private TextView cancelText;
    private ListView listView;
    private String[] items;

    private int textSize;
    private int textColor;

    public ChoosePhotoDialog(Context context, List<String> itemList) {
        this(context, itemList != null ? itemList.toArray(new String[itemList.size()]) : null);
    }

    public ChoosePhotoDialog(Context context, String[] items) {
        super(context, R.style.CommonDialog);
        this.items = items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common_choose_layout);
        listView = (ListView) findViewById(R.id.listView);
        cancelText = (TextView) findViewById(R.id.cancel_text);

        if (items != null && items.length > 0) {
            textSize = 18;
            textColor = Color.parseColor("#333333");
            listView.setAdapter(new MenuAdapter());
        }

        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setOnItemListener(final AdapterView.OnItemClickListener listener) {
        if (listView != null && listener != null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (id >= 0) {
                        listener.onItemClick(parent, view, (int) id, id);
                        dismiss();
                    }
                }
            });
        }
    }

    public void show() {
        super.show();
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = Resources.getSystem().getDisplayMetrics().widthPixels;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    private class MenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getContext());
            textView.setTextSize(textSize);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) (Resources.getSystem().getDisplayMetrics().density * 50));
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            textView.setText(items[position]);
            textView.setTextColor(textColor);

            return textView;
        }
    }
}

