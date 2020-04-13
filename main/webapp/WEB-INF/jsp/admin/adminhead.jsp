<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>繁体字识别平台</title>
<link rel="stylesheet" type="text/css" href="Assets/css/reset.css" />
<script type="text/javascript" src="Assets/js/jquery-1.8.3.min.js"></script>
<script type="text/javascript"
	src="Assets/plugins/FlexSlider/jquery.flexslider.js"></script>
<link rel="stylesheet" type="text/css"
	href="Assets/plugins/FlexSlider/flexslider.css">
<script type="text/javascript" src="Assets/js/js_z.js"></script>
<link rel="stylesheet" type="text/css" href="Assets/css/thems.css">
<script language="javascript">
$(function() {
  $('.flexslider').flexslider({
	animation: "slide"
  });
});
</script>
</head>

<body>
	<div class="h_bg">
		<div class="head clearfix">
			<div class="logo">
				<font size="5" color="white">繁体字智能管理平台</font>
			</div>
			<div class="head_r">
				<div class="nav clearfix">
					<a
						href="${pageContext.request.contextPath }/Index" target="_top">识别平台</a>
					<a href="${pageContext.request.contextPath }/adminlogin" target="_top">管理平台</a>
					</div>
			</div>
		</div>
	</div>
	<script>
check();
function check(){
var ss='${user.phonenumber }';
if(ss=='')
	{
	document.getElementById('login0').style.display='block';
	document.getElementById('login1').style.display='none';
	document.getElementById('login2').style.display='none';
	}
else{
	document.getElementById('login0').style.display='none';
	document.getElementById('login1').style.display='block';
	document.getElementById('login2').style.display='block';
}
}
</script>
</body>

</html>
