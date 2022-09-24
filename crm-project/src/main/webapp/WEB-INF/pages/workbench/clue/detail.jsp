<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String basePath=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<html>
<head>
	<base href="<%=basePath%>">
	<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

<script type="text/javascript">

	//默认情况下取消和保存按钮是隐藏的
	var cancelAndSaveBtnDefault = true;
	
	$(function(){
		$("#remark").focus(function(){
			if(cancelAndSaveBtnDefault){
				//设置remarkDiv的高度为130px
				$("#remarkDiv").css("height","130px");
				//显示
				$("#cancelAndSaveBtn").show("2000");
				cancelAndSaveBtnDefault = false;
			}
		});
		
		$("#cancelBtn").click(function(){
			//显示
			$("#cancelAndSaveBtn").hide();
			//设置remarkDiv的高度为130px
			$("#remarkDiv").css("height","90px");
			cancelAndSaveBtnDefault = true;
		});
		
		$(".remarkDiv").mouseover(function(){
			$(this).children("div").children("div").show();
		});
		
		$(".remarkDiv").mouseout(function(){
			$(this).children("div").children("div").hide();
		});
		
		$(".myHref").mouseover(function(){
			$(this).children("span").css("color","red");
		});
		
		$(".myHref").mouseout(function(){
			$(this).children("span").css("color","#E6E6E6");
		});

		// 给关联市场活动的按钮添加单击事件
		$("#bundActivityBtn").click(function () {
			// 初始化工作,查询所有的市场活动
			// 清空搜索框,和tbody
			$("#serchActivityT").val("");
			$("#bundActivityTbody").html("");
			// 弹出模态窗口
			$("#bundModal").modal("show");
		});

		// 给市场活动搜索框添加键盘弹起事件
		$("#serchActivityT").keyup(function () {
			// 获取参数
			var activityName = $("#serchActivityT").val();
			var clueId = '${requestScope.clue.id}';
			// 发送请求
			$.ajax({
				url: 'workbench/clue/queryActivityForDetailByNameAndClueId.do',
				data: {
					activityName: activityName,
					clueId: clueId
				},
				type: 'post',
				dataType: 'json',
				success: function (data) {
					// 遍历，显示搜索到的市场活动
					var htmlStr = "";
					$.each(data,function (i,obj) {
						htmlStr+="<tr>";
						htmlStr+="<td><input value=\""+obj.id+"\" type=\"checkbox\"/></td>";
						htmlStr+="<td>"+obj.name+"</td>";
						htmlStr+="<td>"+obj.startDate+"</td>";
						htmlStr+="<td>"+obj.endDate+"</td>";
						htmlStr+="<td>"+obj.owner+"</td>";
						htmlStr+="</tr>";
					});
					$("#bundActivityTbody").html(htmlStr);
				}
			})
		});

		// 给关联按钮添加单击事件
		$("#saveBundBtn").click(function () {
			// 收集参数，获取列表中所有被选中的checkbox
			var checkId = $("#bundActivityTbody input[type='checkbox']:checked");
			if (checkId.size() == 0){
				alert("请选择要关联的市场活动");
				return;
			};
			// 获取每一个checkbox 的id
			var ids="";
			$.each(checkId,function (i,obj) {
				ids+="activityId="+this.value+"&";
			})
			ids+="clueId=${clue.id}";
			// 发送请求
			$.ajax({
				url: "workbench/clue/saveClueActivityBund.do",
				data: ids,
				type: "post",
				dataType: "json",
				success: function (data) {
					if (data.code=="1"){
						// 关闭模态窗口
						$("#bundModal").modal("hide");
						// 刷新关联市场活动列表
						var htmlStr="";
						$.each(data.retDate,function (i,obj) {
							htmlStr+="<tr id='tr_"+obj.id+"'>";
							htmlStr+="<td>"+obj.name+"</td>";
							htmlStr+="<td>"+obj.startDate+"</td>";
							htmlStr+="<td>"+obj.endDate+"</td>";
							htmlStr+="<td>"+obj.owner+"</td>";
							htmlStr+="<td><a activityId=\""+obj.id+"\" href=\"javascript:void(0);\"  style=\"text-decoration: none;\"><span class=\"glyphicon glyphicon-remove\"></span>解除关联</a></td>";
							htmlStr+="</tr>";
						});
						$("#activityTbody").append(htmlStr);
					}else {
						alert(data.message);
						$("#bundModal").modal("show");
					}
				}
			});
		});

		// 给所有的解除关联按钮添加单击事件
		$("#activityTbody").on("click","a",function () {
			// 收集参数
			var activityId=$(this).attr("activityId");
			var clueId = "${clue.id}";
			// 发送请求
			if (window.confirm("确定解除吗?")){
				$.ajax({
					url: "workbench/clue/saveDeleteBund.do",
					data: {
						activityId:activityId,
						clueId:clueId
					},
					type: "post",
					dataType: "json",
					success: function (data) {
						if (data.code=="1"){
							// 获取被选中解除关联的市场活动，并删除
							$("#tr_"+activityId).remove();
						}else {
							alert(data.message);
						}
					}
				});
			};
		});

		// 给线索转换按钮添加单击事件
		$("#convertClueBtn").click(function () {
			// 发送同步请求
			window.location.href="workbench/clue/goConvert.do?clueId=${requestScope.clue.id}";
		});
	});
	
</script>

</head>
<body>

	<!-- 关联市场活动的模态窗口 -->
	<div class="modal fade" id="bundModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 80%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">关联市场活动</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" id="serchActivityT" class="form-control" style="width: 300px;" placeholder="请输入市场活动名称，支持模糊查询">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td><input type="checkbox"/></td>
								<td>名称</td>
								<td>开始日期</td>
								<td>结束日期</td>
								<td>所有者</td>
								<td></td>
							</tr>
						</thead>
						<tbody id="bundActivityTbody">
							<%--<tr>
								<td><input type="checkbox"/></td>
								<td>发传单</td>
								<td>2020-10-10</td>
								<td>2020-10-20</td>
								<td>zhangsan</td>
							</tr>--%>
						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" id="saveBundBtn">关联</button>
				</div>
			</div>
		</div>
	</div>


	<!-- 返回按钮 -->
	<div style="position: relative; top: 35px; left: 10px;">
		<a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left" style="font-size: 20px; color: #DDDDDD"></span></a>
	</div>
	
	<!-- 大标题 -->
	<div style="position: relative; left: 40px; top: -30px;">
		<div class="page-header">
			<h3>${requestScope.clue.fullname}${requestScope.clue.appellation} <small>${requestScope.clue.company}</small></h3>
		</div>
		<div style="position: relative; height: 50px; width: 500px;  top: -72px; left: 700px;">
			<button type="button" class="btn btn-default" id="convertClueBtn"><span class="glyphicon glyphicon-retweet"></span> 转换</button>
			
		</div>
	</div>
	
	<br/>
	<br/>
	<br/>

	<!-- 详细信息 -->
	<div style="position: relative; top: -70px;">
		<div style="position: relative; left: 40px; height: 30px;">
			<div style="width: 300px; color: gray;">名称</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${requestScope.clue.fullname}${requestScope.clue.appellation}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">所有者</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${requestScope.clue.owner}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 10px;">
			<div style="width: 300px; color: gray;">公司</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${requestScope.clue.company}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">职位</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${requestScope.clue.job}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 20px;">
			<div style="width: 300px; color: gray;">邮箱</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${requestScope.clue.email}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">公司座机</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${requestScope.clue.phone}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 30px;">
			<div style="width: 300px; color: gray;">公司网站</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${requestScope.clue.website}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">手机</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${requestScope.clue.mphone}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 40px;">
			<div style="width: 300px; color: gray;">线索状态</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${requestScope.clue.state}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">线索来源</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${requestScope.clue.source}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 50px;">
			<div style="width: 300px; color: gray;">创建者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${requestScope.clue.createBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;">${requestScope.clue.createTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 60px;">
			<div style="width: 300px; color: gray;">修改者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${requestScope.clue.editBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;">${requestScope.clue.editTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 70px;">
			<div style="width: 300px; color: gray;">描述</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					${requestScope.clue.description}
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 80px;">
			<div style="width: 300px; color: gray;">联系纪要</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					${requestScope.clue.contactSummary}
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 90px;">
			<div style="width: 300px; color: gray;">下次联系时间</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${requestScope.clue.nextContactTime}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px; "></div>
		</div>
        <div style="position: relative; left: 40px; height: 30px; top: 100px;">
            <div style="width: 300px; color: gray;">详细地址</div>
            <div style="width: 630px;position: relative; left: 200px; top: -20px;">
                <b>
					${requestScope.clue.address}
                </b>
            </div>
            <div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
        </div>
	</div>
	
	<!-- 备注 -->
	<div style="position: relative; top: 40px; left: 40px;">
		<div class="page-header">
			<h4>备注</h4>
		</div>
		<c:forEach items="${requestScope.remarkList}" var="remark">
			<div class="remarkDiv" id="div_${remark.id}" style="height: 60px;">
				<img title="${remark.createBy}" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
				<div style="position: relative; top: -40px; left: 40px;" >
					<h5>${remark.noteContent}</h5>
					<font color="gray">线索</font> <font color="gray">-</font> <b>${requestScope.clue.fullname}${requestScope.clue.appellation}-${requestScope.clue.company}</b> <small style="color: gray;"> ${remark.editFlag=='0'?remark.createTime:remark.editTime} 由 ${remark.editFlag=='0'?remark.createBy:remark.editBy} ${remark.editFlag=='0'?"创建":"修改"}</small>
					<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
						<a class="myHref" name="editA" remarkId="${remark.id}" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<a class="myHref" name="delA" remarkId="${remark.id}" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
					</div>
				</div>
			</div>
		</c:forEach>
		<!-- 备注1 -->
		<%--<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>哎呦！</h5>
				<font color="gray">线索</font> <font color="gray">-</font> <b>李四先生-动力节点</b> <small style="color: gray;"> 2017-01-22 10:10:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>--%>

		<div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px;">
			<form role="form" style="position: relative;top: 10px; left: 10px;">
				<textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"  placeholder="添加备注..."></textarea>
				<p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
					<button id="cancelBtn" type="button" class="btn btn-default">取消</button>
					<button type="button" class="btn btn-primary">保存</button>
				</p>
			</form>
		</div>
	</div>
	
	<!-- 市场活动 -->
	<div>
		<div style="position: relative; top: 60px; left: 40px;">
			<div class="page-header">
				<h4>市场活动</h4>
			</div>
			<div style="position: relative;top: 0px;">
				<table class="table table-hover" style="width: 900px;">
					<thead>
						<tr style="color: #B3B3B3;">
							<td>名称</td>
							<td>开始日期</td>
							<td>结束日期</td>
							<td>所有者</td>
							<td></td>
						</tr>
					</thead>
					<tbody id="activityTbody">
						<c:forEach items="${requestScope.activityList}" var="activity">
							<tr id='tr_${activity.id}'>
								<td>${activity.name}</td>
								<td>${activity.startDate}</td>
								<td>${activity.endDate}</td>
								<td>${activity.owner}</td>
								<td><a activityId="${activity.id}" href="javascript:void(0);"  style="text-decoration: none;"><span class="glyphicon glyphicon-remove"></span>解除关联</a></td>
							</tr>
						</c:forEach>
						<%--<tr>
							<td>发传单</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
							<td>zhangsan</td>
							<td><a href="javascript:void(0);"  style="text-decoration: none;"><span class="glyphicon glyphicon-remove"></span>解除关联</a></td>
						</tr>--%>
					</tbody>
				</table>
			</div>
			
			<div>
				<a href="javascript:void(0);" id="bundActivityBtn" style="text-decoration: none;"><span class="glyphicon glyphicon-plus"></span>关联市场活动</a>
			</div>
		</div>
	</div>
	
	
	<div style="height: 200px;"></div>
</body>
</html>