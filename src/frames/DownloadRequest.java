package frames;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;
/**
 * 
 * @author ankur bhatia
 * @email bhatia.ankur8@gmail.com
 * This frame is sent by the client to the server to initiate the download
 *
 */

public class DownloadRequest extends Frame {
	private String fileName; 
	private byte[] sha1;
	private int identifier;
	
	/**
	 * DownloadRequest Constructor
	 * @param fileName   Name of the file to be downloaded
	 * @param sha1       SHA1 sum of the fie to be downloaded. 
	 *                   It is set to 0 if the download is fresh
	 *                   Else is set to the partial SHA1 if download is progressive
	 * @param identifier A random number assigned to the packet for identification. Set to 0 initially
	 * @throws IllegalArgumentException in the case of the fileName.length() > 256
	 * @throws IllegalArgumentException in the case of the sha1.length != 20
	 * @throws NullPointerException in the case of fileName or sha1 = null
	 */
	public  DownloadRequest(String fileName, byte[] sha1) {
		
		this.fileName = checkNotNull(fileName);
		checkArgument(fileName.length() < 256);
		this.sha1 = sha1;
		checkArgument(sha1.length == 20);
		this.identifier = 0;
	}
	
	@Override
	public String toString() {
		return "DownloadRequest [fileName=" + fileName + ", sha1=" + sha1
				+ ", identifier=" + identifier + "]";
	}
	/**
	 * @return the type of the packet
	 *         for downloadRequest it is 1  
	 */
	@Override
	public int type() {
		return 1;
	}

}
