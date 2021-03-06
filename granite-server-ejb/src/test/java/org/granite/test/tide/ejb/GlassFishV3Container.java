/**
 *   GRANITE DATA SERVICES
 *   Copyright (C) 2006-2015 GRANITE DATA SERVICES S.A.S.
 *
 *   This file is part of the Granite Data Services Platform.
 *
 *   Granite Data Services is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   Granite Data Services is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 *   General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 *   USA, or see <http://www.gnu.org/licenses/>.
 */
package org.granite.test.tide.ejb;

import java.lang.reflect.Field;
import java.util.Map;

import javax.naming.InitialContext;

import org.glassfish.api.deployment.DeployCommandParameters;
import org.glassfish.internal.data.ApplicationInfo;
import org.glassfish.internal.embedded.EmbeddedDeployer;
import org.glassfish.internal.embedded.Server;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.glassfish.api.ShrinkwrapReadableArchive;


public class GlassFishV3Container implements EJBContainer {
	
	private ClassLoader classLoader = null;
	private Server server = null;
	private EmbeddedDeployer deployer = null;
	private String appName = null;
	private InitialContext ctx = null;
	
	public void start(JavaArchive archive) throws Exception {
		Server.Builder builder = new Server.Builder("test");
		server = builder.build();
		server.start();
		deployer = server.getDeployer();
		appName = deployer.deploy(archive.as(ShrinkwrapReadableArchive.class), new DeployCommandParameters());
		Field field = deployer.getClass().getDeclaredField("deployedApps");
		field.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)field.get(deployer);
		Object deployedInfo = map.get(appName);
		field = deployedInfo.getClass().getDeclaredField("appInfo");
		field.setAccessible(true);
		ApplicationInfo appInfo = (ApplicationInfo)field.get(deployedInfo);
		field = appInfo.getClass().getDeclaredField("appClassLoader");
		field.setAccessible(true);
		ClassLoader appClassLoader = (ClassLoader)field.get(appInfo);
		
		classLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(appClassLoader);
		
		ctx = new InitialContext();     
	}
	
	public InitialContext getInitialContext() {
		return ctx;
	}

	public void stop() throws Exception {
		deployer.undeploy(appName, null);		
		server.stop();
        
        Thread.currentThread().setContextClassLoader(classLoader);
	}
}
