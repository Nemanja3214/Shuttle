package com.shuttle.message.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import com.shuttle.message.Message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
	private Long id;
	private String timeOfSending;
	private Long senderId;
	private Long receiverId;
	private String message;
	private Message.Type type;
    private Long rideId;

    public MessageDTO(Message m) {
        this.id = m.getId();
        this.timeOfSending = m.getTime().toString();
        this.senderId = m.getSender().getId();
        this.receiverId = m.getReceiver().getId();
        this.message = m.getMessage();
        this.type = m.getType();
        this.rideId = m.getRide().getId();
    }
}
