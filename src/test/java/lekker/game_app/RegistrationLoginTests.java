package lekker.game_app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.Data;
import lekker.game_app.entities.Role;
import lekker.game_app.entities.User;
import lekker.game_app.requests.AuthenticationRequest;
import lekker.game_app.responses.AuthenticationResponse;
import lekker.game_app.services.JwtService;

import static org.assertj.core.api.Assertions.assertThat;

import org.json.JSONException;
import org.json.JSONObject;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationLoginTests {
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    PasswordEncoder passwordEncoder;

    // clear the database after running tests
    @Test
    public void testRegisterLoginAndSecureEndpoints(){
        User.UserBuilder users = User.builder();
        String pwd = passwordEncoder.encode("tom123");

        UserDetails tom = users
            .firstname("tom")
            .lastname("riddle")
            .email("tom.riddle@lekker.com")
            .password(pwd)
            .role(Role.USER)
            .build();

        ResponseEntity<String> registerResponse = restTemplate
        .exchange("/auth/register", HttpMethod.POST, new HttpEntity<UserDetails>(tom), String.class);

        // test to see that a user is added successfully to the database
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        JSONObject tokenJson;
        String jwtToken = "";
        try {
            tokenJson = new JSONObject(registerResponse.getBody());
            jwtToken = tokenJson.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+jwtToken);

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        
        ResponseEntity<String> demoResponse = restTemplate
        .exchange("/demo", HttpMethod.GET, entity, String.class);

        // test to see that the registered user can access a secure endpoint from register
        assertThat(demoResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        AuthenticationRequest authReq = new AuthenticationRequest();
        authReq.setEmail("tom.riddle@lekker.com");
        authReq.setPassword(pwd);

        ResponseEntity<String> loginResponse = restTemplate
        .exchange("/auth/login", HttpMethod.POST, new HttpEntity<AuthenticationRequest>(authReq) , String.class);

        // test to see if a user can login 
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        try {
            tokenJson = new JSONObject(loginResponse.getBody());
            jwtToken = tokenJson.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+jwtToken);

        HttpEntity<String> entity2 = new HttpEntity<String>(headers);
        
        ResponseEntity<String> demoResponse2 = restTemplate
        .exchange("/demo", HttpMethod.GET, entity2, String.class);

        // test to see if a user can access a secure endpoint from login
        assertThat(demoResponse2.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    public void testUniqueRegistration() {

        // JSONObject tokenJson;
        // String jwtToken = "";
        // try {
    
    }
}
