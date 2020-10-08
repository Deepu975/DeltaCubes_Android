package com.cavox.konverz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.multidex.MultiDex;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.app.deltacubes.R;

import java.util.ArrayList;

import static com.cavox.utils.GlobalVariables.LOG;


public class ZoneSelectActivity extends Activity {
	ArrayList<String> zoneList1 = new ArrayList<>();
	ArrayList<String> zoneList2 = new ArrayList<>();
	ArrayList<String> zoneList3 = new ArrayList<>();
	TextView mytextview;
	ArrayAdapter<String> arrayAdapter;
	ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zone_select);

		listView=(ListView)findViewById(R.id.listViewZone);
		final EditText slectcountry=(EditText)findViewById(R.id.search_country);
		 mytextview = (TextView) findViewById(R.id.textview);
		mytextview.setVisibility(View.INVISIBLE);

		final ArrayList<String> zoneList = getCountries();
		zoneList1 = getCountries();

		for(String str:zoneList) {
			str = str+"("+getZipCodeByName(str)+")";
				zoneList2.add(str);
			    zoneList3.add(str);
			}


		arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1, zoneList2);
		listView.setAdapter(arrayAdapter); 

if(listView.getCount()<=0) {
	mytextview.setVisibility(View.VISIBLE);
} else {
	mytextview.setVisibility(View.INVISIBLE);
}

		listView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3)
			{
				Intent intent=new Intent();
				intent.putExtra("zone", zoneList1.get(position));
				setResult(999,intent);
				finish();
			}
		});

	slectcountry.addTextChangedListener(new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			LOG.info("Yes on touch up:"+slectcountry.getText().toString());
			if(slectcountry.getText().toString().equals("")) {
				zoneList2.clear();
				zoneList3.clear();
				zoneList1.clear();
				zoneList1 = getCountries();
				for(String str:zoneList) {
					str = str+"("+getZipCodeByName(str)+")";
					zoneList2.add(str);
					zoneList3.add(str);
				}

			} else {
				zoneList2.clear();
				zoneList1.clear();

				//for(String str:zoneList3) {
				for(int ii = 0;ii<zoneList3.size();ii++) {
					String str = zoneList3.get(ii);

					String str1 = str.toLowerCase();
					String str2 = slectcountry.getText().toString().toLowerCase();
					if(str1.contains(str2)) {

						zoneList2.add(str);
						zoneList1.add(zoneList.get(ii));

					}
				}
}

			arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_list_item_1, zoneList2);
			listView.setAdapter(arrayAdapter);
			//LOG.info("count:"+listView.getCount());
			if(listView.getCount()<=0) {
				mytextview.setVisibility(View.VISIBLE);
			} else {
				mytextview.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		public void afterTextChanged(Editable editable) {


		}
	});
	}
	public String getZipCodeByName(String countryName){
		String CountryZipCode="";
		String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
		for(int i=0;i<rl.length;i++){
			String[] g=rl[i].split(",");
			if(g[2].trim().equalsIgnoreCase(countryName.trim())){
				CountryZipCode=g[1];
				break;
			}
		}
		return "+"+CountryZipCode;
	}
	public ArrayList<String> getCountries(){
		ArrayList<String> countries = new ArrayList<String>();

		String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
		for(int i=0;i<rl.length;i++){
			String[] g=rl[i].split(",");
			countries.add(g[2]);
			
		}
		return countries;
	}
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
	@Override
	public void onResume() {
		super.onResume();

		try{

			mytextview.setVisibility(View.INVISIBLE);
		} catch(Exception ex) {}

	}
	@Override
	public void onPause() {
		super.onPause();

		try {

		} catch(Exception ex) {}
	}
}
