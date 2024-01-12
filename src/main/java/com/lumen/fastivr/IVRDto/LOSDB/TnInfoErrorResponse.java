package com.lumen.fastivr.IVRDto.LOSDB;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TnInfoErrorResponse {
	private String correlationId;
	private String message;
}