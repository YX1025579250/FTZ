<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
 
	<head>
		<meta charset="UTF-8">
		<title></title>
		<script src="Assets/js/jquery-3.4.1.min.js"></script>	
		 <link rel="stylesheet" href="Assets/css/bootstrap.min.css"/>
	    <link rel="stylesheet" href="Assets/css/flat-ui.css"/> 
	   <link rel="stylesheet" href="layui/css/layui.css"  media="all">
	    <script type="text/javascript" src="Assets/js/html2canvas.js"></script>
	    <script src="layui/layui.js" charset="utf-8"></script>
	   <!--  <script src="../bootstrap-3.3.4/dist/js/jquery-1.11.3.min.js"></script> 
	   <script src="Assets/js/bootstrap.min.js"></script>
	    <script src="Assets/js/flat-ui.min.js"></script>-->
	    
		<script src="layui/layui.js" charset="utf-8"></script>
	
		<!-- <script src="Assets/js/smoove.js"></script>
		<script type="text/javascript" src="Assets/js/js_z.js"></script>
		<script type="text/javascript" src="Assets/js/jquery-1.7.2(1).js"></script> -->
		<style type="text/css">
		<!--控制弹窗的css两个-->
			.black_overlay{ 
            display: none; 
            position: absolute; 
            top: 0%; 
            left: 0%; 
            width: 100%; 
            height: 100%; 
            background-color: black; 
            z-index:1001; 
            -moz-opacity: 0.8; 
            opacity:.80; 
            filter: alpha(opacity=88); 
        } 
        .white_content { 
            display: none; 
            position: absolute; 
            top: 10%; 
            left: 10%; 
            width: 80%; 
            height: 80%; 
            padding: 20px; 
            border: 5px solid #4F4F4F; 
            background-color: white; 
            z-index:1002; 
            overflow: auto; 
        } 
        
			 .row{
           margin-top: 20px;;
        }
        .center{
            text-align: center;
        }
        .pagination{
            background: #cccccc;
        }
		</style>
		
	</head>
 
	<body>
		<!-- <div id="bigPhoto">
			<img id="bigPhotoSrc" src="http://localhost:8080/FTZ/notFound.jpg" width="1080" height="768" border="0" 
			data-method="setTop"/>
		</div>
		<div id="smallPhotos"> 
		<span id="prve" onclick="preGroup()"></span>
			<ul id="smallPhotosList"></ul>
			<span id="next" onclick="nextGroup()"></span>
		</div>
		-->
		<!-- 点击识别详情弹窗的地方 -->
		
        <div id="light" class="white_content">
            <!--添加弹窗页面部分 -->
    		<div id="TanChuang" style="float:left ; width:50%; height:100%;">
    		<img src="" id="originalPhoto"/>
    		</div>
    		
    		<div id="TanChuangRec" style="float:left ; width:50%; height:100%;">
    		<img src="" id="recPhoto"/>
    		</div>
			
            <a href = "javascript:void(0)" onclick = "closeDialog()">点这里关闭本窗口</a>
        </div> 
        <div id="fade" class="black_overlay"></div> 
        
        
        
        
		<div class="container">
    
    <div class="row" id="photoshow">
        
    </div>
	<div id="fenye">
	</div>

    <nav class="center">
        <ul class="pagination  pagination-lg" id="pages">
            <li>
                <a href=" javascript:void(0);" onclick="preGroup();" aria-label="Previous">
                    <span aria-hidden="true">上一页</span>
                </a>
            </li>
            <li><a href=" javascript:void(0);" onclick="jumpToPage(this);">1</a></li>
            <li><a href=" javascript:void(0);" onclick="jumpToPage(this);">2</a></li>
          
            <li>
                <a href=" javascript:void(0);" onclick="nextGroup();"aria-label="Next">
                    <span aria-hidden="true">下一页</span>
                </a>
            </li>
        </ul>
    </nav>

</div>
			<!--<span>标签提供了一种将文本的一部分或者文档的一部分独立出来的方式。
			用于对文档中的行内元素进行组合。-->
			
		
<script type="text/javascript">
//layui分页
layui.use('laypage', function(){
  var laypage = layui.laypage;
  
  //执行一个laypage实例
  laypage.render({
    elem: 'fenye'    //注意，这里的 test1 是 ID，不用加 # 号
    ,count: datas.length //数据总数，从服务端得到
    ,limit:8
    ,layout: ['count', 'prev', 'page', 'next', 'limit', 'refresh', 'skip']
  });
});

function openDialog(obj){
	//alert(obj.id);
    document.getElementById('light').style.display='block';
    document.getElementById('fade').style.display='block';
 	document.getElementById('originalPhoto').src=obj.id;
    document.getElementById('recPhoto').src=obj.id.split(".")[0]+"_rec."+obj.id.split(".")[1];
   
}
function downloadPhotoAndRec()
{
	
	$.ajax({
        type:"POST",
        url:"${pageContext.request.contextPath }/download",
        data: {dat:json,dir:text,flagpdf:document.getElementById("flagpdf").value},
        success:function(data){
        	if (data='sucess'){download(text1, "dlText.txt", "text/plain");}
        	else{
        	download(text1, "dlText.txt", "text/plain");}
        }
    });
}

function closeDialog(){
    document.getElementById('light').style.display='none';
    document.getElementById('fade').style.display='none';
}
//$("aaa").click(function(){
//	alert("我点击图片了2");
//});


	var datas=[];//json数据
	var eg = {};
	eg.$ = function(id) {
		return document.getElementById(id);
	};
	eg.rootUrl = "/FTZ/";
	eg.indexValue=1;
	eg.groupSize = 8; //每组的数量
	eg.groupValue=1;
	function preGroup(){//往前一页
		if(eg.groupValue>1){
			eg.groupValue=parseInt(eg.groupValue)-parseInt("1")
			showThumb(eg.groupValue);
		}
		
	}

	function nextGroup(){//往后一页
		if(eg.groupValue<datas.length/eg.groupSize){
		//	alert(eg.groupValue);
			eg.groupValue=parseInt(eg.groupValue)+parseInt("1");
		//	alert(eg.groupValue);
			showThumb(eg.groupValue);
		}
	}

	function jumpToPage(obj)//跳转到某一页
	{
		//alert(obj.id);
		eg.groupValue=obj.id;
		showThumb(eg.groupValue);
		
	}
	function showThumb(group) {
			var ul = eg.$("photoshow");
			ul.innerHTML = ''; //每次显示时清空旧的内容
			var start = (group - 1) * eg.groupSize; //计算需要的data数据开始位置
			var end = group * eg.groupSize; //计算需要的data数据开始位置
			for(var i = start;
				(i < end && i <datas.length); i++) {
				//循环数据，并根据数据生成html后插入小图列表中
				/*
				str='<form method="get" action="${pageContext.request.contextPath }/uploadImgOfpdf"> <input type="hidden" value="'
						+datas[i].user+'"id="user" name="user" /><input type="hidden" value="'
			       		 +datas[i].bookurl+'"id="bookurl" name="bookurl" /> <input type="hidden" value="'
			       		 +datas[i].recpage+'"id="recpage" name="recpage" /><input type="hidden" value="'
			       		 +datas[i].bookname+'"id="bookname" name="bookname" />'
			       		 +' <div style="position:relative;width:450px;height:550px;left: 350px;" >'
			            	+'<input type="image"  style="width:450px;height:550px"  src="'+obj.src+'" οnclick="document.formName.submit()" id="topImg'
			            	+datas[i].user+datas[i].bookurl+datas[i].recpage+'"/>'
			            	+'<div style="position:absolute;width:450px;height:50px;color: #66CD00; z-indent:2;top:0;">'+"未识别"+'</div>'
			            	+'</form>';
				*/
			            	
				var strhtml='<div class="col-sm-4 col-md-3">'
	            +'<div class="thumbnail" >';
	           //   +' <a href="javascript:void(0);" >'
	           
	                  // +' <h3>第'+datas[i].record+'张照片</h3>'
	              if(datas[i].Recognized==0)
	            	  {
	            	  strhtml=strhtml 
	            	  +'<form method="get" action="${pageContext.request.contextPath }/uploadImg">'
	            	  +' <input type="hidden" value="'+datas[i].recordurl+'"id="recordurl" name="recordurl" />'
	            	  +' <input type="image" data-method="setTop" οnclick="document.formName.submit()" style="width: 100%; height: 200px; display: block;" alt="'
	            	  +(i+1)+'" src="' + eg.rootUrl + datas[i].recordurl + '" id="thumb_'+i+'"  >'
	            	  +'</form>'
	    	          //  +'  </a>'
		              +'  <div class="caption center">'
	            	  +' <h3>未识别</h3>' +' <h3>(请点击图片进行识别)</h3>' ;
	            	 // +' <p><span>(请点击图片进行识别)</span><span></span></p>'
	            	//  +' <p><span>(请点击图片进行识别)</span><span></span></p>'
	            	//  +' <p><span>(请点击图片进行识别)</span><span></span></p>';
	            	 // +' <p   style="  visibility: hidden;  "  ><span>(请点击图片进行识别)</span><span></span></p>';
	            	  }
	              else{
	            	  strhtml=strhtml
	            	  +' <img data-method="setTop" style="width: 100%; height: 200px; display: block;" alt="'
	            	  +(i+1)+'" src="' + eg.rootUrl + datas[i].recordurl + '" id="thumb_'+i+'"  >'
	    	          //  +'  </a>'
		              +'  <div class="caption center">'
	            	  +' <p><span>识别时间：</span><span>'+datas[i].datetime+'</span></p>'
	                   +' <p><span>识别字数：</span><span>'+datas[i].photoWordCount+'</span></p>'
	                  // +' <input type="text" class="btn btn-primary btn-block" οnclick="openDialog()" role="button" alt="'
		            	//  +(i+1)+'"  value="识别详情">'
		            	  
		            //	  +' <input type="text" class="btn btn-primary btn-block" οnclick="document.formName.submit()" role="button" style="margin-top:4px;" alt="'
		          //    +(i+1)+'"  value="下载">'
	      				//+'<a class="btn btn-primary btn-block" style="margin-top:4px;" role="button"  href="javascript:void(0);" οnclick="alert(1)" >test</a>'
	                  // +'<p ><a class="btn btn-primary btn-block" style="margin-top:4px;" role="button" href="javascript:void(0);" οnclick="aa()" >下载</a></p>';
	            	  +'<a class="btn btn-primary btn-block" style="margin-top:4px;" role="button" href="javascript:void(0);" onclick="openDialog(this)" id="'+eg.rootUrl+ datas[i].recordurl+'" >识别详情</a>'
	            	  +'<a class="btn btn-primary btn-block" style="margin-top:4px;" role="button" href="javascript:void(0);" onclick="downloadPhotoAndRec(this)" id="'+eg.rootUrl+ datas[i].recordurl+'" >下载</a>';
	            	 // +'<a class="btn btn-primary btn-block" style="margin-top:4px;" role="button" href="javascript:void(0);" οnclick="openDialog()" >识别详情</a>';
	              }
	                   
	              strhtml=strhtml
	                +'</div>'
	            +'</div>'
	        +'</div>';
	   
	        $("#photoshow").append(strhtml);
	       
			}
			
	};

	
	 function init() {
			showThumb(1); //初始化显示内容
			var countgroup=datas.length/eg.groupSize+1;    //获取当前有几组。
			//alert(2);
			var strpageth='';
			var ul = eg.$("pages");
			ul.innerHTML = ''; //每次显示时清空旧的内容
			if(countgroup<2)
			{
				//alert(countgroup);
				strpageth=  '<li><a href=" javascript:void(0);" onclick="jumpToPage(this);" id="1">1</a></li>' ;

			}else
			{
				//alert(countgroup);
				strpageth= '<li>'
					            +'<a href=" javascript:void(0);" onclick="preGroup();" aria-label="Previous">'
					             + '  <span aria-hidden="true">上一页</span>'
					            +'</a>'
					        +'</li>';
				for(var i=1;i<=countgroup;i++)
				{
					strpageth+='<li><a href=" javascript:void(0);" onclick="jumpToPage(this);" id="'+i+'">'+i+'</a></li>'
				}

				strpageth +=' <li>'
					            +'<a href=" javascript:void(0);" onclick="nextGroup();"aria-label="Next">'
					              +'  <span aria-hidden="true">下一页</span>'
					            +'</a>'
					        +'</li>';
			}
			$("#pages").append(strpageth);
			
		};
	$(document).ready(function () {
		//alert(0);
	    var datass=[];
	    var data = {};
	    data["user"] = "wu";
		data["recpage"] = "wu";
		data["bookname"] = "wu";
		data["recflag"] = "wu";
		datass.push(data);
		//alert(1);
		var jsonString = JSON.stringify(datass);
		//var str='<div style="height:1000px;width:100%"><div style="height: 60px ;width: 100%;text-align:left;line-height:60px"><font size="4"   color="#000000"  ><font style="font-weight: bold"  ></font></font></div><img  style="height:940px;width:100%" src="'+'E:/FTZ/123456/PDF/李禄马-大学成绩单/李禄马-大学成绩单_1.png" id="topImg"/></div>'
	    //  $("#insert").append(str);
		$.ajax({
	            url: "${pageContext.request.contextPath }/personalPhotoListReadedJSON",
	            type: "POST",
	            dataType: 'json',
	            data: jsonString,
	            success: function (msg) {
	            	
	            	for(var i=0;i<msg.length;i++)
	           		{
	            	//	alert(2);
	            		//ob.put("record", photorecord.get(i).getRecord());
				    //	ob.put("userID", photorecord.get(i).getUserId());
				    //	ob.put("datetime", photorecord.get(i).getDateTime());
				    	//ob.put("recordurl", photorecord.get(i).getRecordUrl());
	            		var data = {};
	            	 //   data["record"] = msg[i].record;
	            		data["datetime"] = msg[i].datetime;
	            		data["photoWordCount"] = msg[i].photoWordCount;
	            		data["recordurl"] = msg[i].recordurl;
	            		data["Recognized"] = msg[i].Recognized;
	            		datas.push(data);
	            	//	alert(datas[i]);
	           		}
	            	
	            	init();
	            }
	        });



	});


</script>
		<script>
		layui.use('layer', function(){ //独立版的layer无需执行这一句
		  var $ = layui.jquery, layer = layui.layer; //独立版的layer无需执行这一句
		  
		  //触发事件
		  var active = {
			setTop: function(){
				var httpRequest = new XMLHttpRequest();//第一步：建立所需的对象
				var photoSrc = this.src;
				var url = "http://localhost:8080/ssmAndDl4j/selectRecognized?photoSrc=" + photoSrc;
		        httpRequest.open('GET', url, true);//第二步：打开连接  将请求参数写在url中  ps:"./Ptest.php?name=test&nameone=testone"
		        httpRequest.send();//第三步：发送请求  将请求参数写在URL中
		        /**
		         * 获取数据后的处理程序
		         */
		        httpRequest.onreadystatechange = function () {
		            if (httpRequest.readyState == 4 && httpRequest.status == 200) {
		                var json = httpRequest.responseText;//获取到json字符串，还需解析
		                //console.log(json);
		                if(json=="1"){
			  			  	//多窗口模式，层叠置顶
			  			  	layer.open({
				  				type: 2 //此处以iframe举例
				  				,title: '图片识别详情'
				  				,area: ['1215px', '700px']
				  				,shade: 0
				  				,maxmin: true
				  				,offset: 'auto' 
				  				,content: 'http://localhost:8080/ssmAndDl4j/recognizeDetail2?photoSrc=' + photoSrc
				  				,btn: ['全部关闭'] //只是为了演示
				  				,yes: function(){
				  					layer.closeAll();
				  				}
				  				,zIndex: layer.zIndex //重点1
				  				,success: function(layero){
				  				  layer.setTop(layero); //重点2
				  				}
				  			});
		                } else {
		                	layer.open({
		                		title: '图片识别提示'
		                		,content:'该图片未识别下载，是否现在进行识别？'
		                		,btn:['是','否']
		                		,yes:function(){
		                			window.location.href="http://localhost:8080/ssmAndDl4j/afterwardRecognized?photoSrc="
		                					+ photoSrc + "&indexValue=" + eg.indexValue + "&fromList=true";
		                		}
		                		,btn2:function(){
		                			
		                		}
		                		,cancel:function(){
		                			
		                		}
		                	});
		                }
		            }
		        };
			}
		  };
		  $('#thumb_1').on('click', function(){
			  
			  alert( $(this).alt);
			  eg.indexValue= $(this).alt;
				var othis = $(this), method = othis.data('method');
				active[method] ? active[method].call(this, othis) : '';
			  });
		  
		  // function aaa(obj){
			//  alert(obj.alt);
			//  eg.indexValue=obj.alt;
		//	var othis = $(this), method = othis.data('method');
		//	active[method] ? active[method].call(this, othis) : '';
		 // }
		  
		});
		</script>
	</body>
 
</html>