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

import org.mule.providers.AbstractMessageDispatcher;
import org.mule.umo.UMOEvent;
import org.mule.umo.UMOException;
import org.mule.umo.UMOMessage;
import org.mule.umo.endpoint.UMOImmutableEndpoint;

import org.smslib.COutgoingMessage;
import org.smslib.CService;

/** TODO */
public class SmsMessageDispatcher extends AbstractMessageDispatcher
{
    private SmsConnector connector;
    private CService srv;

    public SmsMessageDispatcher(UMOImmutableEndpoint endpoint)
    {
        super(endpoint);
        connector = (SmsConnector) endpoint.getConnector();
    }

    protected void doDispose()
    {
    }

    protected void doDispatch(UMOEvent event)
            throws Exception
    {
        String receiver = event.getMessage().getStringProperty("SMS_RECEIVER", null);
        if (receiver == null)
        {
            receiver = super.endpoint.getEndpointURI().getAddress();
            if (receiver == null)
            {
                throw new UnsupportedOperationException("Unknow receiver for sms, set SmsProperties.SMS_RECEIVER property on message.");
            }
        }
        logger.debug("Sending message to " + receiver);
        boolean statusreport = event.getMessage().getBooleanProperty("SMS_STATUSREPORT", false);
        boolean flash = event.getMessage().getBooleanProperty("SMS_FLASH", false);
        COutgoingMessage msg = new COutgoingMessage(receiver, event.getTransformedMessageAsString());
        msg.setMessageEncoding(1);
        msg.setStatusReport(statusreport);
        msg.setFlashSms(flash);
        msg.setValidityPeriod(8);
        if (connector.isReconnect())
        {
            connectToService();
        }
        srv.sendMessage(msg);
        if (connector.isReconnect())
        {
            disconnectFromService();
        }
        logger.debug("Message sent.");
    }

    protected UMOMessage doSend(UMOEvent event)
            throws Exception
    {
        doDispatch(event);
        return event.getMessage();
    }

    protected void doConnect(UMOImmutableEndpoint arg0)
            throws Exception
    {
        if (!connector.isReconnect())
        {
            connectToService();
        }
    }

    protected void doDisconnect()
            throws Exception
    {
        if (!connector.isReconnect())
        {
            disconnectFromService();
        }
    }

    protected void disconnectFromService() throws Exception
    {
        logger.debug("Closing mobile connection");
        srv.disconnect();
    }

    private void connectToService() throws Exception
    {
        srv = new CService(connector.getGsmCom(), connector.getGsmBaudrate(), connector.getGsmManufacturer(), connector.getGsmModel());
        srv.connect();
        if (logger.isDebugEnabled())
        {
            StringBuffer buf = new StringBuffer(128);
            buf.append("Mobile Device Information: ");
            buf.append("\n\tManufacturer  : ").append(srv.getDeviceInfo().getManufacturer());
            buf.append("\n\tModel         : ").append(srv.getDeviceInfo().getModel());
            buf.append("\n\tSerial No     : ").append(srv.getDeviceInfo().getSerialNo());
            buf.append("\n\tIMSI          : ").append(srv.getDeviceInfo().getImsi());
            buf.append("\n\tS/W Version   : ").append(srv.getDeviceInfo().getSwVersion());
            buf.append("\n\tBattery Level : ").append(srv.getDeviceInfo().getBatteryLevel()).append("%");
            buf.append("\n\tSignal Level  : ").append(srv.getDeviceInfo().getSignalLevel()).append("%");
            buf.append("\n\tGPRS Status   : ").append(srv.getDeviceInfo().getGprsStatus() ? "Enabled" : "Disabled");
            logger.debug(buf.toString());
        }
    }

    protected UMOMessage doReceive(UMOImmutableEndpoint endpoint, long timeout)
            throws Exception
    {
        throw new UnsupportedOperationException("doReceive");
    }

    public Object getDelegateSession() throws UMOException
    {
        return null;
    }
}
