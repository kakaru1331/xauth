package me.kakaru.xauth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@RestController
public class WebController {

    String AUTH_HEADER = "x-ext-authz";
    String ALLOWED_VALUE = "allow";
    String DENY_BODY = String.format("denied by ext_authz for not found header %s: %s", AUTH_HEADER, ALLOWED_VALUE);
    String OAUTH_URI = "https://accounts.google.com/signin";

    @RequestMapping(value = "**", method = { RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity index(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER);

        if (StringUtils.isEmpty(authHeader)) {
            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .location(URI.create(OAUTH_URI))
                    .build();
        } else if (checkAuthHeader(authHeader)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(DENY_BODY);
        }
    }

    boolean checkAuthHeader(String authHeader) {
        if (StringUtils.isEmpty(authHeader)) {
            return false;
        } else if (!ALLOWED_VALUE.equals(authHeader)) {
            return false;
        }

        return true;
    }
}
