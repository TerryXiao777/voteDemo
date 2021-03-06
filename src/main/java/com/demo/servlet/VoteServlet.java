package com.demo.servlet;

import com.demo.bean.AreaBean;
import com.demo.bean.OptionBean;
import com.demo.dao.AreaDao;
import com.demo.dao.OptionDao;
import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.TextAnchor;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteServlet extends HttpServlet {
    private int width=0;
    private int height=0;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        width=0;
        height=0;
        String servletPath=request.getServletPath();
        if("/vote".equals(servletPath)){
            vote(request,response);
        }
        else if("/showresult".equals(servletPath)){
            showresult(request,response);
        }

    }
    private void vote(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String message="";
        String showpage="";

        int optionid=Integer.parseInt(request.getParameter("movie"));
        int i=new OptionDao().vote(optionid);
        if(i<=0){
            message="?? ???????????????";
            showpage="fail.jsp";
        }
        else{
            HttpSession session=request.getSession();
            session.setMaxInactiveInterval(3600);
            session.setAttribute("ido","yes");
            showpage="success.jsp";
        }

        request.setAttribute("message",message);
        RequestDispatcher rd=request.getRequestDispatcher(showpage);
        rd.forward(request,response);
    }
    protected void showresult(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String forward="";

        Map args=getParams(request);
        String action=(String)args.get("action");
        String method=(String)args.get("method");
        String showmode=(String)args.get("showmode");

        JFreeChart chart=null;
        //???????????????
        if("pie".equals(showmode)){
            chart=getChartForPie(action,method);
        }
        else{
            //???????????????
            chart=getChartForBar(action,method);
        }
        if(chart!=null){
            //??????????????????????????????
            myplot(showmode,chart);

            String webName=getServletContext().getRealPath("/plot");
            String prefix=action+"_"+method+"_"+showmode;
            //??????????????????
            String picpath=webName+"/"+prefix+".jpg";
            //??????????????????
            String mappath=webName+"/"+prefix+".map";

            FileOutputStream plot_fos=new FileOutputStream(picpath);
            PrintWriter map_pw =new PrintWriter(mappath);
            ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());

            //??????????????????
            ChartUtilities.writeChartAsJPEG(plot_fos,0.8f,chart,width,height,info);
            //??????????????????
            ChartUtilities.writeImageMap(map_pw, "mymap", info,false);

            plot_fos.close();
            map_pw.close();

            request.setAttribute("path",prefix);
            forward="/showresult.jsp";
        }
        else{
            request.setAttribute("message","?????????????????????????????????????????????");
            forward="/fail.jsp";
        }

        RequestDispatcher rd=request.getRequestDispatcher(forward);
        rd.forward(request,response);
    }

    private Map getParams(HttpServletRequest request){
        HttpSession session=request.getSession();

        //??????????????????????????????option- ???????????????????????????; area - ????????????????????????
        String action=request.getParameter("action");
        //??????????????????????????????all   - ?????????		day  - ????????????month - ?????????
        String method=request.getParameter("method");
        //??????????????????????????????var   - ????????????	pie  - ?????????
        String showmode=request.getParameter("showmode");
        //???????????????
        if(action==null||action.equals("")||(!action.equals("option")&&!action.equals("area")))
            action=(String)session.getAttribute("action");
        else
            session.setAttribute("action",action);

        //???????????????
        if(method==null||method.equals("")||(!method.equals("all") &&!method.equals("day") &&!method.equals("month"))){
            method=(String)session.getAttribute("method");
        }
        else{
            session.setAttribute("method", method);
        }


        //???????????????
        if(showmode==null||showmode.equals("")||(!showmode.equals("bar")&&!showmode.equals("pie"))){
            showmode=(String)session.getAttribute("showmode");
        }
        else{
            session.setAttribute("showmode", showmode);
        }

        if(action==null||action.equals("")||!action.equals("area")){
            action="option";
        }

        if(method==null||method.equals("")||(!method.equals("day")&&!method.equals("month"))){
            method="all";
        }

        if(showmode==null||showmode.equals("")||!showmode.equals("pie")){
            showmode="bar";
        }

        HashMap args=new HashMap();
        args.put("action",action);
        args.put("method",method);
        args.put("showmode",showmode);

        return args;
    }

    /**???@???????????????????????????JFreeChart */
    private JFreeChart getChartForBar(String action, String method){
        CategoryDataset dataset=null;
        JFreeChart chart=null;

        String title1="";
        String title2="";
        String subtitle="";
        PlotOrientation way=null;

        // ??????????????????
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        // ??????????????????
        standardChartTheme.setExtraLargeFont(new Font("??????", Font.BOLD, 20));
        // ?????????????????????
        standardChartTheme.setRegularFont(new Font("??????", Font.PLAIN, 13));
        // ?????????????????????
        standardChartTheme.setLargeFont(new Font("??????", Font.PLAIN, 13));
        // ??????????????????
        ChartFactory.setChartTheme(standardChartTheme);

        if("day".equals(method)){
            subtitle="????????????(??????)";
        }
        else if("month".equals(method)){
            subtitle="????????????(?????????)";
        }


        //????????????"??????????????????"?????????
        if("area".equals(action)){
            //???????????????
            dataset = getDataSetForBarAndArea(method);
            title1="?????????????????????";
            title2="??????";
            way=PlotOrientation.HORIZONTAL;
            width=500;
            height=100+25*dataset.getColumnCount();

            if(dataset!=null&&dataset.getColumnCount()>0){
                chart = ChartFactory.createBarChart(title1,title2,"??????",dataset,way,false,true,false);
                chart.addSubtitle(new TextTitle(subtitle));
            }
        }
        else{													//????????????"??????????????????"?????????
            dataset = getDataSetForBarAndOption(method);				//???????????????
            title1="??????????????????";
            title2="??????";
            way=PlotOrientation.VERTICAL;
            width=80+50*dataset.getColumnCount();
            height=400;

            if(dataset!=null&&dataset.getColumnCount()>0){
                chart = ChartFactory.createBarChart3D(title1,title2,"??????",dataset,way,false,true,false);
                chart.addSubtitle(new TextTitle(subtitle));
            }
        }

        return chart;
    }

    private CategoryDataset getDataSetForBarAndOption(String method) {
        OptionDao optionDao=new OptionDao();
        List options=null;

        if("all".equals(method)){
            options=optionDao.getOptions();
        }
        else if("day".equals(method)){
            options=optionDao.getOptionsForDay();
        }
        else if("month".equals(method)){
            options=optionDao.getOptionsForMonth();
        }

        optionDao.closed();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for(int i=0;i<options.size();i++){
            OptionBean single=(OptionBean)options.get(i);
            dataset.addValue(single.getOptionBallot(),"",single.getOptionName());
        }

        return dataset;
    }
    private CategoryDataset getDataSetForBarAndArea(String method) {
        DefaultCategoryDataset dataset=null;
        AreaDao areaDao = new AreaDao();
        List areas=null;

        if("all".equals(method)){
            areas=areaDao.getAreas();
        }
        else if("day".equals(method)){
            areas=areaDao.getAreasForDay();
        }
        else if("month".equals(method)){
            areas=areaDao.getAreasForMonth();
        }
        areaDao.closed();

        if(areas!=null&&areas.size()>0){
            dataset = new DefaultCategoryDataset();
            for(int i=0;i<areas.size();i++){
                AreaBean single=(AreaBean)areas.get(i);
                if(single.getAreaBallot()>0)
                    dataset.addValue(single.getAreaBallot(),"",single.getAreaName());
            }
        }

        return dataset;
    }


    /**???@???????????????????????????JFreeChart */
    private JFreeChart getChartForPie(String action,String method){
        DefaultPieDataset dataset=null;
        JFreeChart chart=null;
        String title="";
        String subtitle="";
        width=550;
        height=430;

        if("day".equals(method)){
            subtitle="????????????(??????)";
        }
        else if("month".equals(method)){
            subtitle="????????????(?????????)";
        }

        //????????????"??????????????????"?????????
        if("area".equals(action)){
            //???????????????
            dataset = getDataSetForPieAndArea(method);
            title="?????????????????????";
        }
        else{
            //????????????"??????????????????"?????????
            //???????????????
            dataset = getDataSetForPieAndOption(method);
            title="??????????????????";
        }
        if(dataset!=null&&dataset.getItemCount()>0){
            // ??????????????????
            StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
            // ??????????????????
            standardChartTheme.setExtraLargeFont(new Font("??????", Font.BOLD, 20));
            // ?????????????????????
            standardChartTheme.setRegularFont(new Font("??????", Font.PLAIN, 13));
            // ?????????????????????
            standardChartTheme.setLargeFont(new Font("??????", Font.PLAIN, 13));
            // ??????????????????
            ChartFactory.setChartTheme(standardChartTheme);
            chart = ChartFactory.createPieChart3D(title, dataset, true, true, false);
            chart.addSubtitle(new TextTitle(subtitle));
        }

        return chart;
    }

    private DefaultPieDataset getDataSetForPieAndOption(String method) {
        DefaultPieDataset dataset=null;
        OptionDao optionDao=new OptionDao();
        List options=null;

        if("all".equals(method)){
            options=optionDao.getOptions();
        }
        else if("day".equals(method)){
            options=optionDao.getOptionsForDay();
        }
        else if("month".equals(method)){
            options=optionDao.getOptionsForMonth();
        }
        optionDao.closed();

        if(options!=null&&options.size()!=0){
            dataset = new DefaultPieDataset();
            for(int i=0;i<options.size();i++){
                OptionBean single=(OptionBean)options.get(i);
                if(single.getOptionBallot()>0)
                    dataset.setValue(single.getOptionName(),single.getOptionBallot());
            }
        }

        return dataset;
    }

    private DefaultPieDataset getDataSetForPieAndArea(String method) {
        DefaultPieDataset dataset=null;
        AreaDao areaDao=new AreaDao();
        List areas=null;

        if("all".equals(method)){
            areas=areaDao.getAreas();
        }
        else if("day".equals(method)){
            areas=areaDao.getAreasForDay();
        }
        else if("month".equals(method)){
            areas=areaDao.getAreasForMonth();
        }
        areaDao.closed();

        if(areas!=null&&areas.size()>0){
            dataset = new DefaultPieDataset();
            for(int i=0;i<areas.size();i++){
                AreaBean single=(AreaBean)areas.get(i);
                if(single.getAreaBallot()>0)
                    dataset.setValue(single.getAreaName(),single.getAreaBallot());
            }
        }

        return dataset;
    }

    private void myplot(String showmode,JFreeChart chart){
        if("pie".equals(showmode)){
            PiePlot pieplot=(PiePlot)chart.getPlot();
            pieplot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ??????:{1}"));			// ????????????????????????
            pieplot.setToolTipGenerator(new StandardPieToolTipGenerator("{0} ??????:{2}"));			// ????????????????????????
        }
        else{
            CategoryPlot barplot=(CategoryPlot)chart.getCategoryPlot();
            BarRenderer br=(BarRenderer) barplot.getRenderer();

            //??????????????????
            br.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("{1} ??????:{2}",new DecimalFormat("#,###")));

            //????????????????????????
            br.setItemLabelAnchorOffset(10);

            ItemLabelPosition itemlabelposition = new ItemLabelPosition(ItemLabelAnchor.INSIDE1, TextAnchor.CENTER_RIGHT,TextAnchor.CENTER_RIGHT,0);
            br.setBasePositiveItemLabelPosition(itemlabelposition);

            br.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2} ???",new DecimalFormat("#,###")));
            br.setBaseItemLabelsVisible(true);

            CategoryAxis categoryaxis = barplot.getDomainAxis();
            categoryaxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        }
    }
}
