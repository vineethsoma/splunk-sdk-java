/*
 * Copyright 2011 Splunk, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"): you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.splunk.sdk;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;

/**
 * Low level splunk sdk http incremental reader and data converter
 */

public class Results {

    public XMLEventReader Results(HttpURLConnection urlconn) throws IOException, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);   // default true

        XMLEventReader ereader = factory.createXMLEventReader(new InputStreamReader(urlconn.getInputStream()));

        return ereader;
    }

    public XMLEvent getContentsEvent(XMLEventReader ereader) throws XMLStreamException {
        StringBuilder composite = new StringBuilder();

        // TODO: break out XML processing (?)
        return ereader.nextEvent();
    }

    // common occurrence for samples and examples or known small XML returns
    public String getContentsString(XMLEventReader ereader)  throws XMLStreamException {
        StringBuilder bstring = new StringBuilder();

        while(ereader.hasNext()) {
            XMLEvent event = ereader.nextEvent();
            if (event.getEventType() != XMLEvent.END_DOCUMENT) {
                bstring.append(event.toString());
            }
        }
        return bstring.toString();
    }

    //TODO: add other methods like getHeaders(), getStatusCode(), etc...
    // int response = urlconn.getResponseCode();

}