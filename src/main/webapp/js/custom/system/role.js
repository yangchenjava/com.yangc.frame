Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath("Ext.ux", basePath + "js/lib/ext4.2/ux");
Ext.require(["*"]);

Ext.define("Role", {
    extend: "Ext.data.Model",
    fields: [
		{name: "id",   	     type: "int"},
		{name: "roleName",   type: "string"},
		{name: "createTime", type: "date"}
    ]
});

Ext.define("AuthTree", {
    extend: "Ext.data.Model",
    fields: [
        {name: "id",       type: "int"},
        {name: "text",     type: "string"},
        {name: "leaf",     type: "boolean"},
        {name: "menuId",   type: "int"},
        {name: "menuName", type: "string"},
		{name: "all",      type: "boolean"},
		{name: "sel",      type: "boolean"},
		{name: "add",      type: "boolean"},
		{name: "upd",      type: "boolean"},
		{name: "del",      type: "boolean"}
    ]
});

Ext.onReady(function() {
	/** ------------------------------------- store ------------------------------------- */
	var store_roleGrid = Ext.create("Ext.data.Store", {
		model: "Role",
		pageSize: 20,
		proxy: {
			type: "ajax",
			url: basePath + "roleAction!getRoleList_page.html",
			reader: {
            	root: "dataGrid",
                totalProperty: "totalCount"
            }
		},
		autoLoad: true
	});
	
	var store_authTree = Ext.create("Ext.data.TreeStore", {
        model: "AuthTree",
        nodeParam: "parentMenuId",
        proxy: {
            type: "ajax",
            url: basePath + "aclAction!getAuthTreeList.html"
        },
        root: {
        	id: "0",
	        text: "系统菜单",
	        menuId: "0",
	        menuName: "系统菜单",
	    },
        autoLoad: false
    });
	
	/** ------------------------------------- view ------------------------------------- */
	var grid_role = Ext.create("Ext.grid.Panel", {
        renderTo: Ext.getBody(),
		store: store_roleGrid,
		width: "100%",
		height: document.documentElement.clientHeight,
		border: false,
        collapsible: false,
        multiSelect: false,
        scroll: false,
        viewConfig: {
            stripeRows: true,
            enableTextSelection: true
        },
        columns: [
            {text: "序号",    width: 50, align: "center", xtype: "rownumberer"},
            {text: "角色名称", flex: 1,   align: "center", dataIndex: "roleName"},
            {text: "创建时间", flex: 1,   align: "center", dataIndex: "createTime", renderer: function(value){
            	return Ext.Date.format(value, "Y-m-d H:i:s");
            }}
        ],
        tbar: new Ext.Toolbar({
        	height: 30,
			items: [
		        {width: 5,  disabled: true},
		        {width: 55, text: "创建", handler: createRole, icon: basePath + "js/lib/ext4.2/icons/add.gif"}, "-",
		        {width: 55, text: "修改", handler: updateRole, icon: basePath + "js/lib/ext4.2/icons/edit_task.png"}, "-",
		        {width: 55, text: "删除", handler: deleteRole, icon: basePath + "js/lib/ext4.2/icons/delete.gif"}, "-",
		        {width: 55, text: "授权", handler: authorizeRole, icon: basePath + "js/lib/ext4.2/icons/user_suit.png"}
		    ]
        }),
        bbar: Ext.create("Ext.PagingToolbar", {
        	store: store_roleGrid,
            displayInfo: true,
            displayMsg: "当前显示{0} - {1}条，共 {2} 条记录",
            emptyMsg: "当前没有任何记录"
        })
    });
	
	var panel_addOrUpdate_role = window.top.Ext.create("Ext.form.Panel", {
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
			{id: "addOrUpdate_roleName", name: "roleName", xtype: "textfield", fieldLabel: "角色名称", allowBlank: false, invalidText: "请输入角色名称！"}
		]
	});
    var window_addOrUpdate_role = window.top.Ext.create("Ext.window.Window", {
		layout: "fit",
		width: 500,
		bodyMargin: 10,
		border: false,
		closable: true,
		closeAction: "hide",
		modal: true,
		plain: true,
		resizable: false,
		items: [panel_addOrUpdate_role],
		buttonAlign: "right",
        buttons: [
            {text: "确定", handler: addOrUpdateRoleHandler}, "-",
			{text: "取消", handler: function(){window_addOrUpdate_role.hide();}}
        ]
	});
    
    var panel_addOrUpdate_acl = window.top.Ext.create("Ext.tree.Panel", {
        bodyBorder: false,
        frame: false,
		header: false,
		useArrows: true,
        rootVisible: false,
        store: store_authTree,
        columns: [
			{xtype: "treecolumn", text: "菜单", dataIndex: "menuName", flex: 2, draggable: false, sortable: false, menuDisabled: true},
			{xtype: "checkcolumn", text: "全选", dataIndex: "all", flex: 1, draggable: false, sortable: false, menuDisabled: true},
			{xtype: "checkcolumn", text: "查询", dataIndex: "sel", flex: 1, draggable: false, sortable: false, menuDisabled: true},
			{xtype: "checkcolumn", text: "添加", dataIndex: "add", flex: 1, draggable: false, sortable: false, menuDisabled: true},
			{xtype: "checkcolumn", text: "修改", dataIndex: "upd", flex: 1, draggable: false, sortable: false, menuDisabled: true},
			{xtype: "checkcolumn", text: "删除", dataIndex: "del", flex: 1, draggable: false, sortable: false, menuDisabled: true}
        ],
		listeners: {
			itemmouseup: addOrUpdateAclHandler
		}
	});
    var window_addOrUpdate_acl = window.top.Ext.create("Ext.window.Window", {
		layout: "fit",
		title: "授权",
		width: 750,
		height: 500,
		bodyMargin: 10,
		border: false,
		closable: true,
		closeAction: "hide",
		modal: true,
		plain: true,
		resizable: false,
		items: [panel_addOrUpdate_acl],
		buttonAlign: "right",
        buttons: [
			{text: "确定", handler: function(){window_addOrUpdate_acl.hide();}}
        ]
	});
    
    /** ------------------------------------- handler ------------------------------------- */
    function refreshRoleGrid(){
    	grid_role.getSelectionModel().deselectAll();
    	store_roleGrid.currentPage = 1;
		store_roleGrid.load();
    }
    
	function createRole(){
		panel_addOrUpdate_role.getForm().reset();
		window_addOrUpdate_role.setTitle("创建");
		window_addOrUpdate_role.show();
	}
	
	function updateRole(){
		if (grid_role.getSelectionModel().hasSelection()) {
			panel_addOrUpdate_role.getForm().reset();
			var record = grid_role.getSelectionModel().getSelection()[0];
			panel_addOrUpdate_role.getForm().loadRecord(record);
			window_addOrUpdate_role.setTitle("修改");
			window_addOrUpdate_role.show();
		} else {
			message.info("请先选择数据再操作！");
		}
	}
	
	function deleteRole(){
		if (grid_role.getSelectionModel().hasSelection()) {
			message.confirm("是否删除记录？", function(){
				var record = grid_role.getSelectionModel().getSelection()[0];
				$.post(basePath + "roleAction!delRole.html", {
					id: record.get("id"),
				}, function(data){
					if (data.success) {
						window_addOrUpdate_role.hide();
						message.info(data.message);
						refreshRoleGrid();
					} else {
						message.error(data.message);
					}
				});
			});
		} else {
			message.info("请先选择数据再操作！");
		}
	}
	
	function authorizeRole(){
		if (grid_role.getSelectionModel().hasSelection()) {
			var record = grid_role.getSelectionModel().getSelection()[0];
			store_authTree.proxy.extraParams = {"roleId": record.get("id")};
			store_authTree.load({
				scope: this,
		   	    callback: function(records, operation, success){
		   	    	store_authTree.getRootNode().expand();
		   	    }
			});
			window_addOrUpdate_acl.show();
		} else {
			message.info("请先选择数据再操作！");
		}
	}
	
	function addOrUpdateRoleHandler(){
		var roleName = window.top.Ext.getCmp("addOrUpdate_roleName");
		if (!roleName.isValid()) {
			message.error(roleName.invalidText);
		} else {
			panel_addOrUpdate_role.getForm().submit({
				url: basePath + "roleAction!addOrUpdateRole.html",
				method: "POST",
				success: function(form, action){
					window_addOrUpdate_role.hide();
					message.info(action.result.msg);
					refreshRoleGrid();
				},
				failure: function(form, action){
					message.error(action.result.msg);
				}
			});
		}
	}
	
	function addOrUpdateAclHandler(thiz, record, item, index, e, eOpts){
		var permission = -1, allow = -1, cellIndex = e.target.offsetParent.cellIndex;
		switch (cellIndex) {
		case 1:
			permission = 4;
			if (record.get("all")) {
				record.set("sel", true);
				record.set("add", true);
				record.set("upd", true);
				record.set("del", true);
				allow = 1;
			} else {
				record.set("sel", false);
				record.set("add", false);
				record.set("upd", false);
				record.set("del", false);
				allow = 0;
			}
			break;
		case 2:
			permission = 3;
			if (record.get("sel")) {
				allow = 1;
			} else {
				record.set("all", false);
				allow = 0;
			}
			break;
		case 3:
			permission = 2;
			if (record.get("add")) {
				allow = 1;
			} else {
				record.set("all", false);
				allow = 0;
			}
			break;
		case 4:
			permission = 1;
			if (record.get("upd")) {
				allow = 1;
			} else {
				record.set("all", false);
				allow = 0;
			}
			break;
		case 5:
			permission = 0;
			if (record.get("del")) {
				allow = 1;
			} else {
				record.set("all", false);
				allow = 0;
			}
			break;
		}
		
		if (permission != -1 && allow != -1) {
			$.post(basePath + "aclAction!addOrUpdateAcl.html", {
				roleId: grid_role.getSelectionModel().getSelection()[0].get("id"),
				menuId: record.get("menuId"),
				permission: permission,
				allow: allow
			}, function(data){
				if (!data.success) {
					message.error(data.message);
				}
			});
		}
	}
	
	top_window_destroy = function(){
		window_addOrUpdate_role.destroy();
		window_addOrUpdate_acl.destroy();
	};
});
