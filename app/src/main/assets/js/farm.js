$(function() {

		var  userInfo = JSON.parse(localStorage.getItem("login"));	//用户信息
//		var usergdinfo =JSON.parse(localStorage.getItem("localSgdNum"))
		var  user_id = userInfo.user_id; // 用户id
		var  user_tk = userInfo.accessToken;
		var  playAct = null; // 动作定时器
		var  time = null; //  长豆时间定时器
		var  timeFd = null; //成熟时间定时器
		var  land_info = [];
		var  userList =  JSON.parse(localStorage.getItem("steal"));
		var next = -1;
		get_asset();
		get_gd();
		get_land_count();
		var land_price = parseInt(land_info[0].data.landPrice); //土地单价
		var seed_price = parseInt(land_info[0].data.seedsPrice); //种子单价
		var beimu = 0;
		function testPsw(callback) {
			var token = {
				"user_id": user_id
			};
			$.ajax({
				url: "http://120.79.55.141/GuanDou/user/check.do",
				type: "POST",
				dataType: "json",
				data: JSON.stringify(token),
				contentType: "application/json",
				global: false,
				async: false,
				success: function(rs) {
					callback(rs);
				}
			})
		}
	var ajaxRequire = function(param, callback) {
		$.ajax({
			url: param.requireURL,
			type: param.method,
			dataType: "json",
			data: param.data,
			contentType: "application/json",
			success: function(data) {
				callback(data);
			},
			error: function() {
				mui.toast("网络链接错误，请稍后在重试")
			}
		})
	}

	$(".steal").click(function() {
		var token = {
			"ownerId": user_id
		};
		$.ajax({
			url: "http://120.79.55.141/GuanDou/gameService/queryAllStealLand.do",
			type: "POST",
			dataType: "json",
			data: JSON.stringify(token),
			contentType: "application/json",
			global: false,
			async: false,
			success: function(data) {
				localStorage.setItem("steal", JSON.stringify(data.data))
			}
		})
	})
	// 禁止连续点击
	function disableClick(tag) {
		$(tag).addClass("disable-events");
		setTimeout(function() {
			$(tag).removeClass("disable-events");
		}, 2000)
	}

	function get_asset() { //获取用户贝母
		var token = {
			"accessToken": user_tk
		};
		$.ajax({
			url: "http://120.79.55.141/GuanDou/app/getPointsInfo.do",
			type: "POST",
			dataType: "json",
			data: JSON.stringify(token),
			contentType: "application/json",
			global: false,
			success: function(jf) {
				beimu = jf.data.point_count;
			}
		})
		var d = new Date();
		if(d.getHours() >= 6 && d.getHours() < 18) {
			$(".game-style .mui-content").removeClass("night")
		} else {
			$(".game-style .mui-content").addClass("night");
		}
	}

	function get_gd() {
		var token = {
			"accessToken": user_tk
		};
		$.ajax({
			type: "POST",
			dataType: "json",
			url: "http://120.79.55.141/GuanDou/user/getbeansInfo.do",
			data: JSON.stringify(token),
			contentType: "application/json",
			success: function(result) {
				if(result.success) {
					console.log(result)
					$(".residue").text(result.data.gdnumber);

				}
			}
		});
	}

	function get_land_count() { //获取土地信息
		var queryLand = {
			'ownerId': user_id
		}
		$.ajax({
			url: "http://120.79.55.141/GuanDou/gameService/queryLandInfo.do",
			type: "POST",
			dataType: "json",
			data: JSON.stringify(queryLand),
			contentType: "application/json",
			async: false,
			success: function(result) {
				console.log(result)
				if(result.success) {
					land_info.push(result);
					updateLadb();
				}
			}
		})
	}

	function updateLadb() { //更新土地
		var queryLandInfo = {
			"ownerId": user_id
		};
		$.ajax({
			url: "http://120.79.55.141/GuanDou/gameService/queryLandInfo.do",
			type: "POST",
			dataType: "json",
			data: JSON.stringify(queryLandInfo),
			contentType: "application/json",
			success: function(result) {
				console.log(result)
				$(".field").attr("dn", result.data.zt0); //可开垦数量
				$(".field").attr("jg", result.data.landPrice); //土地价格
				$(".field").attr("zzjg", result.data.seedsPrice); //土地价格

					$(".field").attr("dn", result.data.zt0);
	
				set_land();
			},
			error: function() {
				mui.toast("网络错误，请稍后再重试");
			}
		})
	}

	function set_land() { //设置土地状态
		var queryLand = {
			"ownerId": user_id
		};
		$.ajax({
			url: "http://120.79.55.141/GuanDou/gameService/queryLand.do",
			type: "POST",
			dataType: "json",
			data: JSON.stringify(queryLand),
			contentType: "application/json",
			success: function(result) {
				console.log(result)
				var landStatus = result.data;
				if(!$.isEmptyObject(landStatus)) {
					$(".loading").hide();
					$(".field li").each(function(index, dou) {
						var that = $(this);
						$(this).attr("id", landStatus[index].ID);
						$(this).attr("STATUS", landStatus[index].STATUS);
						$(this).attr("JS", landStatus[index].JS);
						$(this).attr("SF", landStatus[index].SF);
						$(this).attr("RD", landStatus[index].RECEIVEDATE);
						$(this).attr("FD", landStatus[index].FFDATE);
						if($(this).attr("status") > 1) {
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
							$(this).addClass("dou sts1").removeClass("land ripe").find(".status").add($(this).find(".bean")).hide();
						} else if($(this).attr("status") == 2) {
							$(this).addClass("dou young").removeClass("land").find(".status").remove();
							$(this).find(".bean").attr("src", "../images/dou_young.png").show();
						} else if($(this).attr("status") == 3) {
							$(this).addClass("dou grow").removeClass("land").find(".status").remove();
							$(this).find(".bean").attr("src", "../images/dou_grow.png");
						} else if($(this).attr("status") == 4) {
							$(this).addClass("dou ripe").removeClass("land").find(".status").remove();
							$(this).find(".bean").attr("src", "../images/dou_ripe.png");
							if($(this).find(".ok").length == 0) {
								$(this).append("<img src='../images/ok.png'  class='ok'/>")
							}
						}
					})
				} else {
					$(".loading").hide();
					//buy_land();
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
			if($(this).hasClass("dou") && $(".field").attr("cz") !== "ing") {
				$(this).addClass("on").siblings().removeClass("on").find(".time-tip").remove();
				clearTimeout(playAct);
				clearInterval(time);
				if($(this).find(".time-tip").length == 0 && lanStatus != 1 && lanStatus <= 3) {
					$(this).append("<div class='time-tip'><p class='tx'></p><p class='t'></p></div>");
					setInterval(function() {
						that.find(".time-tip").remove();
					}, 4000)
				}
			}
			if(lanStatus == 0 || !lanStatus) { //未开垦弹出消息框是否购买
				$(this).addClass("buy");
				setTimeout(function() {
					buy_land();
				}, 700)
			} else if(lanStatus == 1) { //已经开垦 弹窗提示是否购买种子
				setTimeout(function() {
					buy_seed();
					$(".time-tip").text("待耕种")
				}, 700)
			} else if(lanStatus == 2) {
				var time = setInterval(function(nt, rt) {
					nt = (new Date()).getTime(); //现在时间
					rt = that.attr("rd"); //长豆时间
					that.attr("djs", parseInt(rt * 0.001) - parseInt(nt * 0.001));
					if(that.hasClass("on")) {
						if(that.attr("djs") <= 0) {
							that.removeAttr("djs");
							clearInterval(time);
							updateLadb();
						} else {
							$(".tx").text("距长豆时间");
							$(".time-tip .t").text(formatDuring($(".on").attr("djs")))
						}
					}
				}, 1000)
			} else if(lanStatus == 3) {
				var timeFd = setInterval(function(nt, rt) {
					nt = (new Date()).getTime(); //现在时间
					rt = (new Date(that.attr("FD"))).getTime() //成熟时间
					that.attr("djss", parseInt(rt * 0.001) - parseInt(nt * 0.001));
					if(that.hasClass("on")) {
						if(that.attr("djss") <= 0) {
							$(".t").text("贯豆已长豆~");
							that.removeAttr("djss");
							clearInterval(timeFd);
						} else {
							$(".tx").text("距成熟时间");
							$(".t").text(formatDuring($(".on").attr("djss")))
						}
					}
				}, 1000)
			} else {
				//					$(".tx").text("可成熟").css({"line-height":"3.2","font-size":"14px"});
				//					$(".time-tip .t").remove()
			}

		})
	})

	//动作 
	$(".tool_bg li").each(function() {
		$(this).click(function(toolid, action) {
			disableClick($(".tool_bg li"));
			if($(".dou").hasClass("on")) {
				var toolid = $(this).attr("id");
				switch(toolid) {
					case "water":
						action = 0;
						break;
					case "fertilizer":
						action = 1;
						break;
					case "reap":
						action = 2;
				}
				if($(".on").attr("status") == 3 && action != 2) {
					$(".mui-toast-container").hide().remove();
					mui.toast("即将长豆");
				} else if($(".on").attr("status") == 4 && action != 2) {
					$(".mui-toast-container").hide().remove();
					mui.toast("已长豆");
				} else {
					if($(".field").attr("cz") == "ing") {
						$(".mui-toast-container").hide().remove();
						mui.toast("操作中~");
					} else {
						if(toolid == "reap" && $(".on").attr("status") != "4") {
							$(".mui-toast-container").hide().remove();
							mui.toast("贯豆尚未长豆~");
						} else {
							$(".on")
								.append('<img src="../images/' + toolid + "_action.gif" + '"  class="act ' + toolid + '_action"/> ').attr("action", action);
							$(".field").attr("cz", "ing"); // 动作执行中
							var _landId = $('.on').attr('id');
							var _type = $(".on").attr("action");
							var saveOperation = {
								'operationType': _type,
								'landId': _landId
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
									updateLadb();
									if(toolid == "reap") {
										get_gd();
										$(".on").find(".ok").remove();
										$(".t").text("待耕种");
									}
									mui.toast(result.message);
								},
								error: function() {
									mui.toast("网络错误，请稍后再重试");
								}
							})
						}
					}
				}
			} else {
				mui.toast("请选择土地后再操作");
			}
		})
	})
	//购买土地
	function buy_land() {
		mui.prompt('当前贝母余额：' + beimu +
			"<p class='dj'>土地单价" + land_price + '，种子单价' + seed_price + "</p>" + "<p class='xh'>" + "需消耗贝母：" +
			"<span class='jian'>" + (land_price + seed_price) + "</span>" + "</p>",
			'请输入支付密码', '购买土地与种子', ['取消', '确定'], null, 'div');
		//	'购买土地和种子的数量(默认为1)', '购买土地-单价'+parseInt($(".field").attr("jg")) +"贝母", ['取消', '确定'], null, 'div');
		document.querySelector('.mui-popup-input input').type = 'password';
		$(".mui-popup-in input").addClass("paypsw");
		$(".paypsw").focus();
		mui(".mui-popup-buttons").on("tap", ".mui-popup-button:nth-child(2)", function() {
			testPsw(function(test) {
				if(test.data.type == 1) {
					mui.toast("请设置支付密码后再购买")
				} else {
					var payPsw = {
						"user_id": user_id,
						"password": $(".paypsw").val()
					};
					$.ajax({
						url: "http://120.79.55.141/GuanDou/user/VerifyPwd.do",
						type: "POST",
						dataType: "json",
						data: JSON.stringify(payPsw),
						contentType: "application/json",
						success: function(result) {
							if(result.code == 0) {
								buySingleLand();
							} else {
								mui.toast(result.message);
							}
						},
						error: function(err) {
							console.log(JSON.stringify(err));
							mui.toast("网络错误，请稍后再重试");
						}
					})
				}
			});
		})
		mui(".mui-popup-buttons").on("tap", ".mui-popup-button:nth-child(1)", function() {
			$(".buy").removeClass("buy");
		})
	}
	//购买一颗种子
	function buySingleSeed() {
		var buySeed = {
			"operationType": 3,
			"landId": $(".on").attr("id"),
			"userId": user_id
		};
		if(parseInt(beimu) - seed_price >= 0) {
			$.ajax({
				type: "POST",
				url: "http://120.79.55.141/GuanDou/gameService/saveOperation.do",
				async: true,
				dataType: "json",
				data: JSON.stringify(buySeed),
				contentType: "application/json",
				success: function(result) {
					mui.toast(result.message);
					get_asset();
					set_land();
				},
				error: function() {
					mui.toast("网络错误，请稍后再重试");
				}
			});
		} else {
			mui.alert("贝母不足")
		}
	}
	//购买一块土地
	function buySingleLand() {
		var buyLand = {
			'ownerId': user_id,
			'landCount': 1,
			"landId": $(".buy").attr("id")
		}
		if(parseInt(beimu) - ((land_price + seed_price) * buyLand.landCount) >= 0) {
			$.ajax({
				url: "http://120.79.55.141/GuanDou/gameService/buyLand.do",
				type: "POST",
				dataType: "json",
				data: JSON.stringify(buyLand),
				contentType: "application/json",
				success: function(result) {
					mui.toast(result.message);
					updateLadb();
					$(".buy").removeClass("buy");
					get_asset();
				},
				error: function() {
					mui.toast("网络错误，请稍后再重试");
				}
			})
		} else {
			mui.toast("贝母不足");
			updateLadb();
		}
	}

	//购买种子 
	function buy_seed() {
		mui.prompt('当前贝母余额：' + beimu +
			"<p class='dj'>种子单价：" + seed_price + "</p>" + "<p class='xh'>" + "需消耗贝母：" +
			"<span class='jian'>" + (seed_price) + "</span>" + "</p>",
			'请输入支付密码', '购买种子', ['取消', '确定'], null, 'div');
		//	'购买土地和种子的数量(默认为1)', '购买土地-单价'+parseInt($(".field").attr("jg")) +"贝母", ['取消', '确定'], null, 'div');
		document.querySelector('.mui-popup-input input').type = 'password';
		$('.mui-popup-in input').addClass(".paypsw");
		$(".paypsw").focus();
		mui(".mui-popup-buttons").on("tap", ".mui-popup-button:nth-child(2)", function() {
			testPsw(function(test) {
				if(test.data.type == 1) {
					mui.toast("请先设置支付密码再购买～")
				} else {
					var payPsw = {
						"user_id": user_id,
						"password": $(".paypsw").val()
					};
					$.ajax({
						url: "http://120.79.55.141/GuanDou/user/VerifyPwd.do",
						type: "POST",
						dataType: "json",
						data: JSON.stringify(payPsw),
						contentType: "application/json",
						success: function(result) {
							if(result.code == 0) {
								buySingleSeed();
							} else {
								mui.toast(result.message);
							}
						},
						error: function() {
							mui.toast("网络错误，请稍后再重试");
						}
					})
				}
			});

		})
	}
	//待开发功能提示
	$("#gameSpeed,#gameShop").click(function() {
		mui.toast("功能即将启用！")
	});
	$(".farm-foot .go_steal").click(function() {
		if($(".field").attr("dn") == 12) {
			mui.toast("开垦土地后才能逛农场～");
		} else {
			$(this).attr("href", "get_farm.html");
		}
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