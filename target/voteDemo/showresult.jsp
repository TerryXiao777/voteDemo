<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript" src="js/vote.js"></script>
<body onload="waitload()" bgcolor="#EEEEEE">
<center>
    <jsp:include page="plot/${requestScope.path}.map"/>
    <div ondblclick="size()">
        <img id="pic" src="plot/${requestScope.path}.jpg" title="双击收缩图片"
             alt="正在加载图片，请稍等..." usemap="#mymap" style="border:0px">
    </div>
</center>
</body>
