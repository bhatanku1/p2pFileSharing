package frames;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Arrays;
/**
 * 
 * @author ankur bhatia
 * @email bhatia.ankur8@gmail.com
 * This packet is the actual data sent by the client or the server
 *
 */
public class DataResponse extends Frame {
	private long offset;
	private long length;
	private byte[] data;
	private int identifier;
	
	/**
	 * 
	 * @param offset     The byte position till where the data is present. This means the data is sent from the next byte
	 * @param length     The length in bytes. This represent the length of the data sent
	 * @param data       The actual data
	 * @param identifier This is the same identifer used which was agreed upon during the upload/download response
	 * @throws           Throws NullPointerException if data is null
	 */
	public DataResponse(long offset, long length, byte[] data, int identifier) {
		super();
		this.offset = offset;
		this.length = length;
		this.data = checkNotNull(data);
		this.identifier = identifier;
	}


	@Override
	public String toString() {
		return "DataResponse [offset=" + offset + ", length=" + length
				+ ", data=" + Arrays.toString(data) + ", identifier="
				+ identifier + "]";
	}

	/**
	 * @return 6
	 */
	@Override
	public int type() {
		return 6;
	}

}
