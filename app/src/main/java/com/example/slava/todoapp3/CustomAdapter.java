package com.example.slava.todoapp3;

/**
 * Created by slava on 27.09.17.
 */

        import java.util.ArrayList;
        import java.util.TreeSet;
        import android.content.Context;
        import android.graphics.Paint;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.CheckBox;
        import android.widget.ListView;
        import android.widget.TextView;

class CustomAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<String> mData = new ArrayList<String>();
    private ArrayList<Boolean> boolData = new ArrayList<Boolean>();

    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;

    public CustomAdapter(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final String item) {
        mData.add(item);
        boolData.add(false);
        notifyDataSetChanged();
    }

    public void addItem(int index, final String item) {
        mData.add(index,item);
        boolData.add(index, false);
        notifyDataSetChanged();
    }
    public void addItem(final String item, Boolean state) {
        mData.add(item);
        boolData.add(state);
        notifyDataSetChanged();
    }
    public void addItem(int index, final String item, Boolean state) {
        mData.add(index,item);
        boolData.add(index, state);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final String item) {
        mData.add(item);
        boolData.add(false);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    public void clearAdapter(){
        mData.clear();
        boolData.clear();
        sectionHeader.clear();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    public Boolean getItemState(int pos) {return boolData.get(pos);}

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.snippet_item1, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.text);
                    holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb);
                    holder.checkBox.setChecked(boolData.get(position));
                    if(holder.checkBox.isChecked()){
                        holder.textView.setPaintFlags(holder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }else{
                        holder.textView.setPaintFlags(0);
                    }
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.snippet_item2, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.textSeparator);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mData.get(position));
       // holder.checkBox.setChecked(true);
        return convertView;
    }



    public View getViewByPosition(int pos, ListView listView) {
        try {
            final int firstListItemPosition = listView.getFirstVisiblePosition();
            final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

            if (pos < firstListItemPosition || pos > lastListItemPosition) {
                //This may occure using Android Monkey, else will work otherwise
                return listView.getAdapter().getView(pos, null, listView);
            } else {
                final int childIndex = pos - firstListItemPosition;
                return listView.getChildAt(childIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class ViewHolder {
        public TextView textView;
        public CheckBox checkBox;

        public ViewHolder()
        {
        }

        public ViewHolder(TextView textView, CheckBox checkBox)
        {
            this.checkBox = checkBox;
            this.textView = textView;
        }

        public CheckBox getCheckBox()
        {
            return checkBox;
        }

        public void setCheckBox(CheckBox checkBox)
        {
            this.checkBox = checkBox;
        }

        public TextView getTextView()
        {
            return textView;
        }

        public void setTextView(TextView textView)
        {
            this.textView = textView;
        }


    }

}

/*
Если вы попалю сюда, это вероятно означает, что самое страшное вы уже видели.
Эту записку можно считать извинением за приченный вред вашему перфекционизму.
Обычно я стремлюсь к лучшему, но стоит признать, что и это неплохой результат ввиду отсутствия опыта.
 */