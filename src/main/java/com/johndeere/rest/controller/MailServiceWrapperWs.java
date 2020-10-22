package com.johndeere.rest.controller;

import java.util.HashMap;

import com.johndeere.MailServiceClient;
import com.johndeere.rest.model.MailDeliverySummary;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/")
public class MailServiceWrapperWs {

	@PostMapping("calcPriceAndDeadline")
	MailDeliverySummary checkPriceAndDeliveryTime(@RequestParam int serviceId, @RequestParam String srcPostalCode, @RequestParam String destPostalCode) throws Exception{


		try {
			// get delivery info from mail web service
			MailServiceClient mailWsClient = new MailServiceClient();
			HashMap<String, String> deliveryInfo = mailWsClient.getDeliveryInfo(serviceId, srcPostalCode, destPostalCode);
			 
			// return desired data: code, value and deadline 
			MailDeliverySummary deliverySummary = new MailDeliverySummary(); 
			if (deliveryInfo.containsKey("Codigo"))
				deliverySummary.code = Integer.parseInt(deliveryInfo.get("Codigo"));
			if (deliveryInfo.containsKey("Valor")) 	
				deliverySummary.value = deliveryInfo.get("Valor");
			if (deliveryInfo.containsKey("PrazoEntrega")) 
				deliverySummary.deadline = deliveryInfo.get("DataMaxEntrega");
			return deliverySummary;
		}
		catch (Exception ex) {
			// TODO: handle better each exception
			// unable to access mail service web service or parse its XML
			throw new ResponseStatusException(
			           HttpStatus.INTERNAL_SERVER_ERROR, 
			           String.format("Erro: %s", ex.getMessage()));			
		}
		
	
	}
	
}
