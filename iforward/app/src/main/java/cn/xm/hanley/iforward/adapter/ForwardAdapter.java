package cn.xm.hanley.iforward.adapter;


import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.xm.hanley.iforward.activity.R;
import cn.xm.hanley.iforward.constants.Constants;
import cn.xm.hanley.iforward.domain.Contact;
import cn.xm.hanley.iforward.sqlite.BlockSQLite;
import cn.xm.hanley.iforward.utils.DataBaseFactoryUtil;

public class ForwardAdapter extends BaseAdapter {

	private ArrayList<Contact> list;
	private Context context;
	private Handler handler;
	
	public ForwardAdapter(Context context,ArrayList<Contact> list,Handler handler){
		this.list = list;
		this.context = context;
		this.handler = handler;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		LinearLayout layout = new LinearLayout(context);
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vi.inflate(R.layout.item_forwar, layout,true);
		Button delete = (Button)layout.findViewById(R.id.forward_delete);
		TextView fname = (TextView)layout.findViewById(R.id.forward_name);
		TextView fnumber = (TextView)layout.findViewById(R.id.forward_number);
		fname.setText(list.get(position).getContactName());
		fnumber.setText(list.get(position).getContactNumber());
		delete.setOnClickListener(new OnClickListener(){


			@Override
			public void onClick(View v) {
				Contact delc = list.get(position);
				String cnumber = delc.getContactNumber();
				list.remove(position);
				BlockSQLite block = DataBaseFactoryUtil.createFordwardDB(context);
				block.deleteContactByNumber(cnumber);
				Message msg  = handler.obtainMessage(Constants.RESPONSE_CODE_SHOW_REFRESH, list);
				msg.sendToTarget();
			}
			
		});
		return layout;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
