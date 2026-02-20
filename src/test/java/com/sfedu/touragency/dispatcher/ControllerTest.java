package com.sfedu.touragency.dispatcher;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;

import javax.servlet.http.*;

import static org.mockito.BDDMockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private RequestService reqService;

    @Spy
    private Controller controller;

    @Before
    public void setUp() throws Exception {
        reqService = new RequestService(request, response, null);
    }

    @Test
    public void shouldCallGetAndAnyWhenMethodGet() {
        given(request.getMethod()).willReturn("GET");

        controller.execute(reqService);
        verify(controller, times(1)).get(any());
        verify(controller, times(1)).any(any());
        verify(controller, never()).post(any());
        verify(controller, never()).put(any());
        verify(controller, never()).delete(any());
    }
    }
