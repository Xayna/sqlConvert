package demo.connections.IdbConnections;

import java.sql.Connection;

public interface IConnection {

	public boolean initailize ();
	
	public Connection connect ();
	
	public boolean close ();
	
	
}
