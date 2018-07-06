var ws = null,
	wp = null,
	wo = null;
// H5 plus事件处理
function plusReady() {
	ws = plus.webview.currentWebview();
	wo = ws.opener();
}
if(window.plus) {
	plusReady();
} else {
	document.addEventListener('plusready', plusReady, false);
}

/**
 * 打开webview动画窗口
 * 打开链接,例如：<a id="page.html" onclick="animateWindow(this.id, this, 'zoom-fade-out')" params="{json_key:'json_value'}" >
 * @param {string} page	打开的页面
 * @param {Object} ths	this
 * @param {string} type	动画效果类型
 */
function animateWindow(page, ths, type) {
	var params = $(ths).attr('params');
	params = eval('(' + params + ')');
	wp || (wp = plus.webview.create(page, page, {
		scrollIndicator: 'none',
		scalable: false,
		popGesture: 'none'
	}, params));
	wp.addEventListener('close', function() {
		wp = null;
	}, false);
	wp.show(type);
}

/**
 * 打开webview动画窗口
 * @param {string} page	打开的页面
 * @param {Object} params	参数
 * @param {string} type	动画效果类型
 */
function animateWebview(page, params, type) {
	wp || (wp = plus.webview.create(page, page, {
		scrollIndicator: 'none',
		scalable: false,
		popGesture: 'none'
	}, params));
	wp.addEventListener('close', function() {
		wp = null;
	}, false);
	wp.show(type);
}

// 关闭窗口
var _back = window.back;

function preateBack() {
	wp && (wp.close(), wp = null);
	_back();
}
window.back = preateBack;

function checkLogin(page) {
	var accessToken = plus.storage.getItem('accessToken');
	
	var post_data = 'format=json&v=v1&accessToken=' + accessToken + '&method=user.login.check'
	mui.ajax({
		url: BASE_URL,
		type: "POST",
		data: post_data,
		dataType: 'json',
		success: function(result) {
			if(result.errorcode == 20001 && result.msg == 'invalid token') {
				plus.storage.clear();
				animateWebview('/html/login.html', {}, 'type');
				return;
			}
		},
		error: function(xhr, type, errerThrown) {
			mui.toast(errerThrown)
		}
	})
	
	if(mui.isEmptyObject(accessToken)) {
		animateWebview('/html/login.html', {}, 'type');
		return;
	}
	if(accessToken.length != 64) {
		animateWebview('/html/login.html', {}, 'type');
		return;
	}
	if(page != '') {
		plus.storage.setItem('jump_page', page);
		clicked(page, true);
	}
	return;
}

