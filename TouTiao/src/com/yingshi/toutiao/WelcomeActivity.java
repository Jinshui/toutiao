package com.yingshi.toutiao;

import com.yingshi.toutiao.actions.ParallelTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class WelcomeActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		new ParallelTask<Void>() {
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
				return null;
			}
			
			protected void onPostExecute(Void result){
				Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		}.execute();
	}
}
