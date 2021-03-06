package com.gokhanozg.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import dhexample.security.dh.DHImplementors;
import dhexample.security.dh.DHSecurity;
import dhexample.security.dh.DHUtils;

@Controller
public class IpResolverController {

	private static final String SP_URI = "http://sert-yapi.com/serviceProvider/"; // PROD don't change, remember to switch.
	// private static final String SP_URI = "http://192.168.0.10/serviceProvider/"; //TEST depends on DEV env.
	private SPClientManager spClient = new SPClientManager(SP_URI, 1000l * 60l * 5l, 1000l * 10l);
	private final Map<String, DHSecurity> uidToDHImplMap = new HashMap<>();

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public Object test(HttpServletRequest request, HttpServletResponse response) {
		return "Hello.";
	}

	@RequestMapping(value = "/getToken", method = RequestMethod.GET)
	@ResponseBody
	public String getToken(HttpServletRequest request, HttpServletResponse response, @RequestParam String pKey, @RequestParam String uid) {
		try {
			pKey = pKey.replaceAll(" ", "+");
			PublicKey publicKey2 = DHUtils.convertStringToPublicKey(pKey);
			DHSecurity implementor1 = DHImplementors.constructDHSecurity();
			PublicKey publicKey1 = implementor1.generatePublicKey();
			implementor1.handshake(publicKey2);
			uidToDHImplMap.put(uid, implementor1);
			return DHUtils.convertPublicKeyToString(publicKey1);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/mirror", method = RequestMethod.GET)
	@ResponseBody
	public String mirror(HttpServletRequest request, HttpServletResponse response, @RequestParam String mirror, @RequestParam String uid) {
		DHSecurity dhs = uidToDHImplMap.get(uid);
		if (dhs == null)
			return "Get Token.";
		try {
			return dhs.decrypt(mirror);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Failed to decrypt";
	}

	@RequestMapping(value = "/getLocalIp", method = RequestMethod.GET)
	@ResponseBody
	public Object getLocalIp(HttpServletRequest request, HttpServletResponse response) {
		return spClient.getLocalIp();
	}

	@RequestMapping(value = "/getExternalIp", method = RequestMethod.GET)
	@ResponseBody
	public Object getExternalIp(HttpServletRequest request, HttpServletResponse response) {
		return spClient.getExternalIp();
	}

	@RequestMapping(value = "/getServerIp", method = RequestMethod.GET)
	@ResponseBody
	public Object getServerIp(HttpServletRequest request, HttpServletResponse response) {
		return spClient.getServerIp();
	}

}
