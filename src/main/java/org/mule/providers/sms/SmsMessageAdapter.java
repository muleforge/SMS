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

import org.mule.providers.AbstractMessageAdapter;
import org.mule.umo.provider.MessageTypeNotSupportedException;

import org.smslib.CIncomingMessage;


public class SmsMessageAdapter extends AbstractMessageAdapter
{

    private static final long serialVersionUID = 0xc46645da79d115d2L;
    private CIncomingMessage message = null;

    public SmsMessageAdapter(Object obj)
            throws MessageTypeNotSupportedException
    {

        if (obj instanceof CIncomingMessage)
        {
            message = (CIncomingMessage) obj;
        }
        else
        {
            throw new MessageTypeNotSupportedException(message, org.mule.providers.sms.SmsMessageAdapter.class);
        }
        if (message.getDate() != null)
        {
            setProperty("SMS_DATE", message.getDate());
        }
        setProperty("SMS_ENCODING", Integer.valueOf(message.getMessageEncoding()));
        if (message.getId() != null)
        {
            setProperty("SMS_ID", message.getId());
            id = message.getId();
        }
        if (message.getOriginator() != null)
        {
            setProperty("SMS_SENDER", message.getOriginator());

        }
    }

    //@Override
    public String getPayloadAsString(String encoding) throws Exception
    {
        return message.getText();
    }

    //@Override
    public byte[] getPayloadAsBytes() throws Exception
    {
        return message.getText().getBytes();
    }

    //@Override
    public Object getPayload()
    {
        return message;
    }

    //@Override
    public Object getReplyTo()
    {
        return message.getOriginator();
    }
}
