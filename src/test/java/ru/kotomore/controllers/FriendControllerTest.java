package ru.kotomore.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.kotomore.SocialMediaApplication;
import ru.kotomore.dto.UserDTO;
import ru.kotomore.dto.security.JwtRequest;
import ru.kotomore.models.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SocialMediaApplication.class})
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class FriendControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;


    @Before
    public void registerUser() throws Exception {
        UserDTO userDTO = modelMapper.map(createTestUser(), UserDTO.class);
        mvc.perform(post("/auth/registration").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void getFriends_ShouldReturnNoContent() throws Exception {
        mvc.perform(get("/friends").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getAccessToken())
                        .param("status", "ACCEPTED")
                )
                .andExpect(status().is(204));
    }

    private String getAccessToken() throws Exception {
        User user = createTestUser();

        JwtRequest request = new JwtRequest();
        request.setEmail(user.getEmail());
        request.setPassword(user.getPassword());

        String response = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andReturn().getResponse().getContentAsString();
        return new JSONObject(response).get("accessToken").toString();
    }

    private User createTestUser() {
        return new User(1L, "user", "sw@m.ru", "123");
    }
}
