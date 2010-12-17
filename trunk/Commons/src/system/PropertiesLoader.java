package system;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Loads properties from default.properties. It overrides the properties 
 * from &lt;computer-name&gt;-override.properties.
 * 
 * @author Dhananjay
 */
public class PropertiesLoader {
	
	/** Cache of properties to the specific classloader */
	private static Map<ClassLoader, Properties> g_cache;
	
	/** Name of the computer */
	private static String g_systemName;
	
	/** Flag to verify if the system is initialized. */
	private static Boolean g_isSystemInitialized = false;
	
	/** Initialize the system */
	private static void init() {
		if (!g_isSystemInitialized) {
			synchronized(g_isSystemInitialized) {
				if (!g_isSystemInitialized) {
					g_cache = new HashMap<ClassLoader, Properties>();
					g_systemName = System.getProperty("computername");
				}
			}
		}
	}
	
	
	/**
	 * Uses the default classloader. Although, this might be 
	 * discouraged as the properties might reside in individual
	 * projects, whose classloaders might not be in the 
	 * heirarchy.
	 */
	public static Properties getProperties() {
		return getProperties(PropertiesLoader.class.getClassLoader());
	}
	
	/**
	 * Use the input clazz's classloader.
	 * @param clazz
	 */
	public static Properties getProperties(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Null clazz! Where do you think I'll load the classloader from?");
		}
		return getProperties(clazz.getClassLoader());
	}
	
	/**
	 * Load properties from specific classloader
	 * @param classLoader
	 * @return
	 */
	public static Properties getProperties(ClassLoader classLoader) {
		if (classLoader == null) {
			throw new IllegalArgumentException("Null classLoader! Is it insanity, or ignorance?");
		}
		
		init();
		
		synchronized (classLoader) {
			Properties props = g_cache.get(classLoader);
			if (props == null) {
				props = new Properties();
				
				ResourceBundle bundle = ResourceBundle.getBundle("defualt", Locale.getDefault(), classLoader);
				
				if (bundle != null) {
					System.err.println("Couldn't find default.properties in the classpath.");
					for (String key : bundle.keySet()) {
						props.put(key, bundle.getObject(key));
					}
				}
				
				bundle = ResourceBundle.getBundle(g_systemName, Locale.getDefault(), classLoader);
				
				if (bundle != null) {
					System.err.println("Couldn't find " + g_systemName + ".properties in the classpath.");
					for (String key : bundle.keySet()) {
						props.put(key, bundle.getObject(key));
					}
				}
				
				g_cache.put(classLoader, props);
			}
			return props;
		}
	}
}
