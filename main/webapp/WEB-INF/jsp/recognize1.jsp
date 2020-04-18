<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Title</title>
<link href="Assets/css/bootstrap.css" type="text/css" rel="stylesheet">
<link href="Assets/css/layui.css" type="text/css" rel="stylesheet">
<link href="Assets/css/index.css" type="text/css" rel="stylesheet">
<link href="Assets/css/loaders.css" type="text/css" rel="stylesheet">
<link href="Assets/css/lanrenzhijia.css" type="text/css"
	rel="stylesheet">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
</head>
<body>
	<div class="main clearfix">
		<header class="header">
			<p>定位</p>
		</header>
        <input type="hidden" value="${requestScope.flag}"
			id="flagpdf" name="flagpdf" />
		<input type="hidden" value="${requestScope.filename}"
			id="ChangePhotoName" name="photoname" />

		<div class="col-xs-6 maxHeight" style="background-color: #f2f7f2">
			<div class="center_img_div" overflow-y=‘hidden’>
				<div class="center_img" overflow-y=‘hidden’>
					<img src="Assets/images/p.jpg" draggable="false" class="c_img"
						id="topImg" rotate="0" style="height: 100%;">
					<div class="biaoDiv" id='biaodiv' overflow-y=‘hidden’></div>
				</div>
				<div class="col-xs-12" id="saveBtn">
					<div style="float: right; margin-right: 10px;">
						<div class="btn_base" onclick="save()"
							style="width: 100px; height: 35px; line-height: 35px;">识别</div>
					</div>
				</div>

			</div>
		</div>
		<div class="col-xs-6 maxHeight" style="background-color: #f2f7f2;">
			<div class="center_img_div">
				<div class="center_img">
					<canvas id="c2" width="600" height="580"
						style="margin: 0 auto; display: block"></canvas>
				</div>
				<div class="col-xs-12" id="saveBtn">
					<div style="float: right; margin-right: 10px;">
						<div class="btn_base" id="btn"
							style="width: 100px; height: 35px; line-height: 35px;">下载</div>
					</div>
				</div>
			</div>
		</div>
		<div class="col-xs-3 maxHeight" id="rightScroll"
			style="padding-left: 0; display: none">
			<div class="col-xs-12">
				<table class="table">
					<thead>
						<tr>
							<th>宽</th>
							<th>高</th>
							<th>X轴</th>
							<th>Y轴</th>
						</tr>
					</thead>
					<tbody id="rightContentbody">
					</tbody>
				</table>
				<!--分页-->
			</div>
		</div>
	</div>

	<script src="Assets/js/jquery-3.0.0.min.js"></script>
	<script src="Assets/js/layui.js"></script>
	<script src="Assets/js/download.js"></script>
	<script>
/* drawRc() ;  */
setTimeout("drawRc()",500);
changeSrc(); 
    var jsonlist;
    var showImgHeight = 560,
        xli = null,//图片宽缩放比例
        yli = null;//图片高缩放比例
    var layer;
    $(document).ready(function () {
        var img = new Image()
        img.src = $('#topImg').attr('src')
        img.onload = function () {
            //xli = deletePx($('#topImg').css('width')) / img.width
            xli = deletePx($('#topImg').css('width')) / img.width
            yli = deletePx($('#topImg').css('height')) / img.height
            console.log(deletePx($('#biaodiv').css('width')), img.width)
            console.log(deletePx($('#biaodiv').css('height')), img.height)
            console.log("图片加载完成",xli,yli)
            console.log("代码成功运行")
        }
        layui.use('layer', function () {
            layer = layui.layer;
        })
    })
    /**
     * 监测浏览器屏幕高度
     * */
    function getMaxHeight() {
        var height = $(window).outerHeight() - $('.header').outerHeight();
        $('.maxHeight').css("height", height + "px");
        showImgHeight = height - $('.center_img_div').find('.col-xs-12').outerHeight() - $('#saveBtn').outerHeight() - 30;
    }
    getMaxHeight();
    $(window).resize(function () {
        getMaxHeight()
    })
    /**
     * ajax封装
     * */
    function http(obj) {
        $('.loader').show();
        $.ajax({
            url: obj.url,
            type: obj.type,
            dataType: 'json',
            data: obj.data,
            success: function (result) {
                $('.loader').hide();
                obj.success(result);
                if (result.status == -1) {
                    layui.use('layer', function () {
                        layer = layui.layer;
                        layer.msg(result.msg);
                    })
                }
            },
            error: function (result) {
                $('.loader').hide();
                obj.error(result);
                layui.use('layer', function () {
                    layer = layui.layer;
                    layer.msg(result.msg);
                })
            }
        })
    }
    /**
     * 鼠标点击位置记录，拖动画框，离开结束
     * isStartPaint: 点击开始画
     * isFirstMove：区分框是否存在，不存在添加，存在就改变宽度高度。
     * rectangleArr：所有框id数组
     * rectangleId：当前画框id
     * curRectangle：点击当前框id
     * x:当前框x坐标
     * y:当前框y坐标
     */
    var isStartPaint = false,
        isFirstMove = true;
    var rectangleArr = [];
    var rectangleId = 0;
    var x = 0, y = 0;
    var curRectangle = 0;
    /**
     * 绑定鼠标画矩形事件
     */
    function drawRc() {
 	    var keyList = eval('${arrayList}');
 	
 	    for (var i = 0; i <keyList.length; i++)
 	    {  list1=keyList[i];
 	        x1=list1[0]*xli;
 	        x2=list1[1]*yli;
 	        x3=(list1[2]-list1[0])*xli;
 	        x4=(list1[3]-list1[1])*yli;
 	        if (rectangleArr.length == 0) {
               rectangleArr.push(0)
           } else {
               rectangleArr.push((rectangleArr[rectangleArr.length - 1] + 1));
           }
            rectangleId = rectangleArr[rectangleArr.length - 1]
 	        console.log(list1) ;
 	        console.log(x1,x2,x3,x4) ;
 	        var rectangleView = "<div id='" + rectangleId + "' class='line' x='" + x1 + "' y='" + x2 + "' style='left: " + x1 + "px;top:" + x2 + "px;width:" + x3 + "px;height:" + x4 + "px'><div class='move_box'></div><img draggable='false' class='change_size'><div class='glyphicon glyphicon-remove delete_line'></div></div>";
 	        $('.biaoDiv').append(rectangleView);
 	        //console.log(rectangleId + "first")
 	        $("#rightContentbody").append('<tr id="' + rectangleId + '"><td>' + (x3 ).toFixed(2) + '</td><td>' + (x4 ).toFixed(2) + '</td><td>' + (x1 ).toFixed(2) + '</td><td>' + (x2 ).toFixed(2) + '</td></tr>')
    } 
    }
    function painting() {
        //在绑定之前先解绑，解决了事件多次触发的问题。
        $(document).off('mousedown', '.biaoDiv').on('mousedown', '.biaoDiv', function (event) {
            x = event.pageX - $('#topImg').offset().left
            y = event.pageY - $('#topImg').offset().top
            x = x.toFixed(2)
            y = y.toFixed(2)
            isStartPaint = true;
            if (rectangleArr.length == 0) {
                rectangleArr.push(0)
            } else {
                rectangleArr.push((rectangleArr[rectangleArr.length - 1] + 1));
            }
            rectangleId = rectangleArr[rectangleArr.length - 1]
            console.log(rectangleId)
        })
        $(document).off('mousemove', '.biaoDiv').on('mousemove', '.biaoDiv', function (event) {
            if (isStartPaint) {
                if (isFirstMove) {
                    var rectangleView = "<div id='" + rectangleId + "' class='line' x='" + x + "' y='" + y + "' style='left: " + x + "px;top:" + y + "px'><div class='move_box'></div><img draggable='false' class='change_size'><div class='glyphicon glyphicon-remove delete_line'></div></div>";
                    $('.biaoDiv').append(rectangleView);
                    isFirstMove = false;
                    console.log(rectangleId + "first")
                    $("#rightContentbody").append('<tr id="' + rectangleId + '"><td></td><td></td><td>' + (x * xli).toFixed(2) + '</td><td>' + (y * yli).toFixed(2) + '</td></tr>')
                    $('#rightContentbody tr').each(function () {
                        $(this).click(function () {
                            $(this).siblings().removeClass('table_selected')
                            $(this).addClass('table_selected');
                            var id = $(this).attr('id');
                            $('.biaoDiv .line').removeClass('line_select');
                            $('.biaoDiv #' + id).addClass('line_select');
                            //滚动效果
                            var scrollTop = $('.center_img').scrollTop();
                            var dotop = $('.biaoDiv #' + id).offset().top;
                            hei = $('.center_img').offset().top;
                            $('.center_img').animate({
                                scrollTop: scrollTop + dotop - hei - 50
                            }, 500);
                        })
                    })
                }
                var width = event.pageX - $('#topImg').offset().left - x
                var height = event.pageY - $('#topImg').offset().top - y
                $('#rightContentbody #' + rectangleId + " td").eq(0).html((width * xli).toFixed(2))
                $('#rightContentbody #' + rectangleId + " td").eq(1).html((height * yli).toFixed(2))
                width = width.toFixed(2)
                height = height.toFixed(2)
                $('#' + rectangleId).css({"width": width + 'px', "height": height + 'px'});
                $('#' + rectangleId).attr("w", width)
                $('#' + rectangleId).attr("h", height)
            }
        })
        $(document).off('mouseup', '.biaoDiv').on('mouseup', '.biaoDiv', function (event) {
//            rectangleId = rectangleId + 1;
            isStartPaint = false;
            isFirstMove = true;
            console.log("图片-----up")
            console.log("here！！！")
        })
    }
    /**
     * 解除鼠标画矩形事件
     */
    function offPainting() {
        isStartPaint = false;
        isFirstMove = true;
        x = 0;
        y = 0;
        $(document).off('mousedown', '.biaoDiv')
        $(document).off('mousemove', '.biaoDiv')
        $(document).off('mouseup', '.biaoDiv')
    }

    $(document).on('mouseenter', '.biaoDiv', function () {
//        console.log("图片---enter")
        painting()
    })
    $(document).on('click', '.biaoDiv .line .delete_line', function () {
        var id = $(this).parent().attr('id')
        id = parseInt(id)
        console.log("line---delete")
        rectangleArr.splice($.inArray(id, rectangleArr), 1)
        $('#rightContentbody #' + id).remove()
        console.log(rectangleArr)
        $(this).parent().remove()
    })
    $(document).on('mouseenter', '.biaoDiv .line .change_size', function (event) {
        event.stopPropagation()
//        console.log("size---change")
    })
    $(document).on('mousedown', '.biaoDiv .line .change_size', function (event) {
        event.stopPropagation()
        console.log("size---down")
        x = $(this).parent().attr('x')
        y = $(this).parent().attr('y')
        var id = $(this).parent().attr('id')
        changeSize(id)
    })
    $(document).on('mouseup', '.biaoDiv .line .change_size', function (event) {
//        console.log("size---leave")
        offPainting()
    })
    $(document).on('mouseenter', '.biaoDiv .line', function () {
//        console.log("line---enter")
        curRectangle = $(this).attr('id')
        $(this).find('.change_size').css("display", "block");
        $(this).siblings().find('.change_size').css("display", "none");
        $(this).find('.delete_line').css("display", "block");
        $(this).siblings().find('.delete_line').css("display", "none");
    })
    $(document).on('mousedown', '.biaoDiv .line', function (event) {
        //阻止子元素向父元素传递这个事件
        event.stopPropagation()
//        console.log("line---down")
        $(this).addClass('line_select')
        $(this).siblings().removeClass('line_select')
        curRectangle = $(this).attr('id')
        $('#rightContentbody tr').removeClass('table_selected')
        $('#rightContentbody #' + curRectangle).addClass('table_selected')
        //滚动效果
        var scrollTop = $('#rightScroll').scrollTop();
        var dotop = $('#rightContentbody #' + curRectangle).offset().top;
        hei = $('#rightScroll').offset().top;
        $('#rightScroll').animate({
            scrollTop: scrollTop + dotop - hei - 50
        }, 500);
        offPainting()
    })
    $(document).on('mouseleave', '.biaoDiv .line', function () {
//        console.log("line---leave")
        $(this).find('.change_size').css("display", "none");
        $(this).siblings().find('.change_size').css("display", "none");
        $(this).find('.delete_line').css("display", "none");
        $(this).siblings().find('.delete_line').css("display", "none");
        painting()
    })
    /**
     * 快捷键控制移动
     * */
    $(document).keydown(function (event) {
        var keyNum = event.which
        var item = $('#' + curRectangle)
        switch (keyNum) {
            case 65:
                item.animate({left: '-=2'}, 0)
                refreshTableData(curRectangle)
                break;
            case 87:
                $('.center_img').css('overflow', 'hidden')
                item.animate({top: '-=2'}, 0)
                refreshTableData(curRectangle)
                break;
            case 68:
                item.animate({left: '+=2'}, 0)
                refreshTableData(curRectangle)
                break;
            case 83:
                $('.center_img').css('overflow', 'hidden')
                item.animate({top: '+=2'}, 0)
                refreshTableData(curRectangle)
                break;
            default: {
                break;
            }
        }
        $('.center_img').css('overflow', 'auto')
    })
    $(document).off('mousedown', '.biaoDiv .line .move_box').on('mousedown', '.biaoDiv .line .move_box', function (event) {
    event.stopPropagation()
    $(this).parent().addClass('line_select')
    $(this).parent().siblings().removeClass('line_select')
    // curRectangle = $(this).attr('id')
    $('#rightContentbody tr').removeClass('table_selected')
    $('#rightContentbody #' + curRectangle).addClass('table_selected')
    
    offPainting()
    event.stopPropagation()
    console.log("move---down")
    var x = $(this).parent().attr('x')
    var y = $(this).parent().attr('y')
    console.log('x:', x, 'y:', y) ;
    var id = $(this).parent().attr('id')
    var width = event.pageX - $('#topImg').offset().left - x
    var height = event.pageY - $('#topImg').offset().top - y
    console.log('width:', width, 'height:', height) ;
    isSecondMove = true ;
    moveDiv(id, width, height) ;
})

// 拖动框

function moveDiv(id, width, height){
    $(document).off('mousemove', '.biaoDiv').on('mousemove', '.biaoDiv', function (event) {
        if (isSecondMove){
            var x = event.pageX - $('#topImg').offset().left
            var y = event.pageY - $('#topImg').offset().top
            x = x - width
            y = y - height
            x = x.toFixed(2)
            y = y.toFixed(2)
            $('.biaoDiv #' + id).css({"left": x + 'px', "top": y + "px"});
            $('.biaoDiv #' + id).attr("x", x) ;
            $('.biaoDiv #' + id).attr("y", y) ;
            refreshTableData(id)
            }
    })
    $(document).off('mouseup', '.biaoDiv').on('mouseup', '.biaoDiv', function (event) {
        isSecondMove = false ;
        offPainting()
    })
}
    /**
     * 改变大小
     */
    function changeSize(id) {
        $(document).off('mousemove', '.biaoDiv').on('mousemove', '.biaoDiv', function (event) {
            var width = event.pageX - $('#topImg').offset().left - x
            var height = event.pageY - $('#topImg').offset().top - y
            width = width.toFixed(2)
            height = height.toFixed(2)
            $('.biaoDiv #' + id).css({"width": width + 'px', "height": height + 'px'});
            refreshTableData(id)
        })
        $(document).off('mouseup', '.biaoDiv').on('mouseup', '.biaoDiv', function (event) {
            isStartPaint = false;
            isFirstMove = true;
            console.log("图片size-----up")
        })
    }
    /**
     * 刷新右侧框数据
     * */
    function refreshTableData(id) {
        var td0 = $('#rightContentbody #' + id + " td").eq(0)
        var td1 = $('#rightContentbody #' + id + " td").eq(1)
        var td2 = $('#rightContentbody #' + id + " td").eq(2)
        var td3 = $('#rightContentbody #' + id + " td").eq(3)
        var rec_x = deletePx($('.biaoDiv #' + id).css('left'))
        var rec_y = deletePx($('.biaoDiv #' + id).css('top'))
        var rec_w = deletePx($('.biaoDiv #' + id).css('width'))
        var rec_h = deletePx($('.biaoDiv #' + id).css('height'))
        rec_x = (rec_x * xli).toFixed(2)
        rec_y = (rec_y * yli).toFixed(2)
        rec_w = (rec_w * xli).toFixed(2)
        rec_h = (rec_h * yli).toFixed(2)
        td0.html(rec_w)
        td1.html(rec_h)
        td2.html(rec_x)
        td3.html(rec_y)
    }
    /**
     * 删除px
     * */
    function deletePx(str) {
        var ret = 0;
        if (typeof (str) == 'undefined' || str == null || str.length == 0) {
            ret = 0;
        } else {
            ret = str.substring(0, str.indexOf('px'))
        }
        return ret;
    }
    /**
     * 保存数据
     * */
    function save() {
        var json = [];
        $('#rightContentbody tr').each(function (index) {
            var w = $(this).children('td').eq(0).html()
            var h = $(this).children('td').eq(1).html()
            var x = $(this).children('td').eq(2).html()
            var y = $(this).children('td').eq(3).html()
            var obj = {}
            obj.number = index
            obj.x = x
            obj.y = y
            obj.width = w
            obj.height = h
            json.push(obj)
        })
        if (json.length == 0) {
            alert("数据为空不能保存")
            return
        }
        var obj = {}
        obj.number = 1000
        obj.xli = xli
        obj.yli = yli
        obj.src = $('#topImg').attr('src')
        json.push(obj)
        json = JSON.stringify(json) 
        $.ajax({
            type:"POST",
            url:"${pageContext.request.contextPath }/recognize2",
            data: {ds:json},
           
            success:function(json){
            	var c2 = document.getElementById("c2");
                var context2 = c2.getContext("2d");
                alert("识别完成");
                context2.clearRect(0,0,600,580)
                context2.font = ("25px" + " Arial");		
                for (var k=0;k<json.length;k++)
                context2.fillText(json[k].word,json[k].x*xli,(json[k].y)*yli);
                jsonlist=json;
            }
        });
    }
window.onload=function(){
    	var c2 = document.getElementById("c2");
    c2.onclick =function (evt){
  	     var x = evt.pageX - $('#c2').offset().left;
         var y = evt.pageY - $('#c2').offset().top;
         var context2 = c2.getContext("2d");
         context2.font = ("25px" + " Arial");
         for (var t = 0; t < jsonlist.length; t++) {
             if (x <((jsonlist[t].x+jsonlist[t].width)*xli) && x >(jsonlist[t].x * xli) && y < ((jsonlist[t].y+jsonlist[t].height)* yli) && y >(jsonlist[t].y *yli)) 
             {   var change;
                 change = prompt("您正在修改" + jsonlist[t].word);
                 if (change != null)
              	   jsonlist[t].word = change;
                 context2.clearRect((jsonlist[t].x * xli) ,(jsonlist[t].y * yli) ,jsonlist[t].width * xli, jsonlist[t].height * yli);
                 context2.fillText(jsonlist[t].word,jsonlist[t].x * xli,json[k].y * yli);
             }
         }
      }
  }   
    function changeSrc() {
        document.getElementById("topImg").src ="/FTZ/"+document.getElementById("ChangePhotoName").value;
    } 
    var btn = document.getElementById("btn");
    btn.onclick = function ()  {
    	var json=[]
    	json=jsonlist
    	json = JSON.stringify(json) 
		var text1 = '';
		for (var t = 0; t < jsonlist.length; t++) {
			
				text1 = text1  + jsonlist[t].word+" ";
		}
		text=document.getElementById("ChangePhotoName").value
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
    
   
 
/*     */

</script>
</body>
</html>