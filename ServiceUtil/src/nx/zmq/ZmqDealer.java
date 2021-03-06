package nx.zmq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;

public class ZmqDealer
{
	private ZMQ.Context context;
	private ZMQ.Socket dealer;
	private Poller poller;
	private Integer timeout;

	/**
	 * @param uri
	 * @param timeout
	 */
	public ZmqDealer(String uri, Integer timeout)
	{
		initZmq(null, uri, timeout);
	}

	/**
	 * @param id
	 * @param uri
	 * @param timeout
	 */
	public ZmqDealer(byte[] id, String uri, Integer timeout)
	{
		initZmq(id, uri, timeout);
	}

	/**
	 * @param host
	 * @param port
	 * @param timeout
	 */
	public ZmqDealer(String host, int port, Integer timeout)
	{
		String uri = String.format("tcp://%s:%d", host, port);
		initZmq(null, uri, timeout);
	}

	/**
	 * @param id
	 * @param host
	 * @param port
	 * @param timeout
	 */
	public ZmqDealer(byte[] id, String host, int port, Integer timeout)
	{
		String uri = String.format("tcp://%s:%d", host, port);
		initZmq(id, uri, timeout);
	}

	/**
	 * @param id
	 * @param uri
	 * @param timeout
	 */
	protected void initZmq(byte[] id, String uri, Integer timeout)
	{
		this.timeout = timeout == null ? -1 : timeout;
		context = ZMQ.context(1);
		dealer = context.socket(ZMQ.DEALER);
		if (id != null)
		{
			dealer.setIdentity(id);
		}
		dealer.setLinger(0);
		dealer.connect(uri);
		poller = new Poller(1);
		poller.register(dealer, Poller.POLLIN);
	}

	/**
	 * @return received data in bytes
	 */
	public synchronized byte[] receive()
	{
		if (poller.poll(timeout) == -1)
		{
			return null;
		}
		if (poller.pollin(0))
		{
			return dealer.recv(0);
		}
		return null;
	}

	/**
	 * @return received data in string
	 */
	public synchronized String receiveStr()
	{
		if (poller.poll(timeout) == -1)
		{
			return null;
		}
		if (poller.pollin(0))
		{
			return dealer.recvStr();
		}
		return null;
	}

	/**
	 * @param msg
	 */
	public synchronized void send(byte[] msg)
	{
		dealer.send(msg, 0);
	}

	public synchronized void sendMore(byte[] msg)
	{
		dealer.send(msg, 1);
	}

	/**
	 * @param msg
	 */
	public synchronized void send(String msg)
	{
		dealer.send(msg);
	}

	/**
	 * @param msg
	 */
	public synchronized void sendMore(String msg)
	{
		dealer.sendMore(msg);
	}

	public synchronized void close()
	{
		dealer.close();
		context.term();
	}
}