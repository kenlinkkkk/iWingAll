package com.ligerdev.cmms.component.tree;

public abstract interface ITreeListener {
	
	public abstract void filterTree(Object paramObject);
	public abstract void treeValueChanged(Object paramObject);
}