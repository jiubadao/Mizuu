package com.miz.mizuu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.analytics.tracking.android.EasyTracker;

public class Update extends Activity {

	private CheckBox checkBox, checkBox2;
	private Editor editor;
	private SharedPreferences settings;
	private boolean isMovie;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.update_layout);

		isMovie = getIntent().getExtras().getBoolean("isMovie");

		if (!isMovie)
			setTitle(getString(R.string.updateTvShowsTitle));

		settings = PreferenceManager.getDefaultSharedPreferences(this);
		editor = settings.edit();

		checkBox = (CheckBox) findViewById(R.id.checkBox);
		checkBox.setChecked(false);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				editor.putBoolean("prefsClearLibrary", isChecked);
				editor.commit();
			}
		});

		checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
		checkBox2.setChecked(false);
		checkBox2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				editor.putBoolean("prefsRemoveUnavailable", isChecked);
				editor.commit();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();

		checkBox.setChecked(settings.getBoolean("prefsClearLibrary", false));
		checkBox2.setChecked(settings.getBoolean("prefsRemoveUnavailable", false));
	}

	public void startUpdate(View v) {
		if (isMovie)
			getApplicationContext().startService(new Intent(getApplicationContext(), UpdateMovieService.class));
		else
			getApplicationContext().startService(new Intent(getApplicationContext(), UpdateShowsService.class));
		setResult(1); // end activity and reload Main activity

		finish(); // Leave the Update screen once the update has been started
	}

	@Override
	public void onStart() {
		super.onStart();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default: return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void selectSources(View v) {
		// Add the rowID of the selected movie into a Bundle
		Bundle bundle = new Bundle();
		bundle.putBoolean("fromUpdate", true);
		bundle.putBoolean("isMovie", isMovie);
		bundle.putBoolean("completeScan", checkBox.isChecked());

		// Create a new Intent
		Intent intent = new Intent();
		intent.setClass(this, FileSources.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}
}