<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<html>
<head>
    <title>投票</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
    <script type="text/javascript" src="js/vote.js"></script>
</head>
<body>
<center>
    <c:set var="showpage" value="${requestScope.showpage}"/>
    <c:if test="${empty showpage}">
        <c:set var="showpage" value="welcome.jsp"/>
    </c:if>
    <table border="0" cellpadding="0" cellspacing="0" width="833">
        <tr height="50">
            <td>
                <img src="images/top.jpg">
            </td>
        </tr>
    </table>
    <table border="0" cellpadding="0" cellspacing="0" width="833">
        <tr>
            <td width="213" valign="top" background="images/leftbg.jpg">
                <form action="vote" name="voteform" method="post" target="resultpage">
                    <table border="0" width="100%">
                        <tr height="95" align="center">
                            <td colspan="2">
                            <img src="images/lefttopbg.jpg">
                            </td>
                        </tr>
                        <c:set var="options" value="${requestScope.optionlist}"/>
                        <c:if test="${empty options}">
                            <tr><td colspan="2">没有投票选项</td></tr>
                        </c:if>
                        <c:if test="${!empty options}">
                            <c:forEach var="option" varStatus="ovs" items="${options}">
                                <tr>
                                    <td style="padding-left:20px">
                                        <img src="images/title.jpg"> ${option.optionName}</td>
                                    <td align="center">
                                        <input type="radio" name="movie" value="${option.id}" onclick="message.innerHTML=''">
                                    </td>
                                </tr>
                                <tr><td colspan="2"><img src="images/line.jpg"></td></tr>
                            </c:forEach>
                        </c:if>
                        <tr height="40">
                            <td><b><span id="message" style="color:red"></span></b></td>
                            <td>
                                <input type="button" value="" name="voteb" style="background-image:url(images/submit.jpg);border:0;width:76px;height:23px"
                                       onclick="checkvote();">
                            </td>
                        </tr>
                    </table>
                </form>
            </td>
            <td align="center" valign="top">
    				<span id="wait" style="display:none">
    					正在加载...
    				</span>
                <span id="result" style="display:">
                    <iframe id="resultpage" name="resultpage" frameborder="0" width="100%" height="450px" scrolling="no" src="${showpage}"></iframe>
                </span>
            </td>
        </tr>
    </table>
    <table border="0" cellpadding="0" cellspacing="0">
        <tr><td><img src="images/end.jpg"></td></tr>
    </table>
</center>
</body>
</html>
