package frames;
/**
 * 
 * @author ankur bhatia
 * @email bhatia.ankur8@gmail.com
 * The following packet is sent by the client in case of download and the server in case of upload
 * The client or the server checks the config file to see if the upload/download is fresh or progressive
 * Based on the data present the offset and length is requested
 *
 */
public class DataRequest extends Frame {
	private long offset;
	private long length;
	private int identifier;
	private int status;
	
	/**
	 * 
	 * @param offset The byte position till where the data is present. This means the data is requested from the next byte
	 * @param length The length in bytes. This represent the length of the data requested
	 * @param identifier This is the same identifer used which was agreed upon during the upload/download response
	 */
	public DataRequest(long offset, long length, int identifier, int status) {
		super();
		this.offset = offset;
		this.length = length;
		this.identifier = identifier;
		this.status = status;
	}
	/**
	 * 
	 * @return offset
	 */

	public long getOffset() {
		return offset;
	}

	/**
	 * 
	 * @return length of the data requested
	 */
	public long getLength() {
		return length;
	}

	/**
	 * 
	 * @return identifier used for the session
	 */
	public int getIdentifier() {
		return identifier;
	}

	/**
	 * 
	 * @return status of the uploadRequest/downloadRequest 0 if failure, 1 if success
	 */
	public int getStatus() {
		return status;
	}


	@Override
	public String toString() {
		return "DataRequest [offset=" + offset + ", length=" + length + "]";
	}
	
	/**
	 * @return 5
	 */
	@Override
	public int type() {
		return 5;
	}

}
