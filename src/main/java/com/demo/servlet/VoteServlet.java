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
            message="× 投票失败！";
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
        //绘制饼型图
        if("pie".equals(showmode)){
            chart=getChartForPie(action,method);
        }
        else{
            //绘制柱型图
            chart=getChartForBar(action,method);
        }
        if(chart!=null){
            //设置各标签的显示样式
            myplot(showmode,chart);

            String webName=getServletContext().getRealPath("/plot");
            String prefix=action+"_"+method+"_"+showmode;
            //图片文件路径
            String picpath=webName+"/"+prefix+".jpg";
            //热区文件路径
            String mappath=webName+"/"+prefix+".map";

            FileOutputStream plot_fos=new FileOutputStream(picpath);
            PrintWriter map_pw =new PrintWriter(mappath);
            ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());

            //生成图片文件
            ChartUtilities.writeChartAsJPEG(plot_fos,0.8f,chart,width,height,info);
            //生成热区文件
            ChartUtilities.writeImageMap(map_pw, "mymap", info,false);

            plot_fos.close();
            map_pw.close();

            request.setAttribute("path",prefix);
            forward="/showresult.jsp";
        }
        else{
            request.setAttribute("message","●您所查看的时间段中没有数据！");
            forward="/fail.jsp";
        }

        RequestDispatcher rd=request.getRequestDispatcher(forward);
        rd.forward(request,response);
    }

    private Map getParams(HttpServletRequest request){
        HttpSession session=request.getSession();

        //获取进行统计的对象：option- 统计各选项的得票数; area - 统计各省的投票数
        String action=request.getParameter("action");
        //获取统计数据的方法：all   - 全部；		day  - 日统计；month - 月统计
        String method=request.getParameter("method");
        //获取显示结果的方式：var   - 柱状图；	pie  - 饼型图
        String showmode=request.getParameter("showmode");
        //统计的对象
        if(action==null||action.equals("")||(!action.equals("option")&&!action.equals("area")))
            action=(String)session.getAttribute("action");
        else
            session.setAttribute("action",action);

        //统计的方法
        if(method==null||method.equals("")||(!method.equals("all") &&!method.equals("day") &&!method.equals("month"))){
            method=(String)session.getAttribute("method");
        }
        else{
            session.setAttribute("method", method);
        }


        //显示的方式
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

    /**　@功能：获取柱型图的JFreeChart */
    private JFreeChart getChartForBar(String action, String method){
        CategoryDataset dataset=null;
        JFreeChart chart=null;

        String title1="";
        String title2="";
        String subtitle="";
        PlotOrientation way=null;

        // 创建主题样式
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        // 设置标题字体
        standardChartTheme.setExtraLargeFont(new Font("宋书", Font.BOLD, 20));
        // 设置图例的字体
        standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 13));
        // 设置轴向的字体
        standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 13));
        // 应用主题样式
        ChartFactory.setChartTheme(standardChartTheme);

        if("day".equals(method)){
            subtitle="－日统计(今日)";
        }
        else if("month".equals(method)){
            subtitle="－月统计(当前月)";
        }


        //处理查看"各省的投票数"的请求
        if("area".equals(action)){
            //获取数据集
            dataset = getDataSetForBarAndArea(method);
            title1="各省所投票数图";
            title2="省份";
            way=PlotOrientation.HORIZONTAL;
            width=500;
            height=100+25*dataset.getColumnCount();

            if(dataset!=null&&dataset.getColumnCount()>0){
                chart = ChartFactory.createBarChart(title1,title2,"票数",dataset,way,false,true,false);
                chart.addSubtitle(new TextTitle(subtitle));
            }
        }
        else{													//处理查看"各选项得票数"的请求
            dataset = getDataSetForBarAndOption(method);				//获取数据集
            title1="各项所得票数";
            title2="选项";
            way=PlotOrientation.VERTICAL;
            width=80+50*dataset.getColumnCount();
            height=400;

            if(dataset!=null&&dataset.getColumnCount()>0){
                chart = ChartFactory.createBarChart3D(title1,title2,"票数",dataset,way,false,true,false);
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


    /**　@功能：获取饼型图的JFreeChart */
    private JFreeChart getChartForPie(String action,String method){
        DefaultPieDataset dataset=null;
        JFreeChart chart=null;
        String title="";
        String subtitle="";
        width=550;
        height=430;

        if("day".equals(method)){
            subtitle="－日统计(今日)";
        }
        else if("month".equals(method)){
            subtitle="－月统计(当前月)";
        }

        //处理查看"各省的投票数"的请求
        if("area".equals(action)){
            //获取数据集
            dataset = getDataSetForPieAndArea(method);
            title="各省所投票数图";
        }
        else{
            //处理查看"各选项得票数"的请求
            //获取数据集
            dataset = getDataSetForPieAndOption(method);
            title="各项所得票数";
        }
        if(dataset!=null&&dataset.getItemCount()>0){
            // 创建主题样式
            StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
            // 设置标题字体
            standardChartTheme.setExtraLargeFont(new Font("宋书", Font.BOLD, 20));
            // 设置图例的字体
            standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 13));
            // 设置轴向的字体
            standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 13));
            // 应用主题样式
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
            pieplot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} 票数:{1}"));			// 设置普通标签样式
            pieplot.setToolTipGenerator(new StandardPieToolTipGenerator("{0} 比例:{2}"));			// 设置热区标签样式
        }
        else{
            CategoryPlot barplot=(CategoryPlot)chart.getCategoryPlot();
            BarRenderer br=(BarRenderer) barplot.getRenderer();

            //设置鼠标提示
            br.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("{1} 票数:{2}",new DecimalFormat("#,###")));

            //设置标签显示样式
            br.setItemLabelAnchorOffset(10);

            ItemLabelPosition itemlabelposition = new ItemLabelPosition(ItemLabelAnchor.INSIDE1, TextAnchor.CENTER_RIGHT,TextAnchor.CENTER_RIGHT,0);
            br.setBasePositiveItemLabelPosition(itemlabelposition);

            br.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2} 票",new DecimalFormat("#,###")));
            br.setBaseItemLabelsVisible(true);

            CategoryAxis categoryaxis = barplot.getDomainAxis();
            categoryaxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        }
    }
}
