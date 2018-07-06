$(function() {
	(function() {
		var  userInfo = JSON.parse(localStorage.getItem("login"));	//用户信息
		var usergdinfo =JSON.parse(localStorage.getItem("localSgdNum"))
		
		var  user_id = userInfo.data.user_id; // 用户id
		var  user_tk = userInfo.data.accessToken;
		var  user_jf = $(".txt-box").attr("jf");
		var  playAct = null; // 动作定时器
		var  time = null; //  成熟倒计时		
		
		
		
//		function ats(){
//			console.log(arr);
//			
//		}
		
		
		$(".residue").text(usergdinfo.data.gdnumber); //用户贯豆

		function get_jf(){ //获取用户积分
			var token ={"accessToken":user_tk};
				$.ajax({
					url:"http://120.79.55.141/GuanDou/app/getPointsInfo.do",
					type:"POST",
					dataType:"json",
					data: JSON.stringify(token),
					contentType:"application/json",
					global:false,
					success:function(jf){
						//console.log(jf.data.point_count);
						$(".txt-box").attr("jf",jf.data.point_count);
						
				
					
					
					}
				})				
		}
		get_jf();
//		count_time();
		get_land_count();

		function get_land_count() {       //获取土地信息
			var queryLand = {
				'ownerId': user_id
			}
			$.ajax({
				url: "http://120.79.55.141/GuanDou/gameService/queryLandInfo.do",
				type: "POST",
				dataType: "json",
				data: JSON.stringify(queryLand),
				contentType: "application/json",
				success: function(result) {
					if(result.success) {
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
				$(".field").attr("dn",result.data.zt0); //可开垦数量
				$(".field").attr("jg",result.data.landPrice); //土地价格
				if($(".field").attr("dn") == 0){
					$(".field").attr("dn","12");
				}else{
					$(".field").attr("dn",result.data.zt0);
				}
				set_land();
				}
			})
		}		

		function set_land(){    //设置土地状态
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
					var landInfo = result.data;
						//$(this).attr("ID", landInfo[index].ID);
					if(!$.isEmptyObject(landInfo))
					{
						$(".field li").each(function(index, dou) {	
							$(this).attr("ID", landInfo[index].ID);
							$(this).attr("STATUS", landInfo[index].STATUS);
							$(this).attr("JS", landInfo[index].JS);
							$(this).attr("SF", landInfo[index].SF);
							if($(this).attr("status") != 0) { //已经开垦
								if($(this).attr("SF") == 1){
									$(this).find(".max").attr("src","../images/fertilizer_di.png");

								}
								if($(this).attr("JS") == 1){
									$(this).find(".max").attr("src","../images/water_di.png");
								}
								if($(this).attr("JS") == 1&& $(this).attr("SF") == 1){
									$(this).find(".max").attr("src","../images/full_di.png");
								}
							}
							
							
							if($(this).attr("status") == 1) {
								$(this).addClass("dou young").removeClass("land").find(".status").remove();
								if($(this).hasClass("on")){
									var  that = $(this); 
									time = setInterval(function(nt,rt){
										//nt =  landInfo[index].NOWDATE; // 服务器上的时间
										nt =  (new Date()).getTime(); //现在时间
										rt =  landInfo[index].RECEIVEDATE //成熟时间
										that.attr("djs",parseInt(rt*0.001) - parseInt(nt*0.001));	
										$("#Timetxt").text(formatDuring($(".on").attr("djs")));
										if(that.attr("djs") <= 0){
											$("#Timetxt").text("0");
											that.removeAttr("djs");
											clearInterval(time);
										//	updateLadb();
										}															
									},1000)
								}
							}
							else if($(this).attr("status") == 3)
							{
								$(this).addClass("dou grow").removeClass("land").find(".status").remove();
								$(this).find(".bean").attr("src","../images/dou_grow.png");
							 }
							else if($(this).attr("status") == 2) 
							{
								$(this).addClass("dou ripe").removeClass("land").find(".status").remove();
								$(this).find(".bean").attr("src","../images/dou_ripe.png");
							}
						})
					}
					else
					{
						buy_land()
					}
				}
			})
		}
					
		// 选
		$(".field li").each(function() {
			$(this).click(function() {
				if($(this).hasClass("dou") && $(".field").attr("cz") !== "ing") {

					$(this).addClass("on").siblings().removeClass("on");
					clearTimeout(playAct);
					clearInterval(time);
				}
				if($(this).attr("status") == 0) { //未开垦弹出消息框是否购买
					buy_land()
				}
			})
		})

	//

/*
	function count_time(){
		$(".field li").each(function(){
			var  that = $(this); 
			time = setInterval(function getTime(nt,rt){
				nt =  (new Date()).getTime(); //现在时间
				rt = that.attr("rd"); //成熟时间
				that.attr("djs",parseInt(rt*0.001) - parseInt(nt*0.001));
				if(that.hasClass("on")){
					if(that.attr("djs") <= 0){
						$("#Timetxt").text("0");
						that.removeAttr("djs");
						clearInterval(time);
				//		updateLadb();
					}else{
						$("#Timetxt").text(formatDuring($(".on").attr("djs")))
					}
				}
			},1000)			
		})		
	}
*/
	



	//动作 
		$(".tool_bg li").each(function() {
			$(this).click(function(toolid,action) {
				var toolid = $(this).attr("id");	
				switch(toolid){
					case "water" :
					action = 0;
					break;
					case "fertilizer" :
					action = 1;
					break;
					case "reap" :
					action = 2;
				}
				if($(".field").attr("cz") =="ing"){
					$(".mui-toast-container").hide().remove();
					mui.toast("操作中~");
				}
				else{
					if(toolid =="reap" && $(".on").attr("status") != "2"){
						$(".mui-toast-container").hide().remove();
						mui.toast("贯豆尚未成熟 ！");
					}
					else{
						$(".on").append('<img src="../images/' + toolid + "_action.gif" + '"  class="act ' + toolid + '_action"/> ')
						.attr("action", action)
						$(".field").attr("cz","ing"); // 动作执行中
						playAct = setTimeout(function() {
									$(".act").fadeOut().remove();
									$(".field").attr("cz","over"); //动作完成
							}, 1800);
						var _landId = $('.on').attr('id');
						var _type = $(".on").attr("action");
						var  saveOperation = {
							'operationType': _type,
							'landId': _landId,
						};					
						$.ajax({
								url: "http://120.79.55.141/GuanDou/gameService/saveOperation.do",
								type: "POST",
								dataType: "json",
								data: JSON.stringify(saveOperation),
								contentType: "application/json",
								success: function(result) {
									set_land();
									$(".mui-toast-container").hide().remove();
									mui.toast(result.message);
								}
						})								
					}
				}
			})
		})	

		function buy_land (){
			mui.prompt('确定开垦土地吗？', '购买土地数量', 'title', ['确定', '取消'], null, 'div');
					document.querySelector('.mui-popup-input input').type = 'number';
					document.querySelector('.mui-popup-input input').addEventListener("input",function(e){
						//alert(e.currentTarget.value);
						if(e.currentTarget.value > parseInt($(".field").attr("dn"))){
							$(".mui-toast-container").hide().remove();
							mui.toast("目前最多还能购买" + $(".field").attr("dn") +"块土地");
							e.currentTarget.value = $(".field").attr("dn");
						}
					})
					mui(".mui-popup-buttons").on("tap", ".mui-popup-button:nth-child(1)", function() {
						var buyLand = {
							'ownerId': user_id,
							'landCount': $('.mui-popup-input input').val()
						}
					if( parseInt($(".txt-box").attr("jf")) - (parseInt($(".field").attr("jg")) * buyLand.landCount) >=0)
					{
						$.ajax({
							url: "http://120.79.55.141/GuanDou/gameService/buyLand.do",
							type: "POST",
							dataType: "json",
							data: JSON.stringify(buyLand),
							contentType: "application/json",
							success: function(result) {
									updateLadb();
									mui.toast(result.message);
							}										
						})								
					}
					else{
						mui.toast("积分不足");
					}
				})
				mui(".mui-popup-buttons").on("tap", ".mui-popup-button:nth-child(2)", function() {
					if($(".field").attr("dn") == 0){
						updateLadb();
					}
				})
		}
	})()
	
	//毫秒转换时间
	function formatDuring(mss) {
	    var days = parseInt(mss / (1 * 60 * 60 * 24));
	    var hours = parseInt((mss % (1 * 60 * 60 * 24)) / (1 * 60 * 60));
	    var minutes = parseInt((mss % (1 * 60 * 60)) / (1 * 60));
	    var seconds = (mss % (1 * 60)) / 1;
	    return days + " 天 " + hours + " 小时 " + minutes + " 分钟 " + seconds + " 秒 ";
	}
})