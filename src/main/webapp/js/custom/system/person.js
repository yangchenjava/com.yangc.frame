Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath("Ext.ux", basePath + "js/lib/ext4.2/ux");
Ext.require(["*"]);

Ext.define("Person", {
    extend: "Ext.data.Model",
    fields: [
		{name: "id",   	   type: "int"},
		{name: "name",     type: "string"},
		{name: "sex",      type: "int"},
		{name: "phone",    type: "string"},
		{name: "spell",    type: "string"},
		
		{name: "deptId",   type: "int"},
		{name: "deptName", type: "string"},
		
		{name: "userId",   type: "int"},
		{name: "username", type: "string"},
		{name: "password", type: "string"},
		
		{name: "roleIds",  type: "string"}
    ]
});

Ext.define("Dept", {
	extend: "Ext.data.Model",
	fields: [
        {name: "id",   	   type: "int"},
        {name: "deptName", type: "string"}
    ]
});

Ext.define("Role", {
	extend: "Ext.data.Model",
	fields: [
	    {name: "id",   	   type: "int"},
	    {name: "roleName", type: "string"}
	]
});

Ext.onReady(function() {
	/** ------------------------------------- store ------------------------------------- */
	var store_personGrid = Ext.create("Ext.data.Store", {
		model: "Person",
		pageSize: 20,
		proxy: {
			type: "ajax",
			url: basePath + "personAction!getPersonListByPersonNameAndDeptId_page.html",
			reader: {
            	root: "dataGrid",
                totalProperty: "totalCount"
            }
		},
		autoLoad: true
	});
	
	var store_deptList = Ext.create("Ext.data.Store", {
		model: "Dept",
		proxy: {
			type: "ajax",
			url: basePath + "deptAction!getDeptList.html"
		},
		autoLoad: true
	});

	var store_roleList = Ext.create("Ext.data.Store", {
		model: "Role",
		proxy: {
			type: "ajax",
			url: basePath + "roleAction!getRoleList.html"
		},
		autoLoad: false
	});

	var store_roleIdsList = Ext.create("Ext.data.Store", {
		model: "Person",
		proxy: {
			type: "ajax",
			url: basePath + "personAction!getRoleIdsByUserId.html"
		},
		autoLoad: false
	});
	
	var store_spellList = Ext.create("Ext.data.Store", {
		model: "Person",
		proxy: {
			type: "ajax",
			url: basePath + "personAction!getPersonList.html"
		},
		autoLoad: true
	});
	
	/** ------------------------------------- view ------------------------------------- */
	var grid_person = Ext.create("Ext.grid.Panel", {
        renderTo: Ext.getBody(),
		store: store_personGrid,
		width: "100%",
		height: document.documentElement.clientHeight,
		border: 0,
        collapsible: false,
        multiSelect: false,
        scroll: false,
        viewConfig: {
            stripeRows: true,
            enableTextSelection: true
        },
        columns: [
            {text: "序号", width: 50, align: "center", xtype: "rownumberer"},
            {text: "用户名", flex: 1, align: "center", dataIndex: "username"},
            {text: "昵称", flex: 1, align: "center", dataIndex: "name"},
            {text: "性别", flex: 1, align: "center", dataIndex: "sex", renderer: function(value){
            	return value == 0 ? "女" : "男";
            }},
            {text: "电话", flex: 1, align: "center", dataIndex: "phone"},
            {text: "部门", flex: 1, align: "center", dataIndex: "deptName"}
        ],
        tbar: new Ext.Toolbar({
        	height: 30,
			items: [
		        {width: 5,  disabled: true},
		        {width: 55, text: "创建", handler: createPerson, icon: basePath + "js/lib/ext4.2/icons/add.gif"}, "-",
		        {width: 55, text: "修改", handler: updatePerson, icon: basePath + "js/lib/ext4.2/icons/edit_task.png"}, "-",
		        {width: 55, text: "删除", handler: deletePerson, icon: basePath + "js/lib/ext4.2/icons/delete.gif"},
		        {width: 200,  disabled: true},
		        {width: 180, id: "search_name", xtype: "combobox", emptyText: "昵称", store: store_spellList, forceSelection: true, editable: true, valueField: "name", displayField: "name", hideTrigger: true, queryMode: "local",
		        	listConfig: {
		    			getInnerTpl: function(){
		    				return "{name} ({spell})";
		    			}
		    		},
		        	listeners: {
			        	beforequery: function(queryPlan, eOpts){
			        		if (!queryPlan.forceAll) {
			        			var combo = queryPlan.combo, content = queryPlan.query.trim();
			        			if (content) {
			        				combo.store.filterBy(function(record, id){
			        					var nameSpell = record.get("name") + record.get("spell");
			        					return nameSpell.indexOf(content) != -1;
			        				});
			        				combo.expand();
			        			} else {
			        				combo.collapse();
			        			}
			        			return false;
			        		}
			        	}
			        }
		        },
		        {width: 150, id: "search_dept", xtype: "combobox", emptyText: "部门", store: store_deptList, forceSelection: true, editable: false, valueField: "id", displayField: "deptName"},
		        {width: 55, text: "搜索", handler: refreshPersonGrid, icon: basePath + "js/lib/ext4.2/icons/search.png"}
		    ]
        }),
        bbar: Ext.create("Ext.PagingToolbar", {
        	store: store_personGrid,
            displayInfo: true,
            displayMsg: "当前显示{0} - {1}条，共 {2} 条记录",
            emptyMsg: "当前没有任何记录"
        })
    });
	
	var panel_addOrUpdate_person = window.top.Ext.create("Ext.form.Panel", {
        bodyPadding: 20,
        bodyBorder: false,
        frame: false,
		header: false,
        fieldDefaults: {
            labelAlign: "right",
            labelWidth: 70,
            anchor: "100%"
        },
        items: [
			{xtype: "hidden", name: "id"},
			{xtype: "hidden", name: "userId"},
			{xtype: "container", layout:"column", items: [
                {xtype: "container", columnWidth:.5, layout: "anchor", items: [
                    {id: "addOrUpdate_username", name: "username", xtype: "textfield", fieldLabel: "用户名", allowBlank: false, invalidText: "请输入用户名！"}
                ]},
                {xtype: "container", columnWidth:.5, layout: "anchor", items: [
                    {id: "addOrUpdate_name", name: "name", xtype: "textfield", fieldLabel: "昵称", allowBlank: false, invalidText: "请输入昵称！"}
                ]}
            ]},
            {xtype: "container", layout:"column", items: [
                {xtype: "container", columnWidth:.5, layout: "anchor", items: [
                    {id: "addOrUpdate_password_1", name: "password", xtype: "textfield", inputType:"password", fieldLabel: "密码", allowBlank: false, invalidText: "请输入密码！"}
                ]},
                {xtype: "container", columnWidth:.5, layout: "anchor", items: [
                    {id: "addOrUpdate_password_2", xtype: "textfield", inputType:"password", fieldLabel: "确认密码", allowBlank: false, invalidText: "请输入确认密码！", validator: validatorPasswordRepeatHandler}
                ]}
            ]},
            {id: "addOrUpdate_sex", xtype: "radiogroup", fieldLabel: "性别", allowBlank: false, invalidText: "请选择性别！", items: [
                {boxLabel: "男", name: "sex", inputValue: 1, checked: true},
                {boxLabel: "女", name: "sex", inputValue: 0}
            ]},
            {xtype: "container", layout:"column", items: [
                {xtype: "container", columnWidth:.5, layout: "anchor", items: [
                    {id: "addOrUpdate_phone", name: "phone", xtype: "textfield", fieldLabel: "电话", allowBlank: false, invalidText: "请输入电话！"}
                ]},
                {xtype: "container", columnWidth:.5, layout: "anchor", items: [
                    {id: "addOrUpdate_dept", name: "deptId", xtype: "combobox", fieldLabel: "部门", allowBlank: false, invalidText: "请选择部门！", store: store_deptList, forceSelection: true, editable: false, valueField: "id", displayField: "deptName"}
                ]}
            ]},
            {id: "addOrUpdate_role", name: "roleIds", xtype: "itemselector", fieldLabel: "角色", allowBlank: false, labelSeparator: "", invalidText: "请选择角色！",
            	height: 120, imagePath: basePath + "js/lib/ext4.2/ux/css/images/", buttons: ["add", "remove"], store: store_roleList, valueField: "id", displayField: "roleName"
        	}
		]
	});
    var window_addOrUpdate_person = window.top.Ext.create("Ext.window.Window", {
		layout: "fit",
		width: 500,
		bodyMargin: 10,
		border: false,
		closable: true,
		closeAction: "hide",
		modal: true,
		plain: true,
		resizable: false,
		items: [panel_addOrUpdate_person],
		buttonAlign: "right",
        buttons: [
            {text: "确定", handler: addOrUpdatePersonHandler}, "-",
			{text: "取消", handler: function(){window_addOrUpdate_person.hide();}}
        ]
	});
    
    /** ------------------------------------- handler ------------------------------------- */
    function refreshPersonGrid(){
    	var name = Ext.getCmp("search_name").getValue() ? Ext.getCmp("search_name").getValue() : "";
    	var deptId = Ext.getCmp("search_dept").getValue() ? Ext.getCmp("search_dept").getValue() : 0;
    	
    	grid_person.getSelectionModel().deselectAll();
    	store_personGrid.currentPage = 1;
    	store_personGrid.proxy.extraParams = {"name": encodeURIComponent(name), "deptId": deptId};
		store_personGrid.load();
    }
    
	function createPerson(){
		panel_addOrUpdate_person.getForm().reset();
		window.top.Ext.getCmp("addOrUpdate_role").fromField.store.removeAll();
		store_roleList.load();
		window.top.Ext.getCmp("addOrUpdate_password_1").show();
		window.top.Ext.getCmp("addOrUpdate_password_2").show();
		window_addOrUpdate_person.setTitle("创建");
		window_addOrUpdate_person.show();
	}
	
	function updatePerson(){
		if (grid_person.getSelectionModel().hasSelection()) {
			panel_addOrUpdate_person.getForm().reset();
			window.top.Ext.getCmp("addOrUpdate_role").fromField.store.removeAll();
			store_roleList.load();
			
			var record = grid_person.getSelectionModel().getSelection()[0];
			panel_addOrUpdate_person.getForm().loadRecord(record);
			window.top.Ext.getCmp("addOrUpdate_password_1").hide();
			window.top.Ext.getCmp("addOrUpdate_password_2").setValue(record.get("password"));
			window.top.Ext.getCmp("addOrUpdate_password_2").hide();

			store_roleIdsList.load({
				params: {"userId": record.get("userId")},
		   	 	scope: this,
		   	    callback: function(records, operation, success){
		   	    	window.top.Ext.getCmp("addOrUpdate_role").setValue(records[0].get("roleIds"));
		   	    }
	   	 	});
			window_addOrUpdate_person.setTitle("修改");
			window_addOrUpdate_person.show();
		} else {
			message.info("请先选择数据再操作！");
		}
	}
	
	function deletePerson(){
		if (grid_person.getSelectionModel().hasSelection()) {
			message.confirm("是否删除记录？", function(){
				var record = grid_person.getSelectionModel().getSelection()[0];
				$.post(basePath + "personAction!delPerson.html", {
					id: record.get("id"),
				}, function(data){
					if (data.success) {
						window_addOrUpdate_person.hide();
						message.info(data.message);
						refreshPersonGrid();
					} else {
						message.error(data.message);
					}
				});
			});
		} else {
			message.info("请先选择数据再操作！");
		}
	}
	
	function addOrUpdatePersonHandler(){
		var username = window.top.Ext.getCmp("addOrUpdate_username");
		var name = window.top.Ext.getCmp("addOrUpdate_name");
		var password_1 = window.top.Ext.getCmp("addOrUpdate_password_1");
		var password_2 = window.top.Ext.getCmp("addOrUpdate_password_2");
		var sex = window.top.Ext.getCmp("addOrUpdate_sex");
		var phone = window.top.Ext.getCmp("addOrUpdate_phone");
		var dept = window.top.Ext.getCmp("addOrUpdate_dept");
		var role = window.top.Ext.getCmp("addOrUpdate_role");
		if (!username.isValid()) {
			message.error(username.invalidText);
		} else if (!name.isValid()) {
			message.error(name.invalidText);
		} else if (!password_1.isValid()) {
			message.error(password_1.invalidText);
		} else if (!password_2.isValid()) {
			message.error(password_2.invalidText);
		} else if (!sex.isValid()) {
			message.error(sex.invalidText);
		} else if (!phone.isValid()) {
			message.error(phone.invalidText);
		} else if (!dept.isValid()) {
			message.error(dept.invalidText);
		} else if (!role.isValid()) {
			message.error(role.invalidText);
		} else {
			panel_addOrUpdate_person.getForm().submit({
				url: basePath + "personAction!addOrUpdatePerson.html",
				method: "POST",
				success: function(form, action){
					window_addOrUpdate_person.hide();
					message.info(action.result.msg);
					refreshPersonGrid();
				},
				failure: function(form, action){
					message.error(action.result.msg);
				}
			});
		}
	}
	
	function validatorPasswordRepeatHandler(){
		var password_1 = window.top.Ext.getCmp("addOrUpdate_password_1");
		var password_2 = window.top.Ext.getCmp("addOrUpdate_password_2");
    	if (password_2.getValue().length > 0 && password_1.getValue() != password_2.getValue()) {
    		password_2.invalidText = "两次输入的密码不相同！";
			return password_2.invalidText;
		}
    	return true;
    }
	
	top_window_destroy = function(){
		window_addOrUpdate_person.destroy();
	};
});
