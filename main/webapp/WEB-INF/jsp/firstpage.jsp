<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>繁体字智能识别平台</title>
<link rel="stylesheet" type="text/css" href="Assets/css/reset.css" />
<script type="text/javascript" src="Assets/js/jquery-3.0.0.min.js"></script>
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
  <style type="text/css">
      /*   
        .main{
            width: 100%;
            height:100%;
            position: absolute;
            background-color:		#FFFFE0;
        } */
        .quarter-div{
            width: 49%;
            height: 300px;
            float: left;
            margin-left:10px;
            
        }
        .green{
            background-color:		#FFFFE0;
            margin-top:40px;
        }
        .blue{
            background-color:		#FFFFE0;
            margin-top:10px;
        }
    </style>
</head>

<body>
	<!--幻灯片-->
	<div class="banner">
		<div class="slider">
			<div class="flexslider">
				<ul class="slides">
					<li><a href=""><img src="Assets/upload/banner_a.png"
							alt="古代文献扫一扫\获取简体字" /></a>
							</li>
					<li><a href=""><img src="Assets/upload/banner_b.png"
							alt="" /></a>
							</li>
					<li><a href=""><img src="Assets/upload/banner_c.png"
							alt="" /></a>
							</li>
				</ul>
			</div>
		</div>
	</div>
	<!--幻灯片-->
	<!--主体盒子-->
		 
	 <div class="banner">
		<div class="main">
		 	<div class="quarter-div green"> 
		       <img src="Assets/images/banne1.png">
			</div>
			<div class="quarter-div green"> 
		       <img src="Assets/images/banne2.png" >
			</div>	 
	    </div>
	</div>
    <div class="banner">
		<div class="main">
			<div class="quarter-div blue"> 
	       		<img src="Assets/images/banne3.png">
			</div>	
			<div class="quarter-div blue"> 
	       		<img src="Assets/images/banne4.png">
			</div>	 
    	</div>
	</div>


</body>
</html>
