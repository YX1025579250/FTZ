package com.web.service;

import java.util.List;

import com.web.entity.book;

public interface bookService {
	 List<book> select(Long UserID);
	 //根据用户ID查询识别书的信息--存在多条
	 book selectById(Long BookId);
	 //根据书籍ID查询识别书的信息--单条  
	 List<book> selectByUserIDAndbookname(Long UserID,String bookname);
	//根据用户ID和书名查询识别书的信息
	 boolean UpdataBook(book book);
	  //更新book信息
	 boolean saveBook(book book) ;
	  //保存书籍信息
	//通过url查询book   -刘俊
	 book selectByurl(String url);
}
