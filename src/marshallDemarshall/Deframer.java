package marshallDemarshall;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ankur bhatia
 * The following class is used to deserialize a byte array into a class object
 * @see ByteArrayInputStream, ObjectInputStream
 */


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Deframer {
	private ByteArrayInputStream bais;
	private ObjectInputStream oout;
	
	/**
	 * 
	 * @param payload is the byte array that is to be deserialized 
	 * @return the object, null incase of a ClassNotFoundException or IOException
	 * @throws ClassNotFoundException, NullpointerException and IOException
	 */
	public Object deframer(byte[] payload){
		checkNotNull(payload);
		try{
			bais = new ByteArrayInputStream(payload);
			oout = new ObjectInputStream(bais);
			return oout.readObject();
		}catch(ClassNotFoundException | IOException e){
			e.printStackTrace();
			return null;
		}
		
		
	}
}
