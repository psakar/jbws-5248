/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.systest.wssec.examples.common;

import javax.jws.WebService;

import org.apache.cxf.annotations.EndpointProperties;
import org.apache.cxf.annotations.EndpointProperty;
import org.apache.cxf.annotations.Logging;

@WebService(targetNamespace = "http://www.example.org/contract/DoubleIt",
            serviceName = "DoubleItService",
            portName = "DoubleItSecureConversationPort",
            endpointInterface = "org.apache.cxf.systest.wssec.examples.common.DoubleItPortType",
            wsdlLocation="/DoubleItSecConv.wsdl")
@Logging(pretty=true)
@EndpointProperties({
  @EndpointProperty(key="ws-security.callback-handler", value="org.apache.cxf.systest.wssec.examples.common.CommonPasswordCallback"),
  @EndpointProperty(key="ws-security.signature.properties", value="bob.properties"),
  @EndpointProperty(key="ws-security.encryption.username", value="useReqSigCert"),
  @EndpointProperty(key="ws-security.subject.cert.constraints", value=".*O=apache.org.*")
})
public class DoubleItImpl implements DoubleItPortType
{
  @Override
  public int doubleIt(int numberToDouble)
  {
    return numberToDouble * 2;
  }
}
