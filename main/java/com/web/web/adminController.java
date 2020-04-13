package com.web.web;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.web.entity.PhotoRecord;
import com.web.entity.PhotoWord;
import com.web.entity.Users;
import com.web.service.UserService;
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
	
	@SuppressWarnings("unchecked")
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
		int wordId;
		if(word == null||word==""){
			return "";
		} else {
			try {
				wordId = wordService.getWord(new String(word.getBytes("ISO-8859-1"), "UTF-8")).getWordId();
				pw = photoWordService.getPhotoWordByWordId(wordId);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<Map<Object,Object>> dataList = new ArrayList<>();
		Map<Object, Object> map = new HashMap<>();
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
}
