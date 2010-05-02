/*
 	LogMyNight - Android app for logging night activities. 
 	Copyright (c) 2010 Michael Greifeneder <mikegr@gmx.net>
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package at.madexperts.logmynight;

import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.LiveFolders;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class HistoryActivity extends Activity implements OnClickListener {

	private static final String TAG = HistoryActivity.class.getName();
	private SQLiteDatabase db;
	
	private ListView listView;
	private ListView sumView;
	private Button button;
	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		db = new DatabaseHelper(this).getReadableDatabase();
		
		
		setContentView(R.layout.history);
		listView = (ListView) findViewById(R.id.historyListView);
		sumView = (ListView) findViewById(R.id.historySum);
		button = (Button) findViewById(R.id.historyTimeButton);
		button.setOnClickListener(this);
		showHistory(1);
	}
	
	public void onClick(View v) {
		Log.d(TAG, "onClick from ID: " + v.getId());
		ArrayAdapter<String> a = new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1, 
				new String[] {"last 24h", "last week", "last month", "all"});
		ListView view = new ListView(this);
		view.setAdapter(a);
		
		TextView t = new TextView(this);
		t.setText("Hallo");
		t.setEnabled(true);
		
		//if (v.getId() == button.getId()) {
			PopupWindow window = new PopupWindow(button, 300,300);
 			window.setContentView(t);
			window.showAsDropDown(button);
			
			//window.showAsDropDown(button);
		//}
	}

	private void showHistory(int days) {
		//SELECT d._id, d.name as name, COUNT(l._id) as counter, SUM(l.price) as itemsum FROM drinks d JOIN drinklog l ON d._id = l.drink_id WHERE l.log_time >= datetime('now', '-2 day') GROUP BY d._id, d.name;
		Cursor cursor = db.rawQuery(
				"SELECT d._id as _id, d.name as name, COUNT(l._id) as counter, SUM(l.price) as itemsum " +
				"FROM drinks d JOIN drinklog l ON d._id = l.drink_id " +
				"WHERE l.log_time >= datetime('now', '-1 day') " +
				"GROUP BY d._id, d.name", null); //new String[] {Integer.toString(days)});
		SimpleCursorAdapter adapter =  new SimpleCursorAdapter(this, R.layout.historyrow, cursor, new String[] {"name","counter","itemsum"}, new int[] {R.id.historyRowName, R.id.historyRowAmount, R.id.historyRowSum});		
		
		
		Cursor sumCursor = db.rawQuery(
				"SELECT '0' as _id, 'Sum:' as name, COUNT(l._id) as counter, SUM(l.price) as itemsum " +
				"FROM drinks d JOIN drinklog l ON d._id = l.drink_id " +
				"WHERE l.log_time >= datetime('now', '-1 day') ", null); //new String[] {Integer.toString(days)});
		
		SimpleCursorAdapter sumAdapter =  new SimpleCursorAdapter(this, R.layout.historyrow, sumCursor, new String[] {"name","counter","itemsum"}, new int[] {R.id.historyRowName, R.id.historyRowAmount, R.id.historyRowSum});
		sumView.setAdapter(sumAdapter);
		
		View view = getLayoutInflater().inflate(R.layout.historyrow, null);
		((TextView) view.findViewById(R.id.historyRowName)).setText("Item");
		((TextView) view.findViewById(R.id.historyRowSum)).setText("Sum");
		((TextView) view.findViewById(R.id.historyRowAmount)).setText("Amount");
		
		listView.addHeaderView(view);
		
		listView.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		db.close();
		super.onDestroy();
	}
}
