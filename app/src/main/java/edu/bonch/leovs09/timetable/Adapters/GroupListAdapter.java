package edu.bonch.leovs09.timetable.Adapters;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import java.util.List;

import edu.bonch.leovs09.timetable.R;

/**
 * Created by LeoVS09 on 18.09.2016.
 */
public class GroupListAdapter<T> extends ArrayAdapter<T> {


    public GroupListAdapter(@NonNull Context context,
                        @NonNull List<T> objects) {
        super(context, R.layout.group_view, objects);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView,
                                 @NonNull ViewGroup parent) {
        CheckedTextView checkedTextView = (CheckedTextView) super.getView(position, convertView, parent);
//        checkedTextView.setCheckMarkDrawable(R.drawable.checkbox_default);
        return checkedTextView;
    }


}
