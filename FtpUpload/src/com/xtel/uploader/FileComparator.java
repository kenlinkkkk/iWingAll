package com.xtel.uploader;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File> {

	@Override
	public int compare(File o1, File o2) {
		try {
			return o1.getName().compareTo(o2.getName());
		} catch (Exception e) {
		}
		return 0;
	}
}
