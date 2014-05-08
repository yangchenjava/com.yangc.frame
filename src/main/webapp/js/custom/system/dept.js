Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath("Ext.ux", basePath + "js/lib/ext4.2/ux");
Ext.require(["*"]);

Ext.define("Dept", {
    extend: "Ext.data.Model",
    fields: [
		{name: "id",   	     type: "int"},
		{name: "deptName",   type: "string"},
		{name: "serialNum",  type: "int"},
		{name: "createTime", type: "date"}
    ]
});

Ext.onReady(function() {
	/** ------------------------------------- store ------------------------------------- */
	var store_deptGrid = Ext.create("Ext.data.Store", {
		model: "Dept",
		pageSize: 20,
		proxy: {
			type: "ajax",
			url: basePath + "deptAction!getDeptList_page.html",
			reader: {
            	root: "dataGrid",
                totalProperty: "totalCount"
            }
		},
		autoLoad: true
	});
	
	/** ------------------------------------- view ------------------------------------- */
	var grid_dept = Ext.create("Ext.grid.Panel", {
        renderTo: Ext.getBody(),
		store: store_deptGrid,
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
            {text: "部门名称", flex: 2,   align: "center", dataIndex: "deptName"},
            {text: "排序",    flex: 1,   align: "center", dataIndex: "serialNum"},
            {text: "创建时间", flex: 2,   align: "center", dataIndex: "createTime", renderer: function(value){
            	return Ext.Date.format(value, "Y-m-d H:i:s");
            }}
        ],
        tbar: new Ext.Toolbar({
        	height: 30,
			items: [
		        {width: 5,  disabled: true},
		        {width: 55, text: "创建", handler: createDept, icon: basePath + "js/lib/ext4.2/icons/add.gif"}, "-",
		        {width: 55, text: "修改", handler: updateDept, icon: basePath + "js/lib/ext4.2/icons/edit_task.png"}, "-",
		        {width: 55, text: "删除", handler: deleteDept, icon: basePath + "js/lib/ext4.2/icons/delete.gif"}
		    ]
        }),
        bbar: Ext.create("Ext.PagingToolbar", {
        	store: store_deptGrid,
            displayInfo: true,
            displayMsg: "当前显示{0} - {1}条，共 {2} 条记录",
            emptyMsg: "当前没有任何记录"
        })
    });
	
	var panel_addOrUpdate_dept = window.top.Ext.create("Ext.form.Panel", {
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
			{id: "addOrUpdate_deptName",  name: "deptName",  xtype: "textfield", fieldLabel: "部门名称", allowBlank: false, invalidText: "请输入部门名称！"},
			{id: "addOrUpdate_serialNum", name: "serialNum", xtype: "numberfield", fieldLabel: "顺序",  allowBlank: false, invalidText: "请输入顺序！", minValue: 1}
		]
	});
    var window_addOrUpdate_dept = window.top.Ext.create("Ext.window.Window", {
		layout: "fit",
		width: 500,
		bodyMargin: 10,
		border: false,
		closable: true,
		closeAction: "hide",
		modal: true,
		plain: true,
		resizable: false,
		items: [panel_addOrUpdate_dept],
		buttonAlign: "right",
        buttons: [
            {text: "确定", handler: addOrUpdateDeptHandler}, "-",
			{text: "取消", handler: function(){window_addOrUpdate_dept.hide();}}
        ]
	});
    
    /** ------------------------------------- handler ------------------------------------- */
    function refreshDeptGrid(){
    	grid_dept.getSelectionModel().deselectAll();
    	store_deptGrid.currentPage = 1;
		store_deptGrid.load();
    }
    
	function createDept(){
		panel_addOrUpdate_dept.getForm().reset();
		window_addOrUpdate_dept.setTitle("创建");
		window_addOrUpdate_dept.show();
	}
	
	function updateDept(){
		if (grid_dept.getSelectionModel().hasSelection()) {
			panel_addOrUpdate_dept.getForm().reset();
			var record = grid_dept.getSelectionModel().getSelection()[0];
			panel_addOrUpdate_dept.getForm().loadRecord(record);
			window_addOrUpdate_dept.setTitle("修改");
			window_addOrUpdate_dept.show();
		} else {
			message.info("请先选择数据再操作！");
		}
	}
	
	function deleteDept(){
		if (grid_dept.getSelectionModel().hasSelection()) {
			message.confirm("是否删除记录？", function(){
				var record = grid_dept.getSelectionModel().getSelection()[0];
				$.post(basePath + "deptAction!delDept.html", {
					id: record.get("id"),
				}, function(data){
					if (data.success) {
						window_addOrUpdate_dept.hide();
						message.info(data.message);
						refreshDeptGrid();
					} else {
						message.error(data.message);
					}
				});
			});
		} else {
			message.info("请先选择数据再操作！");
		}
	}
	
	function addOrUpdateDeptHandler(){
		var deptName = window.top.Ext.getCmp("addOrUpdate_deptName");
		var serialNum = window.top.Ext.getCmp("addOrUpdate_serialNum");
		if (!deptName.isValid()) {
			message.error(deptName.invalidText);
		} else if (!serialNum.isValid()) {
			message.error(serialNum.invalidText);
		} else {
			panel_addOrUpdate_dept.getForm().submit({
				url: basePath + "deptAction!addOrUpdateDept.html",
				method: "POST",
				success: function(form, action){
					window_addOrUpdate_dept.hide();
					message.info(action.result.msg);
					refreshDeptGrid();
				},
				failure: function(form, action){
					message.error(action.result.msg);
				}
			});
		}
	}
	
	top_window_destroy = function(){
		window_addOrUpdate_dept.destroy();
	};
});
