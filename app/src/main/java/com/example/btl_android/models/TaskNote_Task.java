package com.example.btl_android.models;

import java.util.Date;
import java.util.List;

public class TaskNote_Task
{
	private String taskTitle;
	private String taskContent;
	private Date dateCreated;
	private boolean isDone;
	private List<TaskNote_Task> subTasks;

	public TaskNote_Task() {}

	public TaskNote_Task(String taskTitle, String taskContent, Date dateCreated, boolean isDone, List<TaskNote_Task> subTasks)
	{
		this.taskTitle = taskTitle;
		this.taskContent = taskContent;
		this.dateCreated = dateCreated;
		this.isDone = isDone;
		this.subTasks = subTasks;
	}

	public String getTaskTitle()
	{
		return taskTitle;
	}

	public void setTaskTitle(String taskTitle)
	{
		this.taskTitle = taskTitle;
	}

	public String getTaskContent()
	{
		return taskContent;
	}

	public void setTaskContent(String taskContent)
	{
		this.taskContent = taskContent;
	}

	public Date getDateCreated()
	{
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated)
	{
		this.dateCreated = dateCreated;
	}

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	public List<TaskNote_Task> getSubTasks() {
		return this.subTasks;
	}

	public void setSubTasks(List<TaskNote_Task> subTasks) {
		this.subTasks = subTasks;
	}
}
