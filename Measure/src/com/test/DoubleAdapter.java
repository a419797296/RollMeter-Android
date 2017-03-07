package com.test;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DoubleAdapter extends BaseAdapter {
	private List<String> datas = new ArrayList<String>();
	private Context context;
	View oldView;
	
	public DoubleAdapter(Context context,List<String> datas){
		this.context = context;
		this.datas = datas;
	}
	@Override
	public int getCount() {
		return datas.size() % 2 == 0 ? datas.size() / 2 : datas.size() / 2 + 1;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHodler hodler = null;
		//if(convertView == null){
			hodler = new ViewHodler();
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.item, null);
			hodler.textView1 = (TextView) convertView.findViewById(R.id.text1);
			hodler.textView2 = (TextView) convertView.findViewById(R.id.text2);
			hodler.layout1 = (LinearLayout) convertView.findViewById(R.id.item_layout1);
			hodler.layout2 = (LinearLayout) convertView.findViewById(R.id.item_layout2);
			convertView.setTag(hodler);
		//}else{
			//hodler = (ViewHodler) convertView.getTag();
		//}
		
		String item1 = "";
		String item2 = "";
		
		if(position * 2 + 1 < datas.size()){
			item1 = datas.get(position * 2);
			item2 = datas.get(position * 2 + 1);
		}else if(position * 2 + 1 == datas.size()){
			item1 = datas.get(position * 2);
			item2 = "";
		}
		
		if(item1 != null){
			hodler.textView1.setText(item1);
			//hodler.layout1.setOnClickListener(this);
			hodler.layout1.setTag(item1);
            if(item1.equals("")){
                hodler.textView1.setBackground(null);
            }else
			{
				hodler.textView1.setBackgroundResource(R.drawable.rollno);
			}
		}
		
		if(item2 != null){
			hodler.textView2.setText(item2);
			//hodler.layout2.setOnClickListener(this);
			hodler.layout2.setTag(item2);
		}
		
		return convertView;
	}
	
	class ViewHodler {
		TextView textView1;
		TextView textView2;
		LinearLayout layout1;
		LinearLayout layout2;
	}


    /*
	public void onClick(View v) {
		int pos = v.getId();
		View view=null;

		if (oldView != null) {

            oldView.setBackgroundDrawable(null);

		}

		oldView = v;
		v.setBackgroundResource(R.drawable.selectroll);

		//Toast.makeText(context, v.getTag().toString(), Toast.LENGTH_LONG).show();
	}*/

}
