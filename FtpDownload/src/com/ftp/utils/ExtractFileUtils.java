package com.ftp.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;

public class ExtractFileUtils {

	public static void unZip(String zipFilePath, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it
				new File(new File(filePath).getParent()).mkdirs();
				extractFile(zipIn, filePath);
			} else {
				// if the entry is a directory, make the directory
				File dir = new File(filePath);
				dir.mkdirs();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	private static void extractFile(ZipInputStream zipIn, String filePath)
			throws IOException {
		FileOutputStream in = new FileOutputStream(filePath);
		BufferedOutputStream bos = new BufferedOutputStream(in);
		byte[] bytesIn = new byte[4096];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
		in.close();
	}

	public static void unGZip(String zipFilePath, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		byte[] buffer = new byte[1024];
		String fileName = new File(zipFilePath).getName();
		fileName = fileName.substring(0, fileName.lastIndexOf("."));

		FileInputStream in = new FileInputStream(zipFilePath);
		GZIPInputStream gzis = new GZIPInputStream(in);
		FileOutputStream out = new FileOutputStream(destDirectory + File.separator + fileName);

		int len;
		while ((len = gzis.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
		gzis.close();
		out.close();
		in.close();
	}

	public static void unTar(String zipFilePath, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		FileInputStream in = new FileInputStream(new File(zipFilePath));
		BufferedInputStream buffer = new BufferedInputStream(in);
		// GzipCompressorInputStream gzip = new
		// GzipCompressorInputStream(buffer);
		TarArchiveInputStream tarIn = new TarArchiveInputStream(buffer);

		TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
		// tarIn is a TarArchiveInputStream
		while (tarEntry != null) {
			// create a file with the same name as the tarEntry
			File destPath = new File(destDirectory, tarEntry.getName());
			if (tarEntry.isDirectory()) {
				destPath.mkdirs();
			} else {
				destPath.createNewFile();
				byte[] btoRead = new byte[2048];
				BufferedOutputStream bout = new BufferedOutputStream(
						new FileOutputStream(destPath));
				int len;
				while ((len = tarIn.read(btoRead)) != -1) {
					bout.write(btoRead, 0, len);
				}
				bout.close();
				btoRead = null;
			}
			tarEntry = tarIn.getNextTarEntry();
		}
		tarIn.close();
		// gzip.close();
		buffer.close();
		in.close();
	}

	public static void unZip(String zipFilePath, String destDirectory, String password) throws IOException, ZipException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		ZipFile zipFile = new ZipFile(zipFilePath);
		if (zipFile.isEncrypted()) {
			zipFile.setPassword(password);
		}
		@SuppressWarnings("unchecked")
		List <FileHeader>fileHeaderList = zipFile.getFileHeaders();
		for (int i = 0; i < fileHeaderList.size(); i++) {
			FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
			zipFile.extractFile(fileHeader, destDirectory);
		}
	}

	public static void unRar(String zipFilePath, String destDirectory) throws RarException, IOException  {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		File f = new File(zipFilePath);
		Archive a = new Archive(f);
		try {
			if(a.isEncrypted()){
				// System.out.println("================");
			} 
			// a.getMainHeader().print();
			com.github.junrar.rarfile.FileHeader fh = a.nextFileHeader();
			while (fh != null) {
				File out = new File(destDirectory + File.separator + fh.getFileNameString()
						.trim().replace("/", File.separator).replace("\\", File.separator));
				if (fh.isDirectory()) {
					out.mkdirs();
				} else {
					out.getParentFile().mkdirs();
					FileOutputStream os = new FileOutputStream(out);
					a.extractFile(fh, os);
					os.close();
				}
				fh = a.nextFileHeader();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				a.close();
			} catch (Exception e2) {
			}
		}
	}
	
	public static void unRar(String filePath, String destDirectory, String password) 
				throws de.innosystec.unrar.exception.RarException, IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		File f  = new File(filePath);
		de.innosystec.unrar.Archive a = new de.innosystec.unrar.Archive(f, password, false);  
		try {
			// a.getMainHeader().print();
			de.innosystec.unrar.rarfile.FileHeader fh = a.nextFileHeader();
			while (fh != null) {
				File out = new File(destDirectory + File.separator + fh.getFileNameString()
						.trim().replace("/", File.separator).replace("\\", File.separator));
				if (fh.isDirectory()) {
					out.mkdirs();
				} else {
					out.getParentFile().mkdirs();
					FileOutputStream os = new FileOutputStream(out);
					a.extractFile(fh, os);
					os.close();
				}
				fh = a.nextFileHeader();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				a.close();
			} catch (Exception e2) {
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception { 
		// unTar("./test/com.cdr.text.tar", "./test");
		// unZip2("./test/backup.zip", "./test", "aa");
		unZip("./test/pas.rar", "./test", "aa");
	}
}
