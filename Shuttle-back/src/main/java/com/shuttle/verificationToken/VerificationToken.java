package com.shuttle.verificationToken;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.shuttle.passenger.Passenger;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class VerificationToken {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private LocalDateTime expireDateTime;

    @OneToOne(targetEntity = Passenger.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "passenger_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Passenger passenger;

	public boolean isExpired() {
		return this.expireDateTime.isBefore(LocalDateTime.now());
	}
}
