package com.lumen.fastivr.IVRUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lumen.fastivr.IVRDto.CurrentAssignmentResponseDto;
import com.lumen.fastivr.IVRDto.LOOP;
import com.lumen.fastivr.IVRDto.ReturnDataSet;
import com.lumen.fastivr.IVRDto.SEG;


@ExtendWith(MockitoExtension.class)
class CurrentAssignmentUtilsTest {

	@Test
	void testIsSEGNotEmpty() {
		CurrentAssignmentResponseDto dto = new CurrentAssignmentResponseDto();
		ReturnDataSet dataSet = new ReturnDataSet();
		LOOP loop = new LOOP();
		List<SEG> segList = new ArrayList<>();
		SEG seg = new SEG();
		seg.setCA("Cable Name");
		seg.setPR("Cable Pair");
		segList.add(seg);
		loop.setSEG(segList);
		List<LOOP> loopList = new ArrayList<>();
		loopList.add(loop);
		dataSet.setLoop(loopList);
		dto.setReturnDataSet(dataSet);
		assertTrue(CurrentAssignmentUtils.isSEGNotEmpty(dto));
	}

	@Test
	void testIsLOOPNotEmptyOrNull() {
		CurrentAssignmentResponseDto dto = new CurrentAssignmentResponseDto();
		ReturnDataSet dataSet = new ReturnDataSet();
		LOOP loop = new LOOP();
		List<SEG> segList = new ArrayList<>();
		SEG seg = new SEG();
		seg.setCA("Cable Name");
		seg.setPR("Cable Pair");
		segList.add(seg);
		loop.setSEG(segList);
		List<LOOP> loopList = new ArrayList<>();
		loopList.add(loop);
		dataSet.setLoop(loopList);
		dto.setReturnDataSet(dataSet);
		assertTrue(CurrentAssignmentUtils.isLoopNotEmptyOrNull(dto));
	}

	@Test
	void testGetCABySegmentNumber() {
		CurrentAssignmentResponseDto dto = new CurrentAssignmentResponseDto();
		ReturnDataSet dataSet = new ReturnDataSet();
		LOOP loop = new LOOP();
		List<SEG> segList = new ArrayList<>();
		SEG seg = new SEG();
		seg.setCA("Cable Name");
		seg.setPR("Cable Pair");
		segList.add(seg);
		loop.setSEG(segList);
		List<LOOP> loopList = new ArrayList<>();
		loopList.add(loop);
		dataSet.setLoop(loopList);
		dto.setReturnDataSet(dataSet);
		assertEquals("Cable Name", CurrentAssignmentUtils.getCABySegmentNumber(dto, 1));
	}

	@Test
	void testGetCablePairBySegmentNumber() {
		CurrentAssignmentResponseDto dto = new CurrentAssignmentResponseDto();
		ReturnDataSet dataSet = new ReturnDataSet();
		LOOP loop = new LOOP();
		List<SEG> segList = new ArrayList<>();
		SEG seg = new SEG();
		seg.setCA("Cable Name");
		seg.setPR("Cable Pair");
		segList.add(seg);
		loop.setSEG(segList);
		List<LOOP> loopList = new ArrayList<>();
		loopList.add(loop);
		dataSet.setLoop(loopList);
		dto.setReturnDataSet(dataSet);
		assertEquals("Cable Pair", CurrentAssignmentUtils.getCablePairBySegmentNumber(dto, 1));
	}

}