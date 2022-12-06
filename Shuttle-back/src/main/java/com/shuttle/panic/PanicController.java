package com.shuttle.panic;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.common.CollectionDTO;


@RestController
@RequestMapping("/api/panic")
public class PanicController {

	@GetMapping
	public ResponseEntity<CollectionDTO<PanicDTO>> getNotifications() {
		return new ResponseEntity<CollectionDTO<PanicDTO>>(new CollectionDTO<PanicDTO>(), HttpStatus.OK);
	}
}
