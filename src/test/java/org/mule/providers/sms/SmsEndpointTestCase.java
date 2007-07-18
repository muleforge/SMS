/**
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements. See the NOTICE file
distributed with this work for additional information
regarding copyright ownership. The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.
*/
package org.mule.providers.sms;

import org.mule.tck.AbstractMuleTestCase;
import org.mule.umo.endpoint.UMOEndpointURI;
import org.mule.impl.endpoint.MuleEndpointURI;

public class SmsEndpointTestCase extends AbstractMuleTestCase
{
    public void testCorrect() throws Exception
    {
        UMOEndpointURI url = new MuleEndpointURI("sms://+441234567");
        assertEquals("sms", url.getScheme());
        assertEquals("+441234567", url.getAddress());
        assertNull(url.getEndpointName());
        assertEquals("sms://+441234567", url.toString());
    }

    public void testCorrectWithDashes() throws Exception
    {
        UMOEndpointURI url = new MuleEndpointURI("sms://+44-1234-567");
        assertEquals("sms", url.getScheme());
        assertEquals("+441234567", url.getAddress());
        assertNull(url.getEndpointName());
        assertEquals("sms://+44-1234-567", url.toString());
    }

}
