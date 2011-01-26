package input;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import system.PropertiesLoader;

/**
 * Generate dataset by reading files from raw.dir, and 
 * writing them to sample.dir.
 * 
 * @see default.properties
 * @author Dhananjay
 *
 */
public class DatasetGenerator {
	
	private static String g_rawDirKey 	 = "raw.dir";
	private static String g_sampleDirKey = "sample.dir";
	private static String g_filefilter	 = "raw.filefilter";
	
	public static void main(String[] args) throws Exception {
		Properties props = PropertiesLoader.getProperties(DatasetGenerator.class);
		String rawDirName = props.getProperty(g_rawDirKey);
		String sampleDirName = props.getProperty(g_sampleDirKey);
		final String fileFilterExt = props.getProperty(g_filefilter);
		
		File rawDir = new File(rawDirName);
		if (!rawDir.exists() || !rawDir.isDirectory()) {
			throw new Exception("Can't read " + rawDirName);
		}
		
		File sampleDir = new File(sampleDirName);
		if ((sampleDir.exists() && !sampleDir.isDirectory()) || sampleDir.mkdir()) {
			throw new Exception("Cannot create/read " + sampleDirName);
		}
		
		FileFilter extensionFilter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String fileName = pathname.getName();
				String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
				return fileFilterExt.contains(extension);
			}
		};
		
		File[] audioFiles = rawDir.listFiles(extensionFilter);
		
		ClipReader reader = new ClipReader(new ClipReader.EdgeDetection());
		
		for (File audio : audioFiles) {
			String filename = audio.getName();
			String outputFilename = filename.substring(0, filename.length() - 4) + ".csv";
			File csvFile = new File(sampleDir, outputFilename);
			csvFile.createNewFile();
			
			InputStream fis = new BufferedInputStream(new FileInputStream(audio));
			FileOutputStream fos = new FileOutputStream(csvFile);
			
			reader.sampleAudio(fis, fos);
			
			fis.close();
			fos.close();
		}
	}
}
