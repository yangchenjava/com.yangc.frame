Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath("Ext.ux", basePath + "js/lib/ext4.2/ux");
Ext.require(["*"]);

Ext.define("MainFrame", {
    extend: "Ext.data.Model",
    fields: [
		{name: "id",   	       type: "int"},
		{name: "menuName",     type: "string"},
		{name: "menuUrl",  	   type: "string"},
		{name: "childRenMenu", type: "object"}
    ]
});

Ext.onReady(function() {
	/** ------------------------------------- store ------------------------------------- */
	Ext.create("Ext.data.Store", {
		model: "MainFrame",
		proxy: {
			type: "ajax",
			url: basePath + "menuAction!showMainFrame.html",
			extraParams: {"parentMenuId": parentMenuId}
		},
		autoLoad: true,
		listeners: {
    		"load": function(thiz, records, successful, eOpts){
				for (var i = 0, recordsLength = records.length; i < recordsLength; i++) {
					var html = "";
					var childRenMenu = records[i].get("childRenMenu");
					for (var j = 0, childRenMenuLength = childRenMenu.length; j < childRenMenuLength; j++) {
						var id = childRenMenu[j].id;
						var title = childRenMenu[j].menuName;
						var url = basePath + childRenMenu[j].menuUrl;
						html += "<div class='menu' onclick='addTab(\"" + id + "\", \"" + title + "\", \"" + url + "\")'>" + title + "</div>";
					}
					left.add({
						title: records[i].get("menuName"),
						html: html
					});
				}
    		}
    	}
	});
	
	/** ------------------------------------- view ------------------------------------- */
	var left = Ext.create("Ext.panel.Panel", {
		title: "你好，" + personName,
		region: "west",
		layout: "accordion",
		width: "20%",
		minWidth: 200,
		collapsible: true,
		split: true,
		items: []
	});
	
	var right = Ext.create("Ext.tab.Panel", {
		region: "center",
		layout: "anchor",
		xtype: "tabpanel",
		minTabWidth: 80,
		plain: true,
		cls: "ui-tab-bar",
		plugins: [
			Ext.create("Ext.ux.TabReorderer"),
			Ext.create("Ext.ux.TabCloseMenu", {
				closeTabText: "关闭当前",
				closeOthersTabsText: "关闭其他",
				closeAllTabsText: "关闭所有"
			})
		],
		items: [],
		listeners: {
			beforeremove: function(thiz, component, eOpts){
				var iframe = window.frames["iframe_" + component.getId()];
				if (typeof(iframe.top_window_destroy) == "function") {
					iframe.top_window_destroy();
				}
			}
		}
	});

    Ext.create("Ext.Viewport", {
		layout: "border",
        items: [left, right]
    });
    
    /** ------------------------------------- handler ------------------------------------- */
    addTab = function(id, title, url){
		if (!right.queryById(id)) {
			if (right.items.length == 5) {
				right.remove(right.items.items[4]);
			}
			right.add({
				id: id,
				title: title,
				closable: true,
				html: "<iframe id='iframe_" + id + "' src='" + url + "' width='100%' height='100%' frameborder='0' border='0' scrolling='auto'></iframe>"
			});
		}
		right.setActiveTab(id);
	};
});
