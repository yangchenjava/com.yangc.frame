Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath("Ext.ux", basePath + "js/lib/ext4.2/ux");
Ext.require([
    "*",
    "Ext.ux.form.MultiSelect",
    "Ext.ux.form.ItemSelector"
]);

Ext.define("TopFrame", {
    extend: "Ext.data.Model",
    fields: [
		{name: "id",   	   type: "int"},
		{name: "menuName", type: "string"},
		{name: "menuUrl",  type: "string"}
    ]
});

Ext.onReady(function() {
	/** ------------------------------------- store ------------------------------------- */
	Ext.create("Ext.data.Store", {
		model: "TopFrame",
		proxy: {
			type: "ajax",
			url: basePath + "menuAction!showTopFrame.html"
		},
		autoLoad: true,
		listeners: {
    		"load": function(thiz, records, successful, eOpts){
				for (var i = 0, length = records.length; i < length; i++) {
					var url = basePath + records[i].get("menuUrl") + "?parentMenuId=" + records[i].get("id");
					body.add({
						title: records[i].get("menuName"),
						border: 0,
						margin: "-1 0 0 0",
						html: "<iframe src='" + url + "' width='100%' height='100%' frameborder='0' border='0' scrolling='auto'></iframe>"
					});
				}
				body.setActiveTab(0);
    		}
    	}
	});
	
	/** ------------------------------------- view ------------------------------------- */
	var head = Ext.create("Ext.panel.Panel", {
		region: "north",
		layout: "anchor",
		border: 0,
		height: 35,
		tbar: new Ext.Toolbar({
			style: {
				"background": "#B3DFDA",
				"background-image": "none !important"
			},
			height: 35,
			padding: "0 20 0 0",
			items: [
			    "->", {height: 20, text: "修改密码", handler: changePassword}, "-",
			    {height: 20, text: "注销", handler: logout}
			]
		})
	});
	
	var body = Ext.create("Ext.tab.Panel", {
		region: "center",
		layout: "anchor",
		border: 0,
		xtype: "tabpanel",
		minTabWidth: 150,
		plain: true,
		cls: "ui-tab-bar",
		bodyCls: "ui-tab-body",
		style: {
			"background": "#B3DFDA"
		},
		items: []
	});
	
	var foot = Ext.create("Ext.panel.Panel", {
		region: "south",
		layout: "anchor",
		border: 0,
        height: 25,
		tbar: new Ext.Toolbar({
			style: {
				"background": "#B3DFDA",
				"background-image": "none !important"
			},
			height: 25,
			items: ["->", "版权所有人: yangc", "->"]
		})
	});
	
	Ext.create("Ext.Viewport", {
		layout: {
            type: "border",
            padding: 1
        },
        items: [head, body, foot]
    });
	
	var panel_changePassword = Ext.create("Ext.form.Panel", {
        bodyPadding: 20,
        bodyBorder: false,
        frame: false,
		header: false,
        fieldDefaults: {
            labelAlign: "right",
            labelWidth: 60,
            anchor: "100%"
        },
        items: [
			{id: "password", xtype: "textfield", inputType:"password", fieldLabel: "原密码", allowBlank: false, invalidText: "请输入原密码！"},
			{id: "newPassword_1", xtype: "textfield", inputType:"password", fieldLabel: "新密码", allowBlank: false, invalidText: "请输入新密码！"},
			{id: "newPassword_2", xtype: "textfield", inputType:"password", fieldLabel: "确认密码", allowBlank: false, invalidText: "请输入确认密码！"}
		]
	});
    var window_changePassword = Ext.create("Ext.window.Window", {
		title: "修改密码",
		layout: "fit",
		width: 350,
		bodyMargin: 10,
		border: false,
		closable: true,
		closeAction: "hide",
		draggable: false,
		modal: true,
		plain: true,
		resizable: false,
		items: [panel_changePassword],
		buttonAlign: "right",
        buttons: [
            {text: "确定", handler: changePasswordHandler}, "-",
			{text: "取消", handler: function(){window_changePassword.hide();}}
        ]
	});
    
    /** ------------------------------------- handler ------------------------------------- */
	function changePassword(){
		panel_changePassword.getForm().reset();
		window_changePassword.show();
	}
	
	function logout(){
		message.confirm("是否确定注销用户？", function(){
			window.location.href = basePath + "userAction!logout.html";
		});
	}
	
	function changePasswordHandler(){
		if (!Ext.getCmp("password").isValid()) {
			message.error(Ext.getCmp("password").invalidText);
		} else if (!Ext.getCmp("newPassword_1").isValid()) {
			message.error(Ext.getCmp("newPassword_1").invalidText);
		} else if (!Ext.getCmp("newPassword_2").isValid()) {
			message.error(Ext.getCmp("newPassword_2").invalidText);
		} else {
			var password = Ext.getCmp("password").getValue();
			var newPassword_1 = Ext.getCmp("newPassword_1").getValue();
			var newPassword_2 = Ext.getCmp("newPassword_2").getValue();
			if (newPassword_1 != newPassword_2) {
				message.error("两次密码输入不同！");
				return;
			}
			$.post(basePath + "userAction!changePassword.html", {
				password: password,
				newPassword: newPassword_1
			}, function(data){
				if (data.success) {
					window_changePassword.hide();
					message.info(data.message, function(){
						window.location.href = basePath + "userAction!logout.html";
					});
				} else {
					message.error(data.message);
				}
			});
		}
	}
});
