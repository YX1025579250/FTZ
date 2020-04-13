package com.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.web.entity.bookRecord;
import com.web.service.bookRecordService;

import com.web.dao.bookrecordDao;
@Service
public class bookRecordServiceImpl implements bookRecordService{
	@Autowired
	private bookrecordDao bookrecordDao;
	@Override
	public List<bookRecord>  bookRecordSelect(Long bookid) {
		// TODO Auto-generated method stub
		return bookrecordDao.bookRecordSelect(bookid);
	}
	public boolean UpdataBookRecord(bookRecord bookrecord)
	{
		return bookrecordDao.UpdataBookRecord(bookrecord);
	}
	  //更新book信息
	public    boolean saveBookRecord(bookRecord bookrecord) 
	{
		return bookrecordDao.saveBookRecord(bookrecord);
	}
	
	 
}
