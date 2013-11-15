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

package org.apache.cxf.systest.wssec.examples;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import org.apache.cxf.systest.wssec.examples.common.CommonPasswordCallback;
import org.apache.cxf.systest.wssec.examples.common.DoubleItImpl;
import org.apache.cxf.systest.wssec.examples.common.DoubleItPortType;
import org.apache.cxf.systest.wssec.examples.saml.SamlCallbackHandler;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class SecureConversationIT
{
  private static final String name = "saml";
  private static final String NAMESPACE = "http://www.example.org/contract/DoubleIt";
  private static final QName SERVICE_QNAME = new QName(NAMESPACE, "DoubleItService");
  private static final String ENDPOINT_URL = "http://" + getServerBindAddress() + ":" + getServerBindPort() + "/" + name + "/DoubleItService";
  private static final String WSDL_URL = ENDPOINT_URL + "?wsdl";

  static String getServerBindAddress() {
    String serverBindAddress = System.getProperty("jboss.bind.address");
    return serverBindAddress == null || serverBindAddress.isEmpty() ? "localhost" : serverBindAddress;
  }

  static String getServerBindPort() {
    String serverBindport = System.getProperty("jboss.bind.port");
    return serverBindport == null || serverBindport.isEmpty() ? "8080" : serverBindport;
  }

  @Deployment
  static WebArchive createDeployment() throws Exception {

    String resourcePath = "src/test/resources";
    WebArchive archive = ShrinkWrap
        .create(WebArchive.class, name + ".war")
        .setManifest(new StringAsset("Manifest-Version: 1.0\n" + "Dependencies: org.apache.cxf,org.apache.ws.security,org.apache.cxf.impl\n"))
        .addAsWebInfResource(new File(resourcePath, "DoubleItSecConv.wsdl"))

        .addClass(SamlCallbackHandler.class)

        .addClass(CommonPasswordCallback.class)
        .addClass(DoubleItImpl.class)
        .addClass(DoubleItPortType.class)
         ;
    archive.as(ZipExporter.class).exportTo(new File("/tmp", archive.getName()), true);
    return archive;
  }

  @Test
  public void testSecureConversation() throws Exception
  {
    URL wsdl = new URL(WSDL_URL);

    Service service = Service.create(wsdl, SERVICE_QNAME);
    QName portQName = new QName(NAMESPACE, "DoubleItSecureConversationPort");
    DoubleItPortType port = service.getPort(portQName, DoubleItPortType.class);

    Map<String, Object> context = ((BindingProvider)port).getRequestContext();
    context.put("ws-security.callback-handler", "org.apache.cxf.systest.wssec.examples.common.CommonPasswordCallback");
    context.put("ws-security.saml-callback-handler", "org.apache.cxf.systest.wssec.examples.saml.SamlCallbackHandler");
    context.put("ws-security.self-sign-saml-assertion", "true");
    context.put("ws-security.signature.properties", "alice.properties");
    context.put("ws-security.signature.username", "alice");
    context.put("ws-security.encryption.properties", "bob.properties");
    context.put("ws-security.encryption.username", "bob");

    assertEquals(50, port.doubleIt(25));

  }
}
