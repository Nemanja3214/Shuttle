package com.shuttle.controllers;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.notification.dto.NotificationDTO;


@RestController
@RequestMapping("/panic")
public class PanicController {

	@GetMapping
	public Collection<NotificationDTO> getNotifications() {
		return new ArrayList<NotificationDTO>();
	}
}
