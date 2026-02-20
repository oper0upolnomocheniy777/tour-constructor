package com.sfedu.touragency.controller;

import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.dispatcher.RequestService;

public final class RedirectController extends Controller {
    private final String path;

    public RedirectController(String path) {
        this.path = path;
    }

    @Override
    public void any(RequestService reqService) {
        reqService.renderPage(path);
    }
}
