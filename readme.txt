
Bully algorithm

Files Required
 1.BootStrapBully.java
 2.BootStrapServerBully.java
 3.BullyNode.java
 4.BullyNodeInterface.java


Process :
----------------------------------------------------------------------------------------------------
1) Compile all the classes using javac *.java
2) Run the BootStrapServerBully.java on a separate system.//Ex glados.cs.rit.edu
3) Now run BullyNode.java on different system(#total number of different systems) and perform the required operations. //Ex yes.cs.rit.edu

Scenario : 
----------------------------------------------------------------------------------------------------
 Step 1:
  a) Run BootStrapServerBully.java on a separate system.//Ex glados.cs.rit.edu
  b) It will start the bootstrap server. Copy the ip address of bootstrapserver

 Step 2:
  a) Run BullyNode.java on a different server.//Ex yes.cs.rit.edu
  b) Program will ask to enter the Bootstrapserver ip address. When user enters the ip address, program asks for the process id.
     If the node is the first node then it will be the leader.
 
 Step 3: Repeat step 2 for 5 different servers.//Ex. kansas.cs.rit.edu , newyork.cs.rit.edu , delaware.cs.rit.edu, arizona.cs.rit.edu.
  
 Step 4: Now all the nodes will periodically will check whether the leader is alive or not.

 Step 5: Close the leader console or use ctrl+c to bring the leader down.

 Step 6: The peer which detects the server crash, will start the election and sends the election message peers with higher process id and wait for their reply. If no reply is received within the limit, peer elects itself as a leader and sends the update message to all the peers.
 
 Step 7: Goto step 4.
