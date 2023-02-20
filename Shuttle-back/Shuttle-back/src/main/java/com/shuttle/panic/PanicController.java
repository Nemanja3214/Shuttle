package com.shuttle.panic;

import java.util.List;

import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shuttle.common.ListDTO;
import com.shuttle.ride.RideController;
import com.shuttle.ride.dto.RideDTO;
import com.shuttle.user.dto.UserDTO;
import com.shuttle.user.dto.UserDTONoPassword;


@RestController
@RequestMapping("/api/panic")
public class PanicController {
	@Autowired
	private IPanicService panicService;
	
	private PanicDTO from(Panic p) {
		//System.out.println("[4][][]" + p.getUser());
	    PanicDTO dto = new PanicDTO();
	    dto.setId(p.getId());
	    dto.setUser(new UserDTONoPassword(p.getUser()));
	  	dto.setRide(RideController.to(p.getRide())); // TODO: convert
	    dto.setTime(p.getTime().toString());
	    dto.setReason(p.getReason());
	    
		return dto;
	}
	
	@PreAuthorize("hasAnyAuthority('admin')")
	@GetMapping
	public ResponseEntity<ListDTO<PanicDTO>> getNotifications() {
		List<Panic> panics = panicService.getAll();
		List<PanicDTO> panicsDTO = panics.stream().map(p -> from(p)).toList();
		return new ResponseEntity<>(new ListDTO<>(panicsDTO), HttpStatus.OK);
	}
	@PreAuthorize("hasAnyAuthority('admin')")
	@GetMapping("/all")
	public ResponseEntity<List<PanicDTO>> getAllNotifications(@PathParam("page") int page) {
		Pageable pageable = PageRequest.of(page,10, Sort.by(Sort.Order.desc("time")));
		List<Panic> panics = panicService.findAll(pageable);
		List<PanicDTO> panicDTOs = panics.stream().map(p -> from(p)).toList();
		return new ResponseEntity<>(panicDTOs, HttpStatus.OK);
	}
}
