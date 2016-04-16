package blm.newandroid.com.toplistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by BLM on 2016/4/15.
 */
public class MyAdapter extends BaseAdapter {
    private ViewHolder viewHolder;
    private LayoutInflater inflater;
//    private LinkedList<String> linkList;
    private List<String> list;

    public MyAdapter(Context context,List<String> list) {
        inflater = LayoutInflater.from(context);
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.items, null);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.items_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(list.get(position));

        return convertView;
    }




}
