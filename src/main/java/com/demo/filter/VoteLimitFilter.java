package com.demo.filter;

import com.demo.dao.VoterDao;
import com.demo.tools.StringHandler;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public class VoteLimitFilter implements Filter {
    private FilterConfig fc=null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.fc=filterConfig;
    }

    @Override
    public void doFilter(ServletRequest sRequest, ServletResponse sResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest)sRequest;
        HttpServletResponse response=(HttpServletResponse)sResponse;
        HttpSession session=request.getSession();

        //查询服务器端该IP上次投票的时间
        String ip=request.getRemoteAddr();
        //获取客户端IP
        long ipnum= StringHandler.getIpNum(ip);
        //获取选择的选项ID
        String optionid=request.getParameter("movie");
        try {
            VoterDao voterDao=new VoterDao();
            //获取当前时间
            Date now=new Date();
            //获取该IP的上次投票时间
            Date last = voterDao.getLastVoteTime(ipnum);

            //数据库中没有记录该IP，则该IP地址没有投过票
            if(last==null){
                //在客户端的cookie中记录该用户已经投过票
                addCookie(request,response);

                String[] params={String.valueOf(ipnum),optionid,StringHandler.timeTostr(now)};
                //在数据库中记录该IP、选择的选项ID和投票时间
                voterDao.saveVoteTime(params);
                chain.doFilter(request,response);
            }
            else{
                //该IP地址投过票，则接着判断客户端cookie中是否记录了用户投票情况（用来解决局域网中某个ip投票后，其他ip不能再进行投票的问题）
                //判断当前使用该IP的用户的客户端的cookie中是否记录了投票标记
                boolean voteincookie=seeCookie(request);
                //如果记录了该用户已经投过票
                if(voteincookie){
                    request.setAttribute("message","● 您已经投过票了，1小时内不允许重复投票！");
                    RequestDispatcher rd=request.getRequestDispatcher("fail.jsp");
                    rd.forward(request,response);
                }
                else{
                    //没有记录该用户是否投过票，则接着判断当前session中是否记录了用户投票的情况（用来解决用户投票后，删除本地cookie实现重复投票）
                    String ido=(String)session.getAttribute("ido");
                    if("yes".equals(ido)){
                        //当前用户已投过票
                        request.setAttribute("message","● 您已经投过票了，1小时内不允许重复投票！");
                        RequestDispatcher rd=request.getRequestDispatcher("fail.jsp");
                        rd.forward(request,response);
                    }
                    else{
                        //在客户端的cookie中记录该用户已经投过票
                        addCookie(request,response);

                        String[] params={String.valueOf(ipnum),String.valueOf(optionid),StringHandler.timeTostr(now)};
                        //记录使用该IP的用户的投票时间
                        voterDao.saveVoteTime(params);
                        chain.doFilter(request,response);
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        this.fc=null;
    }


    private boolean seeCookie(HttpServletRequest request){
        boolean hasvote=false;
        String webName=request.getContextPath();
        webName=webName.substring(1);
        String cookiename=webName+".voter";

        Cookie[] cookies=request.getCookies();
        if(cookies!=null&&cookies.length!=0){
            for(int i=0;i<cookies.length;i++){
                Cookie single=cookies[i];
                if(single.getName().equals(cookiename)&&single.getValue().equals("I-Have-Vote")){
                    hasvote=true;
                    break;
                }
            }
        }
        return hasvote;
    }
    private void addCookie(HttpServletRequest request,HttpServletResponse response){
        String webname=request.getContextPath();
        webname=webname.substring(1);
        //创建一个cookie
        Cookie cookie=new Cookie(webname+".voter","I-Have-Vote");
        cookie.setPath("/");
        //设置cookie在客户端保存的有效时间为1小时
        cookie.setMaxAge(60*60*1);
        //向客户端写入cookie
        response.addCookie(cookie);
    }
}
