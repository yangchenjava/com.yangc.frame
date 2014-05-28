<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.include file="/jsp/frame/head.jspf" />
<link rel="stylesheet" type="text/css" href="<%=css_custom%>frame/index.css" />
<script type="text/javascript" src="<%=js_custom%>frame/index.js"></script>
<script type="text/javascript">
var parentMenuId = 0;
var personName = "${sessionScope.CURRENT_USER.personName}";
</script>
</head>
<body>
</body>
</html>
