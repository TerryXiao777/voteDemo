package com.demo.servlet;

import com.demo.dao.OptionDao;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class IndexServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List options=new OptionDao().getOptions();
        request.setAttribute("optionlist",options);

        RequestDispatcher rd=request.getRequestDispatcher("/main.jsp");
        rd.forward(request,response);
    }
}
