package com.demo.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SubmitFilter implements Filter {

    private FilterConfig fc;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.fc=filterConfig;
    }

    @Override
    public void doFilter(ServletRequest sRequest, ServletResponse sResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest)sRequest;
        String method=request.getMethod();

        if(method.equalsIgnoreCase("POST"))
            chain.doFilter(sRequest,sResponse);
        else{
            request.setAttribute("message","● 不是以POST方式进行的请求！<br>");
            RequestDispatcher rd=request.getRequestDispatcher("fail.jsp");
            rd.forward(request,sResponse);
        }
    }

    @Override
    public void destroy() {
        this.fc=null;
    }
}
