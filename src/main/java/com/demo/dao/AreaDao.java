package com.demo.dao;

import com.demo.bean.AreaBean;
import com.demo.tools.JDBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AreaDao {
    private JDBConnection connection = null;

    public AreaDao(){
        connection = new JDBConnection();
    }

    public List getAreas(){
        StringBuilder sql = new StringBuilder();
        sql.append("select a.*,t.num from tb_area as a left join ");
        sql.append("(select voter_ip,count(id) as num from tb_voter group by voter_ip) t on ");
        sql.append("(t.voter_ip between a.area_ipStart and a.area_ipEnd)");
        List options=getList(sql.toString(),null);
        return options; 
    }

    public List getAreasForDay(){
        String sql="select a.*,t.num from tb_area as a left join (select voter_ip,count(id) as num from tb_voter where day(voter_votetime)=day(curdate()) group by voter_ip) t on (t.voter_ip between a.area_ipStart and a.area_ipEnd)";
        List options =getList(sql,null);
        return options;
    }

    public List getAreasForMonth(){
        String sql="select a.*,t.num from tb_area as a left join (select voter_ip,count(id) as num from tb_voter where month(voter_votetime)=month(curdate()) group by voter_ip) t on (t.voter_ip between a.area_ipStart and a.area_ipEnd)";
        List options =getList(sql,null);
        return options;
    }

    private List getList(String sql,String[] params){
        List options=null;
        connection = new JDBConnection();
        try {
            ResultSet rs = connection.queryByPsStatement(sql,params);
            if(rs!=null){
                options=new ArrayList();
                while(rs.next()){
                    AreaBean single=new AreaBean();
                    single.setAreaIpStart(rs.getLong(1));
                    single.setAreaIpEnd(rs.getLong(2));
                    single.setAreaName(rs.getString(3));
                    single.setAreaBallot(rs.getInt(4));
                    options.add(single);
                }
                rs.close();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return options;
    }

    public void closed(){
        connection.closeAll();
    }
}
