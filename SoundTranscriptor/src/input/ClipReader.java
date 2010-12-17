package input;

import jAudioFeatureExtractor.jAudioTools.AudioMethods;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * Reads the file to a sampled clip.
 * @author Dhananjay
 *
 */
public class ClipReader {
	
	private ISamplesSerializer m_serializer;
	
	public ClipReader(ISamplesSerializer serializer) {
		m_serializer = serializer;
	}
	
	public void sampleAudio(InputStream in, OutputStream out) throws Exception {
		AudioInputStream audIn = AudioMethods.convertUnsupportedFormat(AudioSystem.getAudioInputStream(in));
		double[][] samples = AudioMethods.extractSampleValues(audIn);
		m_serializer.serialize(samples, out);
	}
	
	/**
	 * Converts samples as double[][] to a serial representation.
	 * @author Dhananjay
	 *
	 */
	public static interface ISamplesSerializer {
		/**
		 * Serialize samples and shove them in the OutputStream
		 * @param samples
		 * @param out
		 */
		void serialize(double[][] samples, OutputStream out);
	}
	
	/**
	 * Serializes samples as a csv. Each line represents a channel
	 * @author Dhananjay
	 *
	 */
	public static class CsvSerializer implements ISamplesSerializer {
		
		public void serialize(double[][] samples, OutputStream out) {
			PrintStream ps = new PrintStream(out);
			for (double[] channel : samples) {
				for (int i=0; i<channel.length; i++) {
					if (i!=0) {
						ps.print(",");
					}
					ps.print(channel[i]);
				}
				ps.println();
			}
		}
	}

}
