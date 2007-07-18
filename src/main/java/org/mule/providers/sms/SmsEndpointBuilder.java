/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.providers.sms;

import org.mule.impl.endpoint.AbstractEndpointBuilder;
import org.mule.umo.endpoint.MalformedEndpointException;
import org.mule.util.StringUtils;

import java.net.URI;
import java.util.Properties;
import java.util.regex.Pattern;

/** TODO */
public class SmsEndpointBuilder extends AbstractEndpointBuilder
{
    private Pattern p = Pattern.compile("[^0-9+]");
    
    //@Override
    protected void setEndpoint(URI uri, Properties properties) throws MalformedEndpointException
    {
        address = uri.getHost();
        if (address==null)
        {
            address = uri.getAuthority();
        }
        if(!address.startsWith("+"))
        {
            throw new MalformedEndpointException("Sms endpoint addresses must be in the form of +[country code][phone number]");
        }
        address = p.matcher(address).replaceAll(StringUtils.EMPTY);
    }
}
