package com.web.web;
import com.web.tool.url;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.annotations.Param;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import com.alibaba.fastjson.JSONObject;
import com.web.entity.PhotoRecord;
import com.web.entity.PhotoWord;
import com.web.entity.Users;
import com.web.service.UserService;
import com.web.service.photorecordService;
import com.web.service.photowordService;
import com.web.service.wordService;
import com.web.util.XmlToJson;

@Controller
public class personcontroller {
	@Autowired
	private UserService userService;
	@Autowired
	private photorecordService photorecordService;
	@Autowired
	private wordService wordService;
	@Autowired
	private photowordService photoWordService;

	@RequestMapping("/personmain")
	public String personmain() {
		return "person/personmain";
	}
	@RequestMapping("/personleft")
	public String personleft() {
		return "person/personleft";
	}

	@RequestMapping("/personInformation")
	public String personInformation(HttpSession session,HttpServletRequest request,Map<String,String> map){
		Users user = (Users) session.getAttribute("user");
		Users u = userService.getUserID(user.getPhonenumber());
		Long userId = u.getUserId();
		System.out.println("userId:"+userId);
		Users userInfo = userService.getUserInfo(userId);
		
		
		map.put("phonenumber",userInfo.getPhonenumber());    
		map.put("password",userInfo.getPassword());
		map.put("regDate",userInfo.getRegDate());
		map.put("isVIP",userInfo.getIsVIP().toString());
		map.put("money",userInfo.getMoney().toString());
		map.put("money_wait",userInfo.getMoneyWait().toString());
		map.put("book",userInfo.getBook().toString());
		map.put("photo",userInfo.getPhoto().toString());
//		private long userid;
//		   private String sex;
//		   private String occupation;
//		   private long id;
//		   private Integer age;
//		   private String name;
		if(userInfo.getSex()==1)
			map.put("sex","男");
		else if(userInfo.getSex()==0)
			map.put("sex","女");
		else
			map.put("sex","非法读取，请重新设置性别");
		
		map.put("age",userInfo.getAge().toString());
		map.put("name",userInfo.getName());
		map.put("occupation",userInfo.getOccupation());
		
		request.setAttribute("map", map);
		return "person/personInformation";
	}
	@RequestMapping("/ChangPasswordto")
	public String ChangPasswordto(HttpSession session,HttpServletRequest request,Map<String,String> map){
		Users user = (Users) session.getAttribute("user");
		Users u = userService.getUserID(user.getPhonenumber());
		Long userId = u.getUserId();
		Users userInfo = userService.getUserInfo(userId);
		userInfo.setUserId(userId);
//		userInfo.setIsVIP(2);
//		userInfo.setMoney(2.0);
		String password = request.getParameter("password");
		String checkpassword = request.getParameter("checkpassword");
//		//测试为什么改变密码的时候，丢失其他数据。
//		System.out.println("====================================================================");
//		System.out.println(user.getRegDate());
//		System.out.println(user.getBook());
//		System.out.println(user.getMoney());
//		System.out.println(user.getIsVIP());
//		System.out.println(user.getMoneyWait());
//		System.out.println("====================================================================");
		if(password.equals(checkpassword)&&!checkpassword.equals(""))
		{
			userInfo.setPassword(checkpassword);
			userService.UpdatePassword(userInfo); 
			 return "success";
		}
		else{
			return "fail";
		}
		
	}
	
	@RequestMapping("/ChangPassword")
	public String ChangPassword(HttpSession session,HttpServletRequest request,Map<String,String> map){
		
		return "person/ChangPassword";
	}
	@RequestMapping("/Changeinfo")
	public String Chang_info(HttpSession session,HttpServletRequest request,Map<String,String> map){

		return "person/Changeinfo";
	}
	//ChangInfoto
	@RequestMapping("/ChangInfoto")
	public String ChangInfoto(HttpSession session,HttpServletRequest request,Map<String,String> map) throws IOException {
		Users user = (Users) session.getAttribute("user");
		Users u = userService.getUserID(user.getPhonenumber());
		Long userId = u.getUserId();
		
//		Users userInfo = userService.getUserInfo(userId);
		Users usersinfo=userService.getUserInfo(userId);
		
		usersinfo.setUserId(userId);
		
//		String phonenumber = request.getParameter("phonenumber");
		String sex = new String(request.getParameter("sex").getBytes("iso-8859-1"), "utf-8");//解决获取的前端数据中文乱码的问题
		//String sex = request.getParameter("sex");
		String age = request.getParameter("age");
//		String name = request.getParameter("name");
//		String occupation = request.getParameter("occupation");
		String name = new String(request.getParameter("name").getBytes("iso-8859-1"), "utf-8");
		String occupation = new String(request.getParameter("occupation").getBytes("iso-8859-1"), "utf-8");
		System.out.println(sex+age+name+occupation);
		if((!sex.equals(""))&&(!age.equals(""))&&(!name.equals(""))&&(!occupation.equals("")))
		{
		
			 
			 usersinfo.setAge(Integer.parseInt(age));
			 if(sex.equals("男"))
				 usersinfo.setSex(1);
			 else if(sex.equals("女"))
				 usersinfo.setSex(0);
			 else
				 usersinfo.setSex(2);
			 usersinfo.setName(name);
			 usersinfo.setOccupation(occupation);
			 userService.UpdatePassword(usersinfo);
			 return "success";
		}
		else{
			return "fail";
		}
		
	}
	
	@RequestMapping("/personmage")
	public String personmage(HttpSession session,HttpServletRequest request,Map<String,String> map) {
		Users user = (Users) session.getAttribute("user");
		Users u = userService.getUserID(user.getPhonenumber());
		Long userId = u.getUserId();
		System.out.println("userId:"+userId);
		Users userInfo = userService.getUserInfo(userId);
		map.put("phonenumber",userInfo.getPhonenumber());
		map.put("password",userInfo.getPassword());
		map.put("regDate",userInfo.getRegDate());
		map.put("isVIP",userInfo.getIsVIP().toString());
		map.put("money",userInfo.getMoney().toString());
		map.put("money_wait",userInfo.getMoneyWait().toString());
		map.put("book",userInfo.getPhoto().toString());
		map.put("photo",userInfo.getPhoto().toString());
		request.setAttribute("map", map);
		return  "person/topersonCenter";
	}
	@RequestMapping("/personalPhotoList")
	public String personalPhotoList(HttpSession session,HttpServletRequest request){
		Users user = (Users) session.getAttribute("user");
		Users u = userService.getUserID(user.getPhonenumber());
		Long userId = u.getUserId();
		List<String> urls = photorecordService.personalPhotoList(userId);
		int indexValue = Integer.parseInt(request.getParameter("indexValue"));
		request.setAttribute("urls", urls);
		request.setAttribute("indexValue", indexValue);
		return "personalPhoto/personalPhotoList";
	}
	@RequestMapping("/searchChar")
	public String selectChar(HttpSession session,HttpServletRequest request){
		Users user = (Users) session.getAttribute("user");
		Users u = userService.getUserID(user.getPhonenumber());
		Long userId = u.getUserId();
		request.setAttribute("userId", userId);
		return "personalPhoto/searchChar";
	}
	@RequestMapping(value = "/searchByUserIdAndWord")
	@ResponseBody
	public String searchByUserIdAndWord(@Param("userId") String userId,@Param("word") String word) {
		PhotoRecord temPr = null;
		List<PhotoWord> pw = null;
		PhotoWord temPw = null;
		int wordId;
		if(word == null||word==""||userId==null||userId==""){
			return "";
		} else {
			try {
				wordId = wordService.getWord(new String(word.getBytes("ISO-8859-1"), "UTF-8")).getWordId();
				pw = photoWordService.getPhotoWordByUserIdAndWordId(Long.valueOf(userId), wordId);
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
//	@RequestMapping("/recDetail")
//	public String recDetail(@Param("record") String record){
//		List<PhotoWord> pw = null;
//		if(record == null||record==""){
//			return "";
//		} else {
//			pw = photoWordService.getPhotoWordByRecord(Long.valueOf(record));
//			String jpgUrl = photorecordService.photoRecord(Long.valueOf(record)).getRecordUrl();
//			String xmlUrl = jpgUrl.substring(0, jpgUrl.length()-4) + ".xml";
//			//System.out.println(xmlUrl);
//			try {
//				JSONObject json = XmlToJson.xml2jsonString("E:/FTZ/"+xmlUrl)) ;
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return "personalPhoto/recDetail";
//	}
	@RequestMapping("/recognizeDetail2")
	public String show(HttpServletRequest request) throws JSONException, IOException{
		String photoSrc = request.getParameter("photoSrc");
		//System.out.println(photoSrc);
		String xmlUrl = url.dire+"//" + photoSrc.substring(photoSrc.indexOf("FTZ")).split("\\.")[0] + ".xml";
		//System.out.println(xmlUrl);
		String dataJson;
		dataJson = XmlToJson.xml2jsonString(xmlUrl).getJSONObject("wordandpoints").getJSONArray("point").toString();
		request.setAttribute("photoSrc", photoSrc);
		request.setAttribute("dataJson", dataJson);
		return "personalPhoto/recognizeDetail2";
	}
	//通过url查询是否识别
	@RequestMapping("/selectRecognized")
	@ResponseBody
	public int selectRecognized(HttpServletRequest request){
		String photoSrc = request.getParameter("photoSrc");
		String recordUrl = photoSrc.substring(photoSrc.indexOf("FTZ")+4);
		Integer recognized = photorecordService.selectRecognizedByUrl(recordUrl);
		//之前的脏数据recognized字段为null
		return recognized==null?0:recognized;
	}
}
