package com.johndeere;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

// mail web service client
public class MailServiceClient {

	/**
	 * Get mail delivery information 
	 * @param id delivery ID
	 * @param srcPostalCode source postal code
	 * @param destPostalCode destination postal code
	 * @return hash map with attributes returned by mail web service
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public HashMap<String,String> getDeliveryInfo(int id, String srcPostalCode, String destPostalCode) throws IOException, ParserConfigurationException, SAXException, MailServiceClientError {
		
		// get delivery info from mail web service
		String urlString = String.format("http://ws.correios.com.br/calculador/CalcPrecoPrazo.asmx/CalcPrazo?nCdServico=%s&sCepOrigem=%s&sCepDestino=%s", id, srcPostalCode, destPostalCode);
		String[] attribNames = {"Codigo", "PrazoEntrega", "ValorMaoPropria", "ValorAvisoRecebimento", "ValorValorDeclarado", 
				"EntregaDomiciliar", "EntregaSabado", "Erro", "MsgErro", "ValorSemAdicionais", "obsFim", "DataMaxEntrega", "HoraMaxEntrega"};

		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(conn.getInputStream());
		
		// parse XML response
		Element resultNode = doc.getDocumentElement();
		Element servicesNode = (Element) resultNode.getElementsByTagName("Servicos").item(0);
		Element serviceNode = (Element) servicesNode.getElementsByTagName("cServico").item(0);
		HashMap<String, String> attribs = new HashMap<String,String>(); 
		for (String attribName: attribNames) {
			if (serviceNode.getElementsByTagName(attribName).getLength() > 0) {
				attribs.put(attribName, serviceNode.getElementsByTagName(attribName).item(0).getTextContent());
			}
		}
		// handle mail web service errors
		if (attribs.get("Erro") != null && attribs.get("Erro") != ""){
			throw new MailServiceClientError(attribs.get("MsgErro"));
		}
		
		return attribs;
	}
}
