package input;

import jAudioFeatureExtractor.jAudioTools.AudioMethods;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.PriorityQueue;

import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.spi.MidiFileWriter;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.tritonus.share.sampled.AudioFormats;

import com.sun.media.sound.StandardMidiFileWriter;

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
	
	public static class MidiSerializer implements ISamplesSerializer {
		
		public void serialize(double[][] samples, OutputStream out) {
			StandardMidiFileWriter writer = new StandardMidiFileWriter();
		}
	}
	
	/**
	 * Detects edges in a signal by looking for sharp spikes. 
	 * @author Dhananjay
	 */
	public static class EdgeDetection extends CsvSerializer {
		
		@Override
		public void serialize(double[][] samples, OutputStream out) {
			
			double[][] newSamples = new double[samples.length][];
			int i = 0;
			for (double[] channel : samples) {
				ArrayList<Double> newChannel = new ArrayList<Double>();
				double prevFreq = -1;
				Heap h = new Heap(100);
				for (double freq : channel) {
					if (prevFreq > 0) {
						h.add(freq-prevFreq);
						if (Math.abs(freq - prevFreq) >= 0.277801513671875) {
							newChannel.add(freq);
						}
					}
					prevFreq = freq;
				}
				System.out.println(h.top());
				double[] arr = new double[newChannel.size()];
				int idx = 0;
				for (double freq : newChannel) {
					arr[idx++] = freq;
				}
				newSamples[i++] = arr;
			}
			
			
			super.serialize(newSamples, out);
		}
	}
	
	public static void main(String[] args) {
		Heap heap = new Heap(7);
		for (double i=0; i<10; i++) {
			heap.add(-i);
		}
		heap.print(System.out);
	}
}

class Heap {
	private Double[] arr;
	private int size;
	
	public Heap(int size) {
		arr = new Double[size];
		size = 0;
	}
	
	 
	public void print(PrintStream ps) {
		for (double el : arr) {
			ps.print(el + " ");
		}
		ps.println();
	}
	
	public boolean contains(double i) {
		return r_contains(i, 0);
	}
	
	public void add(double i) {
		if (! contains(i)){
			if (size < arr.length) {
				arr[size++] = i;
				heapify(size-1);
			}
			else {
				if (i > arr[0]) {
					arr[0] = i;
					heapify(0);
				}
			}
		}
	}
	
	public double top() {
		return arr[0];
	}
	
	private void heapify(int idx) {
		int prev_idx = -1; 
		while (idx < size && idx > 0 && prev_idx != idx) {
			prev_idx = idx;
			int parent = (idx-1)/2;
			if (parent < size) {
				if (arr[parent] > arr[idx]) {
					double temp = arr[idx];
					arr[idx] = arr[parent];
					arr[parent] = temp;
					idx = parent;
				}
			}
		}
		
		prev_idx = -1;
		while (idx < size && idx >= 0 && prev_idx != idx) {
			prev_idx = idx;
			int minIdx = idx;
			if (idx*2 + 2 < size && arr[minIdx] > arr[idx*2+2]) {
				minIdx = idx*2+2;
			}
			if (idx*2 + 1 < size && arr[minIdx] > arr[idx*2+1]) {
				minIdx = idx*2+1;
			}
			if (idx != minIdx) {
				double temp = arr[minIdx];
				arr[minIdx] = arr[idx];
				arr[idx] = temp;
				idx = minIdx;
			}
		}
	}
	
	private boolean r_contains(double i, int idx) {
		if (idx >= size || (arr[idx] != null && i < arr[idx]))
			return false;
		else if (arr[idx] != null && i == arr[idx]) 
			return true;
		else
			return r_contains(i, 2*idx+1) || r_contains(i, 2*idx+2);
	}
}
