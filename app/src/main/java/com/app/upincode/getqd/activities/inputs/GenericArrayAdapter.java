package com.app.upincode.getqd.activities.inputs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public abstract class GenericArrayAdapter<T> extends ArrayAdapter<T> {
    private LayoutInflater mInflater;

    public GenericArrayAdapter(Context context, List<T> objects) {
        super(context, 0, objects);
        this.mInflater = LayoutInflater.from(context);
    }

    public String getText(T object) {
        return object.toString();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.textView.setText(getText(getItem(position)));

        return convertView;
    }

    static class ViewHolder {

        TextView textView;

        private ViewHolder(View rootView) {
            textView = (TextView) rootView.findViewById(android.R.id.text1);
        }
    }
}