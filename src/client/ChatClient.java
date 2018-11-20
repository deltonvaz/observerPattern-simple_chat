// This file contains material supporting section 3.7 of the textbook:// "Object Oriented Software Engineering" and is issued under the open-source// license found at www.lloseng.com package client;import common.*;import java.io.*;import java.util.Observable;import java.util.Observer;/** * This class overrides some of the methods defined in the abstract * superclass in order to give more functionality to the client. * * @author Dr Timothy C. Lethbridge * @author Dr Robert Lagani&egrave; * @author Fran&ccedil;ois B&eacute;langer * @author Delton Vaz (edition for OOSE) * @author William D'amico (edition for OOSE) * @version November 2018 */public class ChatClient implements Observer{	//Instance variables **********************************************	/**	 * The interface type variable.  It allows the implementation of 	 * the display method in the client.	 */	ChatIF clientUI;	String username;	ObservableClient observableClient;	//Constructors ****************************************************	/**	 * Constructs an instance of the chat client.	 *	 * @param host The server to connect to.	 * @param port The port number to connect on.	 * @param clientUI The interface type variable.	 */	public ChatClient(String host, int port, ChatIF clientUI) throws IOException	{		observableClient = new ObservableClient(host, port);		this.clientUI = clientUI;		observableClient.openConnection();	}	/**	 * Constructs an instance of the chat client with username.	 *	 * @param host The server to connect to.	 * @param port The port number to connect on.	 * @param clientUI The interface type variable.	 * @param username The username for client	 */	public ChatClient(String host, int port, ChatIF clientUI, String username){		this.clientUI = clientUI;		this.username = username;   		this.observableClient = new ObservableClient(host, port);		this.observableClient.addObserver(this);		this.login();	}	//Instance methods ************************************************	/**	 * This method handles all data that comes in from the server.	 *	 * @param msg The message from the server.	 */	public void handleMessageFromServer(Object msg)	{		String message = (String)msg;		if(message.startsWith("#")) {			message = message.substring(1);			String[] args = message.split(" ");			if(args.length >= 1) {				if("logoff".equals(args[0])){					try {						this.observableClient.closeConnection();					} catch (IOException e) {					}					clientUI.display("The server have been closed.");				}			}		} else {			clientUI.display(msg.toString());		}	}	/**	 * This method handles all data coming from the UI            	 *	 * @param message The message from the UI.    	 */	public void handleMessageFromClientUI(String message)	{		if(message.startsWith("#")){			if(message.equals("#quit")){				this.quit();			}			else if(message.equals("#logoff")) {        	  				if(this.observableClient.isConnected()){					this.logoff();					clientUI.display("You have been disconnected.");				} else {					clientUI.display("You are already disconnected.");				}			}else if(message.equals("#login")) {				this.login();			}else if(message.contains("#sethost")){				String host[] = message.split("\\s");				if(!observableClient.isConnected()){					observableClient.setHost(host[1]);					clientUI.display("Host changed to:"+host[1]);				}else {					clientUI.display("You need to close the connection to change the host.");				}			}else if(message.contains("#setport")){				String port[] = message.split("\\s");				if(!observableClient.isConnected()){					try {						int portInt = Integer.parseInt(port[1]);						observableClient.setPort(portInt);					} catch (NumberFormatException e) {						clientUI.display("The port should be a number.");					}					clientUI.display("Port changed to: "+port[1]);				} else {					clientUI.display("You need to close the connection to change the port."); 				}			}else if(message.contains("#gethost")){				clientUI.display("Host "+observableClient.getHost());			}else if(message.contains("#getport")){				clientUI.display(String.valueOf("Port: "+observableClient.getPort()));			}else {				clientUI.display("Invalid command.");			}		}else{			try{				observableClient.sendToServer(message);			}			catch(IOException e){				clientUI.display("Could not send message to server.  Terminating client.");				quit();			}		}	}	/**	 * This method terminates the client.	 */	public void quit()	{		try{			observableClient.closeConnection();		}		catch(IOException e) {}		System.exit(0);	}	/**	 * Brutal disconnection	 * @param exception	 */	public void connectionException(Exception exception) {		this.clientUI.display("Server was disconnected");	}	/**	 * Sympa disconnection	 */	public void connectionClosed() {		this.clientUI.display("Connection was closed");	}	/**	 * Message sent when conection is stablished	 */	public void connectionStablished(){		this.clientUI.display("Connection stablished");	}	/**(non-Javadoc)	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)	 * Handles message from ClientConsole	 */	@Override	public void update(Observable o, Object arg) {		String msg;		if (arg instanceof Exception) {			connectionException((Exception) arg);			return;		}		if(arg instanceof String){			msg = (String) arg;			if (ObservableClient.CONNECTION_ESTABLISHED.equals(msg)) {				connectionStablished();			} else if (ObservableClient.CONNECTION_CLOSED.equals(msg)) {				connectionClosed();			} else {				handleMessageFromServer(msg);			}		}	}	/**	 * Login method for chat client	 */	public void login() {		try {			this.observableClient.openConnection();			this.observableClient.sendToServer("#login " + this.username);		} catch (IOException e) {			clientUI.display("Connection failed.");		}	}	/**	 * Method for logoff chat client	 */	public void logoff() {		try{			this.observableClient.sendToServer("#logoff");		}		catch(IOException e){			clientUI.display("Could not send message to server.");		}		try{			this.observableClient.closeConnection();		} catch(IOException e) { 			clientUI.display("Error while closing connection");		}	}}//End of ChatClient class