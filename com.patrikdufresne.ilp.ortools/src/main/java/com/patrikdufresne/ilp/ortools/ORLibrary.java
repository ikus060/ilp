package com.patrikdufresne.ilp.ortools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * Used to load OR-tools native library.
 * 
 * @author Patrik Dufresne
 * 
 */
class ORLibrary {
	/**
	 * New line char
	 */
	private static final String DELIMITER;
	/**
	 * OR-tools library name.
	 */
	private static final String ORTOOLS_LIBNAME = "TODO";
	/**
	 * OR-tools java library name.
	 */
	private static final String ORTOOLS_JAVA_LIBNAME = "jnilinearsolver";
	/**
	 * Relative OR-tools path according to current architecture.
	 */
	private static final String ORTOOLS_LIB_DIR;
	/**
	 * True if the JVM is 64bits.
	 */
	private static final boolean IS_64 = longConst() == (long /* int */) longConst();
	/**
	 * OR-tools Major version number (must be >= 0)
	 */
	private static int MAJOR_VERSION = 4;
	/**
	 * OR-tools Minor version number (must be in the range 0..999)
	 */
	private static int MINOR_VERSION = 47;
	/**
	 * The file separator.
	 */
	private static final String SEPARATOR;
	/**
	 * OR-tools library path property name.
	 */
	private static final String ORTOOLS_LIBRARY_PATH = "ortools.library.path";

	static {
		/* Initialize the constant */
		DELIMITER = System.getProperty("line.separator"); //$NON-NLS-1$
		SEPARATOR = System.getProperty("file.separator"); //$NON-NLS-1$
		ORTOOLS_LIB_DIR = ".or-tools" + SEPARATOR + "lib" + SEPARATOR + os() + SEPARATOR + arch(); //$NON-NLS-1$ $NON-NLS-2$
	}

	/**
	 * Adds the specified path to the java library path
	 * 
	 * @param newPath
	 *            the path to add
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	static void addLibraryPath(String newPath) throws SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		final Field usrPathsField = ClassLoader.class
				.getDeclaredField("usr_paths");
		usrPathsField.setAccessible(true);

		// get array of paths
		final String[] usrpaths = (String[]) usrPathsField.get(null);

		// check if the path to add is already present
		for (String path : usrpaths) {
			if (path.equals(newPath)) {
				return;
			}
		}

		// add the new path
		final String[] newPaths = Arrays.copyOf(usrpaths, usrpaths.length + 1);
		newPaths[newPaths.length - 1] = newPath;
		usrPathsField.set(null, newPaths);
	}

	private static String arch() {
		String osArch = System.getProperty("os.arch"); //$NON-NLS-1$
		if (osArch.equals("i386") || osArch.equals("i686"))return "x86"; //$NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
		if (osArch.equals("amd64"))return "x86_64"; //$NON-NLS-1$ $NON-NLS-2$
		if (osArch.equals("IA64N"))return "ia64_32"; //$NON-NLS-1$ $NON-NLS-2$
		if (osArch.equals("IA64W"))return "ia64"; //$NON-NLS-1$ $NON-NLS-2$
		return osArch;
	}

	/**
	 * Check the execution mode of the given file.
	 * <p>
	 * This function does nothing for Windows platform.
	 * 
	 * @param permision
	 *            the new permission mode.
	 * @param path
	 *            the file.
	 */
	private static void chmod(String permision, String path) {
		if (os().equals("win"))return; //$NON-NLS-1$
		try {
			Runtime.getRuntime()
					.exec(new String[] { "chmod", permision, path }).waitFor(); //$NON-NLS-1$
		} catch (Throwable e) {
			// Swallow.
		}
	}

	/**
	 * Used to extract a library from a jar and load it.
	 * 
	 * @param libname
	 *            the library name to be loaded
	 * @param fileName
	 *            an absolute path where to extract the library.
	 * @param resourceName
	 *            the resource location to be extracted
	 * @param message
	 * @return
	 */
	private static boolean extractAndLoad(String libname, String fileName,
			String resourceName, StringBuffer message) {
		FileOutputStream os = null;
		InputStream is = null;
		File file = new File(fileName);
		boolean extracted = false;
		try {
			if (!file.exists()) {
				is = ORLibrary.class.getResourceAsStream("/" + resourceName); //$NON-NLS-1$
				if (is != null) {
					extracted = true;
					int read;
					byte[] buffer = new byte[4096];
					os = new FileOutputStream(fileName);
					while ((read = is.read(buffer)) != -1) {
						os.write(buffer, 0, read);
					}
					os.close();
					is.close();
					chmod("755", fileName);
					if (load(file.getParent(), libname, message))
						return true;
				}
			}
		} catch (Throwable e) {
			try {
				if (os != null)
					os.close();
			} catch (IOException e1) {
			}
			try {
				if (is != null)
					is.close();
			} catch (IOException e1) {
			}
			if (extracted && file.exists())
				file.delete();
		}
		return false;
	}

	/**
	 * Load the OR-tools library.
	 * 
	 * @throws UnsatisfiedLinkError
	 *             is the library can't be loaded.
	 */
	public static void load() {
		// loadLibrary(ORTOOLS_LIBNAME);
		loadLibrary(ORTOOLS_JAVA_LIBNAME);
	}

	/**
	 * This function will update the java.library.path dynamically and then try
	 * to load the library. The advantage of using this function is the ability
	 * to change the search path of the library.
	 * 
	 * @param path
	 *            the search path to be added to java.library.path.
	 * @param libName
	 *            the library name to be loaded.
	 * @param message
	 * @return True is the library is loaded.
	 */
	private static boolean load(String path, String libName,
			StringBuffer message) {
		try {
			if (path != null) {
				// append the path to the library path
				addLibraryPath(path);
			}
			System.loadLibrary(libName);
			return true;
		} catch (Throwable e) {
			if (message.length() == 0)
				message.append(DELIMITER);
			message.append('\t');
			message.append(e.getMessage());
			message.append(DELIMITER);
		}
		return false;
	}

	/**
	 * Loads the shared library that matches the version of the Java code which
	 * is currently running. OR-tools-java shared libraries follow an encoding
	 * scheme where the major, minor and revision numbers are embedded in the
	 * library name and this along with <code>name</code> is used to load the
	 * library. If this fails, <code>name</code> is used in another attempt to
	 * load the library, this time ignoring the OR-tools version encoding
	 * scheme.
	 * 
	 * @param name
	 *            the name of the library to load (without-java or _java).
	 */
	private static void loadLibrary(String name) {
		String prop = System.getProperty("sun.arch.data.model"); //$NON-NLS-1$
		if (prop == null)
			prop = System.getProperty("com.ibm.vm.bitmode"); //$NON-NLS-1$
		if (prop != null) {
			if ("32".equals(prop) && IS_64) { //$NON-NLS-1$
				throw new UnsatisfiedLinkError(
						"Cannot load 64-bit OR-tools libraries on 32-bit JVM"); //$NON-NLS-1$
			}
			if ("64".equals(prop) && !IS_64) { //$NON-NLS-1$
				throw new UnsatisfiedLinkError(
						"Cannot load 32-bit OR-tools libraries on 64-bit JVM"); //$NON-NLS-1$
			}
		}

		/* Compute the library names */
		String libName1;
		libName1 = name;

		StringBuffer message = new StringBuffer();

		/* Try loading library from glpk library path */
		String path = System.getProperty(ORTOOLS_LIBRARY_PATH); //$NON-NLS-1$
		if (path != null) {
			path = new File(path).getAbsolutePath();
			if (load(path, libName1, message))
				return;
		}

		/* Try loading library from java library path */
		if (load(null, libName1, message))
			return;

		/*
		 * Try loading library from the tmp directory if glpk.library.path is
		 * not specified
		 */
		String fileName1 = mapLibraryName(libName1);
		if (path == null) {
			path = System.getProperty("user.home"); //$NON-NLS-1$
			File dir = new File(path, ORTOOLS_LIB_DIR);
			if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
				path = dir.getAbsolutePath();
			} else {
				/* fall back to using the home dir directory */
			}
			if (load(path, libName1, message))
				return;
		}

		/*
		 * Try extracting and loading library from jar. Embedded jars are
		 * organized in sub-directories.
		 */
		if (path != null) {
			if (extractAndLoad(libName1, path + SEPARATOR + fileName1, os()
					+ SEPARATOR + arch() + SEPARATOR + fileName1, message))
				return;
		}

		/* Failed to find the library */
		throw new UnsatisfiedLinkError(
				"Could not load OR-tools library. Reasons: " + message.toString()); //$NON-NLS-1$
	}

	/* Use method instead of in-lined constants to avoid compiler warnings */
	private static long longConst() {
		return 0x1FFFFFFFFL;
	}

	private static String mapLibraryName(String libName) {
		/*
		 * OR-tools libraries in the Macintosh use the extension .jnilib but the
		 * some VMs map to .dylib.
		 */
		libName = System.mapLibraryName(libName);
		String ext = ".dylib"; //$NON-NLS-1$
		if (libName.endsWith(ext)) {
			libName = libName.substring(0, libName.length() - ext.length())
					+ ".jnilib"; //$NON-NLS-1$
		}
		return libName;
	}

	private static String os() {
		String osName = System.getProperty("os.name"); //$NON-NLS-1$
		if (osName.equals("Linux"))return "linux"; //$NON-NLS-1$ $NON-NLS-2$
		if (osName.equals("AIX"))return "aix"; //$NON-NLS-1$ $NON-NLS-2$
		if (osName.equals("Solaris") || osName.equals("SunOS"))return "solaris"; //$NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
		if (osName.equals("HP-UX"))return "hpux"; //$NON-NLS-1$ $NON-NLS-2$
		if (osName.equals("Mac OS X"))return "macosx"; //$NON-NLS-1$ $NON-NLS-2$
		if (osName.startsWith("Win"))return "win"; //$NON-NLS-1$ $NON-NLS-2$
		return osName;
	}
}