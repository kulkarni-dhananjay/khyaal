package input;

import jAudioFeatureExtractor.jAudioTools.AudioMethods;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * Reads the file to a sampled clip.
 * @author Dhananjay
 *
 */
public class ClipReader {
	
	public static void main(String[] args) throws Exception {
		File audioFile = new File("C:/Storage/Songs/Hindustani Classical/Vilayat Khan/Gauti - Maestro's Choice - Vilayat Khan.mp3");
		AudioInputStream is = AudioMethods.convertUnsupportedFormat(AudioSystem.getAudioInputStream(audioFile));
		double [][] samples = AudioMethods.extractSampleValues(is);

		printAudioSamples(samples);
	}
	
	public static void printAudioSamples(double [][] samples) {
		int i=0;
		for (double[] channel : samples) {
			System.out.println("Channel " + i++ + "- " + channel.length);
			
			System.out.println();
		}
	}

}
