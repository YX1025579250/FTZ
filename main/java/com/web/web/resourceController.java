package com.web.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.web.entity.Reply;
import com.web.entity.Resource;
import com.web.entity.Users;
import com.web.service.UserService;
import com.web.service.replyService;
import com.web.service.resourceService;

import net.sf.json.JSONArray;

@Controller
public class resourceController {
	
	@Autowired
	private resourceService rs;
	
	@Autowired
	private UserService us;
	
	@Autowired
	private replyService rps;
	
	@RequestMapping("/resource")
	public String resource(){
		return "resource";
	}
	
	@RequestMapping("resource2")
	@ResponseBody
	public JSONArray resource2(int page,int limit,String keyword) throws UnsupportedEncodingException{
		if(keyword!=null){
			keyword = "%"+new String(keyword.getBytes("ISO-8859-1"), "UTF-8")+"%";
			List<Resource> list = rs.getResourcesBykw(page, limit,keyword);
			PageInfo pageInfo = new PageInfo(list);
			JSONArray jsonArray = JSONArray.fromObject(pageInfo);
			return jsonArray;
		}else{
			List<Resource> list = rs.getResources(page, limit);
			PageInfo pageInfo = new PageInfo(list);
			JSONArray jsonArray = JSONArray.fromObject(pageInfo);
			return jsonArray;
		}
	}
	
	@RequestMapping("/resourcedetail")
	public String resourcedetail(HttpSession session,Long resourceId){
		session.setAttribute("resourceId", resourceId);
		return "resourcedetail";
	}
	
	@RequestMapping("resourcedetail2")
	@ResponseBody
	public JSONArray resourcedetail2(HttpSession session){
		Long resourceId = (Long)session.getAttribute("resourceId");
		Resource resource = rs.getResource(resourceId);
		JSONArray jsonArray = JSONArray.fromObject(resource);
		return jsonArray;
	}
	
	@RequestMapping("reply")
	@ResponseBody
	public JSONArray reply(HttpSession session,int page,int limit){
		Long resourceId = (Long)session.getAttribute("resourceId");
		List<Reply> list = rs.getReplys(resourceId, page, limit);
		PageInfo pageInfo = new PageInfo(list);
		JSONArray jsonArray = JSONArray.fromObject(pageInfo);
		return jsonArray;
	}
	
	@RequestMapping("addreply")
	public void addreply(HttpServletRequest request,HttpServletResponse response,HttpSession session,String phonenumber,Long resourceId,String replyContent) throws Exception{
		Users u = us.getUserID(phonenumber);
		Long userId = u.getUserId();
		System.out.println(userId);
		rps.addReply(userId, resourceId, replyContent);
		request.getRequestDispatcher("/resource?resourceId="+resourceId).forward(request, response);
		return; 
	}
}
