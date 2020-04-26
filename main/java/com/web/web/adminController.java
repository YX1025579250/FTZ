package com.web.web;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.web.entity.PhotoRecord;
import com.web.entity.PhotoWord;
import com.web.entity.Users;
import com.web.entity.WordSourceInfo;
import com.web.entity.book;
import com.web.entity.bookRecord;
import com.web.entity.bookword;
import com.web.service.UserService;
import com.web.service.bookService;
import com.web.service.bookRecordService;
import com.web.service.bookwordService;
import com.web.service.photorecordService;
import com.web.service.photowordService;
import com.web.service.wordService;
import com.alibaba.fastjson.JSONObject;

@Controller
public class adminController {
	@Autowired
	private photorecordService photorecordService;
	@Autowired
	private photowordService photoWordService;
	@Autowired
	private wordService wordService;
	@Autowired
	private UserService userService;
	@Autowired
	private bookService bookService;
	@Autowired
	private bookRecordService bookRecordService;
	@Autowired
	private bookwordService bookWordService;
	@RequestMapping("/adminlogin")
	public String adminlogin() {
		return "admin/adminlogin";
	}

	@RequestMapping("/loginIndex")
	public String loginIndex() {
		return "admin/loginIndex";
	}

	@RequestMapping("/adminleft")
	public String adminleft() {
		return "admin/left";
	}

	@RequestMapping("/admintop")
	public String admintop() {
		return "admin/adminhead";
	}

	@RequestMapping("/adminbottom")
	public String adminbottom() {
		return "admin/bottom";
	}

	@RequestMapping("/adminswitch")
	public String adminswitch() {
		return "admin/switch";
	}

	@RequestMapping("/adminmain")
	public String adminmain() {
		return "admin/main";
	}

	@RequestMapping("/table")
	public String table(HttpServletRequest request) {
		return "admin/table";
	}

	@RequestMapping(value = "/searchByUserId")
	@ResponseBody
	public String search(@Param("userId") String userId) {
		List<PhotoRecord> pr = null;
		Map<Object, Object> map = new HashMap<>();
		if(userId==null||userId==""){
			pr = photorecordService.photoList();
		} else {
			pr = photorecordService.photoRecords(Long.valueOf(userId));			
		}
		map.put("code", 0);
		map.put("msg", "");
		map.put("count", pr.size());
		map.put("data", pr);
		return JSONObject.toJSONString(map);
	}
	@RequestMapping("/userList")
	public String userList() {
		return "admin/userList";
	}
	
	@RequestMapping(value = "/getUsers")
	@ResponseBody
	public String searchUsers(@Param("phoneNumber") String phoneNumber) {
		List<Users> u = null;
		Map<Object, Object> map = new HashMap<>();
		if(phoneNumber==null||phoneNumber==""){
			u = userService.getAllUsers();
		} else {
			u = new ArrayList<>();
			u.add(userService.getUser(phoneNumber));			
		}
		map.put("code", 0);
		map.put("msg", "");
		map.put("count", u.size());
		map.put("data", u);
		return JSONObject.toJSONString(map);
	}
	
	@RequestMapping("/searchCharAdmin")
	public String selectChar(){
		return "admin/searchCharAdmin";
	}
	//通过文字查询所有识别记录
	@RequestMapping(value = "/searchByWord")
	@ResponseBody
	public String searchByWord(@Param("word") String word) {
		PhotoRecord temPr = null;
		List<PhotoWord> pw = null;
		PhotoWord temPw = null;
		List<Map<Object,Object>> dataList = new ArrayList<>();
		Map<Object, Object> map = new HashMap<>();
		int wordId;
		if(word == null||word==""){

		} else {
			try {
				wordId = wordService.getWord(new String(word.getBytes("ISO-8859-1"), "UTF-8")).getWordId();
				pw = photoWordService.getPhotoWordByWordId(wordId);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Iterator<PhotoWord> iter = pw.iterator();
			while(iter.hasNext()){
				temPw = iter.next();
				temPr = photorecordService.photoRecord(temPw.getPhotoRecord());
				Map<Object, Object> dataMap = new HashMap<>();
				dataMap.put("userId",temPr.getUserId());
				dataMap.put("photoWordRecord",temPw.getPhotoWordRecord());
				dataMap.put("photoRecord",temPw.getPhotoRecord());
				dataMap.put("dateTime",temPr.getDateTime());
				dataMap.put("recordUrl",temPr.getRecordUrl());
				dataMap.put("wordUrl",temPw.getWordUrl());
				dataList.add(dataMap);
			}
		}
		map.put("code", 0);
		map.put("msg", "");
		map.put("count", dataList.size());
		map.put("data", dataList);
		return JSONObject.toJSONString(map);
	}
	//跳转识别详情
	@RequestMapping(value = "/recognizeDetail")
	public String recognizeDetail(HttpServletRequest request) {
		String photoSrc = request.getParameter("photoSrc");
		String sourcePhotoSrc = "http://localhost:8080/FTZ/" + photoSrc;
		String simplifiedPhotoSrc = sourcePhotoSrc.split("\\.")[0] + "_Simplified.jpg";
		request.setAttribute("sourcePhotoSrc", sourcePhotoSrc);
		request.setAttribute("simplifiedPhotoSrc", simplifiedPhotoSrc);
		return "personalPhoto/recognizeDetail";
	}
	
	//更新用户权限
	@RequestMapping("/updatePermission")
	@ResponseBody
	public int updatePermission(HttpServletRequest request){
		Long userId = Long.parseLong(request.getParameter("userId"));
		Integer value = Integer.parseInt(request.getParameter("value"));
		if(userService.updatePermission(userId,value)){
			return 1;
		} else {
			return 0;
		}
	}
	@RequestMapping("/adminTopSwitch")
	public String adminTopSwitch() {
		return "admin/topSwitch";
	}
	//根据userId取得用户所有信息，包括个人信息，book信息，bookRecord信息，bookWord信息，PhotoRecord信息，PhotoWord信息
	@RequestMapping("/getAllInfoByUserId")
	@ResponseBody
	public String getAllInfoByUserId(HttpServletRequest request){
		Long userId = Long.parseLong(request.getParameter("userId"));
		Users user = userService.getUserInfo(userId);
		//查找PhotoRecord信息
		List<PhotoRecord> photoRecords = photorecordService.photoRecords(userId);
		user.setPhotoRecords(photoRecords);
		Iterator<PhotoRecord> prIter = photoRecords.iterator();
		while(prIter.hasNext()){
			PhotoRecord tempPhotoRecord = prIter.next();
			Long tempRecord = tempPhotoRecord.getRecord();
			//查找PhotoWord信息
			List<PhotoWord> photoWords = photoWordService.getPhotoWordByRecord(tempRecord);
			tempPhotoRecord.setWordList(photoWords);
		}
		//查找book信息
		List<book> books = bookService.select(userId);
		user.setBooks(books);
		Iterator<book> bkIter = books.iterator();
		while(bkIter.hasNext()){
			book tempBook = bkIter.next();
			Long tempBookId = tempBook.getBookid();
			//查找bookRecord信息
			List<bookRecord> bookRecords = bookRecordService.bookRecordSelect(tempBookId);
			tempBook.setBookRecords(bookRecords);
			Iterator<bookRecord> brIter = bookRecords.iterator();
			while(brIter.hasNext()){
				bookRecord tempBookRecord = brIter.next();
				Long tempRecord = tempBookRecord.getBookrecord();
				//查找bookWord信息
				List<bookword> bookWords = bookWordService.getByBookRecord(tempRecord);
				tempBookRecord.setBookWords(bookWords);
			}
		}
		return JSONObject.toJSONString(user);
	}
	@RequestMapping(value = "/pdfList")
	public String pdfList(HttpServletRequest request){
		List<book> books;
		String idStr = request.getParameter("userId");
		if(idStr!=null){
			if(idStr.equals("")){//参数userId为空字符串，表示查询所有用户的book信息
				books = bookService.selectAll();
			} else {//参数userId不为空字符串，表示查询一个用户的book信息
				books = bookService.select(Long.parseLong(idStr));	
			}
		} else {//参数userId为空，表示查询所有用户的book信息
			books = bookService.selectAll();
		}
		Iterator<book> iter = books.iterator();
		@SuppressWarnings("rawtypes")
		List<Map> mapList = new ArrayList<>();
		while(iter.hasNext()){
			book tempBook = iter.next();
			Map<String,Object> bookInfoMap = new HashMap<>();
			bookInfoMap.put("bookId", tempBook.getBookid());
			bookInfoMap.put("userId", tempBook.getUserID());
			bookInfoMap.put("bookName", tempBook.getBookname());
			bookInfoMap.put("bookPage", tempBook.getBookpage());
			bookInfoMap.put("bookUrl", tempBook.getBookurl());
			bookInfoMap.put("flag", tempBook.getFlag());
			bookInfoMap.put("dateTime", tempBook.getBookdatetime());
			bookInfoMap.put("bookMoney", tempBook.getBookmoney());
			//根据bookId查询已识别的页码数量
			int recognizedPageNum = bookRecordService.getRecognizedPageNumByBookId(tempBook.getBookid());
			String recognizedProgress = new DecimalFormat("#").format(((double)recognizedPageNum*100)/tempBook.getBookpage());
			bookInfoMap.put("recognizedProgress", recognizedProgress);
			int recognizedWordsNum = bookService.getRecognizedWordsNum(tempBook.getBookid());
			bookInfoMap.put("recognizedWordsNum", recognizedWordsNum);
			mapList.add(bookInfoMap);
		}
		request.setAttribute("bookInfoJson", JSONObject.toJSONString(mapList));
		return "admin/PdfList";
	}
	@RequestMapping(value = "/pdfPhotoListAdmin")
	public String pdfPhotoListAdmin(HttpServletRequest request){
		List<bookRecord> br = null;
		String idStr = request.getParameter("bookId");
		Long bookId = Long.parseLong(idStr);
		book bk = bookService.selectById(bookId);
		br = bookRecordService.bookRecordSelect(bookId);
		@SuppressWarnings("rawtypes")
		List<Map> mapList = new ArrayList<>();
		Iterator<bookRecord> brIter = br.iterator();
		while(brIter.hasNext()){
			Map<String,Object> tempMap = new HashMap<>();
			bookRecord tempBr = brIter.next();
			tempMap.put("page", tempBr.getRecpage());
			tempMap.put("recognized", tempBr.getRecflag());
			tempMap.put("photoSrc", bk.getBookurl() + "/" + bk.getBookname() + "_" +tempBr.getRecpage() + ".png");
			mapList.add(tempMap);
		}
		request.setAttribute("pdfPhotoListJson", JSONObject.toJSONString(mapList));;
		return "admin/pdfPhotoListAdmin";
	}
	//从书籍查找文字来源,跳转到searchCharFormPdfAdmin.jsp页面
	@RequestMapping("/searchCharFromPdfAdmin")
	public String searchCharFromPdfAdmin(){
		return "admin/searchCharFromPdfAdmin";
	}
	//从书籍查找文字来源，返回json数据
	@RequestMapping(value = "/searchCharByWordFromPdf")
	@ResponseBody
	public String searchCharByWordFromPdf(@Param("word") String word){
		List<WordSourceInfo> wsi = new ArrayList<>();;
		int wordId;
		Map<Object, Object> map = new HashMap<>();
		if(word == null||word==""){
			
		} else {
			try {
				wordId = wordService.getWord(new String(word.getBytes("ISO-8859-1"), "UTF-8")).getWordId();
				wsi = bookService.searchWordSourceAdmin(wordId);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		map.put("code", 0);
		map.put("msg", "");
		map.put("count", wsi.size());
		map.put("data", wsi);
		return JSONObject.toJSONString(map);
	}
	//管理平台图片管理：图片列表
	@RequestMapping("/photoList")
	public String photoList(HttpServletRequest request) {
		List<PhotoRecord> pr = null;
		String idStr = request.getParameter("userId");
		if(idStr!=null){
			if(idStr.equals("")){//参数userId为空字符串，表示查询所有用户的photo信息
				pr = photorecordService.photoList();
			} else {//参数userId不为空字符串，表示查询一个用户的photo信息
				pr = photorecordService.photoRecords(Long.valueOf(request.getParameter("userId")));	
			}
		} else {//参数userId为空，表示查询所有用户的photo信息
			pr = photorecordService.photoList();
		}
		request.setAttribute("photoListJson", JSONObject.toJSONString(pr));;
		return "admin/photoList";
	}
}
