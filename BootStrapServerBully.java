/**
 * 
 * @author Ishan Gulhane
 *
 */
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implementation of BootStrap server.
 */
public class BootStrapServerBully implements BootStrapBully, Serializable {
	String nodeIp;
	int count;
	protected BootStrapServerBully() throws RemoteException {
	}

	public static void main(String[] args) throws IOException {
	
		try {
			BootStrapServerBully server = new BootStrapServerBully();
			BootStrapBully bootstrap = (BootStrapBully)UnicastRemoteObject.exportObject(server, 8000);
			Registry reg = LocateRegistry.createRegistry(8000);
			reg.rebind("server", server);
			System.out.println(InetAddress.getLocalHost().getHostAddress());
			System.out.println("BootStrap Server Started");
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Stores the ip address of leader node 
	 */
	@Override
	public void BootStrapNode(String ip) throws RemoteException {
		nodeIp = ip;
	}
	
	/**
	 * Removes the stored ip address
	 */
	public void removeBootStrapNode() throws RemoteException{
		nodeIp=null;
	}
	 
	public int getMaxStamp() throws RemoteException {
	 return count;
	 }
	/**
	 * @return Ip address of the leader.
	 */
	@Override
	public String getBootStrapNode() throws RemoteException {
		return nodeIp;
	}
	
	@Override
	public void setMaxStamp(int stamp) throws RemoteException {
		count= stamp;
	}
	
}
