/*
 *  Copyright 2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.ibatis.abator.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.ibatis.abator.api.dom.xml.Attribute;
import org.apache.ibatis.abator.api.dom.xml.XmlElement;

/**
 * @author Jeff Butler
 */
public abstract class PropertyHolder {
	private Map properties;

	/**
	 *  
	 */
	public PropertyHolder() {
		super();
		properties = new HashMap();
	}

	public void addProperty(String name, String value) {
		properties.put(name, value);
	}

	public Map getProperties() {
		return properties;
	}
    
    protected void addPropertyXmlElements(XmlElement xmlElement) {
        Iterator iter = properties.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            XmlElement propertyElement = new XmlElement("property"); //$NON-NLS-1$
            propertyElement.addAttribute(new Attribute("name", (String) entry.getKey())); //$NON-NLS-1$
            propertyElement.addAttribute(new Attribute("value", (String) entry.getValue())); //$NON-NLS-1$
            xmlElement.addElement(propertyElement);
        }
    }
}
