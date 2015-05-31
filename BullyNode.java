/**
 * 
 * @author Ishan Gulhane
 *
 */
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class BullyNode implements Serializable, BullyNodeInterface {
	HashMap<String, Integer> neighbours = new HashMap<String, Integer>();
	String BootStrapIp;
	String ip;
	int stamp;
	String leader;
	boolean response;
	int responseStamp;
	int leaderStamp;
	boolean holdElections;
	boolean bully;

	/**
	 * 
	 *
	 */
	public BullyNode() throws RemoteException {
		
		holdElections = false;
		response = false;
		bully=false;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
			
	}
	
	
	/**
	 * getStamp():
	 *	@return returns the procees id of node 
	 */
	public int getStamp() throws RemoteException {
		return stamp;
	}

	/**
	 * getNeighbours():
	 * @return : Returns the list all the neighbors in network
	 */
	public HashMap<String, Integer> getNeighbours() throws RemoteException {
		return neighbours;
	}

	/**
	 * addIp():
	 *	Updates the neighbor list list and adds the new node.
	 */
	public void addIp(String address, int stamp) throws RemoteException {
		if (!neighbours.containsKey(address)) {
			neighbours.put(address, stamp);
		}
	}

	/**
	 * join() :
	 *	Node joins the network
	 */
	public void join() {
		try {
			System.out.println("Please enter BootStrapServer IP");
			BootStrapIp = new Scanner(System.in).next();
			System.out.println("Please enter process id...");
			this.stamp = new Scanner(System.in).nextInt();
			Registry reg = LocateRegistry.getRegistry(BootStrapIp, 8000);
			BootStrapBully obj = (BootStrapBully) reg.lookup("server");
			String firstIp = obj.getBootStrapNode();
			leader = firstIp;
			leaderStamp = obj.getMaxStamp();
			if (firstIp == null) {//if the node is the first node in the network,set it as a leader
				obj.BootStrapNode(ip);
				obj.setMaxStamp(stamp);
				leader = ip;
				leaderStamp = stamp;
				try{
				BullyNodeInterface bootstrap = (BullyNodeInterface) UnicastRemoteObject.exportObject(this, 8000);
				Registry reg1 = LocateRegistry.createRegistry(8000);
				reg1.rebind("node", this);
				}catch(Exception e){
				}
				System.out.println("Node Joined...");
			} else {
				try{
				Registry registry = LocateRegistry.getRegistry(firstIp, 8000);
				BullyNodeInterface peerInterface = (BullyNodeInterface) registry.lookup("node");
				neighbours.put(firstIp, peerInterface.getStamp());
				HashMap<String, Integer> n = peerInterface.getNeighbours();
				//Updating all the peers in the network
				for (String string : n.keySet()) {
					try{
					Registry registry2 = LocateRegistry.getRegistry(string,8000);
					BullyNodeInterface peerInterface1 = (BullyNodeInterface) registry2.lookup("node");
					peerInterface1.addIp(ip, stamp);
					this.addIp(string, n.get(string));
					}catch(Exception e){
					}
				}
				peerInterface.addIp(ip, stamp);
				try{
				BullyNodeInterface interface1 = (BullyNodeInterface) UnicastRemoteObject.exportObject(this, 8000);
				Registry registry2 = LocateRegistry.createRegistry(8000);
				registry2.rebind("node", this);
				}catch(Exception e){
				}
				System.out.println("Node "+ip+" Joined...");
				
				checkLeader();
				}catch(Exception e){
				}
			}
		} catch (RemoteException |NotBoundException e) {
			
		}
	}

	/**
	 * print()
	 *
	 */
	public String print(String  s) throws RemoteException {
		return s;
	}

	/**
	 * checkLeader():
	 *	Periodically checks if the leader is active or not.
	 */
	public void checkLeader() throws RemoteException {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Registry reg = null;

				try {
					if (!leader.equalsIgnoreCase(ip)) {
						System.out.println("Checking Leader..");
						reg = LocateRegistry.getRegistry(leader, 8000);
						BullyNodeInterface bullyNodeInterface = (BullyNodeInterface) reg.lookup("node");
						String s = bullyNodeInterface.print("Leader is alive");
						System.out.println(s);
					}
				} catch (Exception e) {
					/*try {
						reg.unbind(leader);
					} catch (NotBoundException | RemoteException e1) {

					}
*/					cancel();
					
					if(!bully){
					System.out.println("Leader is dead. Starting Elections");
					holdElection();
					}
				}

			}
		}, 5000, 10000);

	}

	/**
	 * becomeLeader() : 
	 * Node sends the elected message to the other nodes
	 */
	public void becomeLeader() {
		try {
			String oldLeader = leader;
			this.neighbours.remove(leader);
			try{
			Registry reg = LocateRegistry.getRegistry(BootStrapIp, 8000);
			BootStrapBully obj = (BootStrapBully) reg.lookup("server");
			obj.BootStrapNode(ip);
			obj.setMaxStamp(stamp);
			}catch(Exception e){
				
			}
			System.out.println("Elected as a Leader. Sending messages to other peers");
			HashMap<String, Integer> n = this.neighbours;
			for (String str : n.keySet()) {
				if (!oldLeader.equalsIgnoreCase(str)) {
					System.out.println("Send elected message to " + str);
					try {
						Registry reg1 = LocateRegistry.getRegistry(str, 8000);
						BullyNodeInterface peerInterface = (BullyNodeInterface) reg1.lookup("node");
						peerInterface.updateLeader(ip, stamp);
						peerInterface.checkLeader();
						peerInterface.setBully(false);
						
					} catch (Exception e) {
					}
				}
			}
			this.leader = ip;
		} catch (Exception e) {
			
		}
		
	}

	public void setBully(boolean b) throws RemoteException {
		this.bully = b;
	}

	/**
	 * updateLeader() :  Updates the leader on current node
	 *
	 */
	public void updateLeader(String ipAdress, int stamp) throws RemoteException {
		System.out.println(ipAdress + " is the new Leader");
		this.leader = ipAdress;
		this.leaderStamp = stamp;
		this.holdElections = false;
	}

	/**
	 * HoldElection() : Node starts the election and sends the election message to other nodes
	 *
	 */
	public void holdElection() {
		response = false;
		holdElections = true;
		String temp=leader;
		HashMap<String, Integer> n = this.neighbours;
		for (String str : n.keySet()) {
			if (!str.equals(temp) && stamp < n.get(str)) {
				System.out.println("Sending Election message to " + str);
				try {
					Registry reg = LocateRegistry.getRegistry(str, 8000);
					BullyNodeInterface peerInterface = (BullyNodeInterface) reg.lookup("node");
					peerInterface.setBully(true);
					peerInterface.acknowledgement(ip);
				} catch (Exception e) {
				}

			}
		}
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if (!response) {
					becomeLeader();
				}
			}
		}, 20000);
		
	}

	/**
	 * acknowledgement(): Sends the acknowledgement message to other nodes.
	 *
	 */
	public void acknowledgement(String ipAdress) throws RemoteException {
		System.out.println("Election Message Received from " + ipAdress);
		try {
			Registry reg = LocateRegistry.getRegistry(ipAdress, 8000);
			BullyNodeInterface peerInterface = (BullyNodeInterface) reg.lookup("node");
			peerInterface.setReponse(true);
			peerInterface.print("Acknowledgement Received from " + ip);
			if (!holdElections) {
				holdElection();
			}
		} catch (Exception e) {

		}
	}

	public static void main(String[] args) throws RemoteException {
		BullyNode node = new BullyNode();
		node.join();
	}

	@Override
	public void setReponse(boolean b) throws RemoteException {
		response = b;
	}

}
