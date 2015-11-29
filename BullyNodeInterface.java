/**
 * 
 * @author Ishan Gulhane
 *
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.io.*;

/**
 * Interface for the client node
 */

public interface BullyNodeInterface extends Remote{
	public void addIp(String address,int stamp) throws RemoteException;
	public HashMap<String,Integer> getNeighbours() throws RemoteException;
	public int getStamp()throws RemoteException;
	public void acknowledgement(String ipAdress) throws RemoteException;
	public void updateLeader(String ipAdress,int stamp) throws RemoteException;
	public void checkLeader() throws RemoteException;
	public void setReponse(boolean b)throws RemoteException;
	public void setBully(boolean b) throws RemoteException;
	public String print(String s)throws RemoteException;
}
