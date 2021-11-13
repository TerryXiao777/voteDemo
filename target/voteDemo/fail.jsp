<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" type="text/css" href="css/style.css">
<script type="text/javascript" src="js/vote.js"></script>
<body onload="waitload()">
<center>
    <br><br>
    <div style="background-image:url(images/message.jpg);width:357px;height:205px">
        <br><br><br><br>
        ${requestScope.message}<br>
        <a href="javascript:window.history.go(-1)">返回</a>
    </div>
</center>
</body>