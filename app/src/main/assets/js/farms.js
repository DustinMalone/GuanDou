$(function() {
		var  userInfo = JSON.parse(localStorage.getItem("login"));	//用户信息	
		var  playAct = null; // 动作定时器
		var  time = null; //  长豆时间定时器
		var  timeFd = null; //成熟时间定时器
		var  user_id = userInfo.user_id; // 用户id
		var  user_tk = userInfo.accessToken;
		var  userList =  JSON.parse(localStorage.getItem("steal"));
		var next = 1;
		get_gd();
	
		function get_gd(){
			var token = {
				"accessToken": user_tk
			};
				$.ajax({
					type:"POST",
					dataType:"json",
					url:"http://120.79.55.141/GuanDou/user/getbeansInfo.do",
					data:JSON.stringify(token),
					contentType:"application/json",
					success:function(result){
						if(result.success){
							$(".residue").text(result.data.gdnumber);
						}
					}
				});
				var d = new Date();
				if(d.getHours() >= 6 && d.getHours() < 18) {
					$(".game-style .mui-content").removeClass("night")
				} else {
					$(".game-style .mui-content").addClass("night");
				}
		}
	set_land(userList[0].USERID);
	$(".get_id").text(userList[0].USERID);

	//next

	$(".gplus").bind("click", function() {
		$(".gplus").attr("disabled", true);
		if(next >= userList.length) {
			mui.toast("没有更多土地了");
			return false;
		} else {
			$(".field .ok").remove();
			$(".loading").show();
			$(".time-tip").remove();
			setTimeout(function() {
				nextEmpty();
				$(".get_id").text(userList[next].USERID);
				set_land(userList[next].USERID);
				$("#Timetxt").text("未选择土地");
				next++;
			}, 700);

			$(".gplus").removeAttr("disabled");
		}
	});
	// 禁止连续点击
	function disableClick(tag) {
		$(tag).addClass("disable-events");
		setTimeout(function() {
			$(tag).removeClass("disable-events");
		}, 1500)
	}

	function nextEmpty() { //重置土地状态
		clearInterval(timeFd);
		clearInterval(time);
		$(".field li").empty();
		$(".field li").removeAttr("js sf");
		$(".field li").attr("class", "land");
		$(".field li").append("<img src='../images/di.png' class='max' />");
		$(".field li").append("<img src='../images/un.png' class='status' />");
		$(".field li").append("<img src='../images/dou_young.png' class='bean' />");
	}

	function set_land(id) { //设置土地状态
		var queryLand = {
			"ownerId": id
		};
		$.ajax({
			url: "http://120.79.55.141/GuanDou/gameService/queryLand.do",
			type: "POST",
			dataType: "json",
			data: JSON.stringify(queryLand),
			contentType: "application/json",
			success: function(result) {
				var landStatus = result.data;
				if(!$.isEmptyObject(landStatus)) {
					$(".loading").hide();
					$(".field li").each(function(index, dou) {
						var that = $(this);
						$(this).attr("id", landStatus[index].ID);
						$(this).attr("STATUS", landStatus[index].STATUS);
						if($(this).attr("status") > 1) {
							$(this).attr("id", landStatus[index].ID);
							$(this).attr("STATUS", landStatus[index].STATUS);
							$(this).attr("JS", landStatus[index].JS);
							$(this).attr("SF", landStatus[index].SF);
							$(this).attr("RD", landStatus[index].RECEIVEDATE);
							$(this).attr("FD", landStatus[index].FFDATE);
							//已经开垦
							if($(this).attr("JS") == 1 && $(this).attr("SF") == 1) {
								$(this).find(".max").attr("src", "../images/full_di.png");
							} else if($(this).attr("JS") == 1) {
								$(this).find(".max").attr("src", "../images/water_di.png");
							} else if($(this).attr("SF") == 1) {
								$(this).find(".max").attr("src", "../images/fertilizer_di.png");
							}
						}
						if($(this).attr("status") == 1) {
							$(this).attr("class", "dou").removeClass("land ripe").find(".status").add($(this).find(".bean")).hide();
						} else if($(this).attr("status") == 2) {
							$(this).attr("class", "dou young").removeClass("land").find(".status").remove();
							$(this).find(".bean").attr("src", "../images/dou_young.png").show();
						} else if($(this).attr("status") == 3) {
							$(this).attr("class", "dou grow").removeClass("land").find(".status").remove();
							$(this).find(".bean").attr("src", "../images/dou_grow.png");
						} else if($(this).attr("status") == 4) {
							$(this).attr("class", "dou ripe").removeClass("land").find(".status").remove();
							$(this).find(".bean").attr("src", "../images/dou_ripe.png");
							$(this).append("<img src='../images/ok.png'  class='ok'/>")
						}
					})

				}
			},
			error: function() {
				mui.toast("网络错误，请稍后再重试");
			}
		})
	}
	// 选
	$(".field li").each(function() {
		$(this).click(function() {
			var that = $(this);
			disableClick(that);
			var lanStatus = that.attr("status");
			if(lanStatus == 0) {
				mui.toast("该土地未开垦");
			} else if(lanStatus == 1) {
				mui.toast("该土地未耕种");
			}
			if(that.hasClass("dou") && lanStatus > 1) {
				clearInterval(timeFd);
				clearInterval(time);
				$(this).addClass("on").siblings().removeClass("on");
				if($(this).find(".time-tip").length == 0 && lanStatus != 4) {
					$(this).append("<div class='time-tip'><p class='tx'></p><p class='t'></p></div>").siblings().find(".time-tip").remove();
				}
			}
			if(lanStatus == 2) {
				time = setInterval(function(nt, rt) {
					nt = (new Date()).getTime(); //现在时间
					rt = that.attr("rd"); //长豆时间
					that.attr("djs", parseInt(rt * 0.001) - parseInt(nt * 0.001));
					if(that.hasClass("on")) {
						if(that.attr("djs") <= 0) {
							that.removeAttr("djs");
							//	clearInterval(time);
						} else {
							$(".tx").text("距长豆时间");
							$(".time-tip .t").text(formatDuring($(".on").attr("djs")))
						}
					}
				}, 1000)
			} else if(lanStatus == 3) {
				timeFd = setInterval(function(nt, rt) {
					nt = (new Date()).getTime(); //现在时间
					rt = (new Date(that.attr("fd"))).getTime() //成熟时间
					if (/(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent)){
						var x = that.attr("fd");
						rt = (new Date(x.replace(/-/g,"/"))).getTime()
					}
					that.attr("djss", parseInt(rt * 0.001) - parseInt(nt * 0.001));
					if(that.hasClass("on")) {
						if(that.attr("djss") <= 0) {
							$(".time-tip .tx").text("贯豆已长豆~").css({"line-height":"3.2","font-size":"14px"});
							that.removeAttr("djss");
							clearInterval(timeFd);
						} else {
							$(".tx").text("距成熟时间");
							$(".time-tip .t").text(formatDuring($(".on").attr("djss")))
						}
					}
				}, 1000)
			} else if(lanStatus == 4) {
				if($(this).hasClass("dou") && $(".field").attr("cz") !== "ing") {
					clearTimeout(playAct);
					clearInterval(time);
					$(".on").append('<img src="../images/reap_action.gif" class="act reap_action" />');
					$(".field").attr("cz", "ing"); // 动作执行中
					var _landId = $('.on').attr('id');
					var saveOperation = {
						'operationType': 4,
						'landId': _landId,
						'userId': user_id
					};
					playAct = setTimeout(function() {
						$(".act").fadeOut().remove();
						$(".field").attr("cz", "over"); //动作完成
					}, 1800);
					$.ajax({
						url: "http://120.79.55.141/GuanDou/gameService/saveOperation.do",
						type: "POST",
						dataType: "json",
						data: JSON.stringify(saveOperation),
						contentType: "application/json",
						success: function(result) {
							$(".mui-toast-container").hide().remove();
							get_gd();
							$(".on").find(".ok").remove();
							mui.toast(result.message);
						},
						error: function() {
							mui.toast("网络错误，请稍后再重试");
						}
					})
				}
			}
		})
	})

	//动作 
	//		$("#reap").click(function(toolid,action) {
	//			if ($(".dou").hasClass("on")) {
	//				if($(".on").attr("status") == 4){
	//
	//					}
	//					else
	//					{
	//						mui.toast("贯豆尚未长豆~");
	//					}
	//				}
	//			else{
	//				mui.toast("请选择土地后再操作");
	//			}
	//		})
	//待开发功能提示
	$("#gameSpeed,#gameShop").click(function() {
		mui.toast("功能即将启用！")
	})

})
//毫秒转换时间
function formatDuring(mss) {
	var days = parseInt(mss / (1 * 60 * 60 * 24));
	var hours = parseInt((mss % (1 * 60 * 60 * 24)) / (1 * 60 * 60));
	var minutes = parseInt((mss % (1 * 60 * 60)) / (1 * 60));
	var seconds = (mss % (1 * 60)) / 1;
	if(mss > (86400000 * 0.001)) {
		return days + " 天 " + hours + " 时 ";
	} else {
		return hours + "时 " + minutes + "分" + seconds + "秒 ";
	}
}