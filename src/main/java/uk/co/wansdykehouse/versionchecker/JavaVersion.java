package uk.co.wansdykehouse.versionchecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Tool for checking whether a Java version is outdated or not.
 * 
 * Consistent with the version used in the java.version system property.
 * 
 * @author rich.midwinter@gmail.com
 */
public class JavaVersion {
	
	private static final Logger log = Logger.getLogger(JavaVersion.class.getName());
	
	private static final Map<String, String> versions = new HashMap<String, String>();
	
	private static String maxMajorVersion = "-1";

	private JavaVersion() {}
	
	static {
		try {
			HttpURLConnection con = (HttpURLConnection) new URL("http://javadl-esd-secure.oracle.com/update/baseline.version").openConnection();
			
			readVersions(con.getInputStream());
		} catch (Exception e) {
			log.warning("Failed to pull latest version data. " +e.getMessage());
			
			try {
				readVersions(Thread.currentThread().getContextClassLoader().getResourceAsStream("baseline.version"));
			} catch (IOException e1) {
				log.severe("Failed to read local version cache.");
			}
		}
	}
	
	private static void readVersions(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = in.readLine()) != null) {
			String majorVersion = line.substring(0, line.lastIndexOf("."));
			String minorVersion = line.substring(line.lastIndexOf(".") +1);
			
			versions.put(majorVersion, minorVersion);
			
			int candidateMax = getInt(majorVersion);
			if (candidateMax > getInt(maxMajorVersion)) {
				maxMajorVersion = majorVersion;
			}
		}
	}
	
	/**
	 * @param version The {@link String} representing the Java version.
	 * @return True if a newer version is available, false otherwise.
	 */
	public static CheckResult check(String version) {
		if (!version.matches("\\d+\\.\\d+\\.[_\\d]+")) {
			return new CheckResult().add("Unrecognised version format: "+ version);
		}
		
		String majorVersion = version.substring(0, version.lastIndexOf("."));
		String minorVersion = version.substring(version.lastIndexOf(".") +1);
		
		CheckResult result = new CheckResult();
		checkMajorVersion(result, version, majorVersion);
		
		if (!result.isUpgradeAvailable()) {
			checkMinorVersion(result, version, majorVersion, minorVersion);
		}
		
		return result;
	}
	
	private static int getInt(String version) {
		return Integer.valueOf(version.replaceAll("[^\\d]", ""));
	}
	
	private static void checkMajorVersion(CheckResult result, String version, String majorComponent) {
		if (getInt(majorComponent) < getInt(maxMajorVersion)) {
			result.setUpgradeAvailable();
			
			result.add(version +" is behind latest major release " +maxMajorVersion);
		}
	}
	
	private static void checkMinorVersion(CheckResult result, String version, String majorComponent, String minorComponent) {
		if (versions.containsKey(majorComponent)) {
			String latestMinorVersion = versions.get(majorComponent);
			if (getInt(minorComponent) < getInt(latestMinorVersion)) {
				result.setUpgradeAvailable();
				result.add(String.format("%s is superseded by %s.%s", version, majorComponent, latestMinorVersion));
			} else {
				result.add(version +" is the latest available version.");
			}
		}
	}
	
	public static void main(String[] args) {
		List<String> messages = JavaVersion.check(System.getProperty("java.version"))
				.getMessages();
		
		for (String message : messages) {
			System.out.println(message);
		}
	}
}
