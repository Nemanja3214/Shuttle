package com.shuttle.message.dto;

import com.shuttle.message.Message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageDTO {
	private String message;
	private Message.Type type;
	private long rideId;
}
