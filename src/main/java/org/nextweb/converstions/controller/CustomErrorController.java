package org.nextweb.converstions.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {


    private final ErrorAttributes errorAttributes;  // Inject ErrorAttributes

    @Autowired
    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        // Get the error attributes from the request
        WebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE));

        // Customize the error response
        Map<String, Object> errorResponse = new HashMap<>();
        HttpStatus status = getStatus(request);
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", getErrorMessage(errorAttributes)); // Extract message

        return new ResponseEntity<>(errorResponse, status);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
      Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

      if (statusCode == null) {
          return HttpStatus.INTERNAL_SERVER_ERROR;
      }
      try {
          return HttpStatus.valueOf(statusCode);
      } catch (IllegalArgumentException e) {
          // Handle the case where the status code is not a valid HttpStatus
          return HttpStatus.INTERNAL_SERVER_ERROR;
      }
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Throwable error = (Throwable) request.getAttribute("javax.servlet.error.exception");
         if (error != null) {
            map.put("message", error.getMessage());
        }
        else{
            map.put("message", "No error message available");
        }
        return map;
    }

    private String getErrorMessage(Map<String, Object> errorAttributes) {
        Object message = errorAttributes.get("message");
        return message != null ? message.toString() : "No error message available";
    }
}
