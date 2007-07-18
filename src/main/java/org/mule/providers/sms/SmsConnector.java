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
import org.mule.umo.UMOComponent;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.provider.UMOMessageReceiver;
import org.mule.util.StringUtils;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    public UMOMessageReceiver createReceiver(UMOComponent component, UMOEndpoint endpoint)
            throws Exception
    {
        long polling;
        polling = pollingFrequency;
        Map props = endpoint.getProperties();
        if (props != null)
        {
            String tempPolling = (String) props.get("pollingFrequency");
            if (tempPolling != null)
            {
                polling = Long.parseLong(tempPolling);
            }
            if (polling <= 0L)
            {
                polling = 60000L;
            }
            if (logger.isDebugEnabled())
            {
                logger.debug("set polling frequency to: " + polling);
            }
            String tempGsmCom = (String) props.get("gsmCom");
            if (tempGsmCom != null)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("set gsmCom to: " + tempGsmCom);
                }
                gsmCom = tempGsmCom;
            }
            String tempGsmBaudrate = (String) props.get("gsmBaudrate");
            if (tempGsmBaudrate != null)
            {
                gsmBaudrate = Integer.parseInt(tempGsmBaudrate);
                if (gsmBaudrate <= 0)
                {
                    gsmBaudrate = 2400;
                }
                if (logger.isDebugEnabled())
                {
                    logger.debug("set gsmBaudrate to: " + gsmBaudrate);
                }
            }
            String tempGsmManufacturer = (String) props.get("gsmManufacturer");
            if (tempGsmManufacturer != null)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("set gsmManufacturer to: " + tempGsmManufacturer);
                }
                gsmManufacturer = tempGsmManufacturer;
            }
            String tempGsmModel = (String) props.get("gsmModel");
            if (tempGsmModel != null)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("set gsmModel to: " + tempGsmModel);
                }
                gsmModel = tempGsmModel;
            }
            String tempDeleteReadMessages = (String) props.get("deleteReadMessages");
            if (tempDeleteReadMessages != null)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("set deleteReadMessages to: " + tempDeleteReadMessages);
                }
                deleteReadMessages = Boolean.parseBoolean(tempDeleteReadMessages);
            }
            String tempReconnect = (String) props.get("reconnect");
            if (tempReconnect != null)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("set deleteReadMessages to: " + tempReconnect);
                }
                reconnect = Boolean.parseBoolean(tempReconnect);
            }
            Map srvOverride = (Map) props.get("serviceOverrides");
            if (srvOverride != null)
            {
                if (super.serviceOverrides == null)
                {
                    super.serviceOverrides = new Properties();
                }
                super.serviceOverrides.setProperty("inbound.transformer", (org.mule.transformers.NoActionTransformer.class).getName());
                super.serviceOverrides.setProperty("outbound.transformer", (org.mule.transformers.NoActionTransformer.class).getName());
            }
        }
        return super.serviceDescriptor.createMessageReceiver(this, component, endpoint, new Object[]{
                gsmCom, Integer.valueOf(gsmBaudrate), gsmManufacturer, gsmModel, Boolean.valueOf(reconnect), new Long(polling)
        });
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
}
