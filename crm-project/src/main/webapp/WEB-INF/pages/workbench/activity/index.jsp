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
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bs_pagination-master/css/jquery.bs_pagination.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
<script type="text/javascript" src="jquery/bs_pagination-master/js/jquery.bs_pagination.min.js"></script>
<script	type="text/javascript" src="jquery/bs_pagination-master/localization/en.js"></script>

	<script type="text/javascript">

	$(function(){
		// 给“创建”按钮添加单击事件
		$("#createActivityBtn").click(function () {
			// 初始化工作,清空表单中的数据,获取表单的dom对象，重置from表单
			$("#createActivityFrom").get(0).reset();

			// 弹出市场活动的模态窗口
			$("#createActivityModal").modal("show");
		});

		// 给“保存”按钮添加单机事件
		$("#saveCreateActivityBtn").click(function () {
			// 收集参数
			/*var optionId = $(".optionS option:checked").attr("id");
			alert(optionId)*/
			var owner = $(".optionS option:checked").attr("id");
			var name = $.trim($("#create-marketActivityName").val());
			var startDate = $("#create-startDate").val();
			var endDate = $("#create-endDate").val();
			var cost = $.trim($("#create-cost").val());
			var description = $.trim($("#create-description").val());
			// 表单验证
			if (owner == ""){
				alert("所有者不能为空！！！")
				return;
			};
			if (name == ""){
				alert("名称不能为空！！！")
				return;
			};
			if (startDate != "" && endDate != ""){
				// 使用字符串的大小来代替日期的大小
				if (startDate>endDate){
					alert("结束日期不能小于开始日期");
					return;
				}
			};
			var regExp = /^(([1-9]\d*)|0)$/;
			if (!regExp.test(cost)){
				alert("成本只能为非负整数");
				return;
			};

			// 发送请求
			$.ajax({
				url: 'workbench/activity/saveCreateActivity.do',
				data:{
					owner:owner,
					name:name,
					startDate:startDate,
					endDate:endDate,
					cost:cost,
					description:description
				},
				dataType:'json',
				type:'post',
				success:function (data) {
					if (data.code==1){
						// 请求成功，关闭模态窗口
						$("#createActivityModal").modal("hide");
						// 刷新市场活动列，显示第一页数据，保持首页显示条数不变
						queryActivityByConditionForPage(1,$("#demo_page").bs_pagination("getOption","rowsPerPage"));
					}else {
						// 提示信息
						alert(data.message);
						// 模态窗口不关闭
						$("#createActivityModal").modal("show");
					}
				}
			});
		});
		// 当容器加载完成之后，对容器调用工具函数
		$(".myDate").datetimepicker({
			language:'zh-CN',			//语言
			format:'yyyy-mm-dd',		//日期格式
			minView:'month',			// 可以选择的最小视图
			initialDate:new Date(),		//初始化显示日期
			autoclose:true,				//设置选择完日期之后，是否自动关闭日历
			todayBtn:true,				// 设置是否显示“今天”的按钮，默认false
			clearBtn:true				// 设置是否显示清空的按钮，默认false
		});

		// 当市场活动主页面加载完之后，查询所有数据的第一页以及所有数据的总条数
		queryActivityByConditionForPage(1,10);

		// 给查询按钮添加单击事件
		$("#queryActivityBtn").click(function () {
			// 查询符合所有条件的第一页以及所有符合条件的总条数
			// $("#demo_page").bs_pagination("getOption","rowsPerPage") 获取总条数
			// 点击查询按钮之后，保持原来的查询数据的总条数不变
			queryActivityByConditionForPage(1,$("#demo_page").bs_pagination("getOption","rowsPerPage"));
		});


		// 给“全选按钮”添加单击事件
		$("#checkAll").click(function () {
			// 如果全选按钮是选中的，那么列表中的所有checkbox都选中
			// this.checked 获取当前对象的checked属性
			$("#tBody input[type='checkbox']").prop("checked",this.checked);

		});

		// 给列表中的checkbox添加单机事件
		$("#tBody").on("click","input[type='checkbox']",function () {
			// 如果列表中的所有checkbox都被选中，则全选按钮也被选中
			if ($("#tBody input[type='checkbox']").size() == $("#tBody input[type='checkbox']:checked").size()){
				$("#checkAll").prop("checked",true);
			}else {
				$("#checkAll").prop("checked",false);
			};
		});

		// 给“删除”按钮添加单机事件
		$("#deleteActivityByIdsBtn").click(function () {
			// 收集参数，获取列表中所有被选中的checkbox
			var checkboxIds = $("#tBody input[type='checkbox']:checked");
			if (checkboxIds.size() == 0){
				alert("请选择需要删除的市场活动")
				return;
			}

			if (window.confirm("确定删除吗?")){
				var ids = "";
				$.each(checkboxIds,function () {
					ids += "id="+this.value+"&";
				});
				ids = ids.substr(0,ids.length-1);
				alert(ids)
				$.ajax({
					url: 'workbench/activity/deleteActivityByIds.do',
					data: ids,
					dataType: 'json',
					type: 'post',
					success: function (data) {
						if (data.code == "1"){
							// 刷新市场活动列表，显示第一页信息，保持每页条数不变
							queryActivityByConditionForPage(1,$("#demo_page").bs_pagination("getOption","rowsPerPage"));
						}else {
							// 提示信息
							alert(data.message);
						}
					}
				});
			}

		});

		// 给修改按钮添加单击事件
		$("#editActivityBtn").click(function () {
			// 收集参数
			// 收集被选中的checkbox的id
			var checkIds = $("#tBody input[type=checkbox]:checked");
			if (checkIds.size()==0){
				alert("请选择需要修改的市场活动...");
				return;
			}
			if (checkIds.size()>1){
				alert("每次只能修改一次市场活动");
				return;
			}
			let id = checkIds.val();
			$.ajax({
				url: 'workbench/activity/queryActivityById.do',
				data:{
					id:id
				},
				dataType:'json',
				type:'post',
				success:function (data) {
					$("#edit-id").val(data.id);
					$("#edit-marketActivityOwner").val(data.owner)
					$("#edit-marketActivityName").val(data.name);
					$("#edit-startTime").val(data.startDate);
					$("#edit-endTime").val(data.endDate);
					$("#edit-cost").val(data.cost);
					$("#edit-description").val(data.description);

					// 显示模态窗口
					$("#editActivityModal").modal("show")
				}
			});
		});

		// 给更新按钮添加单机事件
		$("#saveEditActivityBtn").click(function () {
			// 收集数据
			var owner = $("#edit-marketActivityOwner option:selected").attr("id");
			var name = $.trim($("#edit-marketActivityName").val());
			var startTime = $("#edit-startTime").val();
			var endTime = $("#edit-endTime").val();
			var cost = $.trim($("#edit-cost").val());
			var description = $.trim($("#edit-description").val());
			var id = $("#edit-id").val();

			$.ajax({
				url:"workbench/activity/saveEditActivity.do",
				data:{
					id:id,
					name:name,
					owner:owner,
					startDate:startTime,
					endDate:endTime,
					cost:cost,
					description:description
				},
				dataType:"json",
				type:"post",
				success:function (data) {
					if (data.code=="1"){
						$("#editActivityModal").modal("hide");
						// 刷新市场活动列表，显示第一页信息，保持每页条数不变
						queryActivityByConditionForPage($("#demo_page").bs_pagination("getOption","currentPage"),
								$("#demo_page").bs_pagination("getOption","rowsPerPage"));
					}else {
						// 提示信息
						alert(data.message);
					}
				}
			});
			//alert(name+startTime+endTime+cost+description+id);
			//$("#editActivityModal").modal("hide")
		});

		// 给批量导出按钮添加单击事件
		$("#exportActivityAllBtn").click(function () {
			// 发送同步请求
			window.location.href = "workbench/activity/exportActivitys.do";
			//alert(1)
		});

		/* 给导入按钮添加单击事件 */
		$("#importActivityBtn").click(function () {
			// 收集参数
			var activityFileName = $("#activityFile").val();
			var suffix = activityFileName.substr(activityFileName.lastIndexOf(".")+1).toLocaleLowerCase();
			if (suffix != "xls"){
				alert("只支持xls文件");
				return;
			}
			// 获取上传的文件
			var activityFile = $("#activityFile")[0].files[0];
			// 判断文件大小不能超过5MB
			if (activityFile.size>5*1024*1024){
				alert("文件大小不能超过5MB");
				return;
			}

			// FormData 是ajax提供的接口，可以模拟键值对向后台提供参数
			// FormData 最大的优势，不仅可以提供文本数据，还可以提交二进制数据
			var formData = new FormData();
			formData.append("activityFile",activityFile);
			// 发送请求 默认情况下，ajax每次向后台提交参数都会将参数统一转为字符串
			$.ajax({
				url:'workbench/activity/importActivity.do',
				data:formData,
				processData:false,//设置ajax向后台提交参数之前，是否把参数统一转换成字符串：true--是,false--不是,默认是true
				contentType:false,//设置ajax向后台提交参数之前，是否把所有的参数统一按urlencoded编码：true--是,false--不是，默认是true
				type:'post',
				dataType:'json',
				success:function (data) {
					if(data.code=="1"){
						//提示成功导入记录条数
						alert("成功导入"+data.retDate+"条记录");
						//关闭模态窗口
						$("#importActivityModal").modal("hide");
						//刷新市场活动列表,显示第一页数据,保持每页显示条数不变
						queryActivityByConditionForPage(1,
								$("#demo_page").bs_pagination("getOption","rowsPerPage"));
					}else{
						//提示信息
						alert(data.message);
						//模态窗口不关闭
						$("#importActivityModal").modal("show");
					}
				}
			});
		});

	});

	/*  分页查询 */
	function queryActivityByConditionForPage(pageNo,pageSize) {
		// 收集参数
		var name = $("#queryName").val();
		var owner = $("#queryOwner").val();
		var startDate = $("#queryStartDate").val();
		var endDate = $("#queryEndDate").val();
		/*var pageNo = 1;
		var pageSize = 10;*/
		// 发送请求
		$.ajax({
			url: 'workbench/activity/queryActivityByConditionForPage.do',
			data: {
				name:name,
				owner:owner,
				startDate:startDate,
				endDate:endDate,
				pageNo:pageNo,
				pageSize:pageSize
			},
			type: 'post',
			dataType: 'json',
			success: function (data) {
				// 显示总条数
				// $("#totalRowsB").text(data.totalRows);
				// 显示市场活动的列表
				// 遍历ActivityList，拼接所有数据
				var htmlStr = "";
				$.each(data.activityList,function (index,obj) {
					htmlStr+="<tr class=\"active\">";
					htmlStr+="<td><input type=\"checkbox\" value=\""+obj.id+"\"/></td>";
					htmlStr+="<td><a style=\"text-decoration: none; cursor: pointer;\" onclick=\"window.location.href='workbench/activity/detailActivity.do?id="+obj.id+"'\">"+obj.name+"</a></td>";
					htmlStr+="<td>"+obj.owner+"</td>";
					htmlStr+="<td>"+obj.startDate+"</td>";
					htmlStr+="<td>"+obj.endDate+"</td>";
					htmlStr+="</tr>";
				});
				$("#tBody").html(htmlStr);

				// 取消全选按钮
				$("#checkAll").prop("checked",false);
				// 计算总页数
				var totalPages = 1;
				if (data.totalRows%pageSize==0){
					totalPages = data.totalRows/pageSize;
				}else {
					totalPages = parseInt(data.totalRows/pageSize) + 1;
				}
				/*var total = data.totalRows%pageSize==0?data.totalRows/pageSize:data.totalRows/pageSize +1*/
				//对容器调用bs_pagination工具函数，显示翻页信息
				$("#demo_page").bs_pagination({
					currentPage:pageNo,			//当前页号,相当于pageNo

					rowsPerPage:pageSize,		//每页显示条数,相当于pageSize
					totalRows:data.totalRows,	//总条数
					totalPages: totalPages,  	//总页数,必填参数.

					visiblePageLinks:5,			//最多可以显示的卡片数

					showGoToPage:true,			//是否显示"跳转到"部分,默认true--显示
					showRowsPerPage:true,		//是否显示"每页显示条数"部分。默认true--显示
					showRowsInfo:true,			//是否显示记录的信息，默认true--显示

					//用户每次切换页号，都自动触发本函数;
					//每次返回切换页号之后的pageNo和pageSize
					onChangePage: function(event,pageObj) { // returns page_num and rows_per_page after a link has clicked
						//js代码
						//alert(pageObj.currentPage);
						//alert(pageObj.rowsPerPage);
						queryActivityByConditionForPage(pageObj.currentPage,pageObj.rowsPerPage);
					}
				});

			}
		});

	}
</script>
</head>
<body>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form" id="createActivityFrom">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control optionS" id="create-marketActivityOwner">
								  <c:forEach items="${users}" var="u">
									  <option id="${u.id}">${u.name}</option>
								  </c:forEach>
								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-marketActivityName">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startDate" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control myDate" id="create-startDate">
							</div>
							<label for="create-endDate" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control myDate" id="create-endDate">
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveCreateActivityBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
						<input type="hidden" id="edit-id">
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-marketActivityOwner">
									<c:forEach items="${users}" var="u">
										<option id="${u.id}" >${u.name}</option>
									</c:forEach>
								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-marketActivityName" value="发传单">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label ">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control myDate" id="edit-startTime" value="2020-10-10">
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label myDate">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control myDate" id="edit-endTime" value="2020-10-20">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost" value="5,000">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-description">市场活动Marketing，是指品牌主办或参与的展览会议与公关市场活动，包括自行主办的各类研讨会、客户交流会、演示会、新产品发布会、体验会、答谢会、年会和出席参加并布展或演讲的展览会、研讨会、行业交流会、颁奖典礼等</textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveEditActivityBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 导入市场活动的模态窗口 -->
    <div class="modal fade" id="importActivityModal" role="dialog">
        <div class="modal-dialog" role="document" style="width: 85%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">导入市场活动</h4>
                </div>
                <div class="modal-body" style="height: 350px;">
                    <div style="position: relative;top: 20px; left: 50px;">
                        请选择要上传的文件：<small style="color: gray;">[仅支持.xls]</small>
                    </div>
                    <div style="position: relative;top: 40px; left: 50px;">
                        <input type="file" id="activityFile">
                    </div>
                    <div style="position: relative; width: 400px; height: 320px; left: 45% ; top: -40px;" >
                        <h3>重要提示</h3>
                        <ul>
                            <li>操作仅针对Excel，仅支持后缀名为XLS的文件。</li>
                            <li>给定文件的第一行将视为字段名。</li>
                            <li>请确认您的文件大小不超过5MB。</li>
                            <li>日期值以文本形式保存，必须符合yyyy-MM-dd格式。</li>
                            <li>日期时间以文本形式保存，必须符合yyyy-MM-dd HH:mm:ss的格式。</li>
                            <li>默认情况下，字符编码是UTF-8 (统一码)，请确保您导入的文件使用的是正确的字符编码方式。</li>
                            <li>建议您在导入真实数据之前用测试文件测试文件导入功能。</li>
                        </ul>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button id="importActivityBtn" type="button" class="btn btn-primary">导入</button>
                </div>
            </div>
        </div>
    </div>
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" id="queryName" type="text">
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" id="queryOwner" type="text">
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control myDate" type="text" id="queryStartDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control myDate" type="text" id="queryEndDate">
				    </div>
				  </div>
				  <button type="button" class="btn btn-default" id="queryActivityBtn">查询</button>
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="createActivityBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editActivityBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteActivityByIdsBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				<div class="btn-group" style="position: relative; top: 18%;">
                    <button type="button" class="btn btn-default" data-toggle="modal" data-target="#importActivityModal" ><span class="glyphicon glyphicon-import"></span> 上传列表数据（导入）</button>
                    <button id="exportActivityAllBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-export"></span> 下载列表数据（批量导出）</button>
                    <button id="exportActivityXzBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-export"></span> 下载列表数据（选择导出）</button>
                </div>
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="checkAll" /></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="tBody">
						<%--<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>
                        </tr>--%>
					</tbody>
				</table>

				<%-- 翻页 --%>
				<div id="demo_page"></div>
			</div>


			<%--<div style="height: 50px; position: relative;top: 30px;">
				<div>
					<button type="button" class="btn btn-default" style="cursor: default;">共<b id="totalRowsB">50</b>条记录</button>
				</div>
				<div class="btn-group" style="position: relative;top: -34px; left: 110px;">
					<button type="button" class="btn btn-default" style="cursor: default;">显示</button>
					<div class="btn-group">
						<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
							10
							<span class="caret"></span>
						</button>
						<ul class="dropdown-menu" role="menu">
							<li><a href="#">20</a></li>
							<li><a href="#">30</a></li>
						</ul>
					</div>
					<button type="button" class="btn btn-default" style="cursor: default;">条/页</button>
				</div>
				<div style="position: relative;top: -88px; left: 285px;">
					<nav>
						<ul class="pagination">
							<li class="disabled"><a href="#">首页</a></li>
							<li class="disabled"><a href="#">上一页</a></li>
							<li class="active"><a href="#">1</a></li>
							<li><a href="#">2</a></li>
							<li><a href="#">3</a></li>
							<li><a href="#">4</a></li>
							<li><a href="#">5</a></li>
							<li><a href="#">下一页</a></li>
							<li class="disabled"><a href="#">末页</a></li>
						</ul>
					</nav>
				</div>
			</div>--%>
			
		</div>
		
	</div>
</body>
</html>