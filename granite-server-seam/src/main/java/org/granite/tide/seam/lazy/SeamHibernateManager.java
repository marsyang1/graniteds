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
package org.granite.tide.seam.lazy;

import java.io.Serializable;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.Entity;

/**
 * Manager responsible for looking up the Hibernate Session for the
 * passed in SessionManagerName
 * @author CIngram
 */
public class SeamHibernateManager extends HibernateContextManager  {
	
	private String sessionManagerName = null;
	private Session session = null;

	public SeamHibernateManager(String sessionManagerName) {
		this.sessionManagerName = sessionManagerName;
	}
	

	/**
	 * Attach the passed in entity with the HibernateManager stored 
	 * in Seam.
	 * @param entity
	 * @return the attached hibernate object
	 */
    @Override
    public Object fetchEntity(Object entity, String[] fetch) {
        Serializable id = (Serializable)Entity.forClass(entity.getClass()).getIdentifier(entity);
        if (id == null)
            return null;
        
        this.session = (Session)Component.getInstance(sessionManagerName);
        
        if (fetch == null)
        	return session.get(entity.getClass(), id);
        
        for (String f : fetch) {
	        Query q = session.createQuery("select e from " + entity.getClass().getName() + " e left join fetch e." + f + " where e = :entity");
	        q.setParameter("entity", entity);
	        entity = q.uniqueResult();
        }
        return entity;
    }
	
	/**
	 * Disconnects from the current hibernate session.
	 */
	@Override
	protected void disconnectSession() {
		session.disconnect();
	}
}
