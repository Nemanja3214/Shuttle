package com.shuttle.panic;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.common.ListDTO;


@RestController
@RequestMapping("/api/panic")
public class PanicController {

	@GetMapping
	public ResponseEntity<ListDTO<PanicDTO>> getNotifications() {
		
		ListDTO<PanicDTO> panics = new ListDTO<>();
		panics.setTotalCount(243);
		panics.getResults().add(new PanicDTO());
		
		return new ResponseEntity<>(panics, HttpStatus.OK);
	}
}
