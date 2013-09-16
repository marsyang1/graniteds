/**
 *   GRANITE DATA SERVICES
 *   Copyright (C) 2006-2013 GRANITE DATA SERVICES S.A.S.
 *
 *   This file is part of Granite Data Services.
 *
 *   Granite Data Services is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or (at your
 *   option) any later version.
 *
 *   Granite Data Services is distributed in the hope that it will be useful, but
 *   WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *   FITNESS FOR A PARTICULAR PURPOSE. See the GNU Library General Public License
 *   for more details.
 *
 *   You should have received a copy of the GNU Library General Public License
 *   along with this library; if not, see <http://www.gnu.org/licenses/>.
 */
package org.granite.gravity.jetty8;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;
import org.granite.context.GraniteContext;
import org.granite.gravity.Gravity;
import org.granite.gravity.GravityManager;
import org.granite.logging.Logger;
import org.granite.messaging.webapp.ServletGraniteContext;

import flex.messaging.messages.CommandMessage;
import flex.messaging.messages.Message;

public class JettyWebSocketHandler extends WebSocketHandler {
	
	private static final Logger log = Logger.getLogger(JettyWebSocketHandler.class);
	
	private final ServletContext servletContext;
	
	public JettyWebSocketHandler(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
    public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
    	if (!"org.granite.gravity".equals(protocol))
    		return null;
    	
		Gravity gravity = GravityManager.getGravity(servletContext);
		JettyWebSocketChannelFactory channelFactory = new JettyWebSocketChannelFactory(gravity, servletContext);
		
		try {
			String connectMessageId = request.getHeader("connectId") != null ? request.getHeader("connectId") : request.getParameter("connectId");
			String clientId = request.getHeader("GDSClientId") != null ? request.getHeader("GDSClientId") : request.getParameter("GDSClientId");
			String clientType = request.getHeader("GDSClientType") != null ? request.getHeader("GDSClientType") : request.getParameter("GDSClientType");
			String sessionId = null;
			HttpSession session = request.getSession(false);
			if (session != null) {
		        ServletGraniteContext.createThreadInstance(gravity.getGraniteConfig(), gravity.getServicesConfig(), 
		        		this.servletContext, session, clientType);
		        
				sessionId = session.getId();
			}
			else {
				for (int i = 0; i < request.getCookies().length; i++) {
					if ("JSESSIONID".equals(request.getCookies()[i].getName())) {
						sessionId = request.getCookies()[i].getValue();
						break;
					}
				}				
		        ServletGraniteContext.createThreadInstance(gravity.getGraniteConfig(), gravity.getServicesConfig(), 
		        		this.servletContext, sessionId, clientType);
			}
			
			log.info("WebSocket connection started %s clientId %s sessionId %s", protocol, clientId, sessionId);
			
			CommandMessage pingMessage = new CommandMessage();
			pingMessage.setMessageId(connectMessageId != null ? connectMessageId : "OPEN_CONNECTION");
			pingMessage.setOperation(CommandMessage.CLIENT_PING_OPERATION);
			if (clientId != null)
				pingMessage.setClientId(clientId);
			
			Message ackMessage = gravity.handleMessage(channelFactory, pingMessage);
			
			JettyWebSocketChannel channel = gravity.getChannel(channelFactory, (String)ackMessage.getClientId());
			
			if (!ackMessage.getClientId().equals(clientId))
				channel.setConnectAckMessage(ackMessage);
			
			return channel;
		}
		finally {
			GraniteContext.release();
		}
    }
}