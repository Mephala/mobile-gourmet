package com.gokhanozg.controller;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import service.provider.client.executor.ServiceClient;
import service.provider.common.dto.RemembererDto;
import service.provider.common.request.GetAllRememberersRequestDto;
import service.provider.common.request.RequestDtoFactory;
import service.provider.common.response.GetAllRememberersResponseDto;

public class SPClientManager {

	private final String serverAddr;
	private final Long scheduleRate;
	private final Long startDelayRate;
	private List<RemembererDto> rememberers;
	private final Lock readLock;
	private final Lock writeLock;
	/**
	 * 
	 * List of hardcoded property keys.
	 */
	private static final String LOCAL_SP_IP_KEY = "sp.local.uri";
	private static final String SERVER_SP_IP_KEY = "sp.server.uri";
	private static final String SP_EXTERNAL_IP = "sp.external.ip";
	private static final String DEFAULT_LOCAL_IP = "http://192.168.0.10/serviceProvider/";
	private static final String DEFAULT_EXTERNAL_IP = "46.196.100.145";
	private static final String DEFAULT_SERVER_URL = "http://sert-yapi.com/serviceProvider/";

	private String localIp;
	private String serverIp;
	private String externalIp;

	public SPClientManager(String serverAddr, Long scheduleRate, Long startDelayRate) {
		super();
		this.serverAddr = serverAddr;
		this.scheduleRate = scheduleRate;
		this.startDelayRate = startDelayRate;
		ReadWriteLock rwlock = new ReentrantReadWriteLock();
		this.readLock = rwlock.readLock();
		this.writeLock = rwlock.writeLock();
		ServiceClient.initialize(serverAddr);
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				assignIPValues();
			}
		}, startDelayRate, scheduleRate, TimeUnit.MILLISECONDS);
	}

	private void assignIPValues() {
		try {
			writeLock.lock();
			GetAllRememberersRequestDto getAllRememberers = RequestDtoFactory.createGetAllRemembererRequestDto();
			GetAllRememberersResponseDto resp = ServiceClient.getAllRemembererList(getAllRememberers);
			rememberers = resp.getAllRememberers();
			for (RemembererDto remembererDto : rememberers) {
				if (LOCAL_SP_IP_KEY.equals(remembererDto.getKey())) {
					localIp = remembererDto.getValue();
				} else if (SP_EXTERNAL_IP.equals(remembererDto.getKey())) {
					externalIp = remembererDto.getValue();
				} else if (SERVER_SP_IP_KEY.equals(remembererDto.getKey())) {
					serverIp = remembererDto.getValue();
				}
			}
		} catch (Exception e) {
			// TODO Submit log.
		} finally {
			writeLock.unlock();
		}
	}

	public String getServerIp() {
		try {
			readLock.lock();
			return serverIp;
		} catch (Exception e) {

		} finally {
			readLock.unlock();
		}
		return DEFAULT_SERVER_URL;
	}

	public String getLocalIp() {
		try {
			readLock.lock();
			return localIp;
		} catch (Exception e) {

		} finally {
			readLock.unlock();
		}
		return DEFAULT_LOCAL_IP;
	}

	public String getExternalIp() {
		try {
			readLock.lock();
			return externalIp;
		} catch (Exception e) {

		} finally {
			readLock.unlock();
		}
		return DEFAULT_EXTERNAL_IP;
	}

}
