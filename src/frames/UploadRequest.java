package frames;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
/**
 * 
 * @author ankur bhatia
 * @email bhatia.ankur8@gmail.com
 * The packet is send when the client wants to uplaod a file to the server
 * The filename, sha1 and the size are sent to the server. 
 * The server checks the config file to determine the state of the upload
 * If the file has been partially uploaded before, the server sends a data request from 
 * the corresponding offset.
 *
 */
public class UploadRequest extends Frame {
	private String fileName;
	private byte[] sha1;
	private long size;
	
	/**
	 * UploadRequest Constructor
	 * @param fileName Filename to be uploaded
	 * @param sha1 SHA1 of the file to be uploaded
	 * @param size Size of the file to be uploaded
	 * @throws IllegalArgumentException in case the length of filename > 256 or sha.length != 20
	 */
	public UploadRequest(String fileName, byte[] sha1, long size) {
		super();
		this.fileName = fileName;
		checkArgument(fileName.length() < 256);
		this.sha1 = sha1;
		checkArgument(sha1.length == 20);
		this.size = size;
	}


	public String getFileName() {
		return fileName;
	}


	public byte[] getSha1() {
		return sha1;
	}


	public long getSize() {
		return size;
	}


	@Override
	public String toString() {
		return "UploadRequest [fileName=" + fileName + ", sha1="
				+ Arrays.toString(sha1) + ", size=" + size + "]";
	}

	/**
	 * @return return 3
	 */
	@Override
	public int type() {
		return 3;
	}

}
