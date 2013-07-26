package org.openmrs.module.chicaops.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filters files in a directory based on the extension.  The file
 *         name must start with the provided text string and the 
 *         last modified time must be greater than the provided 
 *         time.
 *
 * @author Steve McKee
 */
public class FileListTimeFilter implements FilenameFilter {
	private String name;
	private String extension;
	private Long sinceLastModDate;
	private Long bufferDate;

	/**
	 * Constructor method
	 * 
	 * @param name the filename to look for (excluding the extension).  This can be null if all filenames are wanted.
	 * @param extension The extension of the file (without the '.' character).  This can be null if all extensions are 
	 * wanted.
	 * @param sinceLastModDate files wanted between this time and the current time.  This can be null if last modified 
	 * date filtering is not requested.
	 * @param bufferDate Files be at least this old to be included.  This can be null if no buffer time is desired.
	 */
	public FileListTimeFilter(String name, String extension, Long sinceLastModDate, Long bufferDate) {
		this.name = name;
		this.extension = extension;
		this.sinceLastModDate = sinceLastModDate;
		this.bufferDate = bufferDate;
	}

	/**
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File directory, String filename) {
		boolean ok = true;

		if (name != null) {
			ok &= (filename.startsWith(name + ".") || filename.startsWith(name + "_") 
					|| filename.startsWith("_"+name + "_"));
			if (!ok) return false;
		}
		
		if (extension != null && ok) {
			ok &= filename.endsWith('.' + extension);
			if (!ok) return false;
		}
		
		long lastModified = -1;
		if ((sinceLastModDate != null || bufferDate != null) && ok) {
			File file = new File(directory, filename);
			lastModified = file.lastModified();
		}
		
		if (sinceLastModDate != null && ok) {
			ok &= lastModified > sinceLastModDate;
		}
		
		if (bufferDate != null && ok) {
			ok &= lastModified < bufferDate;
		}

		return ok;
	}
	
}
