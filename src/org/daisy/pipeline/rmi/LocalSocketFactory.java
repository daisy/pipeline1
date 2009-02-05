/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.pipeline.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

/**
 * Client socket factory implementation creating sockets on the local host.
 * 
 * @author Romain Deltour
 * 
 */
public class LocalSocketFactory implements RMIClientSocketFactory,RMIServerSocketFactory,
		Serializable {

	private static final long serialVersionUID = -7246215298577363002L;

	public Socket createSocket(String host, int port) throws IOException {
		return new Socket(
				InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }), port);
	}

	public ServerSocket createServerSocket(int port) throws IOException {
		return new ServerSocket(port, 0, InetAddress.getByAddress(new byte[] {
				127, 0, 0, 1 }));
	}

}
