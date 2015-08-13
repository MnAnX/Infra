package nx.service.wrapper;

public class ServiceException extends Exception
{
	public ServiceException(String errMsg) {
		super(errMsg);
	}

	public ServiceException(String errMsg, Exception e) {
		super(errMsg, e);
	}

}
