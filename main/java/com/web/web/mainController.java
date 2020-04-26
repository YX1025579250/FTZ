package com.web.web;

import java.io.File;
import com.web.tool.url;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
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
import com.web.entity.PhotoRecord;
import com.web.entity.PhotoWord;
import com.web.entity.Users;
import com.web.entity.book;
import com.web.entity.bookRecord;
import com.web.entity.bookword;
import com.web.entity.word;
import com.web.interceptor.Filemove;
import com.web.interceptor.SAXWrite;
import com.web.service.UserService;
import com.web.service.photorecordService;
import com.web.service.bookService;
import com.web.service.bookRecordService;
import com.web.service.bookwordService;
import com.web.service.photowordService;
import com.web.service.wordService;
import com.web.util.CutBigImage;
import com.web.util.ImageSplit;
import com.web.util.ScreenShot;
import com.web.util.posepoint;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class mainController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private photorecordService photorecordService;
	@Autowired
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
		return "recognize1";
	}
	//查看图片时对未识别的图片进行识别
	@RequestMapping("/afterwardRecognized")
	public String afterwardRecognized(HttpServletRequest request, Model model) throws IOException {
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
		if("photoList".equals(fromList)){//请求来自photoList,置flag为0
			model.addAttribute("flag",0);
		} else if("pdfPhotoList".equals(fromList)){
			model.addAttribute("flag",1);//请求来自pdfPhotoList,置flag为1,传递bookId参数
			model.addAttribute("bookId",request.getParameter("bookId"));
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
		user.setMoneyWait(0.0);
		user.setBook(0);
		user.setPhoto(0);
		user.setMoney(0.0);
		boolean flag = userService.saveUser(user);
		if (flag) {
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
		String flagpdf = request.getParameter("flagpdf");
		String dir = request.getParameter("dir");
		JSONArray json = JSONArray.fromObject(ds);
		SAXWrite.savexml(json,dir);
		if ("1".equals(flagpdf))
		{	
			//System.out.println(dir);
			//输出类似“123472//PDF//gushi20200421153307940//gushi_1.png”时，后面代码执行正常
			//输出类似“123472//PDF//gushi20200421153307940/gushi_1.png”时，后面代码报空指针异常
			//因此改用以下代码来统一格式
			dir = dir.replaceAll("//", "/");
			char page = dir.charAt(dir.lastIndexOf(".")-1);
			String bookdir = dir.substring(0,dir.lastIndexOf("/"));
			bookdir = bookdir.replaceAll("/", "//");
			//char page=dir.split("\\.")[0].charAt(dir.split("\\.")[0].length()-1);
			//String bookdir=new String(dir.split("//")[0]+"//"+dir.split("//")[1]+"//"+dir.split("//")[2]);
			
			book book=bookService.selectByurl(bookdir);
			
            bookRecord bookrecord=bookRecordService.bookRecordbyidandpage(book.getBookid(),Integer.valueOf(String.valueOf(page)));
            Date d=new Date();
            bookrecord.setRecbookdatetime(d);
            bookrecord.setRecflag(1);
            bookRecordService.UpdataBookRecord(bookrecord);
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
			 System.out.println(json);
			JSONObject jsonOne;
			 for(int i=0;i<json.size()-1;i++){
				
				 jsonOne = json.getJSONObject(i); 
				 System.out.println(jsonOne);
				 System.out.println(String.valueOf(jsonOne.get("word")));
				 word selectword_id = wordService.selectword_id(String.valueOf(jsonOne.get("word")));
				 System.out.println(selectword_id);
				 PhotoWord PhotoWord=new PhotoWord();
				 PhotoWord.setWordId(selectword_id.getWordId());
				 PhotoWord.setPhotoRecord(photorecord.getRecord());
				 String wordUrl=new Filemove().filesave((String.valueOf(jsonOne.get("dir"))), String.valueOf(jsonOne.get("word")),userId);
				 PhotoWord.setWordUrl(wordUrl);
				 photowordService.savePhotoWord(PhotoWord);
			 }
		}
		return "sucess";
	}
}

