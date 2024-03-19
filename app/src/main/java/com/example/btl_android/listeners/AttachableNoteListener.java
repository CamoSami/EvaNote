package com.example.btl_android.listeners;

import com.example.btl_android.models.AttachableNote_Container;

public interface AttachableNoteListener
{
	void OnFileClick(AttachableNote_Container container);

	void OnLinkClick(AttachableNote_Container container);

	void OnAddOrEditContainer(int position);

	void OnContainerDelete(int position);

	void OnContainerEdit(int position, AttachableNote_Container container);

	void OnContainerAdd(AttachableNote_Container container);
}
