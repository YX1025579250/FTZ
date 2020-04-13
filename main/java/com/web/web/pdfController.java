package com.web.web;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.web.entity.PhotoRecord;
import com.web.entity.Users;
import com.web.entity.book;
import com.web.entity.bookRecord;
import com.web.service.UserService;
import com.web.service.bookService;
import com.web.service.bookRecordService;
//import com.web.tool.ChangePdfToImg;
import com.web.tool.PDF2IMAGE;
import com.web.util.CutBigImage;
import com.web.util.posepoint;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.web.tool.url;
@Controller
public class pdfController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private bookService bookservice;
	@Autowired
	private bookRecordService bookRecordService;
	//@RequestParam   MultipartFile对象需要用这个注解
    @RequestMapping("/SavePDF")
    public String uploaderResumes(@RequestParam("file") MultipartFile file, HttpServletRequest request,HttpSession session) {  
        // 判断文件是否为空  
        if (!file.isEmpty()) {  
            try {  
            	//获取时间
                Date d1 = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
        		String res = sdf.format(new Date());//20200227125641769   类似于这个东西
        		
        		
                // 文件保存路径  
            	Users user = (Users) session.getAttribute("user");
                Users u = userService.getUserID(user.getPhonenumber());
        		Long userId = u.getUserId();
        		
                String filePath = url.dir+"\\"+ userId.toString()+"\\PDF" 
                        + System.getProperty("file.separator") 
                       +file.getOriginalFilename().split("\\.")[0]+res+"."+file.getOriginalFilename().split("\\.")[1];  
                // 转存文件  
                file.transferTo(new File(filePath)); 
                String filepath1=url.dir+"\\"+ userId.toString()+"\\PDF";
                int page=PDF2IMAGE.pdf2Image(filePath,filepath1,res, 300);///扔进pdf转照片的方法中
                
                //更改文件的名字，方便读取同名文件
//                File f = new File(filePath);
//                String newFilepath= "E:/FTZ/"+ userId.toString()+"/PDF" 
//                        + System.getProperty("file.separator") 
//                       +file.getOriginalFilename().split("\\.")[0]+res+file.getOriginalFilename().split("\\.")[1]; 
//                File nf = new File(newFilepath);
//                try {
//                	            f.renameTo(nf); // 修改文件名
//            	      } catch (Exception err) {
//            	           err.printStackTrace();
//                       return null;
//            	       }
        		
        		//往book表写入
                book savebook=new book();
                String name=file.getOriginalFilename().split("\\.")[0];///获取书的名字（不带扩展名）
				savebook.setBookname(name);
				savebook.setBookpage(page);
				savebook.setBookurl(name+res);
				savebook.setFlag(1);
				savebook.setUserID(userId);
				savebook.setBookdatetime(d1);
				savebook.setBookmoney(0);
				savebook.setBookbelonging(0);
				bookservice.saveBook(savebook);//////往book表写入数据
				System.out.println("------------------------------------------------");
				System.out.println(userId+name);
				List<book> b=bookservice.selectByUserIDAndbookname(userId, name);
				
//				long id=1;
//				for(int i = 0 ; i < b.size() ; i++) {
//					if(b.get(i).getBookname().equals(savebook.getBookname()))
//					{
//						id=b.get(i).getBookid();
//						break;
//					}
//				}
				long id=b.get(b.size()-1).getBookid();///获取一个用户多个同名文件的最后一个（也就是刚添加的那一个）
				
				bookRecord savebookRecord=new bookRecord();
				savebookRecord.setBookid(id);
				savebookRecord.setRecflag(1);
				savebookRecord.setRecbookdatetime(d1);
				
				for(int i=1;i<=page;i++)
				{
					savebookRecord.setRecpage(i);
					bookRecordService.saveBookRecord(savebookRecord);
				}//将pdf每页数据写入book_record。
				
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }
        return "pdf/ImgFromPDF";  
    }
    @RequestMapping("/pdfIndex")
	public String pdfupload() {
//    	ChangePdfToImg a=new ChangePdfToImg();
//    	a.changePdfToImg("E:/FTZ/TestPDF/A.pdf","E:/FTZ/TestPDF/");
    	
		return "toUploadPDF";
	}
    
    ///动态json传输
    @RequestMapping("/sendjsonImgFromPDF")
    @ResponseBody
	public JSONArray jsonTranslation(HttpServletRequest request,HttpSession session) {
    	
    	JSONArray array = new JSONArray();
    	System.out.println("------------------------------------------------");
    	System.out.println("------------------------------------------------");
//    	ArrayList<bookRecord> bookrecord = null;
    	Users user = (Users) session.getAttribute("user");
        Users u = userService.getUserID(user.getPhonenumber());
		Long userId = u.getUserId();
		
		List<book> b=bookservice.select(userId);
		Long bookid=b.get(b.size()-1).getBookid();
		List<bookRecord> thisBookRecord=bookRecordService.bookRecordSelect(bookid);
		
		book bb=bookservice.selectById(bookid);
		
		for(int i = 0 ; i < thisBookRecord.size() ; i++) {
			JSONObject ob=new JSONObject();
	    	ob.put("user", userId);
	    	ob.put("bookname", bb.getBookname());
	    	//ob.put("bookdatetime", b.get(i).getBookdatetime());
	    	ob.put("recpage", thisBookRecord.get(i).getRecpage());
	    	ob.put("bookurl", bb.getBookurl());
	    	ob.put("recflag", thisBookRecord.get(i).getRecflag());
	    	array.add(ob);
		}
		System.out.print(array.toString());
		return array;
	}
    
    @RequestMapping("/sendjsonPDF")
    @ResponseBody
	public JSONArray jsonPdfTranslation(HttpServletRequest request,HttpSession session) {
    	
    	JSONArray array1 = new JSONArray();
    	System.out.println("------------------------------------------------");
    	System.out.println("------------------------------------------------");
//    	ArrayList<bookRecord> bookrecord = null;
    	Users user = (Users) session.getAttribute("user");
        Users u = userService.getUserID(user.getPhonenumber());
		Long userId = u.getUserId();
		
		List<book> b=bookservice.select(userId);
		
		
		for(int i = 0 ; i < b.size() ; i++) {
			JSONObject ob=new JSONObject();
	    	ob.put("user", userId);
	    	ob.put("bookname", b.get(i).getBookname());
	    	//ob.put("bookdatetime", b.get(i).getBookdatetime());
	    	ob.put("bookid", b.get(i).getBookid());
	    	ob.put("bookurl", b.get(i).getBookurl());
	    	ob.put("bookflag", b.get(i).getFlag());
	    	array1.add(ob);
		}
		System.out.print(array1.toString());
		return array1;
	}
    
    
    
    
    //LoadingImgFromOneOfPDF
    @RequestMapping("/LoadingImgFromOneOfPDF")
    @ResponseBody
	public JSONArray LoadingImg(HttpServletRequest request,HttpSession session) {
    	JSONArray array1 = new JSONArray();
    	Long id=Long.parseLong(request.getParameter("bookid"));
    	//String id = request.getParameter("bookid");
    	System.out.print(id);
//    	Long id=Long.parseLong(request.getParameter("appname"));
    	List<bookRecord> selectbookRecord=bookRecordService.bookRecordSelect(id);

    	
    	Users user = (Users) session.getAttribute("user");
        Users u = userService.getUserID(user.getPhonenumber());
		Long userId = u.getUserId();
		
		book b=bookservice.selectById(id);
		
		//List<book> b=bookservice.select(userId);
		
		
		for(int i = 0 ; i < selectbookRecord.size() ; i++) {
			JSONObject ob=new JSONObject();
	    	ob.put("user", userId);
	    	ob.put("bookname", b.getBookname());
	    	//ob.put("bookdatetime", b.get(i).getBookdatetime());
	    	ob.put("recpage", selectbookRecord.get(i).getRecpage());
	    	ob.put("bookurl", b.getBookurl());
	    	ob.put("recflag", selectbookRecord.get(i).getRecflag());
	    	array1.add(ob);
		}
		System.out.print(array1.toString());
	//	request.setAttribute("urls", array1);
		return array1;
	}
    
    @RequestMapping("/uploadImgOfpdf")
	public String uploadImgOfPDF(HttpSession session, HttpServletRequest request, Model model) throws IOException {
    	String userida=request.getParameter("user");
    	String bookurl=request.getParameter("bookurl");
    	String recpage=request.getParameter("recpage");
    	String bookname=request.getParameter("bookname");
    	
//		Users user = (Users) session.getAttribute("user");
//		Users u = userService.getUserID(user.getPhonenumber());
//		Long userId = u.getUserId();
		Date d1 = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
		String res = sdf.format(new Date());
		// uploads文件夹位置
		String rootPath = url.dir+"\\"+userida + "\\" +"PDF"+"\\"+bookurl+"\\"+bookname+"_"+recpage+".png";
		// 原始名称
		//String originalFileName = file.getOriginalFilename();
		// 新文件名
		//String newFileName = "sliver" + res + originalFileName.substring(originalFileName.lastIndexOf("."));
		// 创建年月文件夹
		
		File dateDirs = new File(userida);

		// 新文件
		File newFile = new File(rootPath);
		// 判断目标文件所在目录是否存在
		if (!newFile.getParentFile().exists()) {
			// 如果目标文件所在的目录不存在，则创建父目录
			newFile.getParentFile().mkdirs(); 
		}
		System.out.println(newFile);
		// 将内存中的数据写入磁盘
		//file.transferTo(newFile);
		// 完整的url
//		String fileUrl = dirft +"\\"+userida + "\\" +"PDF"+"\\"
//				+ newFileName;
		ArrayList<Object> listpoint = CutBigImage.cutBigImage(rootPath);
		model.addAttribute("arrayList", listpoint);
		model.addAttribute("filename",
				userida + "\\" +"PDF"+"\\"+bookurl+"\\"+bookname+"_"+recpage+".png");
		bookRecord bookrecord=new bookRecord();
		bookrecord.setRecflag(0);
		bookrecord.setRecbookdatetime(d1);
		bookrecord.setRecpage(Integer.parseInt(recpage));
		
		List<book> b =bookservice.selectByUserIDAndbookname(Long.parseLong(userida), bookname);
		
		for(int i=0;i<b.size();i++)
		{
			if(b.get(i).getBookurl().equals(bookurl))
			{
				bookrecord.setBookid(b.get(i).getBookid());
				break;
			}
		}
		List<bookRecord> br=bookRecordService.bookRecordSelect(bookrecord.getBookid());
		for(int i=0;i<br.size();i++)
		{
			if(br.get(i).getRecpage().toString().equals(recpage))
			{
				bookrecord.setBookrecord(br.get(i).getBookrecord());
			}
		}
		bookRecordService.UpdataBookRecord(bookrecord);
		///判断整本书是否识别完成。
		List<bookRecord> brr=bookRecordService.bookRecordSelect(bookrecord.getBookid());
		int flag=0;
		for(int i=0;i<brr.size();i++)
		{
			flag+=brr.get(i).getRecflag();
			
			
		}
		if(flag==0)
		{
			book bb=bookservice.selectById(bookrecord.getBookid());
			bb.setFlag(flag);
			bookservice.UpdataBook(bb);
			
		}
		
		
		return "recognize1";
	}
}
