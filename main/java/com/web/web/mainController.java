package com.web.web;


import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import com.web.tool.url;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.lowagie.text.pdf.codec.Base64.InputStream;
import com.lowagie.text.pdf.codec.Base64.OutputStream;
import com.web.entity.PhotoRecord;
import com.web.entity.PhotoWord;
import com.web.entity.Users;
import com.web.entity.word;
import com.web.interceptor.Filemove;
import com.web.interceptor.SAXWrite;
import com.web.service.UserService;
import com.web.service.photorecordService;
import com.web.service.photowordService;
import com.web.service.wordService;
import com.web.util.CutBigImage;
import com.web.util.ImageSplit;
import com.web.util.ScreenShot;
import com.web.util.posepoint;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;

import org.apache.commons.codec.binary.Base64;
/////刘俊学长的更新。
import java.util.List;
import com.web.entity.book;
import com.web.entity.bookRecord;
import com.web.entity.bookword;
import com.web.service.bookService;
import com.web.service.bookRecordService;
import com.web.service.bookwordService;
import com.web.tool.base64ToImg;;
@Controller
public class mainController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private photorecordService photorecordService;
	@Autowired
	//刘俊学长的更新
	private bookService bookService;
	@Autowired
	private bookRecordService bookRecordService;
	@Autowired
	private bookwordService bookwordService;
	@Autowired
	
	private photowordService photowordService;
	@Autowired
	private wordService wordService;
	
	@RequestMapping("/shibeipingtai")
	public String shibeipingtai() {
		return "shibeipingtai";
	}

	@RequestMapping("/Index")
	public String index() {
		return "index";
	}

	@RequestMapping("/first")
	public String first() {
		return "firstpage";
	}
    //文字识别
	@RequestMapping("/recognize2")
	@ResponseBody
	public JSONArray testJson(HttpServletRequest request, Model model) {
		String ds = request.getParameter("ds");
		JSONArray json = JSONArray.fromObject(ds);
		
		//System.out.println(json.toString());
		
		ImageSplit sp = new ImageSplit();
		ArrayList<posepoint> dirlist = null;
		try {
			dirlist = sp.splitImage2(json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dirlist = userService.recongnize(dirlist);
		JSONArray jsonArray = JSONArray.fromObject(dirlist);
		//System.out.println(jsonArray.toString());
		return jsonArray;
	}
	
	//识别后的简体字截图
	@RequestMapping("/screenShot")
	@ResponseBody
	public void screenShot(HttpServletRequest request){
		String photoName = request.getParameter("photoName");
		String prefixName = photoName.substring(photoName.indexOf("FTZ"),photoName.indexOf(".")).replaceAll("//", "/");
		ScreenShot.run(prefixName);
	}
	
	//上传图片界面
	@RequestMapping("/recognize")
	public String recognize() {
		return "torecognize";
	}
	
	//上传图片定位
	@RequestMapping("/fileupload")
	public String upload(HttpSession session,MultipartFile file, HttpServletRequest request, Model model) throws IOException {
		Users user = (Users) session.getAttribute("user");
		Users u = userService.getUserID(user.getPhonenumber());
		Long userId = u.getUserId();
		Date d1 = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
		String res = sdf.format(new Date());
		// uploads文件夹位置
		String rootPath = url.dir;
		// 原始名称
		String originalFileName = file.getOriginalFilename();
		// 新文件名
		String newFileName = "sliver" + res + originalFileName.substring(originalFileName.lastIndexOf("."));
		// 创建年月文件夹
		Calendar date = Calendar.getInstance();
		File dateDirs = new File(userId.toString());

		// 新文件
		File newFile = new File(rootPath + File.separator + dateDirs + File.separator+"jpg" +File.separator+ newFileName);
		// 判断目标文件所在目录是否存在
		if (!newFile.getParentFile().exists()) {
			// 如果目标文件所在的目录不存在，则创建父目录
			newFile.getParentFile().mkdirs();
		}
		System.out.println(newFile);
		// 将内存中的数据写入磁盘
		file.transferTo(newFile);
		// 完整的url
		String fileUrl = url.dir +"\\"+ userId.toString() + "\\" +"jpg"+"\\"
				+ newFileName;
		ArrayList<Object> listpoint = CutBigImage.cutBigImage(fileUrl);
		model.addAttribute("arrayList", listpoint);
		model.addAttribute("filename",
				userId.toString() + "//" +"jpg"+"//"+ newFileName);
		PhotoRecord PhotoRecord=new PhotoRecord();
		PhotoRecord.setUserId(userId);
		PhotoRecord.setDateTime(d1);
		PhotoRecord.setRecordUrl(userId.toString() + "//" +"jpg"+"//"+ newFileName);
		PhotoRecord.setRecognized(0);
		photorecordService.savePhotoRecord(PhotoRecord);
		
		model.addAttribute("flag",0);
		return "recognize1";
	}
	
	//查看图片时对未识别的图片进行识别
	@RequestMapping("/afterwardRecognized")
	public String afterwardRecognized(HttpServletRequest request, Model model,HttpSession session) throws IOException {
		String photoSrc = request.getParameter("photoSrc");
		int indexValue = Integer.valueOf(request.getParameter("indexValue"));
		//fromList=true表示页面从图片列表跳转
		String fromList = request.getParameter("fromList");
		String fileUrl = photoSrc.substring(photoSrc.indexOf("FTZ")+4);
		ArrayList<Object> listpoint = CutBigImage.cutBigImage(url.dir+"\\"+fileUrl);
		model.addAttribute("arrayList", listpoint);
		model.addAttribute("filename",fileUrl);
		model.addAttribute("indexValue",indexValue);
		model.addAttribute("fromList",fromList);
		
		if("1".equals(request.getParameter("flagpdf")))
		{
			model.addAttribute("flag",1);
			session.setAttribute("fromPDF", "1");
			
		}
		else
		{
			model.addAttribute("flag",0);
			session.setAttribute("fromPDF", "0");
		}
		return "recognize1";
	}
    //登录界面
	@RequestMapping("/login")
	public String login() {
		return "tologin";
	}
	//登录验证
	@RequestMapping(value = "/loginCheck", method = RequestMethod.POST)
	private String tologin(@Param("phonenumber") String phonenumber, @Param("password") String password, Map<String, Object> map,
			HttpServletRequest request, HttpServletResponse response) {
		Users user = userService.getByPhonenumberAndPassword(phonenumber, password);
		HttpSession session = request.getSession();
	//	System.out.println("当前的sessionId是:"+session.getId());
		if (user != null) {
			// ==========新增
			// 保存登录时间
			Date loginTime = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String ltime = sdf.format(loginTime);
			int num = user.getLoginNum().intValue() + 1;
//						System.out.println("登录时间为：" + ltime);
//						System.out.println("登录次数为：" + num);
			// 更新数据库中用户的上一次登录时间和登录次数
			boolean flag = userService.UpdataTimeAndNum(ltime, num, user.getUserId());
//						if (flag == true)
//							System.out.println("时间和次数更新成功！");
			File file = new File(url.dir + "\\temp.txt");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			FileWriter writer;
			try {
				writer = new FileWriter(file,true);//true表示可追加写入
				System.out.println(ltime);
				writer.write(user.getPhonenumber()+"---"+ltime+", "+num);
				writer.write("\r\n");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//活跃度排名
			List<Users> rank = userService.getRank();
			for(int i=0;i<rank.size();i++) {
				if(rank.get(i).getUserId().intValue()==user.getUserId().intValue())
					session.setAttribute("rank", i+1);//将排名放入session中
				System.out.println(i);
			}
			// 新增========

			session.setAttribute("user", user);// 放在session域中，执行某些操作需要先判断用户是否登录
			System.out.println("登录成功！");
			return "shibeipingtai";
		} else {
			request.setAttribute("msg", "账号或密码错误，请重新登录！");
			System.out.println("登录失败！");
			return "tologin";
		}
	}

	// 注册
	// 转向注册页面
	@RequestMapping("/regist")
	public String regist() {
		return "toregist";
	}
   //注册写入数据库
	@RequestMapping(value = "/registCheck")
	public String toregist(Users user,@Param("phonenumber") String phonenumber, @Param("password") String password) {
		Date d=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String regDate = sdf.format(d);
        user.setPhonenumber(phonenumber);//写入号码
        user.setPassword(password);//写入密码
		user.setRegDate(regDate);//写入注册日期
		user.setIsVIP(0);//写入是否VIP标志
		//刘俊学长的更新
		user.setMoneyWait(0.0);
		user.setBook(0);
		user.setPhoto(0);
		user.setMoney(0.0);
		user.setAdminFlag(0);
		user.setAge(0);
		user.setName("0");
		user.setOccupation("0");
		user.setSex(3);
		user.setLastLoginTime(regDate);// 初始上一次登陆时间为注册日期
		user.setLoginNum(0);// 初始登录次数为0
		boolean flag = userService.saveUser(user);
		if (flag) {
			//刘俊学长的更新
			String filepath1=url.dir+"\\"+user.getUserId();
            File file2=new File(filepath1);		
            if(!file2.exists()){//如果文件夹不存在		
            	file2.mkdir();//创建文件夹	
            	}
			return "tologin";// 注册成功跳转到登录页面
		} else
			return "toregist";// 注册失败留在原页面
	}
	//退出登录
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
	//	System.out.println("当前的sessionId是:"+session.getId());
		session.invalidate();

	    return "redirect:/Index";

	}
	@RequestMapping("/download")
	@ResponseBody
	public String download(HttpServletRequest request, Model model,HttpSession session) throws Exception {
		Users user = (Users) session.getAttribute("user");
		Users u = userService.getUserID(user.getPhonenumber());
		Long userId = u.getUserId();
		String ds = request.getParameter("dat");
		String flagpdf = request.getParameter("flagpdf");///刘俊学长的更新
		System.out.println(flagpdf);
		System.out.println("---------------------------------------------------------------");
		String dir = request.getParameter("dir");
		JSONArray json = JSONArray.fromObject(ds);
		SAXWrite.savexml(json,dir);
		if ("1".equals(flagpdf))
		{
			char page=dir.split("\\.")[0].charAt(dir.split("\\.")[0].length()-1);
			////李禄马更改，目的是为了识别保存书籍中的一页。
			String bookdir=new String();		
			if("1".equals(session.getAttribute("fromPDF")))
			{
				bookdir=dir.split("//")[0]+"//"+dir.split("//")[1]+"//"+dir.split("//")[2].split("/")[0];
			}
			else{
				bookdir=dir.split("//")[0]+"//"+dir.split("//")[1]+"//"+dir.split("//")[2];
			}
			//System.out.println(dir.split("//")[2].split("/")[0]);
			book book=bookService.selectByurl(bookdir);
			
            bookRecord bookrecord=bookRecordService.bookRecordbyidandpage(book.getBookid(),Integer.valueOf(String.valueOf(page)));
            Date d=new Date();
            bookrecord.setRecbookdatetime(d);
            bookrecord.setRecflag(1);
            bookRecordService.UpdataBookRecord(bookrecord);
            
            //更新了标志位之后再查
            book b=bookService.selectByurl(bookdir);
            if(b.getMoneyflag()==2&&b.getFlag()==1)
			{
				b.setMoneyflag(0);
				Users user2 =userService.getUserInfo(b.getBookbelonging());
				user2.setMoneyWait(user2.getMoneyWait()+b.getBookmoney());
				userService.UpdatePassword(user2);
				bookService.UpdataBook(b);
			}
            JSONObject jsonOne;
	   		 for(int i=0;i<json.size()-1;i++){
	   			 jsonOne = json.getJSONObject(i); 
	   			 word selectword_id = wordService.selectword_id(String.valueOf(jsonOne.get("word")));
	   			 bookword bookword=new bookword();
	   			 bookword.setWordid(selectword_id.getWordId());
	   			 bookword.setRecord(bookrecord.getBookrecord());
	   			 String wordUrl=new Filemove().filesave((String.valueOf(jsonOne.get("dir"))), String.valueOf(jsonOne.get("word")),userId);
	   			 bookword.setWordurl(wordUrl);
	   			 bookwordService.savebookword(bookword);
	   		 	}
		 }
		else{
			
			PhotoRecord photorecord=photorecordService.getPhotoRecordID(dir);
			//更新识别标记为1
			photorecord.setRecognized(1);
			photorecordService.updateRecognized(photorecord);
			//更新识别标记为1
			JSONObject jsonOne;
			 for(int i=0;i<json.size()-1;i++){
				 jsonOne = json.getJSONObject(i); 
				 word selectword_id = wordService.selectword_id(String.valueOf(jsonOne.get("word")));
				 PhotoWord PhotoWord=new PhotoWord();
	//			 System.out.println(selectword_id.getWordId());
				 PhotoWord.setWordId(selectword_id.getWordId());
				 PhotoWord.setPhotoRecord(photorecord.getRecord());
				 String wordUrl=new Filemove().filesave((String.valueOf(jsonOne.get("dir"))), String.valueOf(jsonOne.get("word")),userId);
				 PhotoWord.setWordUrl(wordUrl);
				 photowordService.savePhotoWord(PhotoWord);
		 	}
			
   		 }
		return "sucess";
	}
	@RequestMapping("/uploadImg")//对上传未识别的照片点击之后进行上传的处理方法
	public String uploadImg(HttpSession session, HttpServletRequest request, Model model) throws IOException {
    	String recordurl=request.getParameter("recordurl");
    	
		// uploads文件夹位置
		String rootPath = url.dir+"\\"+recordurl;

		// 新文件
		File newFile = new File(rootPath);
		// 判断目标文件所在目录是否存在
		if (!newFile.getParentFile().exists()) {
			// 如果目标文件所在的目录不存在，则创建父目录
			newFile.getParentFile().mkdirs(); 
		}
		System.out.println(newFile);

		ArrayList<Object> listpoint = CutBigImage.cutBigImage(rootPath);
		model.addAttribute("arrayList", listpoint);
		model.addAttribute("filename",
				recordurl);
		model.addAttribute("flag",0);
		return "recognize1";
	}
	
	/////保存已经识别好的照片
	
	@RequestMapping(value = { "saveSBphoto" }, method = { RequestMethod.POST })  
    @ResponseBody  
    public String petUpgradeTarget(HttpServletRequest request, String data) { 
		String imgStr=request.getParameter("myImage"); 
		String url=request.getParameter("URL");
//		System.out.println(url);
//		System.out.println(url.split("\\.")[0]);
//		System.out.println(url.split("\\.")[1]);
//		System.out.println(url.split("\\.")[1]);
//		System.out.println("/FTZ/"+url.split("\\.")[0]+"_rec."+url.split("\\.")[1]);
		
	//	String aa="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";
		boolean a=base64ToImg.base64ToImg(imgStr.split(",")[1],"/FTZ/"+url.split("\\.")[0]+"_rec."+url.split("\\.")[1]);
		if(a)
			return "suceess";
		else
			return "fail";
		
    }  

}
