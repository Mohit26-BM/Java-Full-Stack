package com.examples.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.examples.security.dao.TokenDao;

@Component
public class TokenServiceImpl implements TokenService {
	
	@Autowired
	TokenDao tokenDao;

	@Override
	public String firstService() {
		// TODO Auto-generated method stub
		return tokenDao.daoService1();
	}

	@Override
	public String secondService() {
		// TODO Auto-generated method stub
		return tokenDao.daoService2();
	}

}
