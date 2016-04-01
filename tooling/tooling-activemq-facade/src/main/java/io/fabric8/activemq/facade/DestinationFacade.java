/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.activemq.facade;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.command.ActiveMQDestination;

/**
 * 
 */
public class DestinationFacade {

    private String jmsDestination;
    private String jmsDestinationType;
    private BrokerFacade brokerFacade;

    public DestinationFacade(BrokerFacade brokerFacade) {
        this.brokerFacade = brokerFacade;
    }

    public String toString() {
        return super.toString() + "[destination:" + jmsDestination + "; type=" + jmsDestinationType + "]";
    }

    // Operations
    // -------------------------------------------------------------------------
    public void removeDestination() throws Exception {
        getValidDestination();
        if (isQueue()) {
            getBrokerAdmin().removeQueue(getJMSDestination());
        } else {
            getBrokerAdmin().removeTopic(getJMSDestination());
        }
    }

    public void addDestination() throws Exception {
        if (isQueue()) {
            getBrokerAdmin().addQueue(getValidDestination());
        } else {
            getBrokerAdmin().addTopic(getValidDestination());
        }
    }

    // Properties
    // -------------------------------------------------------------------------
    public BrokerViewMBean getBrokerAdmin() throws Exception {
        if (brokerFacade == null) {
            throw new IllegalArgumentException("No brokerFacade injected!");
        }
        BrokerViewMBean answer = brokerFacade.getBrokerAdmin();
        if (answer == null) {
            throw new IllegalArgumentException("No brokerAdmin on the injected brokerFacade: " + brokerFacade);
        }
        return answer;
    }

    public BrokerFacade getBrokerFacade() {
        return brokerFacade;
    }

    public boolean isQueue() {
        if (jmsDestinationType != null && jmsDestinationType.equalsIgnoreCase("topic")) {
            return false;
        }
        return true;
    }

    public String getJMSDestination() {
        return jmsDestination;
    }

    public void setJMSDestination(String destination) {
        this.jmsDestination = destination;
    }

    public String getJMSDestinationType() {
        return jmsDestinationType;
    }

    public void setJMSDestinationType(String type) {
        this.jmsDestinationType = type;
    }

    protected ActiveMQDestination createDestination() {
        byte destinationType = isQueue() ? ActiveMQDestination.QUEUE_TYPE : ActiveMQDestination.TOPIC_TYPE;
        return ActiveMQDestination.createDestination(getValidDestination(), destinationType);
    }

    protected String getValidDestination() {
        if (jmsDestination == null) {
            throw new IllegalArgumentException("No JMSDestination parameter specified");
        }
        return jmsDestination;
    }
    
    protected QueueViewMBean getQueueView() throws Exception {
        String name = getPhysicalDestinationName();
        return getBrokerFacade().getQueue(name);
    }    

    protected String getPhysicalDestinationName() {
        return createDestination().getPhysicalName();
    }
}
