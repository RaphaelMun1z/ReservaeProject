package user_profile_service.auth;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import user_profile_service.dtos.req.UserRequestDTO;

@RestController
@RequestMapping("/token")
public class TokenController {
    private final RestTemplate restTemplate;

    public TokenController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/")
    public ResponseEntity<String> token(@RequestBody UserRequestDTO user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", user.clientId());
        formData.add("username", user.username());
        formData.add("password", user.password());
        formData.add("grant_type", user.grantType());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

        var result = restTemplate.postForEntity("http://localhost:8080/realms/matchpass/protocol/openid-connect/token", entity, String.class);
        return result;
    }


}
