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
import org.mule.providers.AbstractMessageReceiver;
import org.mule.transaction.TransactionCallback;
import org.mule.transaction.TransactionTemplate;
import org.mule.umo.UMOComponent;
import org.mule.umo.UMOException;
import org.mule.umo.UMOMessage;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.lifecycle.InitialisationException;
import org.mule.umo.lifecycle.LifecycleException;
import org.mule.umo.provider.UMOConnector;
import org.mule.umo.provider.UMOMessageAdapter;

import java.util.LinkedList;

import javax.resource.spi.work.Work;

import org.apache.log4j.Logger;
import org.smslib.IInboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.Message;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.gateway.AbstractGateway;
import org.smslib.gateway.ModemGateway;

/** TODO */
public class SmsMessageReceiver extends AbstractMessageReceiver
{
    private Service service;
    private AbstractGateway gateway;
    private SmsConnector smsConnector;

    public SmsMessageReceiver(UMOConnector connector, UMOComponent component, UMOEndpoint endpoint)
            throws InitialisationException
    {
        super(connector, component, endpoint);

        smsConnector = (SmsConnector) connector;
    }

    protected AbstractGateway createGateway(SmsConnector smsConnector) throws InitialisationException
    {
        String gatewayId = "Mule.sms." + smsConnector.getGsmCom() + ":" + smsConnector.getGsmBaudrate();
        String gsmCom = smsConnector.getGsmCom();
        int gsmBaud = smsConnector.getGsmBaudrate();

        if (smsConnector.getModemIp() != null && smsConnector.getModemPort() > -1)
        {
            gsmCom = smsConnector.getModemIp();
            gsmBaud = smsConnector.getModemPort();
            logger.info("Using IP Modem: " + gsmCom + ":" + gsmBaud);
        }

        ModemGateway gateway = new ModemGateway(gatewayId, gsmCom, gsmBaud,
                smsConnector.getGsmManufacturer(), smsConnector.getGsmManufacturer(), Logger.getLogger(getClass()));

        if (smsConnector.getSimPin() != null)
        {
            gateway.setSimPin(smsConnector.getSimPin());
        }
        gateway.setInbound(true);
        gateway.setOutbound(false);

        try
        {
            if (logger.isDebugEnabled())
            {
                StringBuffer buf = new StringBuffer(128);
//                buf.append("Mobile Device Information: ");
//                buf.append("\n\tManufacturer  : ").append(gateway.getManufacturer());
//                buf.append("\n\tModel         : ").append(gateway.getModel());
//                buf.append("\n\tSerial No     : ").append(gateway.getSerialNo());
//                buf.append("\n\tIMSI          : ").append(gateway.getImsi());
//                buf.append("\n\tS/W Version   : ").append(gateway.getSwVersion());
//                buf.append("\n\tBattery Level : ").append(gateway.getBatteryLevel()).append("%");
//                buf.append("\n\tSignal Level  : ").append(gateway.getSignalLevel()).append("%");
//                buf.append("\n\tGPRS Status   : ").append(gateway.getGprsStatus() ? "Enabled" : "Disabled");
                logger.debug(buf.toString());
            }
        }
        catch (Exception e)
        {
            throw new InitialisationException(e, this);
        }
        return gateway;
    }

    public void poll() throws Exception
    {
        LinkedList msgList = new LinkedList();
        if (smsConnector.isReconnect())
        {
            connectToService();
        }
        service.readMessages(msgList, InboundMessage.MessageClass.UNREAD);
        for (int i = 0; i < msgList.size(); i++)
        {
            InboundMessage msg = (InboundMessage) msgList.get(i);
            if (smsConnector.isDeleteReadMessages())
            {
                service.deleteMessage(msg);
            }
            MuleMessage message = new MuleMessage(super.connector.getMessageAdapter(msg));
            routeMessage(message, super.endpoint.isSynchronous());
        }

        if (smsConnector.isReconnect())
        {
            disconnectFromService();
        }
    }

    public void doConnect() throws Exception
    {
        if (!smsConnector.isReconnect())
        {
            connectToService();
        }
    }

    public void doDisconnect() throws Exception
    {
        if (!smsConnector.isReconnect())
        {
            disconnectFromService();
        }
    }

    private void disconnectFromService() throws Exception
    {
        logger.debug("Closing mobile connection");
        service.stopService();
    }

    private void connectToService() throws Exception
    {
        if (!connected.get())
        {
            service = new Service();
            gateway = createGateway(smsConnector);
            gateway.setInboundNotification(new InboundNotification());
            service.addGateway(gateway);
        }
    }

    //@Override
    public void doStart() throws UMOException
    {
        try
        {
            service.startService();
        }
        catch (Exception e)
        {
            throw new LifecycleException(e, this);
        }

    }

    //@Override
    public void doStop() throws UMOException
    {
        try
        {
            service.stopService();
        }
        catch (Exception e)
        {
            throw new LifecycleException(e, this);
        }
    }

    public class InboundNotification implements IInboundMessageNotification
    {
        public void process(String gatewayId, Message.MessageType msgType, String memLoc, int memIndex)
        {
            LinkedList msgList;

            if (Message.MessageType.INBOUND.equals(msgType))
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("New Inbound message detected from Gateway: " + gatewayId + " : " + memLoc + " @ " + memIndex);
                }
                try
                {
                    // Read...
                    msgList = new LinkedList();
                    service.readMessages(msgList, InboundMessage.MessageClass.UNREAD, gatewayId);
                    for (int i = 0; i < msgList.size(); i++)
                    {
                        InboundMessage msg = (InboundMessage) msgList.get(i);
                        if (smsConnector.isDeleteReadMessages())
                        {
                            service.deleteMessage(msg);
                        }
                        getWorkManager().scheduleWork(new Worker(msg));
                    }
                }
                catch (Exception e)
                {
                    handleException(e);
                }
            }
            else if (Message.MessageType.STATUSREPORT.equals(msgType))
            {

                if (logger.isTraceEnabled())
                {
                    logger.trace("New Status Report message detected from Gateway: " + gatewayId + " : " + memLoc + " @ " + memIndex);
                }
            }
        }
    }


    private class Worker implements Work
    {
        private InboundMessage message;

        public Worker(InboundMessage message)
        {
            this.message = message;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            TransactionTemplate tt = new TransactionTemplate(endpoint.getTransactionConfig(),
                    connector.getExceptionListener());

            // Receive messages and process them in a single transaction
            // Do not enable threading here, but serveral workers
            // may have been started
            TransactionCallback cb = new TransactionCallback()
            {
                public Object doInTransaction() throws Exception
                {
                    UMOMessageAdapter adapter = connector.getMessageAdapter(message);
                    return routeMessage(new MuleMessage(adapter));
                }
            };

            try
            {
                UMOMessage result = (UMOMessage) tt.execute(cb);
                if (result != null)
                {
                    service.sendMessage(new OutboundMessage(message.getOriginator(), result.getPayloadAsString()), gateway.getGatewayId());
                }
            }
            catch (Exception e)
            {
                getConnector().handleException(e);
            }
        }

        public void release()
        {
            // no op
        }
    }
}
