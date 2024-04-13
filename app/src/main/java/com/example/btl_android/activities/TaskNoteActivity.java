package com.example.btl_android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.PopupMenu;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.btl_android.R;
import com.example.btl_android.adapters.TaskNote_SubTaskAdapter;
import com.example.btl_android.databinding.ActivityTaskNoteBinding;
import com.example.btl_android.listeners.TaskNoteListener;
import com.example.btl_android.models.TaskNote;
import com.example.btl_android.models.TaskNote_SubTask;
import com.example.btl_android.models._DefaultNote;
import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.NewlineInputFilter;
import com.example.btl_android.utilities.PreferenceManager;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskNoteActivity extends AppCompatActivity implements TaskNoteListener
{
	private ActivityTaskNoteBinding binding;
	private Date dateCreated = null;
	private boolean isEditing = true;
	private boolean isFavorite = false;
	private String fileName = null;
	private TaskNote.RepeatableType repeatableType = TaskNote.RepeatableType.Minutes;
	private List<TaskNote_SubTask> subTasksList;
	private TaskNote_SubTaskAdapter subTasksAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.binding = ActivityTaskNoteBinding.inflate(this.getLayoutInflater());
		setContentView(this.binding.getRoot());

		//      Filters
		this.binding.titleEditText.setFilters(new InputFilter[]{new NewlineInputFilter()});

		ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
				this,
				R.array.repeatableType,
				R.layout.spinner_item_left
		);
		arrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
		this.binding.spinnerRepeatableType.setAdapter(arrayAdapter);
		this.binding.spinnerRepeatableType.setSelection(2);     //  Hours

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();

		//      Listeners
		this.SetListeners();
		this.ReadToActivity(bundle);
		this.SetEditting(this.isEditing);
	}

	@Override
	public void onBackPressed()
	{
		View view = new View(this);
		view.requestFocus();

		if (
				this.binding.noteTaskCheckBox.isChecked() &&
						this.binding.editTextRepeatableCount.getText().toString().length() != 0 &&
						Integer.parseInt(this.binding.editTextRepeatableCount.getText().toString()) > 0
			)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(TaskNoteActivity.this);

			builder.setTitle("Delete Confirmation");
			builder.setMessage("Since you have completed the Task, do you wish to delete the " +
					"Note?");

			builder.setPositiveButton("Yes :(", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (fileName == null) {
						finish();

						return;
					}

					if (_DefaultNote.DeleteFromStorage(TaskNoteActivity.this, fileName)) {
						TaskNoteActivity.this.finish();
					}
					else {
						Log.d("AttachableNoteActivityTemp", "FileName = " + fileName);

						Toast.makeText(TaskNoteActivity.this, "Failed to delete note",
								Toast.LENGTH_SHORT).show();
					}
				}
			});

			builder.setNegativeButton("Nah", new DialogInterface.OnClickListener()
			{
				@Override public void onClick(DialogInterface dialog, int which)
				{
					TaskNoteActivity.this.SaveNote(view);

					finish();

					return;
				}
			});

			builder.create().show();
		}
		else if (this.SaveNote(view)) {
			super.onBackPressed();
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle("Leave Confirmation");
			builder.setMessage("Note can not be saved, do you still want to leave?");

			builder.setPositiveButton("Yes :(", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					if (TaskNoteActivity.this.fileName != null)
					{
						_DefaultNote.DeleteFromStorage(TaskNoteActivity.this, TaskNoteActivity.this.fileName);
					}

					finish();
				}
			});

			builder.setNegativeButton("Nah", null);

			builder.create().show();

			return;
		}
	}

	private void ReadToActivity(Bundle bundle) {
		if (bundle == null) {
			//      If New
			this.subTasksList = new ArrayList<>();
			this.subTasksAdapter = new TaskNote_SubTaskAdapter(this.subTasksList, this, this);
			this.binding.subTaskRecyclerView.setAdapter(this.subTasksAdapter);
		}
		else {
			//      If only Edit FIle
			PreferenceManager preferenceManager = new PreferenceManager(this);
			this.isEditing = preferenceManager.getBoolean(Constants.SETTINGS_NOTE_DEFAULT_IS_EDITING);

			//      Get File
			String fileName = bundle.getString(Constants.BUNDLE_FILENAME_KEY);

			TaskNote taskNote = (TaskNote) TaskNote.ReadFromStorage(this, fileName);

			this.subTasksList = taskNote.getSubTasks();
			if (this.subTasksList == null) {
				this.subTasksList = new ArrayList<>();
			}

			this.subTasksAdapter = new TaskNote_SubTaskAdapter(this.subTasksList, this, this);
			this.binding.subTaskRecyclerView.setAdapter(this.subTasksAdapter);

			this.fileName = fileName;
			this.binding.titleEditText.setText(taskNote.getTitle());
			this.binding.contentEditText.setText(taskNote.getContent());
			this.isFavorite = taskNote.isFavorite();
			this.dateCreated = taskNote.getDateCreated();

			this.binding.noteTaskCheckBox.setChecked(taskNote.isDone());

			if (taskNote.getRepeatableCount() != 0)
			{
				this.binding.editTextRepeatableCount.setText(Integer.toString(taskNote.getRepeatableCount()));
			}

			switch (taskNote.getRepeatableType()) {
				case Minutes:
					this.binding.spinnerRepeatableType.setSelection(0);

					break;
				case Hours:
					this.binding.spinnerRepeatableType.setSelection(1);

					break;
				case Days:
					this.binding.spinnerRepeatableType.setSelection(2);

					break;
				case Weeks:
					this.binding.spinnerRepeatableType.setSelection(3);

					break;
				case Months:
					this.binding.spinnerRepeatableType.setSelection(4);

					break;
			}

			if (taskNote.isHasDeadline()) {
				Date dueDate = taskNote.getDueDate();

				//      Return Color
				String dateFormat = "EEE, dd/MM/yyyy";
				String timeFormat = "HH:mm";

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
				SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(timeFormat, Locale.getDefault());

				this.binding.dueTimePicker.setText(simpleTimeFormat.format(dueDate));
				this.binding.redoTimeButton.setVisibility(View.VISIBLE);

				this.binding.dueDatePicker.setText(simpleDateFormat.format(dueDate));
				this.binding.redoDateButton.setVisibility(View.VISIBLE);
			}
		}
	}

	private void SetListeners()
	{
		//      Back Button
		this.binding.backButton.setOnClickListener(view ->
		{
			this.onBackPressed();
		});

		//      Menu Button!
		this.binding.settingsButton.setOnClickListener(view ->
		{
			PopupMenu popupMenu = new PopupMenu(view.getContext(), this.binding.settingsButton);

			popupMenu.getMenuInflater().inflate(R.menu.menu_attachable_note, popupMenu.getMenu());

			if (this.isFavorite)
			{
				popupMenu.getMenu().findItem(R.id.menuItemAddToFavorite).setTitle("Remove from Favorites");
			}
			else
			{
				popupMenu.getMenu().findItem(R.id.menuItemAddToFavorite).setTitle("Add to Favorites");
			}

			popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
			{
				@Override
				public boolean onMenuItemClick(MenuItem menuItem)
				{
					int id = menuItem.getItemId();

					if (id == R.id.menuItemAddToFavorite)
					{
						TaskNoteActivity.this.isFavorite = !TaskNoteActivity.this.isFavorite;
					}
					else if (id == R.id.menuItemDelete)
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(TaskNoteActivity.this);

						builder.setTitle("Delete Confirmation");
						builder.setMessage("Are you sure you want to delete the Note?");

						builder.setPositiveButton("Yes :(", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (fileName == null) {
									finish();

									return;
								}

								if (_DefaultNote.DeleteFromStorage(TaskNoteActivity.this, fileName)) {
									TaskNoteActivity.this.finish();
								}
								else {
									Log.d("AttachableNoteActivityTemp", "FileName = " + fileName);

									Toast.makeText(TaskNoteActivity.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
								}
							}
						});

						builder.setNegativeButton("Nah", null);

						builder.create().show();
					}

					return true;
				}
			});

			// Showing the popup menu
			popupMenu.show();
		});

		//      Spinner
		this.binding.spinnerRepeatableType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				switch (position) {
					case 0:
						TaskNoteActivity.this.repeatableType = TaskNote.RepeatableType.Minutes;

						break;
					case 1:
						TaskNoteActivity.this.repeatableType = TaskNote.RepeatableType.Hours;

						break;
					case 2:
						TaskNoteActivity.this.repeatableType = TaskNote.RepeatableType.Days;

						break;
					case 3:
						TaskNoteActivity.this.repeatableType = TaskNote.RepeatableType.Weeks;

						break;
					case 4:
						TaskNoteActivity.this.repeatableType = TaskNote.RepeatableType.Months;

						break;
				}
			}

			@Override public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});

		//      The Pencil / Book Button
		this.binding.editButton.setOnClickListener(view ->
		{
			this.SetEditting(!this.isEditing);
		});

		//      The Checkbox
		this.binding.noteTaskCheckBox.setOnClickListener(view ->
		{
			TypedValue typedValueSecondary = new TypedValue(), typedValuePrimary = new TypedValue();
			Resources.Theme theme = TaskNoteActivity.this.getTheme();
			theme.resolveAttribute(
					com.google.android.material.R.attr.colorOnPrimary,
					typedValuePrimary,
					true
			);
			theme.resolveAttribute(
					com.google.android.material.R.attr.colorOnSecondary,
					typedValueSecondary,
					true
			);

			this.binding.contentEditText.setTextColor(
				this.binding.noteTaskCheckBox.isChecked() ?
						typedValuePrimary.data :
						typedValueSecondary.data
			);

			if (
					this.binding.editTextRepeatableCount.getText().toString().length() > 0 &&
					Integer.parseInt(this.binding.editTextRepeatableCount.getText().toString()) > 0
				)
			{
				Toast.makeText(this, "Note has been completed! Preparing to repeat the TaskNote...",
						Toast.LENGTH_SHORT
				).show();

				(new Handler()).postDelayed(this::RepeatTaskNote, 3000);
			}
			else
			{
				Toast.makeText(this, "Note has been completed! TaskNote does not repeat...",
						Toast.LENGTH_SHORT
				).show();
			}
		});

		//      The Add Button
		this.binding.iconAddSubTask.setOnClickListener(view -> {
			if (this.binding.editTextSubTask.getText().toString().length() > 0)
			{
				this.subTasksList.add(
						new TaskNote_SubTask(
								this.binding.editTextSubTask.getText().toString(),
								false
						)
				);
				this.subTasksAdapter.notifyItemInserted(this.subTasksList.size() - 1);
				this.binding.editTextSubTask.setText("");
			}
		});

		//      Due Date
		this.binding.dueDatePicker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Calendar calendar = Calendar.getInstance();
				String dateFormat = "EEE, dd/MM/yyyy";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());

				if (TaskNoteActivity.this.binding.dueDatePicker.getText().toString().length() > 0) {
					try
					{
						Date date = simpleDateFormat.parse(TaskNoteActivity.this.binding.dueDatePicker.getText().toString());

						calendar.setTime(date);
					}
					catch (ParseException e)
					{
						Log.d("DatePickerTemp", "Parse Error: " + e.getMessage());

						Toast.makeText(TaskNoteActivity.this, "Parse Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}

				DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
				{
					@Override
					public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
					{
						calendar.set(year, month, dayOfMonth);

						TaskNoteActivity.this.binding.dueDatePicker.setText(simpleDateFormat.format(calendar.getTime()));
						TaskNoteActivity.this.binding.redoDateButton.setVisibility(View.VISIBLE);
					}
				};

				new DatePickerDialog(
						TaskNoteActivity.this,
						date,
						calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH),
						calendar.get(Calendar.DAY_OF_MONTH)
				).show();
			}
		});

		//      Due Time
		this.binding.dueTimePicker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Calendar calendar = Calendar.getInstance();
				String timeFormat = "HH:mm";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat, Locale.getDefault());

				if (TaskNoteActivity.this.binding.dueTimePicker.getText().toString().length() > 0) {
					try
					{
						Date time = simpleDateFormat.parse(TaskNoteActivity.this.binding.dueTimePicker.getText().toString());

						calendar.setTime(time);
					}
					catch (ParseException e)
					{
						Log.d("DatePickerTemp", "Parse Error: " + e.getMessage());

						Toast.makeText(TaskNoteActivity.this, "Parse Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}

				TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener()
				{
					@Override public void onTimeSet(TimePicker view, int hourOfDay, int minute)
					{
						calendar.set(1, 1, 1, hourOfDay, minute);

						TaskNoteActivity.this.binding.dueTimePicker.setText(simpleDateFormat.format(calendar.getTime()));
						TaskNoteActivity.this.binding.redoTimeButton.setVisibility(View.VISIBLE);
					}
				};

				TimePickerDialog dialog = new TimePickerDialog(
						TaskNoteActivity.this,
						time,
						calendar.get(Calendar.HOUR_OF_DAY),
						calendar.get(Calendar.MINUTE),
						true
				);

				MaterialTimePicker timePicker = new MaterialTimePicker();
				MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();

				timePicker = builder.setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK).build();

				timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
				timePicker.setMinute(calendar.get(Calendar.MINUTE));
				timePicker.show(TaskNoteActivity.this.getSupportFragmentManager(), "TimePicker");

//				dialog.show();
			}
		});

		//      Redo Date
		this.binding.redoDateButton.setOnClickListener(view -> {
			this.binding.dueDatePicker.setText("");
			this.binding.redoDateButton.setVisibility(View.GONE);
		});

		//      Redo Time
		this.binding.redoTimeButton.setOnClickListener(view -> {
			this.binding.dueTimePicker.setText("");
			this.binding.redoTimeButton.setVisibility(View.GONE);
		});
	}
	private void RepeatTaskNote() {
		if (!this.binding.noteTaskCheckBox.isChecked()) {
			Toast.makeText(this,
					"Repeating the TaskNote canceled...",
					Toast.LENGTH_SHORT
			).show();

			return;
		}

		//      Reset
		this.binding.noteTaskCheckBox.setChecked(!this.binding.noteTaskCheckBox.isChecked());

		for (TaskNote_SubTask subTask : this.subTasksList) {
			subTask.setDone(false);
		}
		this.subTasksAdapter.notifyItemRangeChanged(0, this.subTasksList.size());

		Calendar calendar = Calendar.getInstance();

		String dateFormat = "EEE, dd/MM/yyyy";
		String timeFormat = "HH:mm";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(timeFormat, Locale.getDefault());

		if (this.binding.dueDatePicker.getText().toString().length() > 0)
		{
			try
			{
				Date date = simpleDateFormat.parse(
						this.binding.dueDatePicker.getText().toString()
				);

				Calendar tempCalendar = Calendar.getInstance();
				tempCalendar.setTime(date);

				calendar.set(Calendar.YEAR, tempCalendar.get(Calendar.YEAR));
				calendar.set(Calendar.MONTH, tempCalendar.get(Calendar.MONTH));
				calendar.set(Calendar.DATE, tempCalendar.get(Calendar.DATE));
			}
			catch (ParseException e)
			{
				Log.d("DatePickerTemp", "Parse Error: " + e.getMessage());
			}
		}

		if (this.binding.dueTimePicker.getText().toString().length() > 0)
		{
			try
			{
				Date date = simpleTimeFormat.parse(
						this.binding.dueTimePicker.getText().toString()
				);

				Calendar tempCalendar = Calendar.getInstance();
				tempCalendar.setTime(date);

				calendar.set(Calendar.HOUR_OF_DAY, tempCalendar.get(Calendar.HOUR_OF_DAY));
				calendar.set(Calendar.MINUTE, tempCalendar.get(Calendar.MINUTE));
			}
			catch (ParseException e)
			{
				Log.d("DatePickerTemp", "Parse Error: " + e.getMessage());
			}
		}

		//      Next
		Date dueDate = TaskNote.AcceleDate(
				calendar.getTime(),
				Integer.parseInt(this.binding.editTextRepeatableCount.getText().toString()),
				this.repeatableType
		);

		//      Return Color
		this.binding.dueTimePicker.setText(simpleTimeFormat.format(dueDate));
		this.binding.redoTimeButton.setVisibility(View.VISIBLE);

		this.binding.dueDatePicker.setText(simpleDateFormat.format(dueDate));
		this.binding.redoDateButton.setVisibility(View.VISIBLE);

		this.binding.contentEditText.setTextColor(
				this.getResources().getColor(R.color.primary_text,
						this.getTheme()
				)
		);
	}

	private boolean SaveNote(View view) {
		view.requestFocus();

		if (this.dateCreated == null)
		{
			this.dateCreated = Calendar.getInstance().getTime();
		}

		boolean hasDeadline = false;
		Calendar calendar = Calendar.getInstance();

		String dateFormat = "EEE, dd/MM/yyyy";
		String timeFormat = "HH:mm";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(timeFormat, Locale.getDefault());

		if (this.binding.dueDatePicker.getText().toString().length() > 0)
		{
			hasDeadline = true;

			try
			{
				Date date = simpleDateFormat.parse(
						this.binding.dueDatePicker.getText().toString()
				);

				Calendar tempCalendar = Calendar.getInstance();
				tempCalendar.setTime(date);

				calendar.set(Calendar.YEAR, tempCalendar.get(Calendar.YEAR));
				calendar.set(Calendar.MONTH, tempCalendar.get(Calendar.MONTH));
				calendar.set(Calendar.DATE, tempCalendar.get(Calendar.DATE));
			}
			catch (ParseException e)
			{
				Log.d("DatePickerTemp", "Parse Error: " + e.getMessage());
			}
		}

		if (this.binding.dueTimePicker.getText().toString().length() > 0)
		{
			hasDeadline = true;

			try
			{
				Date date = simpleTimeFormat.parse(
						this.binding.dueTimePicker.getText().toString()
				);

				Calendar tempCalendar = Calendar.getInstance();
				tempCalendar.setTime(date);

				calendar.set(Calendar.HOUR_OF_DAY, tempCalendar.get(Calendar.HOUR_OF_DAY));
				calendar.set(Calendar.MINUTE, tempCalendar.get(Calendar.MINUTE));
			}
			catch (ParseException e)
			{
				Log.d("DatePickerTemp", "Parse Error: " + e.getMessage());
			}
		}

		hasDeadline =
				hasDeadline ||
				(this.binding.editTextRepeatableCount.getText().toString().length() > 0 &&
						Integer.parseInt(this.binding.editTextRepeatableCount.getText().toString()) > 0
				);

		Log.d("DatePickerTemp", "Has Deadline: " + hasDeadline);

		TaskNote taskNote = new TaskNote(
				this.fileName,
				this.binding.titleEditText.getText().toString(),
				this.dateCreated,
				Calendar.getInstance().getTime(),
				this.binding.contentEditText.getText().toString(),
				this.isFavorite,
				this.binding.editTextRepeatableCount.getText().toString().length() == 0 ?
					0 :
					Integer.parseInt(this.binding.editTextRepeatableCount.getText().toString()),
				this.repeatableType,
				calendar.getTime(),
				this.subTasksList,
				hasDeadline,
				this.binding.noteTaskCheckBox.isChecked()
		);

		//      Not validated, just simply want to leave
		if (!taskNote.Validate()) {
//			this.isConfirmed = true;
//			this.finish();

			return false;
		}

		return taskNote.WriteToStorage(this, false);
	}

	public void SetEditting(boolean isEditting)
	{
		if (!isEditting)
		{
			this.binding.editButton.setImageResource(R.drawable.icon_read);

			this.binding.spinnerRepeatableType.setEnabled(false);

			if (this.binding.editTextRepeatableCount.getText().toString().length() == 0) {
				this.binding.editTextRepeatableCount.setVisibility(View.GONE);
				this.binding.spinnerRepeatableType.setVisibility(View.GONE);
				this.binding.repeatableView.setVisibility(View.GONE);
			}

			if (this.binding.dueDatePicker.getText().toString().length() == 0) {
				this.binding.dueDatePicker.setVisibility(View.GONE);
				this.binding.dueDateView.setVisibility(View.GONE);
			}
			else {
				this.binding.redoDateButton.setVisibility(View.GONE);
			}

			if (this.binding.dueTimePicker.getText().toString().length() == 0) {
				this.binding.dueTimePicker.setVisibility(View.GONE);
				this.binding.dueTimeView.setVisibility(View.GONE);
			}
			else {
				this.binding.redoTimeButton.setVisibility(View.GONE);
			}

			this.binding.iconAddSubTask.setVisibility(View.GONE);
			this.binding.editTextSubTask.setVisibility(View.GONE);

			if (this.subTasksList.size() == 0)
			{
				this.binding.subTaskView.setVisibility(View.GONE);
			}

			this.binding.contentEditText.setFocusable(false);
			this.binding.contentEditText.setFocusableInTouchMode(false);
			this.binding.contentEditText.setHint("");

			this.binding.titleEditText.setFocusable(false);
			this.binding.titleEditText.setFocusableInTouchMode(false);
			this.binding.titleEditText.setHint("");

			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

			imm.hideSoftInputFromWindow(this.binding.contentEditText.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(this.binding.titleEditText.getWindowToken(), 0);

			if (this.binding.editTextRepeatableCount.getText().toString().length() == 0)
			{
				this.binding.editTextRepeatableCount.setVisibility(View.GONE);
				this.binding.spinnerRepeatableType.setVisibility(View.GONE);
				this.binding.repeatableView.setVisibility(View.GONE);
			}
			else
			{
				this.binding.editTextRepeatableCount.setFocusable(false);
				this.binding.editTextRepeatableCount.setFocusableInTouchMode(false);
				this.binding.editTextRepeatableCount.setHint("");

				imm.hideSoftInputFromWindow(this.binding.editTextRepeatableCount.getWindowToken(), 0);
			}

			//      Due Date
			this.binding.dueDatePicker.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v)
				{

				}
			});

			//      Due Time
			this.binding.dueTimePicker.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v)
				{

				}
			});
		}
		else
		{
			this.binding.editButton.setImageResource(R.drawable.icon_edit);

			this.binding.spinnerRepeatableType.setEnabled(true);

			if (this.binding.editTextRepeatableCount.getText().toString().length() == 0) {
				this.binding.editTextRepeatableCount.setVisibility(View.VISIBLE);
				this.binding.spinnerRepeatableType.setVisibility(View.VISIBLE);
				this.binding.repeatableView.setVisibility(View.VISIBLE);
			}

			if (this.binding.dueDatePicker.getText().toString().length() == 0) {
				this.binding.dueDatePicker.setVisibility(View.VISIBLE);
				this.binding.dueDateView.setVisibility(View.VISIBLE);
			}
			else {
				this.binding.redoDateButton.setVisibility(View.VISIBLE);
			}

			if (this.binding.dueTimePicker.getText().toString().length() == 0) {
				this.binding.dueTimePicker.setVisibility(View.VISIBLE);
				this.binding.dueTimeView.setVisibility(View.VISIBLE);
			}
			else {
				this.binding.redoTimeButton.setVisibility(View.VISIBLE);
			}

			this.binding.iconAddSubTask.setVisibility(View.VISIBLE);
			this.binding.editTextSubTask.setVisibility(View.VISIBLE);

			if (this.subTasksList.size() != 0)
			{
				this.binding.subTaskView.setVisibility(View.VISIBLE);
			}

			this.binding.contentEditText.setFocusable(true);
			this.binding.contentEditText.setFocusableInTouchMode(true);
			this.binding.contentEditText.setHint(R.string.this_is_the_note_s_text_content);

			this.binding.titleEditText.setFocusable(true);
			this.binding.titleEditText.setFocusableInTouchMode(true);
			this.binding.titleEditText.setHint(R.string.title_of_the_note);

			if (this.binding.editTextRepeatableCount.getText().toString().length() == 0)
			{
				this.binding.editTextRepeatableCount.setVisibility(View.VISIBLE);
				this.binding.spinnerRepeatableType.setVisibility(View.VISIBLE);
				this.binding.repeatableView.setVisibility(View.VISIBLE);
			}
			else
			{
				this.binding.editTextRepeatableCount.setFocusable(true);
				this.binding.editTextRepeatableCount.setFocusableInTouchMode(true);
				this.binding.editTextRepeatableCount.setHint("Repeat every...");
			}

			//      Due Date
			this.binding.dueDatePicker.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Calendar calendar = Calendar.getInstance();
					String dateFormat = "EEE, dd/MM/yyyy";
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());

					if (TaskNoteActivity.this.binding.dueDatePicker.getText().toString().length() > 0) {
						try
						{
							Date date = simpleDateFormat.parse(TaskNoteActivity.this.binding.dueDatePicker.getText().toString());

							calendar.setTime(date);
						}
						catch (ParseException e)
						{
							Log.d("DatePickerTemp", "Parse Error: " + e.getMessage());

							Toast.makeText(TaskNoteActivity.this, "Parse Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}

					DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
					{
						@Override
						public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
						{
							calendar.set(year, month, dayOfMonth);

							TaskNoteActivity.this.binding.dueDatePicker.setText(simpleDateFormat.format(calendar.getTime()));
							TaskNoteActivity.this.binding.redoDateButton.setVisibility(View.VISIBLE);
						}
					};

					new DatePickerDialog(
							TaskNoteActivity.this,
							date,
							calendar.get(Calendar.YEAR),
							calendar.get(Calendar.MONTH),
							calendar.get(Calendar.DAY_OF_MONTH)
					).show();
				}
			});

			//      Due Time
			this.binding.dueTimePicker.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Calendar calendar = Calendar.getInstance();
					String timeFormat = "HH:mm";
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat, Locale.getDefault());

					if (TaskNoteActivity.this.binding.dueTimePicker.getText().toString().length() > 0) {
						try
						{
							Date time = simpleDateFormat.parse(TaskNoteActivity.this.binding.dueTimePicker.getText().toString());

							calendar.setTime(time);
						}
						catch (ParseException e)
						{
							Log.d("DatePickerTemp", "Parse Error: " + e.getMessage());

							Toast.makeText(TaskNoteActivity.this, "Parse Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}

					TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener()
					{
						@Override public void onTimeSet(TimePicker view, int hourOfDay, int minute)
						{
							calendar.set(1, 1, 1, hourOfDay, minute);

							TaskNoteActivity.this.binding.dueTimePicker.setText(simpleDateFormat.format(calendar.getTime()));
							TaskNoteActivity.this.binding.redoTimeButton.setVisibility(View.VISIBLE);
						}
					};

					new TimePickerDialog(
							TaskNoteActivity.this,
							time,
							calendar.get(Calendar.HOUR_OF_DAY),
							calendar.get(Calendar.MINUTE),
							true
					).show();
				}
			});
		}

		this.isEditing = isEditting;
		this.subTasksAdapter.SetEditing(isEditting);
	}

	@Override public void OnSubTaskDelete(int position)
	{
		this.subTasksList.remove(position);
		this.subTasksAdapter.notifyItemRemoved(position);
	}
}