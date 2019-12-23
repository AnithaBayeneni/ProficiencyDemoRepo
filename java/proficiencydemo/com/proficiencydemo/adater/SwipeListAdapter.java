package mydemo.com.mydemo.adater;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import mydemo.com.mydemo.R;
import mydemo.com.mydemo.model.Rows;

public class SwipeListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Rows> itemList;

    public SwipeListAdapter(Activity activity, List<Rows> itemList) {
        this.activity = activity;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int location) {
        return itemList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.textViewtitle);
        TextView tvDesc = (TextView) convertView.findViewById(R.id.textViewdesc);
        ImageView ivImage = (ImageView) convertView.findViewById(R.id.imageView);

        String title = itemList.get(position).getTitle();
        String desc = itemList.get(position).getDescription();
        String image = itemList.get(position).getImageHref();

        if (isEmptyString(title)) {
            tvTitle.setText("");
        } else {
            tvTitle.setText(title);
        }

        if (isEmptyString(desc)) {
            tvDesc.setText("");
        } else {
            tvDesc.setText(desc);
        }
        if (isEmptyString(image)) {
            Picasso.with(activity).load(R.mipmap.ic_launcher).into(ivImage);
        } else {
            Picasso.with(activity).load(image).fit().placeholder(R.mipmap.ic_launcher).into(ivImage, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                }
            });
        }
        return convertView;
    }

    public static boolean isEmptyString(String text) {
        return (text.equals("null") || text.isEmpty());
    }

    public void refreshList(List<Rows> events) {
        this.itemList.clear();
        this.itemList.addAll(events);
        notifyDataSetChanged();
    }
}
