<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="com.yangc.utils.Constants"%>
<jsp:directive.include file="/jsp/frame/head.jspf" />
<link rel="stylesheet" type="text/css" href="<%=css_lib%>404/pageNotFound.css" />
<script type="text/javascript" src="<%=js_lib%>404/pageNotFound.js"></script>
</head>
<body>
	<div id="rocket"></div>
	<hgroup>
		<h2><b>404.</b> 抱歉! 您访问的资源不存在!</h2>
		<h2>给您带来的不便我们深表歉意! <a href="<%=basePath + Constants.INDEX_PAGE%>" style="color: #08c;">返回网站首页</a>.</h2>
	</hgroup>
</body>
</html>
