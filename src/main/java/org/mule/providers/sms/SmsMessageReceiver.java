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

import org.mule.impl.MuleMessage;
import org.mule.providers.PollingMessageReceiver;
import org.mule.umo.UMOComponent;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.lifecycle.InitialisationException;
import org.mule.umo.provider.UMOConnector;

import java.util.LinkedList;

import org.smslib.CIncomingMessage;
import org.smslib.CService;

/**
 * TODO
 */
public class SmsMessageReceiver extends PollingMessageReceiver
{
    private String gsmCom;
    private int gsmBaudrate;
    private String gsmManufacturer;
    private String gsmModel;
    private boolean reconnect;
    private CService srv;

    public SmsMessageReceiver(UMOConnector connector, UMOComponent component, UMOEndpoint endpoint, String gsmCom, Integer gsmBaudrate, String gsmManufacturer, String gsmModel,
                              Boolean reconnect, Long pollingFrequency)
            throws InitialisationException
    {
        super(connector, component, endpoint, pollingFrequency);
        this.gsmCom = gsmCom;
        this.gsmBaudrate = gsmBaudrate.intValue();
        this.gsmManufacturer = gsmManufacturer;
        this.gsmModel = gsmModel;
        this.reconnect = reconnect.booleanValue();
    }

    public void poll() throws Exception
    {
        LinkedList msgList = new LinkedList();
        if (reconnect)
        {
            connectToService();
        }
        srv.readMessages(msgList, 2);
        for (int i = 0; i < msgList.size(); i++)
        {
            CIncomingMessage msg = (CIncomingMessage) msgList.get(i);
            if (((SmsConnector) super.connector).isDeleteReadMessages())
            {
                srv.deleteMessage(msg);
            }
            MuleMessage message = new MuleMessage(super.connector.getMessageAdapter(msg));
            routeMessage(message, super.endpoint.isSynchronous());
        }

        if (reconnect)
        {
            disconnectFromService();
        }
    }

    public void doConnect() throws Exception
    {
        if (!reconnect)
        {
            connectToService();
        }
    }

    public void doDisconnect() throws Exception
    {
        if (!reconnect)
        {
            disconnectFromService();
        }
    }

    private void disconnectFromService() throws Exception
    {
        logger.debug("Closing mobile connection");
        srv.disconnect();
    }

    private void connectToService() throws Exception
    {
        srv = new CService(gsmCom, gsmBaudrate, gsmManufacturer, gsmModel);
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
}
