package com.shuttle.panic;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.notification.dto.PanicNotificationsDTO;


@RestController
@RequestMapping("/api/panic")
public class PanicController {

	@GetMapping
	public ResponseEntity<PanicNotificationsDTO> getNotifications() {
		return new ResponseEntity<PanicNotificationsDTO>(new PanicNotificationsDTO(), HttpStatus.OK);
	}
}
