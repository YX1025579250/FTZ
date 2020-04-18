package com.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.entity.book;
import com.web.service.bookService;
import com.web.dao.bookDao;
@Service
public class bookServiceImpl implements bookService{
	@Autowired
	private bookDao bookDao;
	@Override
	public List<book> select(Long UserID) {
		// TODO Auto-generated method stub
		return bookDao.select(UserID);
	}
	@Override
	public book selectById(Long BookId)
	{
		return bookDao.selectById(BookId);
	}
	@Override
	public List<book> selectByUserIDAndbookname(Long UserID,String bookname)
	{
		return bookDao.selectByUserIDAndbookname(UserID, bookname);
	}
	@Override
	public boolean UpdataBook(book book)
	{
		return bookDao.UpdataBook(book);
	}
	@Override
	  //更新book信息
	public    boolean saveBook(book book) 
	{
		return bookDao.saveBook(book);
	}
	@Override
	public book selectByurl(String url) {
		// TODO Auto-generated method stub
		return bookDao.selectByurl(url);
	}
	
	    //保存书籍信息

}
