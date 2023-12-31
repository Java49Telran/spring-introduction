package telran.spring;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import telran.exceptions.NotFoundException;
import telran.spring.controller.GreetingsController;
import telran.spring.service.GreetingsService;
record PersonIdString(String id, String name, String city, String mail, String phone) {
	
}
@WebMvcTest
public class GreetingsControllerTest {
    private static final String PERSON_NOT_FOUND = "person not found";
	private static final String GET_GREETINGS_RETURN = "kuku";
	@Autowired //this annotation allows dependency injection inside following field 
	GreetingsController controller;
    @MockBean
    GreetingsService greetingsService;
    @Autowired
    MockMvc mockMvc; //imitator of Web Server
    Person personNormal = new Person(123, "Vasya", "Rehovot", "vasya@gmail.com",
    		"054-1234567");
    Person personNormalUpdated = new Person(123, "Vasya", "Lod", "vasya@gmail.com",
    		"054-1234567");
    Person personWrongPhone = new Person(124, "Vasya", "Rehovot", "vasya@gmail.com",
    		"54-1234567");
    Person personWrongCity = new Person(124, "Vasya", null, "vasya@gmail.com",
    		"+972-54-1234567");
    Person personWrongName = new Person(123, "as", "Rehovot", "vasya@gmail.com",
    		"054-1234567");
    PersonIdString personWrongId = new PersonIdString("abc", "Vasya", "Rehovot", "vasya@gmail.com",
    		"054-1234567");
    Person personWrongMail = new Person(123, "Vasya", "Rehovot", "vasya",
    		"054-1234567");
    
    @Autowired
    ObjectMapper objectMapper;
     @Test
     void applicationContextTest() {
    	 assertNotNull(controller);
    	 assertNotNull(greetingsService);
    	 assertNotNull(mockMvc);
    	 assertNotNull(objectMapper);
    	 
     }
     @Test
     void normalFlowAddPerson() throws Exception{
    	 when(greetingsService.addPerson(personNormal)).thenReturn(personNormal);
    	 String personJson = objectMapper.writeValueAsString(personNormal);
    	 String response = mockMvc.perform(post("http://localhost:8080/greetings")
    			 .contentType(MediaType.APPLICATION_JSON)
    			 .content(personJson))
    	 .andDo(print()).andExpect(status().isOk())
    	 .andReturn().getResponse().getContentAsString();
    	 assertEquals(personJson, response);
     }
     @Test
     void alreadyExistsAddPerson() throws Exception{
    	 String exceptionMessage = "already exists";
    	 when(greetingsService.addPerson(personNormal))
    	 .thenThrow(new IllegalStateException(exceptionMessage));
    	 String personJson = objectMapper.writeValueAsString(personNormal);
    	 String response = mockMvc.perform(post("http://localhost:8080/greetings")
    			 .contentType(MediaType.APPLICATION_JSON)
    			 .content(personJson))
    	 .andDo(print()).andExpect(status().isBadRequest())
    	 .andReturn().getResponse().getContentAsString();
    	 assertEquals(exceptionMessage, response);
     }
     @Test
     void addPersonWrongPhone() throws Exception{
    	 String response = mockMvc.perform(post("http://localhost:8080/greetings")
    			 .contentType(MediaType.APPLICATION_JSON)
    			 .content(objectMapper.writeValueAsString(personWrongPhone)))
    	 .andDo(print()).andExpect(status().isBadRequest())
    	 .andReturn().getResponse().getContentAsString();
    	 assertEquals("not Israel mobile phone",response);
     }
     @Test
     void addPersonWrongMail() throws Exception{
    	 
    	String response = mockMvc.perform(post("http://localhost:8080/greetings")
    			 .contentType(MediaType.APPLICATION_JSON)
    			 .content(objectMapper.writeValueAsString(personWrongMail)))
    	 .andDo(print()).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
    	 assertEquals("must be a well-formed email address",response);
     }
     @Test
     void addPersonWrongCity() throws Exception{
    	 String response = mockMvc.perform(post("http://localhost:8080/greetings")
    			 .contentType(MediaType.APPLICATION_JSON)
    			 .content(objectMapper.writeValueAsString(personWrongCity)))
    	 .andDo(print()).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
    	 assertEquals("must not be empty",response);
     }
     @Test
     void addPersonWrongName() throws Exception{
    	 String response = mockMvc.perform(post("http://localhost:8080/greetings")
    			 .contentType(MediaType.APPLICATION_JSON)
    			 .content(objectMapper.writeValueAsString(personWrongName)))
    	 .andDo(print()).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
    	 assertEquals("Wrong name structure",response);
     }
     @Test
     void addPersonWrongId() throws Exception{
    	 String response = mockMvc.perform(post("http://localhost:8080/greetings")
    			 .contentType(MediaType.APPLICATION_JSON)
    			 .content(objectMapper.writeValueAsString(personWrongId)))
    	 .andDo(print()).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
	 assertEquals("JSON parse error: Cannot deserialize value of type `long` from String \"abc\": not a valid `long` value",response);
     }
     @Test
     void updatePersonTest() throws Exception {
    	 when(greetingsService.updatePerson(personNormalUpdated))
    	 .thenReturn(personNormalUpdated);
    	 String personUpdatedJSON = objectMapper.writeValueAsString(personNormalUpdated);
    	 String response = mockMvc.perform(put("http://localhost:8080/greetings")
    			 .contentType(MediaType.APPLICATION_JSON)
    			 .content(personUpdatedJSON))
    	 .andDo(print()).andExpect(status().isOk())
    	 .andReturn().getResponse().getContentAsString();
    	 assertEquals(personUpdatedJSON, response);
     }
     @Test
     void updatePersonNotFoundTest() throws Exception {
    	 when(greetingsService.updatePerson(personNormalUpdated))
    	 .thenThrow(new NotFoundException(PERSON_NOT_FOUND));
    	 String personUpdatedJSON = objectMapper.writeValueAsString(personNormalUpdated);
    	 String response = mockMvc.perform(put("http://localhost:8080/greetings")
    			 .contentType(MediaType.APPLICATION_JSON)
    			 .content(personUpdatedJSON))
    	 .andDo(print()).andExpect(status().isNotFound())
    	 .andReturn().getResponse().getContentAsString();
    	 assertEquals(PERSON_NOT_FOUND, response);
     }
     @Test
     void getPersonTest() throws Exception {
    	 long id = 123;
    	 long idNotExist = 124;
    	 when(greetingsService.getPerson(id)).thenReturn(personNormal);
    	 when(greetingsService.getPerson(idNotExist)).thenReturn(null);
    	 String personJson = objectMapper.writeValueAsString(personNormal);
    	 String response = getPersonResponse(id);
    	 assertEquals(personJson, response);
    	 response = getPersonResponse(idNotExist);
    	 assertEquals("", response);
    	 
     }
	private String getPersonResponse(long id) throws UnsupportedEncodingException, Exception {
		String response = mockMvc.perform(get("http://localhost:8080/greetings/id/" + id))
    	 .andDo(print()).andExpect(status().isOk())
    	 .andReturn().getResponse().getContentAsString();
		return response;
	}
     @Test
     void getGreetingsTest() throws Exception {
    	 long id = 123;
    	 when(greetingsService.getGreetings(id)).thenReturn(GET_GREETINGS_RETURN);
    	String response = mockMvc.perform(get("http://localhost:8080/greetings/" + id))
    	 .andDo(print()).andExpect(status().isOk())
    	 .andReturn().getResponse().getContentAsString();
    	assertEquals(GET_GREETINGS_RETURN, response);
     }
     @Test
     void getPersonsByCityTest() throws Exception {
    	 String city = "Rehovot";
    	 when(greetingsService.getPersonsByCity(city))
    	 .thenReturn(List.of(personNormal, personNormalUpdated));
    	Person[] expected = {
    			personNormal,
    			personNormalUpdated
    	};
		String response = mockMvc.perform(get("http://localhost:8080/greetings/city/" + city))
    	 .andDo(print()).andExpect(status().isOk())
    	 .andReturn().getResponse().getContentAsString();
		Person[]actual = objectMapper.readValue(response, Person[].class);
		assertArrayEquals(expected, actual);
     }
     @Test
     void deletePersonTest() throws Exception {
    	 long id = 123;
    	 when(greetingsService.deletePerson(id)).thenReturn(personNormal);
    	 String personJson = objectMapper.writeValueAsString(personNormal);
    	 String response = mockMvc.perform(delete("http://localhost:8080/greetings/" + id))
    	 .andDo(print()).andExpect(status().isOk())
    	 .andReturn().getResponse().getContentAsString();
    	 assertEquals(personJson, response);
     }
     @Test
     void deletePersonNotFoundTest() throws Exception {
    	 long id = 124;
    	 when(greetingsService.deletePerson(id))
    	 .thenThrow(new NotFoundException(PERSON_NOT_FOUND));
    	 String response = mockMvc.perform(delete("http://localhost:8080/greetings/" + id))
    	 .andDo(print()).andExpect(status().isNotFound())
    	 .andReturn().getResponse().getContentAsString();
    	 assertEquals(PERSON_NOT_FOUND, response);
     }
     
}
