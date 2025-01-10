package com.example.cleanorarest.service.Imp;

import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceImplTest {

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private SendGrid sendGrid;

    @InjectMocks
    private MailServiceImpl mailService;

    private MockHttpServletRequest httpRequest;

    @BeforeEach
    public void setup() {
        httpRequest = new MockHttpServletRequest();
        httpRequest.setRequestURI("/resetPassword");
    }

    @Test
    void sendToken_ValidInput_SendsEmail() throws IOException {
        String token = "testToken";
        String to = "test@example.com";
        String expectedTemplateOutput = "<html><body><a href='http://localhost:8080/changePassword?token=testToken'>here</a></body></html>";

        when(templateEngine.process(eq("email/emailTemplate"), any(Context.class))).thenReturn(expectedTemplateOutput);

        Response sendGridResponse = new Response();
        sendGridResponse.setStatusCode(202);
        when(sendGrid.api(any(Request.class))).thenReturn(sendGridResponse);

        mailService.sendToken(token, to, httpRequest);

        verify(sendGrid, times(1)).api(any(Request.class));
        verify(templateEngine, times(1)).process(eq("email/emailTemplate"), any(Context.class));
    }

    @Test
    void sendToken_IOException_HandlesException() throws IOException {
        String token = "testToken";
        String to = "test@example.com";

        when(templateEngine.process(eq("email/emailTemplate"), any(Context.class)))
                .thenReturn("<html><body>Sample Template</body></html>");

        doThrow(new IOException("Test IOException")).when(sendGrid).api(any(Request.class));

        mailService.sendToken(token, to, httpRequest);

        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
    }
}
