package co.trucom.eurekaclient.controllers;

import javax.ws.rs.core.MediaType;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
public class SimpleControllerTest {

	MockMvc mockMvc;

	@InjectMocks
	SimpleController simpleController;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(simpleController)
				.build();
	}

	@Test
	void pathShouldReturnDesiredString() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/hello"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("Hi"));
	}

	@Test
	void pathShouldReturnCorrectJson() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/json")
			.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(333)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.is("Hello, the World!")));
	}

	@Test
	void pathShouldReturnCorrectListSize() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/jsonlist")
			.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)));
	}

}
