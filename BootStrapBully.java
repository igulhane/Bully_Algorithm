/**
 * 
 * @author Ishan Gulhane
 *
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * Interface for the BootStrap Server
 */
public interface BootStrapBully extends Remote{
	public void BootStrapNode(String ip) throws RemoteException;
	public void removeBootStrapNode() throws RemoteException;
	public String getBootStrapNode() throws RemoteException;
	public void setMaxStamp(int stamp) throws RemoteException;
	public int getMaxStamp() throws RemoteException;
}
