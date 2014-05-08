<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="/jsp/frame/head.jspf" />
<link rel="stylesheet" type="text/css" href="<%=css_custom%>frame/main.css" />
<script type="text/javascript" src="<%=js_custom%>frame/main.js"></script>
<script type="text/javascript">
var parentMenuId = "${param.parentMenuId}";
var personName = "${sessionScope.LOGIN_USER.personName}";
</script>
</head>
<body>
</body>
</html>
