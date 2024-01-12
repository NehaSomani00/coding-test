package com.lumen.fastivr.IVRDto.LOSDB;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TNInfoRequestDto {
	private TNInfoDto tnInfo;
	private String tn;
}