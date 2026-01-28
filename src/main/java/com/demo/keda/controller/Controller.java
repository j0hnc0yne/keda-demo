package com.demo.keda.controller;

import com.demo.keda.service.CustomerService;
import com.demo.keda.vo.CustomerAccounts;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

	private final CustomerService customerService;

	public Controller(CustomerService customerService) {
		this.customerService = customerService;
	}

	@GetMapping("/customer/{customerId}/accounts")
	public CustomerAccounts getCustomerAccounts(@PathVariable("customerId") Long customerId) {
		return customerService.getCustomerAccounts(customerId);
	}

}
