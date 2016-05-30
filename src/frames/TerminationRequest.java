package frames;
/**
 * 
 * @author ankur bhatia
 * @email bhatia.ankur8@gmail.com
 * The following packet is sent by the client or the server to indicate the end of the 
 * upload or download session. 
 * status is 0 if error, 1 if successful
 *
 */
public class TerminationRequest extends Frame {
	private int status;
	/**
	 * TerminationRequest Constructor
	 * @param status 0 if error, 1 if success
	 */
	public TerminationRequest(int status) {
		super();
		this.status = status;
	}
	/**
	 * @return type of the packet. 9 for termination
	 */
	@Override
	public int type() {
		return 9;
	}

}
