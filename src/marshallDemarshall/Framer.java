package marshallDemarshall;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
/**
 * 
 * @author ankur bhatia
 * @email bhatia.ankur8@gmail.com
 * @see class ByteArrayOutputStream, ObjectOutputStream
 * The following class converts an object into byte array
 * that can be send through across the socket.
 *
 */
public class Framer {
	private  ByteArrayOutputStream baos;
	private ObjectOutputStream oout;
	
	/**
	 * 
	 * @param o is the object of the class that will be serialized
	 * @return the byte array that can be transported through the socket
	 *         null in case of an exception
	 * @throws IOException, NullpointerException 
	 */
	public byte[] framer(Object o){
		
		try {
			checkNotNull(o);
			baos = new ByteArrayOutputStream();
			oout = new ObjectOutputStream(baos);
			oout.writeObject(o);
			return baos.toByteArray();
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
}
