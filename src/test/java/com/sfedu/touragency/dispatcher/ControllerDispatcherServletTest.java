package com.sfedu.touragency.dispatcher;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.spy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ControllerDispatcherServletTest {
    @Spy
    private Controller controller;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private ControllerDispatcherServlet dispatcher;


    @Before
    public void setUp() throws Exception {
        dispatcher = new ControllerDispatcherServlet();
        given(request.getSession()).willReturn(session);
        given(request.getRequestDispatcher(any())).willReturn(mock(RequestDispatcher.class));
    }

    @Test
    public void testSingleMethodMappingHit() throws ServletException, IOException {
        given(request.getPathInfo()).willReturn("/path1/path2");
        given(request.getMethod()).willReturn("GET");

        dispatcher.addMapping("/path1/path2", controller);
        dispatcher.service(request, response);

        verify(controller, times(1)).get(any());
        verify(controller, never()).post(any());
    }

    @Test
    public void testMultipleControllerMappedToSamePath() throws ServletException, IOException {
        given(request.getPathInfo()).willReturn("/path1/path2");
        given(request.getMethod()).willReturn("GET");

        Controller serviceController = spy(Controller.class);

        dispatcher.addMapping("/path1/path2", controller);
        dispatcher.addMapping("/path1/path2", serviceController);

        dispatcher.service(request, response);

        verify(controller, times(1)).get(any());
        verify(serviceController, never()).get(any());
    }

    @Test
    public void testServiceControllerMapping() throws ServletException, IOException {
        given(request.getPathInfo()).willReturn("/path1/path2");
        given(request.getMethod()).willReturn("GET");

        Controller serviceController = spy(new Controller() {
            @Override
            public boolean isService() {
                return true;
            }
        });

        dispatcher.addMapping("/path1/path2", controller);
        dispatcher.addMapping("/path1/path2", serviceController);

        dispatcher.service(request, response);

        verify(controller, times(1)).get(any());
        verify(serviceController, times(1)).get(any());
    }

    @Test
    public void testSingleMethodMappingMiss() throws ServletException, IOException {
        given(request.getPathInfo()).willReturn("/path1/path");
        given(request.getMethod()).willReturn("GET");

        dispatcher.addMapping("/path1/path2", controller);
        dispatcher.service(request, response);

        verify(controller, never()).get(any());
        verify(controller, never()).any(any());
    }

    @Test
    public void testSeveralMethodMappingHit() throws ServletException, IOException {
        given(request.getPathInfo()).willReturn("/path1/path2", "/path1/path2");
        given(request.getMethod()).willReturn("GET");

        dispatcher.addMapping("/path1/path2", controller);

        dispatcher.service(request, response);
        verify(controller, times(1)).get(any());
        verify(controller, never()).post(any());

        given(request.getMethod()).willReturn("POST");

        dispatcher.service(request, response);
        verify(controller, times(1)).get(any());
        verify(controller, times(1)).post(any());
    }


    // TODO: redirect, flash params, render
    @Test
    public void testRenderPage() throws ServletException, IOException {
        given(request.getPathInfo()).willReturn("/path1/path2", "/path1/path2");
        given(request.getMethod()).willReturn("GET");

        dispatcher.addMapping("/path1/path2", new Controller() {
            @Override
            public void get(RequestService reqService) {
                reqService.renderPage("/index.html");
            }
        });

        dispatcher.service(request, response);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(request, times(1)).getRequestDispatcher(pathCaptor.capture());
        assertEquals("/index.html", pathCaptor.getValue());
    }

    @Test
    public void testRedirect() throws ServletException, IOException {
        given(request.getPathInfo()).willReturn("/path1/path2", "/path1/path2");
        given(request.getMethod()).willReturn("GET");

        dispatcher.addMapping("/path1/path2", new Controller() {
            @Override
            public void get(RequestService reqService) {
                reqService.redirect("/index.html");
            }
        });

        dispatcher.service(request, response);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(response, times(1)).sendRedirect(pathCaptor.capture());
        assertEquals("/index.html", pathCaptor.getValue());
    }


}
