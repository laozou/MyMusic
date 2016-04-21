package test9.mymusic.ListViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import test9.mymusic.R;

/**
 * Created by 123 on 2016/3/14.
 */
public class ListViewAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private List<InformationList> mInformationLists = new ArrayList<>();

    public ListViewAdapter(Context context, List<InformationList> informationLists) {
        mInformationLists = informationLists;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mInformationLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mInformationLists.get(position);
    }

    @Override
    public long getItemId(int position) {return position;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        //返回视图
        if(convertView == null)
        {
            convertView= mLayoutInflater.inflate(R.layout.list_view_layout, null);
        }

        //获取控件
        textView=(TextView) convertView.findViewById(R.id.textView);
        //数据绑定
        textView.setText(mInformationLists.get(position).getmInfo());

        return convertView;
    }
}
