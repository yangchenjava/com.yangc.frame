Ext.require([
    "Ext.form.*"
]);

Ext.onReady(function(){
	Ext.apply(Ext.form.field.VTypes, {
		/********************** 中英文数字下划线中划线 ***********************/
		basic: function(val, field){
			return /^[\w\u4E00-\u9FA5_\-]*$/.test(val);
		},
		basicText: "不能包含特殊字符",

		/********************** 密码 ***********************/
		password: function(val, field){
			return /^[\w_\-]{6,15}$/.test(val);
		},
		passwordText: "长度为6-15，不能包含特殊字符",

		/********************** 邮箱 ***********************/
		email: function(val, field){
			return /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/.test(val);
		},
		emailText: "请输入正确的邮箱地址，例如：user@example.com",

		/********************** URL ***********************/
		_url: function(val, field){
			return /[a-zA-z]+:\/\/[^\s]*/.test(val);
		},
		_urlText: "请输入正确的URL，例如：http://www.baidu.com",

		/********************** 国内固定电话号码(不是手机) ***********************/
		telephone: function(val, field){
			return /\d{3}-\d{8}|\d{4}-\d{7,8}/.test(val);
		},
		telephoneText: "请输入正确的固定电话号码，例如：010-XXXXXXXX",

		/********************** QQ ***********************/
		qq: function(val, field){
			return /[1-9][0-9]{4,}/.test(val);
		},
		qqText: "请输入正确的QQ号码",

		/********************** 中国邮政编码 ***********************/
		postcode: function(val, field){
			return /[1-9]\d{5}(?!\d)/.test(val);
		},
		postcodeText: "请输入正确的中国邮政编码，例如：110035",

		/********************** 中国居民身份证号 ***********************/
		chinaId: function(val, field){
			return /^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/.test(val);
		},
		chinaIdText: "请输入正确的中国居民身份证号，例如：21010520000101****"
	});
});
