package com.gokhanozg.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IpResolverController {

	@RequestMapping(value = "/test", method = RequestMethod.GET, produces = "charset=utf-8")
	@ResponseBody
	public Object savePcaPersonList(HttpServletRequest request, HttpServletResponse response) {
		return "Hello world";
	}

}
