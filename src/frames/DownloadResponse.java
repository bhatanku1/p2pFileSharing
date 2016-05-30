package frames;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;
/**
 * 
 * @author ankur bhatia
 * @email bhatia.ankur8@gmail.com
 * The following frame is the response for the DownloadRequest. It is sent
 * by the server to the client. Incase of success, the status is 1 and in 
 * case of error it is 0
 *
 */
public class DownloadResponse extends Frame {
	private int port;
	private long size;
	private byte [] sha1;
	private int identifier;
	private int status;
	
	/**
	 * @return type of the downloadRequest Packet: 2
	 */
	@Override
	public int type() {
		return 2;
	}
	/**
	 * DownloadResponse constructor
	 * @param port Port number for data connection 
	 * @param size Size of the file
	 * @param sha1 SHA1 of the file
	 * @param identifier Identifier for the Download Session. The same will be used throughout the session
	 * @param status Status for the Download Request. 0 for Error. 1 for Success
	 * @throws IllegalArgumentException in case the length of sha1 is not 20
	 */

	public DownloadResponse(int port, long size, byte[] sha1, int identifier,
			int status) {
		super();
		this.port = port;
		this.size = size;
		this.sha1 = sha1;
		checkArgument(sha1.length == 20);
		this.identifier = identifier;
		this.status = status;
	}
	@Override
	public String toString() {
		return "DownloadResponse [port=" + port + ", size=" + size + ", sha1="
				+ Arrays.toString(sha1) + ", identifier=" + identifier
				+ ", status=" + status + "]";
	}

}
