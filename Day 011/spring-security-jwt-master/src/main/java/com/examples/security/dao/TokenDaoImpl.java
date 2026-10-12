package com.examples.security.dao;

import org.springframework.stereotype.Component;

@Component
public class TokenDaoImpl implements TokenDao {

	@Override
	public String daoService1() {
		// TODO Auto-generated method stub
		return "message from Service1of Dao";
	}

	@Override
	public String daoService2() {
		// TODO Auto-generated method stub
		return "message from Service2 of Dao";
	}

}
