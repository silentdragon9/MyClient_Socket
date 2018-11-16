/*
This program will communicate using socket 8080 and sending SNMP trap version 1 and version 2 to server (MyServer_Socket.java).

Name: Mohammad Ariff Bin Idris
ID: 2017430762
Subject: Test 3 ITT786
Dateline: 18 November 2018
*/

package myclient_socket;

import java.util.Date;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

  
public class MyClient_Socket
{
  public static final String  community  = "public";

  //  Hantar perangkap untuk sistem lokasi merujuk kepada RFC1213
  public static final String  trapOid          = "1.3.6.1.2.1.25.2.2.0";                         

  public static final String  ipAddress      = "127.0.0.1";
  
  public static final int     port      = 8080;
  
  public MyClient_Socket()
  {
  }

  public static void main(String[] args)
  {
    MyClient_Socket snmp4JTrap = new MyClient_Socket();

    /* Hantar perangkap versi 1 */
    snmp4JTrap.sendSnmpV1Trap();

    /* Hantar perangkap versi 2 */
    snmp4JTrap.sendSnmpV2Trap();
  }

  /**
   * Hantar perangkap versi 1 kepada Localhost melalui port 8080
   */
  public void sendSnmpV1Trap()
  {
    try
    {
      //Buat transport mapping versi 1
      TransportMapping transport = new DefaultUdpTransportMapping();
      transport.listen();

      //Buat Sasaran 
      CommunityTarget comtarget = new CommunityTarget();
      comtarget.setCommunity(new OctetString(community));
      comtarget.setVersion(SnmpConstants.version1);
      comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
      comtarget.setRetries(2);
      comtarget.setTimeout(5000);

      //Buat PDU versi 1
      PDUv1 pdu = new PDUv1();
      pdu.setType(PDU.V1TRAP);
      pdu.setEnterprise(new OID(trapOid));
      pdu.setGenericTrap(PDUv1.ENTERPRISE_SPECIFIC);
      pdu.setSpecificTrap(1);
      pdu.setAgentAddress(new IpAddress(ipAddress));

      //Hantar PDU
      Snmp snmp = new Snmp(transport);
      System.out.println("Sending V1 Trap to " + ipAddress + " on Port " + port);
      snmp.send(pdu, comtarget);
      snmp.close();
    }
    catch (Exception e)
    {
      System.err.println("Error in Sending V1 Trap to " + ipAddress + " on Port " + port);
      System.err.println("Exception Message = " + e.getMessage());
    }
  }

  
  /**
   * Hantar perangkap versi 2 kepada localhost melalui port 8080
   */
  public void sendSnmpV2Trap()
  {
    try
    {
      //Buat transport mapping versi 2
      TransportMapping transport = new DefaultUdpTransportMapping();
      transport.listen();

      //Buat sasaran
      CommunityTarget comtarget = new CommunityTarget();
      comtarget.setCommunity(new OctetString(community));
      comtarget.setVersion(SnmpConstants.version2c);
      comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
      comtarget.setRetries(2);
      comtarget.setTimeout(5000);

      //Buat PDU versi 2
      PDU pdu = new PDU();
      
      // Tentukan masa bagi sistem
      pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new OctetString(new Date().toString())));
      pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(trapOid)));
      pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(ipAddress)));

      // Pembolehubah bagi objek
      pdu.add(new VariableBinding(new OID(trapOid), new OctetString("Major"))); 
      pdu.setType(PDU.NOTIFICATION);
      
      //Hantar PDU
      Snmp snmp = new Snmp(transport);
      System.out.println("Sending V2 Trap to " + ipAddress + " on Port " + port);
      snmp.send(pdu, comtarget);
      snmp.close();
    }
    catch (Exception e)
    {
      System.err.println("Error in Sending V2 Trap to " + ipAddress + " on Port " + port);
      System.err.println("Exception Message = " + e.getMessage());
    }
  }
}