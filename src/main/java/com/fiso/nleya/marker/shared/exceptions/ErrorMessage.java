package com.fiso.nleya.marker.shared.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage implements Serializable {

    private static final long serialVersionUID = 83478578349234785L;

	private ZonedDateTime eventTime;

	private String errorDescription;

	private HttpStatus errorCode;
}
