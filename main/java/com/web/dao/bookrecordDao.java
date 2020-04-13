package com.web.dao;


import java.util.List;


import com.web.entity.bookRecord;

public interface bookrecordDao {
    List<bookRecord> bookRecordSelect(Long bookid);
    //根据bookid查询bookRecord的信息
    boolean UpdataBookRecord(bookRecord bookrecord);
  //更新bookRecord信息
    boolean saveBookRecord(bookRecord bookrecord) ;
    //保存bookRecord信息
}
