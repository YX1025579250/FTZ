<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;">
<link rel="stylesheet" type="text/css" href="Assets/css/reset.css" />
<link rel="stylesheet" type="text/css" href="Assets/css/thems.css">

</head>

<body>
	<%@ include file="head.jsp"%>
	<!-- <div class="banner banner_s"><img src="Assets/upload/banner_b.jpg" alt=""/></div>
 -->
	<!--幻灯片-->
	<!--主体盒子-->

	<div class="scd_bg">
		<div class="scd clearfix">
			<div class="s_head clearfix">
				<a id="pdf" href="javascript:void(0);" onclick="js_method_pdf() " class="">pdf识别</a> <a id="img" href="javascript:void(0);"   class="on">图片上传识别</a>
				<div class="pst">
					<a href="">首页</a> - <a href="">识别平台</a>
				</div>
			</div>
			<div class="scd_m clearfix">
				<div class="company">
					<dl class="clearfix">
						<iframe name="myiframe" id="myrame"
							src="${pageContext.request.contextPath }/recognize"
							frameborder="0" align="center" width="100%" height="640px"
							scrolling="yes">
							<!--  -->
							
							<p>你的浏览器不支持iframe标签</p>
						</iframe>


					</dl>

				</div>
			</div>
		</div>
	</div>

	<%@ include file="foot.jsp"%>

	<script type="text/javascript">
	     function js_method_pdf(){
	    	 var divp = document.getElementById('pdf');
	    	 //div.setAttribute("className", "on");
	    	 var divi = document.getElementById('img');
	    	 var iframeSrc = document.getElementById('myrame');
	    	 if(divp.innerText=="pdf识别")
	    	 {
	    		 
	    		 divp.innerText = "图片上传识别";
		    	 divi.innerText = "pdf识别";
		    	 
		    	 iframeSrc.src="${pageContext.request.contextPath }/pdfIndex";
		    	 
	    	 }else
    		 {
	    		 divp.innerText = "pdf识别";
		    	 divi.innerText = "图片上传识别";
		    	 iframeSrc.src="${pageContext.request.contextPath }/recognize";
    		 }
	     }
	</script>
	<script src="Assets/js/smoove.js"></script>
	<script type="text/javascript" src="Assets/js/jquery-1.7.2(1).js"></script>
	<!-- <script type="text/javascript" src="Assets/js/js_z.js"></script>
<!-- <script type="text/javascript">

</script> -->
</body>
</html>

