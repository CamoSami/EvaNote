package com.example.btl_android.models;

public class TaskNote_SubTask
{
	private String taskTitle;
	private boolean isDone;

	public TaskNote_SubTask()
	{
	}

	public TaskNote_SubTask(String taskTitle, boolean isDone)
	{
		this.taskTitle = taskTitle;
		this.isDone = isDone;
	}

	public String getTaskTitle()
	{
		return taskTitle;
	}

	public void setTaskTitle(String taskTitle)
	{
		this.taskTitle = taskTitle;
	}

	public boolean isDone()
	{
		return isDone;
	}

	public void setDone(boolean isDone)
	{
		this.isDone = isDone;
	}
}
