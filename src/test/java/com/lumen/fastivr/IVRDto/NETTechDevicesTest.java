package com.lumen.fastivr.IVRDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
class NETTechDevicesTest {

	@InjectMocks NETTechDevices devices;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testNETTechDevices() {
		List<String> list = new ArrayList<>();
		list.add("COURIER");
		ObjectMapper objectMapper = new ObjectMapper();
		 JsonNodeFactory nodeFactory = objectMapper.getNodeFactory();
		 ObjectNode objectNode = nodeFactory.objectNode();
		 objectNode.putObject("MOBILE");
		 objectNode.putObject("EMAIL");
		 ObjectNode root = nodeFactory.objectNode();
		 root.setAll(objectNode);
		 JsonNode jsonNode = root;
		devices.setDevices(jsonNode);
		JsonNode deviceJsonNodes = devices.getDevices();
		devices.toString();
		
		assertNotNull(deviceJsonNodes);
		
	}

}
