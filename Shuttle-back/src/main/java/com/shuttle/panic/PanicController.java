package com.shuttle.panic;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.notification.dto.NotificationDTO;


@RestController
@RequestMapping("/api/panic")
public class PanicController {

	@GetMapping
	public ResponseEntity<Collection<NotificationDTO>> getNotifications() {
		return new ResponseEntity<Collection<NotificationDTO>>( new ArrayList<NotificationDTO>(), HttpStatus.OK);
	}
}
