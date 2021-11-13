<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<html>
<head>
    <title>显示投票结果</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
    <script type="text/javascript" src="js/vote.js"></script>
</head>
<body onload="waitload()">
<center>
    <table border="0" width="100%" cellspacing="5" cellpadding="0">
        <tr>
            <td>
                <a href="showresult?action=option&method=all" target="resultpic" onclick="waitclick()">各项所得票数</a><br>
                ●<a href="showresult?action=option&method=day" target="resultpic" onclick="waitclick()">日统计票数</a><br>
                ●<a href="showresult?action=option&method=month" target="resultpic" onclick="waitclick()">月统计票数</a><br>
            </td>
            <td>
                <a href="showresult?action=area&method=all" target="resultpic" onclick="waitclick()">各省所投票数</a><br>
                ●<a href="showresult?action=area&method=day" target="resultpic" onclick="waitclick()">日统计票数</a><br>
                ●<a href="showresult?action=area&method=month" target="resultpic" onclick="waitclick()">月统计票数</a><br>
            </td>
        </tr>
        <tr height="40" valign="bottom">
            <td colspan="2">
                <a href="showresult?showmode=bar" target="resultpic" onclick="waitclick()">柱型图</a>&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="showresult?showmode=pie" target="resultpic" onclick="waitclick()">饼型图</a>&nbsp;&nbsp;&nbsp;&nbsp;
            </td>
        </tr>
        <tr height="80" bgcolor="#EEEEEE">
            <td style="border:1px solid" colspan="2" align="center">
    				<span id="wait" style="display:none">
    					正在加载...
    				</span>
                <span id="result" style="display:">
    					<iframe id="resultpic" name="resultpic" frameborder="0" scrolling="auto" width="100%" height="350" src="showresult"></iframe>
    				</span>
            </td>
        </tr>
    </table>
</center>
</body>
</html>
