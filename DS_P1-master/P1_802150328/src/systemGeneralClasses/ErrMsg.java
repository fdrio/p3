package systemGeneralClasses;
/**
 * 
 * @author pirvos
 * 
 * This class is meant to represent object to hold some
 * type of error messages. That type of object will hold
 * some extra message whenever and invalid command is
 * detected. 
 *  
 */
public class ErrMsg {
	private String msg; 
	public ErrMsg() { 
		msg = null; 
	}
	public ErrMsg(String msg) { 
		this.msg = msg; 
	}
	public boolean isEmpty() { 
		return msg == null; 
	}
	public void reset() { 
		msg = null; 
	}
	public String getMessage() { 
		return msg; 
	}
	public void setMessage(String msg) { 
		this.msg = msg; 
	}

}
