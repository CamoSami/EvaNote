package com.example.btl_android.models;

import com.example.btl_android.activities.AttachableNoteActivity;
import com.example.btl_android.databinding.ActivityAttachableNoteBinding;

import java.util.UUID;

public class AttachableNote_Container
{
	private String containerLink;
	private String containerName;
	private String containerContent;
	private int containerType;

	public AttachableNote_Container()
	{
	}

	public AttachableNote_Container(String fileDirectory, String fileName, String containerContent, int containerType)
	{
		this.containerLink = fileDirectory;
		this.containerName = fileName;
		this.containerType = containerType;
		this.containerContent = containerContent;
	}

	public String getContainerLink()
	{
		return containerLink;
	}

	public void setContainerLink(String containerLink)
	{
		this.containerLink = containerLink;
	}

	public String getContainerName()
	{
		return containerName;
	}

	public void setContainerName(String containerName)
	{
		this.containerName = containerName;
	}

	public int getContainerType()
	{
		return containerType;
	}

	public void setContainerType(int containerType)
	{
		this.containerType = containerType;
	}

	public String getContainerContent()
	{
		return containerContent;
	}

	public void setContainerContent(String containerContent)
	{
		this.containerContent = containerContent;
	}
}
