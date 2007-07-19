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

import org.mule.providers.AbstractServiceEnabledConnector;
import org.mule.util.StringUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gnu.io.CommPortIdentifier;

public class SmsConnector extends AbstractServiceEnabledConnector
{

    private static Log logger = LogFactory.getLog(org.mule.providers.sms.SmsConnector.class);

    public static final String DEFAULT_GSM_COM = "COM40";
    public static final int DEFAULT_GSM_BAUD_RATE = 57600;
    //public static final int DEFAULT_GSM_BAUDERATE = 2400;
    public static final String DEFAULT_GSM_MANUFACTURER = "Nokia";
    public static final String DEFAULT_GSM_MODEL = StringUtils.EMPTY;

    public static final String PROPERTY_POLLING_FREQUENCY = "pollingFrequency";
    public static final String PROPERTY_GSM_COM = "gsmCom";
    public static final String PROPERTY_GSM_BAUDRATE = "gsmBaudrate";
    public static final String PROPERTY_GSM_MANUFACTURER = "gsmManufacturer";
    public static final String PROPERTY_GSM_MODEL = "gsmModel";
    public static final String PROPERTY_SERVICE_OVERRIDE = "serviceOverrides";
    public static final String PROPERTY_DELETE_MESSAGE = "deleteReadMessages";
    public static final String PROPERTY_RECONNECT = "reconnect";
    public static final long DEFAULT_POLLING_FREQUENCY = 60000L;

    private boolean deleteReadMessages;
    private long pollingFrequency;
    private String gsmCom;
    private int gsmBaudrate;
    private String gsmManufacturer;
    private String gsmModel;
    /* If you prefer to use an IP modem, use the modemIp and modemPort and leave the gsmCom and gsmBaudRte blank */
    private String modemIp;
    private int modemPort;
    private String simPin;

    private boolean reconnect;

    public SmsConnector()
    {
        gsmCom = DEFAULT_GSM_COM;
        gsmBaudrate = DEFAULT_GSM_BAUD_RATE;
        gsmManufacturer = DEFAULT_GSM_MANUFACTURER;
        gsmModel = DEFAULT_GSM_MODEL;
        reconnect = false;
    }

    public boolean isReconnect()
    {
        return reconnect;
    }

    public void setReconnect(boolean reconnect)
    {
        this.reconnect = reconnect;
    }

    public String getProtocol()
    {
        return "sms";
    }

    public int getGsmBaudrate()
    {
        return gsmBaudrate;
    }

    public void setGsmBaudrate(int gsmBaudrate)
    {
        this.gsmBaudrate = gsmBaudrate;
    }

    public String getGsmCom()
    {
        return gsmCom;
    }

    public void setGsmCom(String gsmCom)
    {
        this.gsmCom = gsmCom;
    }

    public String getGsmManufacturer()
    {
        return gsmManufacturer;
    }

    public void setGsmManufacturer(String gsmManufacturer)
    {
        this.gsmManufacturer = gsmManufacturer;
    }

    public String getGsmModel()
    {
        return gsmModel;
    }

    public void setGsmModel(String gsmModel)
    {
        this.gsmModel = gsmModel;
    }

    public long getPollingFrequency()
    {
        return pollingFrequency;
    }

    public void setPollingFrequency(long pollingFrequency)
    {
        this.pollingFrequency = pollingFrequency;
    }

    public boolean isDeleteReadMessages()
    {
        return deleteReadMessages;
    }

    public void setDeleteReadMessages(boolean deleteReadMessages)
    {
        this.deleteReadMessages = deleteReadMessages;
    }

    public String getModemIp()
    {
        return modemIp;
    }

    public void setModemIp(String modemIp)
    {
        this.modemIp = modemIp;
    }

    public int getModemPort()
    {
        return modemPort;
    }

    public void setModemPort(int modemPort)
    {
        this.modemPort = modemPort;
    }

    public String getSimPin()
    {
        return simPin;
    }

    public void setSimPin(String simPin)
    {
        this.simPin = simPin;
    }

    public List getComPorts()
    {
        CommPortIdentifier portId;
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        List ports = new ArrayList(10);

        while (portList.hasMoreElements())
        {
            portId = (CommPortIdentifier) portList.nextElement();

            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
                ports.add(portId.getName());
            }
        }
        return ports;
    }
}
