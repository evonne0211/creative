/*
 * Created on Sep 25, 2006
 *
 */
package jPDFPrintSamples;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.jar.JarFile;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class SampleUtil
{
    private static File m_LastFileDir;
    public final static int OPEN_FILE = 0;
    public final static int SAVE_FILE = 1;
    
    
    public static File getFile (Component parent, int dialogType, FileFilter filter)
    {
        // Get first file name
        JFileChooser fileChooser = new JFileChooser ();
    
        // Initialize directory
        if (m_LastFileDir != null)
        {
            fileChooser.setCurrentDirectory(m_LastFileDir);
        }
        
        // Look for PDF files
    	fileChooser.addChoosableFileFilter(filter);
    
    	// Show the dialog
    	if (dialogType == OPEN_FILE)
    	{
    	    // Show the open dialog
    	    if (fileChooser.showOpenDialog(parent) == JFileChooser.CANCEL_OPTION)
    	    {
    	        return null;
    	    }
    	}
    	else
    	{
            // Show the save dialog
    	    if (fileChooser.showSaveDialog(parent) == JFileChooser.CANCEL_OPTION)
    	    {
    	        return null;
    	    }
    	}
    	
    	// Save last directory location
    	File chosenFile = fileChooser.getSelectedFile();
    	if (chosenFile != null)
    	{
    	    m_LastFileDir = chosenFile.getParentFile();
    	}
    	return chosenFile;
    }
    
    public static void centerDialog (Frame parentFrame, JDialog dialog)
    {
        // bit of a hack, IE frames report their location as 0, 0 regardless of where they are on the window
        if (parentFrame.getClass().getName() != null && parentFrame.getClass().getName().toLowerCase().indexOf("iexplorer") != -1)
        {
            int centerX = (parentFrame.getWidth() - dialog.getWidth()) / 2;
            int centerY = (parentFrame.getHeight() - dialog.getHeight()) / 2;
            dialog.setLocation (parentFrame.getLocationOnScreen().x + centerX, parentFrame.getLocationOnScreen().y + centerY);
        }
        else
        {
            int centerX = (parentFrame.getWidth() - dialog.getWidth()) / 2;
            int centerY = (parentFrame.getHeight() - dialog.getHeight()) / 2;
            dialog.setLocation (parentFrame.getX() + centerX, parentFrame.getY() + centerY);
        }
    }    

    
	//-------------------------------------------------------------------------------------------------------------------------------------------------------
	// Write environment info to System.out
	//	 - System.out is (generally) redirected to a log file in our samples
	//
    public static void libraryLogStart(Class<?> libClass)
	{
		// Log: Library name, version and build
		try
		{
			// Find and call getVersion() method in class
			Method meth_getVersion = libClass.getDeclaredMethod("getVersion", (Class<?>[])null);
			String libVersion = (String)meth_getVersion.invoke(null, (Object[])null);
			
			System.out.println("Library: " + libVersion);
		}
		catch (Throwable t)
		{
			System.out.println("Error finding/calling library's getVersion() method.");
		}	

		// Log: Build number
		String installDir = null;
		try
		{
			// Access JAR file for class
			File file = new File(libClass.getProtectionDomain().getCodeSource().getLocation().toURI());
			
			// Get JAR file grand-parent directory as "Installation Directory"
			if (file.getParentFile() != null && file.getParentFile().getParentFile() != null)
			{
				installDir = file.getParentFile().getParentFile().getCanonicalPath();
				installDir = installDir.replace("%20", " ");
			}
			
			// Build #, if present, is in JAR file manifest
			JarFile jarFile = new JarFile(file);
			String buildNum = jarFile.getManifest().getMainAttributes().getValue("QBuild-Number");
			jarFile.close();
			
			System.out.println("Build #: " + buildNum);
		}
		catch (Throwable t) 
		{
			// couldn't find or open JAR file - maybe running code from Eclipse?
			System.out.println("Build #: * none *");
		}	
		
        // Log: O/S and Java version
		System.out.println("Operating System:  " + getOSInfo());
		System.out.println("Java version:  " + System.getProperty("java.version") + ", " + System.getProperty("sun.arch.data.model") + "-bit, " + System.getProperty("java.vendor"));
		
		// Log: Java VM args
		Object[] inputArgs = ManagementFactory.getRuntimeMXBean().getInputArguments().toArray();		
		for (int i = 0; i < inputArgs.length; ++i)
		{
			String arg = toString(inputArgs[i], null);
			if (arg != null)
			{
				// Ignore the args after any of these if it doesn't start with a dash
				if (arg.startsWith("-Dinstall4j") || arg.startsWith("-Dexe4j") || arg.startsWith("-Djava.library.path"))
				{ 
					while (i + 1 < inputArgs.length && inputArgs[i + 1] != null && !(arg = toString(inputArgs[i + 1], null)).startsWith("-"))
					{
						++i;
					}
				}
				else
				{
					System.out.println("  " + arg);
				}
			}
		}
		
		// Log:  Install directory
		if (installDir != null)
			System.out.println("Installation Directory:  " + installDir);				
		
		System.out.println();	// Blank line (before rest of logging)
	}
	
	private static String toString(Object obj, String defString)
	{
		if (obj != null)
		{
			return obj.toString();
		}
		else
		{
			return defString;
		}
	}
	
    private static String getOSInfo()
    {
    	String osInfo = System.getProperty("os.name");
    	
    	// Workaround for JDK not reporting os.name correctly for Windows 8
    	if (osInfo.equals("Windows NT (unknown)") && System.getProperty("os.version").equals("6.2"))
    	{
    		osInfo = "Windows 8";
    	}
    	
    	boolean isWindows = (osInfo.toLowerCase().indexOf("windows") != -1);
    	boolean isLinux = (osInfo.toLowerCase().indexOf("linux") != -1);
    	
    	// Windows os.version doesn't really make sense (xp=5.1,vista=6.0,win7=6.1)
        // so show patch level instead
        if (isWindows)
        {
        	osInfo += " " + toString(System.getProperty("sun.os.patch.level"), "");
        }
        else
        {
        	osInfo += " " + toString(System.getProperty("os.version"), "");
        }
        
        if (isLinux)
        {
        	String distribution = getLinuxDistribution();
        	if (distribution != null && distribution.length() > 0)
        	{
        		osInfo += " " + distribution;
        	}
        }
        
        // add more O/S information i386_32 (32 is the bits)
        osInfo += " " + System.getProperty("os.arch");
        
        if (System.getProperty("os.arch.data.model") != null)
        {
        	osInfo += "_" + System.getProperty("os.arch.data.model");
        }
        
        return osInfo;
    }
    
	private static final String LINUX_DISTRIBUTION_KEY = "DISTRIB_ID";
	private static final String LINUX_DISTRIBUTION_VERSION = "DISTRIB_RELEASE";
	private static final String LINUX_DISTRIBUTION_FALLBACK_KEY = "NAME";
	private static final String LINUX_DISTRIBUTION_FALLBACK_VERSION = "VERSION_ID";
	
    private static String getLinuxDistribution()
	{
		// read the /etc/*-release files
    	File etc = new File("/etc");
    	if (etc.exists() && etc.isDirectory())
    	{
    		File [] releaseFiles = etc.listFiles(new FilenameFilter() {
				
				public boolean accept(File dir, String name)
				{
					return name.toLowerCase().endsWith("-release");
				}
			});
    		
    		Properties properties = new Properties();
    		for (int i = 0; i < releaseFiles.length; i++)
    		{
    			try
				{
    				if (!releaseFiles[i].isDirectory())
    				{
    					properties.load(new FileInputStream(releaseFiles[i]));
    				}
				} 
    			catch (Exception e)
				{
    				e.printStackTrace();
				}
    		}
    		
    		String distribution = properties.getProperty(LINUX_DISTRIBUTION_KEY);
    		if (distribution == null)
    		{
    			distribution = properties.getProperty(LINUX_DISTRIBUTION_FALLBACK_KEY);
    		}
    		
    		String version = properties.getProperty(LINUX_DISTRIBUTION_VERSION);
    		if (version == null)
    		{
    			version = properties.getProperty(LINUX_DISTRIBUTION_FALLBACK_VERSION);
    		}
    		
    		return (distribution == null ? "" : distribution) + (version == null ? "" : " " + version);
    	}
    	
    	return "";
	}
	
}
