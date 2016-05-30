package frames;
/**
 * 
 * @author ankur bhatia
 * @email bhatia.ankur8@gmail.com
 * This packet is sent by the server to the client as the response to the
 * UploadRequest. The server sends a port number in the response which is used for the
 * data connection. Status is set to 1 if success, 0 if failure
 *
 */
public class UploadResponse extends Frame {
	private int status;
	private int port;
	private int identifier;
	
	/**
	 * UploadResponse Constructor
	 * @param status 0 if failure, 1 if success
	 * @param port Port number for the data connection
	 * @param identifier Identifier used for the session
	 * 
	 */
	public UploadResponse(int status, int port, int identifier) {
		super();
		this.status = status;
		this.port = port;
		this.identifier = identifier;
	}

	@Override
	public String toString() {
		return "UploadResponse [status=" + status + ", port=" + port + "]";
	}
	/**
	 * 
	 * @return the type of the packet: 4
	 */
	@Override
	public int type() {
		return 4;
	}

}
